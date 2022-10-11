package dev.atedeg.mdm.dto

import dev.atedeg.mdm.IncomingEvents.*
import dev.atedeg.mdm.Temperature
import dev.atedeg.mdm.utils.serialization.DTO
import dev.atedeg.mdm.utils.serialization.DTOGenerators.*
import dev.atedeg.mdm.utils.serialization.DTOOps.*

final case class TemperatureDTO(value: Double)
object TemperatureDTO:
  given DTO[Temperature, TemperatureDTO] = productTypeDTO

final case class TemperatureOutOfRangeDTO(temperature: TemperatureDTO, device: String)
object TemperatureOutOfRangeDTO:
  given DTO[TemperatureOutOfRange, TemperatureOutOfRangeDTO] = productTypeDTO

