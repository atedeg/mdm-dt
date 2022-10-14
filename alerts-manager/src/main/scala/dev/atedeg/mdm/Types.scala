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
