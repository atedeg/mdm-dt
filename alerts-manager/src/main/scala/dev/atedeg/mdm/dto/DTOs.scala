package dev.atedeg.mdm.dto

import dev.atedeg.mdm.BatchID
import dev.atedeg.mdm.IncomingEvents.*
import dev.atedeg.mdm.Maintenance
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

final case class PackagingMachineFailedDTO(batchID: String, cutterTemperature: TemperatureDTO)
object PackagingMachineFailedDTO:
  given DTO[PackagingMachineFailed, PackagingMachineFailedDTO] = productTypeDTO

final case class PackageDamagedDTO(batchID: String, cutterTemperature: TemperatureDTO)
object PackageDamagedDTO:
  given DTO[PackageDamaged, PackageDamagedDTO] = productTypeDTO

final case class MaintenanceDTO(motivation: String)
object MaintenanceDTO:
  given DTO[Maintenance, MaintenanceDTO] = productTypeDTO

final case class PackagingMachineMaintenanceDTO(maintenance: MaintenanceDTO)
object PackagingMachineMaintenanceDTO:
  given DTO[PackagingMachineMaintenance, PackagingMachineMaintenanceDTO] = productTypeDTO
