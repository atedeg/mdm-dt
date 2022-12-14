package dev.atedeg.mdm.stocking

import java.time.LocalDateTime
import java.util.UUID

import cats.data.NonEmptyList

import dev.atedeg.mdm.products.*
import dev.atedeg.mdm.utils.*

/**
 * The quantity in stock of a certain [[Product product]].
 */
final case class AvailableQuantity(n: NonNegativeNumber)

/**
 * The desired quantity of a certain [[Product product]] to be in stock.
 */
final case class DesiredQuantity(n: PositiveNumber)

/**
 * A quantity of something.
 */
final case class Quantity(n: PositiveNumber)

/**
 * The required quantity of a certain [[Product product]] to reach the desired stock level.
 */
final case class MissingQuantity(n: NonNegativeNumber)

/**
 * The currently available [[Product products]] in stock; each one is available
 * in a certain [[AvailableQuantity quantity]] (that could also be zero if the
 * product is out-of-stock).
 */
final case class AvailableStock(availableStock: Map[Product, AvailableQuantity])

/**
 * The [[DesiredQuantity desired quantity]] of each [[Product product]] that should
 * always be in stock in order to have a safe margin to keep order fulfillment going.
 */
final case class DesiredStock(desiredStock: Map[Product, DesiredQuantity])

/**
 * A batch of products of a certain [[CheeseType type]], uniquely identified by an [[BatchID ID]],
 * which hasn't been quality assured.
 */
enum Batch:
  /**
   * An aging batch that will become [[Batch.ReadyForQualityAssurance ready for quality assurance]] at the given date and time.
   */
  case Aging(id: BatchID, cheeseType: CheeseType, readyFrom: LocalDateTime)

  /**
   * A batch that is ready for quality assurance.
   */
  case ReadyForQualityAssurance(id: BatchID, cheeseType: CheeseType)

/**
 * A batch of products of a certain [[CheeseType type]] uniquely identified by an [[BatchID ID]],
 * which has undergone quality assurance.
 */
enum QualityAssuredBatch:
  /**
   * A batch which [[approveBatch() passed quality assurance]].
   */
  case Passed(id: BatchID, cheeseType: CheeseType)

  /**
   * A batch which [[rejectBatch() failed quality assurance]].
   */
  case Failed(id: BatchID, cheeseType: CheeseType)

/**
 * An ID used to uniquely identify a [[Batch batch]].
 */
final case class BatchID(id: UUID)

/**
 * A [[Product product]] with its respective [[Quantity quantity]] and the [[BatchID ID of the batch]] it belongs to.
 */
final case class LabelledProduct(product: Product, quantity: AvailableQuantity, batchID: BatchID)
