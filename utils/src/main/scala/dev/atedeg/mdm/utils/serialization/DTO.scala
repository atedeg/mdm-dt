package dev.atedeg.mdm.utils.serialization

import java.time.{ LocalDate, LocalDateTime }
import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.compiletime.*
import scala.deriving.*
import scala.util.Try

import cats.Monad
import cats.data.NonEmptyList
import cats.syntax.all.*
import eu.timepit.refined.api.Refined

import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.monads.*

trait DTO[E, D]:
  def elemToDto(e: E): D
  def dtoToElem(dto: D): Either[String, E]

object DTOOps:
  extension [D](dto: D) def toDomain[E](using d: DTO[E, D]): Either[String, E] = d.dtoToElem(dto)
  extension [E](e: E) def toDTO[D](using d: DTO[E, D]) = d.elemToDto(e)
  def validate[D, E, M[_]: Monad: CanRaise[String]](dto: D)(using DTO[E, D]): M[E] = dto.toDomain[E].getOrRaise

object DTO:
  import DTOGenerators.*
  import DTOOps.*

  given DTO[Int, Int] = idDTO
  given DTO[Double, Double] = idDTO
  given DTO[String, String] = idDTO

  given mapListDTO[KE, VE, D](using DTO[(KE, VE), D]): DTO[Map[KE, VE], List[D]] with
    override def elemToDto(e: Map[KE, VE]): List[D] = e.toList.map(_.toDTO[D])
    override def dtoToElem(dto: List[D]): Either[String, Map[KE, VE]] = dto.traverse(_.toDomain[(KE, VE)]).map(_.toMap)

  given mapDTO[KE, KD, VE, VD](using DTO[KE, KD])(using DTO[VE, VD]): DTO[Map[KE, VE], Map[KD, VD]] with
    override def elemToDto(e: Map[KE, VE]): Map[KD, VD] = e.map(_.bimap(_.toDTO[KD], _.toDTO[VD]))
    override def dtoToElem(dto: Map[KD, VD]): Either[String, Map[KE, VE]] =
      for
        keys <- dto.keys.toList.traverse(_.toDomain[KE])
        values <- dto.values.toList.traverse(_.toDomain[VE])
      yield keys.zip(values).toMap

  given listDTO[E, D](using DTO[E, D]): DTO[List[E], List[D]] with
    override def dtoToElem(dto: List[D]): Either[String, List[E]] = dto.traverse(_.toDomain)
    override def elemToDto(e: List[E]): List[D] = e.map(_.toDTO)

  given nonEmptyListDTO[E, D](using DTO[E, D]): DTO[NonEmptyList[E], List[D]] with
    override def elemToDto(e: NonEmptyList[E]): List[D] = e.toList.map(_.toDTO)
    override def dtoToElem(dto: List[D]): Either[String, NonEmptyList[E]] =
      dto.toNel.toRight("Got an empty list, it should contain at least one element").flatMap(_.traverse(_.toDomain))

  given DTO[UUID, String] with
    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    override def elemToDto(e: UUID): String = e.toString
    override def dtoToElem(dto: String): Either[String, UUID] =
      Try(UUID.fromString(dto)).toEither.leftMap(_ => s"Invalid UUID: $dto")

  given DTO[LocalDate, String] with
    override def elemToDto(e: LocalDate): String = e.format(DateTimeFormatter.ISO_LOCAL_DATE)
    override def dtoToElem(dto: String): Either[String, LocalDate] =
      Try(LocalDate.parse(dto, DateTimeFormatter.ISO_LOCAL_DATE)).toEither.leftMap(_ => s"Invalid date: $dto")

  given DTO[LocalDateTime, String] with
    override def elemToDto(e: LocalDateTime): String = e.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    override def dtoToElem(dto: String): Either[String, LocalDateTime] =
      Try(LocalDateTime.parse(dto, DateTimeFormatter.ISO_LOCAL_DATE_TIME)).toEither.leftMap(_ => s"Invalid date: $dto")

  given refinedDTO[E, D, P: ValidFor[E]](using d: DTO[E, D]): DTO[E Refined P, D] with
    override def elemToDto(e: E Refined P): D = e.value.toDTO
    override def dtoToElem(dto: D): Either[String, E Refined P] = d.dtoToElem(dto).flatMap(_.refined[P])

