package dev.atedeg.mdm.utils

import cats.data.NonEmptyList
import cats.mtl.{Raise, Tell}
import cats.Monad

/**
 * Signals that a method could emit elements of a given type which are accumulated in a list.
 */
type CanEmit[Emitted] = [M[_]] =>> Tell[M, List[Emitted]]

/**
 * Signals that a method will always emit one or more elements of a given type which are accumulated in a list.
 * @note this check is not imposed neither at compile-time not at run-time. This is just used to
 *       better document the behaviour of methods.
 */
type Emits[Emitted] = [M[_]] =>> Tell[M, List[Emitted]]

/**
 * Signals that a method could fail with a given error type.
 */
type CanRaise[Raised] = [M[_]] =>> Raise[M, Raised]

/**
 * Emits an element of type `E` in a context `M[_]` that accumulates emitted elements in a list.
 */
def emit[M[_], E](e: E)(using T: Tell[M, List[E]]): M[Unit] = T.tell(List(e))

/**
 * Raises an error of type `E` in a context `M[_]` where the computation can be aborted.
 */
def raise[M[_], E, A](e: E)(using R: Raise[M, E]): M[A] = R.raise(e)

/**
 * `unless(cond)(a)` performs the monadic action `a` if the condition `cond` is false.
 */
def unless[M[_], A](cond: => Boolean)(action: => M[A])(using M: Monad[M]): M[Unit] = M.unlessA(cond)(action)

/**
 * `when(cond)(a)` performs the monadic action `a` if the condition `cond` holds true.
 */
def when[M[_], A](cond: => Boolean)(action: => M[A])(using M: Monad[M]): M[Unit] = M.whenA(cond)(action)
