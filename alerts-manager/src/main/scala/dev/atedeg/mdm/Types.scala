package dev.atedeg.mdm

import java.time.LocalTime

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Greater
import eu.timepit.refined.numeric.Interval

final case class Ph(value: Double Refined Interval.Closed[0.0, 14.0])
final case class Temperature(value: Double Refined Greater[-273.15])

final case class TemperatureFailure(timestamp: LocalTime, temperature: Temperature, device: String)
final case class PhFailure(timestamp: LocalTime, ph: Ph, device: String)

final case class TemperatureFailureMessage(message: String)
final case class PhFailureMessage(message: String)

final case class BatchID(id: UUID)

final case class PackagingMachineFailure(timestamp: LocalTime, batchID: BatchID, cutterTemperature: Temperature)
final case class PackageDamageFailure(timestamp: LocalTime, batchID: BatchID, cutterTemperature: Temperature)

final case class PackagingMachineFailureMessage(message: String)
final case class PackageDamagdMessage(message: String)

final case class Maintenance(motivation: String)
