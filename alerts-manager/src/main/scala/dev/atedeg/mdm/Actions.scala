package dev.atedeg.mdm

import cats.Monad

import dev.atedeg.mdm.utils.monads.{ emit, Emits }

def manageTemperatureAlarm(failure: TemperatureFailure): String = """
        |Temperature out of range.
        |Timestamp: ${failure.timestamp}
        |Temperature: ${failure.temperature.value}
        |Device: ${failure.device}
        |""".stripMargin

def managePhAlarm(failure: PhFailure): String = """
        |pH out of range.
        |Timestamp: ${failure.timestamp}
        |pH: ${failure.temperature.value}
        |Device: ${failure.device}
        |""".stripMargin

def managePackageDamage(failure: PackageDamageFailure): String = """
        |Package damaged.
        |Timestamp: ${failure.timestamp}
        |BatchID: ${failure.batchID.id}
        |Cutter temperature: ${failure.cutterTemperature.value}
        |""".stripMargin

def managePackagingMachineFailure(failure: PackagingMachineFailure): String = """
        |Packaging machine failure.
        |Timestamp: ${failure.timestamp}
        |BatchID: ${failure.batchID.id}
        |Cutter temperature: ${failure.cutterTemperature.value}
""".stripMargin
