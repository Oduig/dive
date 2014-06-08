package com.gjos.android.dive

import scala.concurrent.Future

trait Connection {

  def open(): Future[Unit]

  def isOpen(): Boolean

  def send(b: Byte): Future[Unit]

  def onReceive(handler: Byte => Unit): Unit

  def close(): Future[Unit]
}
