package dev.atedeg.mdm

import scala.concurrent.duration.*

import SseClient.*
import cats.effect.Temporal
import fs2.{ Pull, RaiseThrowable, Stream }
import org.http4s.*
import org.http4s.ServerSentEvent.EventId
import org.http4s.client.Client
import org.http4s.headers.{ `Cache-Control`, Accept }

final class SseClient[F[_]] private (
    httpClient: Client[F],
    maxRetries: Int,
    initialRetryInterval: FiniteDuration,
)(implicit timer: Temporal[F], rt: RaiseThrowable[F]):

  def stream(uri: Uri): Stream[F, ServerSentEvent] = autoReconnectStream(uri)

  private def autoReconnectStream(uri: Uri): Stream[F, ServerSentEvent] =
    def go(s: Stream[F, ServerSentEvent], metadata: SseMetadata): Pull[F, ServerSentEvent, Unit] =
      s.pull.uncons1.flatMap {
        case Some((event, tail)) =>
          val newMetadata = metadata.copy(
            eventId = event.id.fold(metadata.eventId)(id => if id != EventId.reset then Some(id.value) else None),
            retry = event.retry.fold(metadata.retry)(r => Some(r)),
          )
          val metadataChanged = newMetadata != metadata
          Pull.output1(event) >> {
            // Add a new error handler only if the metadata changed.
            if metadataChanged then goWithErrorHandled(tail, newMetadata) else go(tail, metadata)
          }
        case None =>
          reconnect(metadata, None)
      }

    def reconnect(metadata: SseMetadata, err: Option[Throwable]): Pull[F, ServerSentEvent, Unit] =
      val delay = metadata.retry.getOrElse(initialRetryInterval)
      val newMetadata = metadata.copy(attempts = metadata.attempts + 1)

      if newMetadata.attempts > maxRetries then Pull.raiseError[F](MaxRetriesReached(err))
      else goWithErrorHandled(newStream(uri, newMetadata.eventId).delayBy(delay), newMetadata)

    def goWithErrorHandled(
        stream: Stream[F, ServerSentEvent],
        metadata: SseMetadata,
    ): Pull[F, ServerSentEvent, Unit] =
      go(stream, metadata).handleErrorWith {
        case e: MaxRetriesReached => Pull.raiseError[F](e)
        case other => reconnect(metadata, Some(other))
      }

    goWithErrorHandled(newStream(uri, None), SseMetadata(None, None, 1)).stream

  private def newStream(uri: Uri, eventId: Option[String]): Stream[F, ServerSentEvent] =
    httpClient
      .stream(
        Request(
          uri = uri,
          headers = BaseHeaders ++ Headers(eventId.map(Header("Last-Event-ID", _)).toList),
        ),
      )
      // todo: Check if we actually got SSE before decoding.
      .flatMap(_.body)
      .through(ServerSentEvent.decoder[F])

object SseClient:
  val DefaultRetryInterval: FiniteDuration = 5.seconds
  val DefaultMaxRetries: Int = 10

  def apply[F[_]](
      httpClient: Client[F],
      maxRetries: Int = DefaultMaxRetries,
      initialRetryInterval: FiniteDuration = DefaultRetryInterval,
  )(implicit timer: Temporal[F], rt: RaiseThrowable[F]): SseClient[F] =
    new SseClient(httpClient, maxRetries, initialRetryInterval)

  private case class SseMetadata(eventId: Option[String], retry: Option[FiniteDuration], attempts: Int)
  private case class MaxRetriesReached(underlying: Option[Throwable])
      extends Exception("Reached maximum number of retries", underlying.orNull)

  private val BaseHeaders: Headers = Headers.of(
    `Cache-Control`(CacheDirective.`no-cache`()),
    Accept(MediaType.`text/event-stream`),
  )
