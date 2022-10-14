package dev.atedeg.mdm.dto

import dev.atedeg.mdm.IncomingEvents.*
import dev.atedeg.mdm.Ph
import dev.atedeg.mdm.Temperature
import dev.atedeg.mdm.utils.serialization.DTO
import dev.atedeg.mdm.utils.serialization.DTOGenerators.*
import dev.atedeg.mdm.utils.serialization.DTOOps.*

final case class PhDTO(value: Double)
object PhDTO:
  given DTO[Ph, PhDTO] = productTypeDTO

final case class PhOutOfRangeDTO(ph: PhDTO, device: String)
object PhOutOfRangeDTO:
  given DTO[PhOutOfRange, PhOutOfRangeDTO] = productTypeDTO

final case class TemperatureDTO(value: Double)
object TemperatureDTO:
  given DTO[Temperature, TemperatureDTO] = productTypeDTO

final case class TemperatureOutOfRangeDTO(temperature: TemperatureDTO, device: String)
object TemperatureOutOfRangeDTO:
  given DTO[TemperatureOutOfRange, TemperatureOutOfRangeDTO] = productTypeDTO

private given DTO[BatchID, String] = unwrapFieldDTO

final case class PackagingMachineFailureDTO(batchID: String, cutterTemperature: TemperatureDTO)
object PackagingMachineFailureDTO:
  given DTO[PackagingMachineFailure, PackagingMachineFailureDTO] = productTypeDTO

final case class PackageDamagedDTO(batchID: String, cutterTemperature: TemperatureDTO)
object PackageDamagedDTO:
  given DTO[PackageDamaged, PackageDamagedDTO] = productTypeDTO
