package dev.atedeg.mdm

enum IncomingEvents:
  case TemperatureOutOfRange(temperature: Temperature, device: String)
  case PhOutOfRange(ph: Ph, device: String)
  case PackagingMachineFailed(batchID: BatchID, cutterTemperature: Temperature)
  case PackageDamaged(batchID: BatchID, cutterTemperature: Temperature)
  case PackagingMachineMaintenance(maintenance: Maintenance)