object DTOGenerators:
  def idDTO[T]: DTO[T, T] = new DTO[T, T]:
    override def elemToDto(e: T): T = e
    override def dtoToElem(dto: T): Either[String, T] = dto.asRight[String]

  inline def unwrapFieldDTO[E, D](using p: Mirror.ProductOf[E]): DTO[E, D] =
    inline numberOfFields(p) match
      case 1 =>
        type t = First[p.MirroredElemTypes]
        val instance: DTO[t, D] = summonInline[DTO[t, D]]
        new DTO[E, D]:
          def elemToDto(e: E): D = instance.elemToDto(nthField(e, 0))
          def dtoToElem(dto: D): Either[String, E] = instance.dtoToElem(dto).map(e => p.fromProduct(e *: EmptyTuple))
      case _ => compiletime.error("Can only derive for case classes with only one field")

  inline def productTypeDTO[C1, C2](using p1: Mirror.ProductOf[C1])(using p2: Mirror.ProductOf[C2]): DTO[C1, C2] =
    inline if numberOfFields(p1) != numberOfFields(p2)
    then compiletime.error("Can only derive DTO for case classes with same number of fields")
    else
      type DTOs = Tuple.Map[Tuple.Zip[p1.MirroredElemTypes, p2.MirroredElemTypes], DTOFromTuple]
      val instances = summonAll[DTOs].toList.asInstanceOf[List[DTO[Any, Any]]]
      new DTO[C1, C2]:
        override def elemToDto(e: C1): C2 =
          val fields = e.asInstanceOf[Product].productIterator.zip(instances).map(fi => fi._2.elemToDto(fi._1)).toArray
          p2.fromProduct(Tuple.fromArray(fields))

        override def dtoToElem(dto: C2): Either[String, C1] = dto
          .asInstanceOf[Product]
          .productIterator
          .zip(instances)
          .toList
          .traverse(fi => fi._2.dtoToElem(fi._1))
          .map(l => p1.fromProduct(Tuple.fromArray(l.toArray)))

  inline def sumTypeDTO[E, D](using s: Mirror.SumOf[E])(using p: Mirror.ProductOf[D]) =
    checkDTO(s, p)
    type DTOs = Tuple.Map[Tuple.Zip[s.MirroredElemTypes, Tuple.Map[Tail[p.MirroredElemTypes], UnOption]], DTOFromTuple]
    val instances = summonAll[DTOs].toList.asInstanceOf[List[DTO[Any, Any]]]
    val tags = constValueTuple[s.MirroredElemLabels].toList
    val baseList = List.fill(tags.size)(None)
    new DTO[E, D]:
      def elemToDto(e: E): D =
        val index = s.ordinal(e)
        val l = tags(index) :: baseList.updated(index, Some(instances(index).elemToDto(e)))
        p.fromProduct(Tuple.fromArray(l.toArray))
      def dtoToElem(d: D): Either[String, E] =
        val tag: String = nthField(d, 0)
        val index = tags.indexOf(tag)
        if index < 0 then s"Unknown DTO tag: $tag".asLeft[E]
        else
          d.asInstanceOf[Product].productElement(index + 1) match
            case None => s"Found tag $tag but the corresponding field does not contain the needed data".asLeft[E]
            case Some(dto) => instances(index).dtoToElem(dto).asInstanceOf[Either[String, E]]

  inline private def hasEnoughFieldsToHoldAllCases[E, D](
      inline s: Mirror.SumOf[E],
      inline p: Mirror.ProductOf[D],
  ): Boolean =
    numberOfCases(s) + 1 == numberOfFields(p)

  private type UnOption[T] = T match
    case Option[t] => t
  private type DTOFromTuple[T] = T match
    case (t1 *: t2 *: EmptyTuple) => DTO[t1, t2]
  private type First[T <: Tuple] = Tuple.Elem[T, 0]
  private type Tail[T <: Tuple] <: Tuple = T match
    case (? *: t) => t

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  private def nthField[T, X](t: T, n: Int): X = t.asInstanceOf[Product].productElement(n).asInstanceOf[X]
  inline private def numberOfFields[A](inline p: Mirror.ProductOf[A]): Int = constValue[Tuple.Size[p.MirroredElemTypes]]
  inline private def numberOfCases[A](inline s: Mirror.SumOf[A]): Int = constValue[Tuple.Size[s.MirroredElemTypes]]
  inline def isFirstFieldAString[A](inline p: Mirror.ProductOf[A]): Boolean =
    inline erasedValue[First[p.MirroredElemTypes]] match
      case _: String => true
      case _ => false

  inline private def checkDTO[E, D](s: Mirror.SumOf[E], p: Mirror.ProductOf[D]): Unit =
    inline if hasEnoughFieldsToHoldAllCases(s, p) then
      inline if isFirstFieldAString(p) then ()
      else compiletime.error("The first field of the DTO type should be a String that can bee used as a tag")
    else compiletime.error("The DTO type should have exactly one field more than the Elem type's number of cases")
