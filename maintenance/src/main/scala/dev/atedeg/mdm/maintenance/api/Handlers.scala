package dev.atedeg.mdm.maintenance.api

import cats.Monad
import cats.effect.IO
import cats.effect.LiftIO
import cats.implicits.*
import cats.syntax.all.*

import dev.atedeg.mdm.maintenance.*
import dev.atedeg.mdm.maintenance.IncomingEvents.*
import dev.atedeg.mdm.maintenance.OutgoingEvents.*
import dev.atedeg.mdm.maintenance.dto.PackageDamagedDTO
import dev.atedeg.mdm.maintenance.dto.PackagingMachineFailureDTO
import dev.atedeg.mdm.maintenance.dto.PackagingMachineMaintenanceDTO
import dev.atedeg.mdm.utils.monads.*
import dev.atedeg.mdm.utils.serialization.DTOOps.*

def packagingMachineFailureHandler[M[_]: Monad: LiftIO: CanRaise[String]](
    failure: PackagingMachineFailureDTO,
): M[Unit] =
  for
    failureEvent <- validate(failure)
    _ <- collectMachineFailure(failureEvent)
    _ <- writeFailureToDB(failureEvent)
  yield ()

def packageDamagedHandler[M[_]: Monad: LiftIO: CanRaise[String]](damage: PackageDamagedDTO): M[Unit] =
  for
    damageEvent <- validate(damage)
    _ <- collectPackageDamage(damageEvent)
    _ <- writeFailureToDB(damageEvent)
  yield ()

def packagingMachineMaintenanceHandler[M[_]: Monad: LiftIO](
    maintenance: PackagingMachineMaintenance,
): M[PackagingMachineMaintenanceDTO] =
  for _ <- IO.println("Maintenance needed").liftIO[M]
  yield PackagingMachineMaintenanceDTO(maintenance.maintenance.value)

private def writeFailureToDB[M[_]: Monad: LiftIO](failure: IncomingEvents): M[Unit] = ???
