package dev.atedeg.mdm

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global
import scala.jdk.OptionConverters.*

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import concurrent.duration.DurationInt
import fs2.Stream
import fs2.io.stdout
import fs2.text
import io.circe.*
import io.circe.generic.auto.*
import io.circe.parser.*
import io.circe.syntax.*
import org.eclipse.ditto.client.DittoClient
import org.eclipse.ditto.client.DittoClients
import org.eclipse.ditto.client.configuration.BasicAuthenticationConfiguration
import org.eclipse.ditto.client.configuration.WebSocketMessagingConfiguration
import org.eclipse.ditto.client.messaging.AuthenticationProviders
import org.eclipse.ditto.client.messaging.MessagingProvider
import org.eclipse.ditto.client.messaging.MessagingProviders
import org.eclipse.ditto.things.model.ThingId
import org.http4s.Method
import org.http4s.Request
import org.http4s.Uri
import org.http4s.blaze.client.BlazeClientBuilder
import sttp.model.sse.ServerSentEvent
import sttp.tapir.*
import sttp.tapir.client.http4s.Http4sClientInterpreter
import sttp.tapir.server.http4s.serverSentEventsBody

import dev.atedeg.mdm.api.handleTemperatureOutOfRangeEvent
import dev.atedeg.mdm.dto.TemperatureOutOfRangeDTO
import dev.atedeg.mdm.utils.monads.*

object Main extends IOApp:
  @SuppressWarnings(Array("wart"))
  override def run(args: List[String]): IO[ExitCode] = IO.unit.as(ExitCode.Success)
  // DittoClients.newInstance(
  //   MessagingProviders.webSocket(
  //     WebSocketMessagingConfiguration.newBuilder()
  //       .endpoint("wss://ditto.eclipseprojects.io")
  //       .build(),
  //       AuthenticationProviders.basic(BasicAuthenticationConfiguration.newBuilder()
  //                                   .username("ditto")
  //                                   .password("ditto")
  //                                   .build())
  //     )
  //   ).connect().thenAccept(c => {
  //     c.live().startConsumption().toCompletableFuture().get()
  //     c.live().forId(ThingId.of("dev.atedeg.mdm:milk-tank-1")).registerForMessage("globalMessageHandler", "temperature-out-of-range", (message) => {
  //       (for
  //         payload <- message.getRawPayload().toScala
  //         payloadDTO <- decode[TemperatureOutOfRangeDTO](String(payload.array())).toOption
  //       yield payloadDTO) match
  //         case Some(dto) => val r = handleTemperatureOutOfRangeEvent(dto)
  //         case None => ???
  //     })
  //   })
  // IO.unit.as(ExitCode.Success)

  // val a = for
  //   client <- BlazeClientBuilder[IO](global).stream
  //   req = Request[IO](
  //     Method.GET,
  //     Uri.unsafeFromString(
  //       "https://ditto.eclipseprojects.io/api/2/things/dev.atedeg.mdm:milk-tank-1/features/Temperature/outbox/messages/temperature-out-of-range",
  //     ),
  //   )
  //   res <- client.stream(req).flatMap(_.body.chunks)
  // yield res
  // a.compile.drain.as(ExitCode.Success)
