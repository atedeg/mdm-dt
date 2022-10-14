package dev.atedeg.mdm.maintenance

import dev.atedeg.mdm.maintenance.Maintenance

enum IncomingEvents:
    case PackagingMachineFailure(batchId: BatchID, cutterTemperature: Temperature)
    case PackageDamaged(batchId: BatchID)

enum OutgoingEvents:
    case PackagingMachineMaintenance(maintenance: Maintenance)
