package dev.atedeg.mdm.stocking

import java.time.LocalDateTime

import dev.atedeg.mdm.products.{ CheeseType, Product }
import dev.atedeg.mdm.utils.*

/**
 * The events that may be produced by the bounded context.
 */
enum OutgoingEvent:
  /**
   * Fired when a label is printed for a [[Product product]], which is then stocked.
   */
  case ProductStocked(labelledProduct: LabelledProduct)

/**
 * The events that have to be handled by the bounded context.
 */
enum IncomingEvent:
  /**
   * Received when a [[Batch batch]] is ready for quality assurance.
   */
  case BatchReadyForQualityAssurance(batch: BatchID)

  /**
   * Received when a [[Product product]] is removed from the stock.
   */
  case ProductRemovedFromStock(quantity: Quantity, product: Product)

  /**
   * Received when a [[Batch.Aging batch]] is created.
   */
  case NewBatch(batchID: BatchID, cheeseType: CheeseType, readyFrom: LocalDateTime)

  /**
   * Received when the metal detector rejects a [[Batch.Aging batch]].
   */
  case MetalDetectorRejectBatch(batchID: BatchID, quantity: PositiveNumber)

  /**
   * Received when the scale completes a [[Batch.Aging batch]].
   */
  case ScaleCompleteBatch(batchID: BatchID, droppedPackages: PositiveNumber)
