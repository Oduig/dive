package com.gjos.android.dive.connectivity

import java.io.IOException
import java.net.{InetAddress, DatagramSocket, DatagramPacket}
import scala.concurrent.{Future, blocking}
import scala.concurrent.ExecutionContext.Implicits.global

class UdpConnection(protected val ip: String, protected val port: Int) extends ConnectionImpl {

  private val ipAddress = InetAddress.getByName(ip)
  private val packet = new DatagramPacket(Array.empty[Byte], 0, ipAddress, port)
  var connection: Option[DatagramSocket] = None

  protected def openSafely() {
    val sock = new DatagramSocket()
    connection = Some(sock)
  }

  protected def closeSafely() = connection match {
    case Some(sock) =>
      blocking(sock.close())
      connection = None
    case _ =>
  }

  def send(line: String) = connection match {
    case Some(sock) =>
      val bytes = (line + '\n').getBytes
      packet setData bytes
      packet setLength bytes.length
      Future(sock send packet)
    case None =>
      throw new IOException("Cannot send TCP data while disconnected")
  }

  def onReceive(handler: Byte => Unit) {

  }
}
