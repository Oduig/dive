package com.gjos.android.dive.connectivity

import scala.concurrent.Future

trait Connection {

  def open(): Future[Unit]

  def isOpen(): Boolean

  def send(line: String): Unit

  def onReceive(handler: Byte => Unit): Unit

  def close(): Future[Unit]
}
