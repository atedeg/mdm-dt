package dev.atedeg.mdm.api

import java.time.LocalTime

import cats.Monad
import cats.effect.IO
import cats.effect.LiftIO
import cats.implicits.*
import cats.syntax.all.*
import eu.timepit.refined.numeric.Greater
import eu.timepit.refined.refineV

import dev.atedeg.mdm.{
  managePackageDamage,
  managePackagingMachineFailure,
  managePackagingMachineMaintenance,
  managePhAlarm,
  manageTemperatureAlarm,
}
import dev.atedeg.mdm.IncomingEvents.*
import dev.atedeg.mdm.Maintenance
import dev.atedeg.mdm.PackageDamageFailure
import dev.atedeg.mdm.PackagingMachineFailure
import dev.atedeg.mdm.PhFailure
import dev.atedeg.mdm.TemperatureFailure
import dev.atedeg.mdm.TemperatureFailureMessage
import dev.atedeg.mdm.dto.*
import dev.atedeg.mdm.dto.given
import dev.atedeg.mdm.utils.monads.*
import dev.atedeg.mdm.utils.monads.{ CanRaise, Emits }
import dev.atedeg.mdm.utils.monads.getOrRaise
import dev.atedeg.mdm.utils.serialization.DTOOps.*

def handleTemperatureOutOfRangeEvent[M[_]: Monad: LiftIO: CanRaise[String]](event: TemperatureOutOfRangeDTO): M[Unit] =
  for
    r <- validate(event)
    message = manageTemperatureAlarm(TemperatureFailure(LocalTime.now(), r.temperature, r.device))
    _ <- IO.println("Sending an e-mail to the admin...").liftIO[M]
    _ <- IO.println(message).liftIO[M]
  yield ()

def handlePhOutOfRangeEvent[M[_]: Monad: LiftIO: CanRaise[String]](event: PhOutOfRangeDTO): M[Unit] =
  for
    r <- validate(event)
    message = managePhAlarm(PhFailure(LocalTime.now(), r.ph, r.device))
    _ <- IO.println("Sending an e-mail to the admin...").liftIO[M]
    _ <- sendEmail(message)
  yield ()

def handlePackagingMachineFailureEvent[M[_]: Monad: LiftIO: CanRaise[String]](
    event: PackagingMachineFailedDTO,
): M[Unit] =
  for
    r <- validate(event)
    message = managePackagingMachineFailure(PackagingMachineFailure(LocalTime.now(), r.batchID, r.cutterTemperature))
    _ <- IO.println("Sending an e-mail to the admin...").liftIO[M]
    _ <- sendEmail(message)
  yield ()

def handlePackageDamagedEvent[M[_]: Monad: LiftIO: CanRaise[String]](event: PackageDamagedDTO): M[Unit] =
  for
    r <- validate(event)
    message = managePackageDamage(PackageDamageFailure(LocalTime.now(), r.batchID, r.cutterTemperature))
    _ <- IO.println("Sending an e-mail to the admin...").liftIO[M]
    _ <- sendEmail(message)
  yield ()

def handlePackagingMachineMaintenanceEvent[M[_]: Monad: LiftIO: CanRaise[String]](
    event: PackagingMachineMaintenanceDTO,
): M[Unit] =
  for
    r <- validate(event)
    message = managePackagingMachineMaintenance(r.maintenance)
    _ <- IO.println("Sending an e-mail to the admin...").liftIO[M]
    _ <- sendEmail(message)
  yield ()

private def sendEmail[M[_]: Monad: LiftIO](content: String): M[Unit] = ???
