package com.gjos.android.dive.connectivity

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.blocking
import scala.concurrent.Future
import java.net.{InetSocketAddress, Socket}
import java.io.{DataOutputStream, IOException}

class TcpConnection(protected val ip: String, protected val port: Int) extends ConnectionImpl {

  var connection: Option[(Socket, DataOutputStream)] = None

  protected def openSafely() {
    val sock = new Socket()
    sock setTcpNoDelay true
    sock.connect(new InetSocketAddress(ip, port), 3000)
    val stream = new DataOutputStream(sock.getOutputStream)
    connection = Some((sock, stream))
  }

  protected def closeSafely() = connection match {
    case Some((sock, _)) =>
      blocking(sock.close())
      connection = None
    case _ =>
  }

  def send(line: String) = connection match {
    case Some((_, stream)) =>
      Future(stream writeBytes (line + '\n'))
    case None =>
      throw new IOException("Cannot send TCP data while disconnected")
  }

  def onReceive(handler: Byte => Unit) {

  }
}
