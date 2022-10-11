package dev.atedeg.mdm.api

import java.time.LocalTime

import cats.Monad
import cats.effect.LiftIO
import cats.syntax.all.*
import cats.implicits.*
import eu.timepit.refined.numeric.Greater
import eu.timepit.refined.refineV

import dev.atedeg.mdm.IncomingEvents.*
import dev.atedeg.mdm.TemperatureFailure
import dev.atedeg.mdm.TemperatureFailureMessage
import dev.atedeg.mdm.manageTemperatureAlarm
import dev.atedeg.mdm.utils.monads.{ CanRaise, Emits }
import dev.atedeg.mdm.utils.monads.getOrRaise
import dev.atedeg.mdm.utils.serialization.DTOOps.*
import dev.atedeg.mdm.utils.monads.*
import dev.atedeg.mdm.dto.*
import cats.effect.IO

def handleTemperatureOutOfRangeEvent[M[_]: Monad: LiftIO: CanRaise[String]](event: TemperatureOutOfRangeDTO): M[Unit] =
  for
    r <- validate(event)
    message = manageTemperatureAlarm(TemperatureFailure(LocalTime.now(), r.temperature, r.device))
    _ <- IO.println("Sending an e-mail to the admin...").liftIO[M]
    _ <- IO.println(message).liftIO[M]
  yield ()