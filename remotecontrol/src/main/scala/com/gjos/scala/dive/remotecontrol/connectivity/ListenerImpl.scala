package com.gjos.scala.dive.remotecontrol.connectivity

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ListenerImpl extends Listener {

  private var connectionIsOpen = false

  final def open() = Future[Unit] {
    connectionIsOpen = true
    openSafely()
  }

  final def close() = Future[Unit] {
    closeSafely()
    connectionIsOpen = false
  }

  final def isOpen = connectionIsOpen

  protected def openSafely(): Unit

  protected def closeSafely(): Unit
}
