package dev.atedeg.mdm.maintenance

import java.util.UUID

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Greater

final case class Temperature(value: Double Refined Greater[-273.15])

/**
 * An ID used to uniquely identify a [[Batch batch]].
 */
final case class BatchID(id: UUID)

final case class Maintenance(motivation: String)
