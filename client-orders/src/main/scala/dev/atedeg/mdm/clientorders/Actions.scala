package dev.atedeg.mdm.clientorders

import java.time.LocalDateTime
import scala.annotation.tailrec

import cats.Monad
import cats.data.NonEmptyList
import cats.kernel.Comparison.*
import cats.syntax.all.*

import dev.atedeg.mdm.clientorders.InProgressOrderLine.*
import dev.atedeg.mdm.clientorders.OrderCompletionError.*
import dev.atedeg.mdm.clientorders.OutgoingEvent.{ OrderProcessed, ProductPalletized }
import dev.atedeg.mdm.clientorders.PalletizationError.*
import dev.atedeg.mdm.clientorders.utils.*
import dev.atedeg.mdm.clientorders.utils.QuantityOps.*
import dev.atedeg.mdm.clientorders.utils.QuantityOps.given
import dev.atedeg.mdm.products.Product
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given
import dev.atedeg.mdm.utils.monads.*

/**
 * Given an [[IncomingOrderLine incoming order line]] and a [[PriceInEuroCents price]],
 * [[PricedOrderLine prices the order line]].
 */
def priceOrderLine(orderLine: IncomingOrderLine, price: PriceInEuroCents): PricedOrderLine =
  PricedOrderLine(orderLine.quantity, orderLine.product, price)

/**
 * Turns an [[IncomingOrder incoming order]] into a [[PricedOrder priced order]],
 * given the [[PricedOrderLine priced order lines]].
 */
def priceOrder[M[_]: Monad: Emits[OrderProcessed]](
    incomingOrder: IncomingOrder,
    pricedOrderLines: NonEmptyList[PricedOrderLine],
): M[PricedOrder] =
  val totalPrice = pricedOrderLines.map(_.totalPrice).reduce(_ + _)
  val pricedOrder = PricedOrder(
    incomingOrder.id,
    pricedOrderLines,
    incomingOrder.client,
    incomingOrder.deliveryDate,
    incomingOrder.deliveryLocation,
    totalPrice,
  )
  emit(OrderProcessed(incomingOrder): OrderProcessed).thenReturn(pricedOrder)

/**
 * Turns a [[Order.PricedOrder priced order]] into an [[Order.InProgressOrder in-progress order]] that can then be
 * fulfilled by operators.
 */
def startPreparingOrder(pricedOrder: PricedOrder): InProgressOrder =
  val PricedOrder(id, ols, client, deliveryDate, deliveryLocation, totalPrice) = pricedOrder
  val newOrderLine = ols.map(ol => Incomplete(0.palletizedQuantity, ol.quantity, ol.product, ol.totalPrice))
  InProgressOrder(id, newOrderLine, client, deliveryDate, deliveryLocation, totalPrice)

/**
 * Palletizes a [[Product product]] in the specified [[Order.Quantity quantity]] for a given
 * [[InProgressOrder order in progress]]. The result is an [[Order.InProgressOrder in-progress order]]
 * where the corresponding [[Order.InProgressOrderLine line]] has been updated with the
 * [[Order.Quantity specified quantity]].
 */
def palletizeProductForOrder[M[_]: CanRaise[PalletizationError]: Monad: Emits[ProductPalletized]](
    quantity: Quantity,
    product: Product,
)(
    inProgressOrder: InProgressOrder,
): M[InProgressOrder] =
  val InProgressOrder(id, ol, client, dd, dl, totalPrice) = inProgressOrder
  for
    orderLine <- ol.find(hasProduct(product)).ifMissingRaise(ProductNotInOrder(product))
    updatedLine <- addToLine(orderLine)(quantity)
    newOrderLines = ol.map {
      case Incomplete(_, _, `product`, _) => updatedLine
      case l @ _ => l
    }
    _ <- emit(ProductPalletized(product, quantity): ProductPalletized)
  yield InProgressOrder(id, newOrderLines, client, dd, dl, totalPrice)

private def hasProduct(product: Product)(ol: InProgressOrderLine): Boolean = ol match
  case Incomplete(_, _, p, _) => p === product
  case Complete(_, p, _) => p === product

private def addToLine[M[_]: Monad: CanRaise[PalletizedMoreThanRequired]](ol: InProgressOrderLine)(
    quantityToAdd: Quantity,
): M[InProgressOrderLine] = ol match
  case _: Complete => raise(PalletizedMoreThanRequired(0.missingQuantity): PalletizedMoreThanRequired)
  case Incomplete(palletized, required, product, price) =>
    val missingQuantity = (required.n.toNonNegative - palletized.n).missingQuantity
    missingQuantity.n.comparison(quantityToAdd.n.toNonNegative) match
      case GreaterThan => Incomplete(palletized + quantityToAdd.toPalletizedQuantity, required, product, price).pure
      case EqualTo => Complete(required, product, price).pure
      case LessThan => raise(PalletizedMoreThanRequired(missingQuantity): PalletizedMoreThanRequired)

/**
 * Completes an [[Order.InProgressOrder in-progress order]].
 */
def completeOrder[Result[_]: CanRaise[OrderCompletionError]: Monad](
    inProgressOrder: InProgressOrder,
): Result[CompletedOrder] =
  val InProgressOrder(id, ols, client, dd, dl, totalPrice) = inProgressOrder
  for
    completedOrderLines <- getCompletedOrderLines(ols).ifMissingRaise(OrderNotComplete())
    completeOrderLines = completedOrderLines.map(ol => CompleteOrderLine(ol.quantity, ol.product, ol.price))
  yield CompletedOrder(id, completeOrderLines, client, dd, dl, totalPrice)

private def getCompletedOrderLines(orderLines: NonEmptyList[InProgressOrderLine]): Option[NonEmptyList[Complete]] =
  @tailrec
  def go(acc: Option[List[Complete]])(l: List[InProgressOrderLine]): Option[List[Complete]] =
    l match
      case (c @ _: Complete) :: tail => go(acc.map(c :: _))(tail)
      case (_: Incomplete) :: _ => None
      case Nil => acc

  go(Some(Nil))(orderLines.toList).flatMap(_.toNel).map(_.reverse)

/**
 * Computes the total [[Order.WeightInKilograms weight]] of a [[Order.CompletedOrder complete order]].
 */
def weightOrder(completeOrder: CompletedOrder): WeightInKilograms =
  completeOrder.orderLines
    .map(r => (r.product, r.quantity))
    .map { case (product, quantity) => (product.weight.n * quantity.n).toDecimal / 1_000 }
    .map(WeightInKilograms(_))
    .reduce(_ + _)

/**
 * Creates a [[Order.TransportDocument transport document]] from a [[Order.CompletedOrder complete order]].
 */
def createTransportDocument(completeOrder: CompletedOrder, weight: WeightInKilograms): TransportDocument =
  val CompletedOrder(_, orderLines, client, _, deliveryLocation, _) = completeOrder
  val transportDocumentLines = orderLines.map(l => TransportDocumentLine(l.quantity, l.product))
  val date = LocalDateTime.now
  TransportDocument(deliveryLocation, mambelliDeliveryLocation, client, date, transportDocumentLines, weight)

private val mambelliDeliveryLocation = Location(Latitude(12.0), Longitude(44.0))
