package dev.atedeg.mdm

import cats.Monad

import dev.atedeg.mdm.utils.monads.{ emit, Emits }

def manageTemperatureAlarm(failure: TemperatureFailure): String = """
        |Temperature out of range.
        |Timestamp: ${failure.timestamp}
        |Temperature: ${failure.temperature.value}
        |Device: ${failure.device}
        |""".stripMargin

def managePhAlarm[M[_]: Monad: Emits[PhFailureMessage]](failure: PhFailureMessage): M[Unit] =
  val message = """
          |pH out of range.
          |Timestamp: ${failure.timestamp}
          |pH: ${failure.temperature.value}
          |Device: ${failure.device}
          |""".stripMargin
  emit(PhFailureMessage(message))
