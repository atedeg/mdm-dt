package dev.atedeg.mdm.maintenance.dto

import dev.atedeg.mdm.maintenance.BatchID
import dev.atedeg.mdm.maintenance.IncomingEvents.*
import dev.atedeg.mdm.maintenance.OutgoingEvents.*
import dev.atedeg.mdm.maintenance.Temperature
import dev.atedeg.mdm.utils.serialization.DTO
import dev.atedeg.mdm.utils.serialization.DTOGenerators.*
import dev.atedeg.mdm.utils.serialization.DTOOps.*

private given DTO[BatchID, String] = unwrapFieldDTO
private given DTO[Temperature, Double] = unwrapFieldDTO

final case class TemperatureDTO(value: Double)
object TemperatureDTO:
  given DTO[Temperature, TemperatureDTO] = productTypeDTO

final case class PackagingMachineFailureDTO(batchID: String, temperature: Double)
object PackagingMachineFailureDTO:
  given DTO[PackagingMachineFailure, PackagingMachineFailureDTO] = productTypeDTO

final case class PackageDamagedDTO(batchID: String)
object PackageDamagedDTO:
  given DTO[PackageDamaged, PackageDamagedDTO] = productTypeDTO

final case class PackagingMachineMaintenanceDTO(maintenance: String)
object PackagingMachineMaintenanceDTO:
  given DTO[PackagingMachineFailure, PackagingMachineFailureDTO] = productTypeDTO
