package dev.atedeg.mdm

enum IncomingEvents:
  case TemperatureOutOfRange(temperature: Temperature, device: String)
  case PhOutOfRange(ph: Ph, device: String)
  case PackagingMachineFailure(batchID: BatchID, cutterTemperature: Temperature)
  case PackageDamaged(batchID: BatchID, cutterTemperature: Temperature)
