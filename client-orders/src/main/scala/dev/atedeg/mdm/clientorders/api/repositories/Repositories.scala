package dev.atedeg.mdm.clientorders.api.repositories

import cats.Monad
import cats.effect.LiftIO

import dev.atedeg.mdm.clientorders.InProgressOrder
import dev.atedeg.mdm.clientorders.dto.*
import dev.atedeg.mdm.utils.monads.*

trait PriceListRepository:
  def read[M[_]: Monad: LiftIO]: M[PriceListDTO]

trait OrderRepository:
  def writeInProgressOrder[M[_]: Monad: LiftIO](inProgressOrder: InProgressOrderDTO): M[Unit]
  def readInProgressOrder[M[_]: Monad: LiftIO: CanRaise[String]](orderID: String): M[InProgressOrderDTO]
  def updateOrderToCompleted[M[_]: Monad: LiftIO: CanRaise[String]](completedOrder: CompletedOrderDTO): M[Unit]

final case class PriceListRepositoryDB(connectionString: String) extends PriceListRepository:
  override def read[M[_]: Monad: LiftIO]: M[PriceListDTO] = ???

final case class OrderRepositoryDB(connectionString: String) extends OrderRepository:
  override def writeInProgressOrder[M[_]: Monad: LiftIO](inProgressOrder: InProgressOrderDTO): M[Unit] = ???
  override def readInProgressOrder[M[_]: Monad: LiftIO: CanRaise[String]](orderID: String): M[InProgressOrderDTO] = ???
  override def updateOrderToCompleted[M[_]: Monad: LiftIO: CanRaise[String]](order: CompletedOrderDTO): M[Unit] = ???
