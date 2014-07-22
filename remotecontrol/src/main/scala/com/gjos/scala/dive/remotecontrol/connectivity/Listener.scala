package com.gjos.scala.dive.remotecontrol.connectivity

import scala.concurrent.Future

trait Listener {

  def open(): Future[Unit]

  def isOpen(): Boolean

  def send(bs: String): Future[Unit]

  def onReceive(handler: String => Unit): Unit

  def close(): Future[Unit]
}
