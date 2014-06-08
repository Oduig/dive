package com.gjos.android.dive.connectivity

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ConnectionImpl extends Connection {

  private var connectionIsOpen = false

  final def open() = Future[Unit] {
    openSafely()
    connectionIsOpen = true
  }

  final def close() = Future[Unit] {
    closeSafely()
    connectionIsOpen = false
  }

  final def isOpen = connectionIsOpen

  protected def openSafely(): Unit

  protected def closeSafely(): Unit
}
