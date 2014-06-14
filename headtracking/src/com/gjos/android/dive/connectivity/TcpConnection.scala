package com.gjos.android.dive.connectivity

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.blocking
import scala.concurrent.Future
import scala.concurrent.duration._
import java.net.Socket
import java.io.{DataOutputStream, IOException}

class TcpConnection(protected val ip: String, protected val port: Int) extends ConnectionImpl {

  var connection: Option[(Socket, DataOutputStream)] = None

  protected def openSafely() {
    val sock = new Socket(ip, port)
    val stream = new DataOutputStream(sock.getOutputStream)
    connection = Some((sock, stream))
  }

  protected def closeSafely() = connection match {
    case Some((sock, _)) =>
      blocking(sock.close())
      connection = None
    case _ =>
  }

  def send(bs: Array[Byte]) = connection match {
    case Some((_, stream)) =>
      Future(stream write bs)
    case None =>
      throw new IOException("Cannot send TCP data while disconnected")
  }

  def onReceive(handler: Byte => Unit) {

  }
}
