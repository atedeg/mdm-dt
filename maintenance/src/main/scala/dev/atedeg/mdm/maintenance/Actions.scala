package dev.atedeg.mdm.maintenance

import cats.Monad
import cats.effect.LiftIO
import dev.atedeg.mdm.maintenance.OutgoingEvents.*
import dev.atedeg.mdm.maintenance.IncomingEvents.*
import dev.atedeg.mdm.utils.{ percent as _, * }
import dev.atedeg.mdm.utils.given
import dev.atedeg.mdm.utils.monads.*
import dev.atedeg.mdm.utils.ranges.*

def collectMachineFailure[M[_] : Monad](failure: PackagingMachineFailure): M[Unit] = ???
def collectPackageDamage[M[_] : Monad](failure: PackageDamaged): M[Unit] = ???
def magicAiPredictorMaintenance[M[_]: Monad: CanEmit[PackagingMachineMaintenance]](): M[Boolean] = ???
