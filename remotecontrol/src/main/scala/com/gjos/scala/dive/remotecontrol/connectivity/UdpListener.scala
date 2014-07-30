package com.gjos.scala.dive.remotecontrol.connectivity

import java.net.{DatagramPacket, DatagramSocket}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.blocking
import scala.concurrent.Future

class UdpListener(protected val port: Int) extends ListenerImpl {

  var connection: Option[DatagramSocket] = None
  var subscribers = List[String => Unit]()

  protected def openSafely() {
    val sock = new DatagramSocket(port)
    connection = Some(sock)
    pollForData(sock)
  }

  protected def closeSafely() = connection match {
    case Some(sock) =>
      blocking(sock.close())
      connection = None
    case _ =>
  }

  def send(bs: String) = {
  }

  def onReceive(handler: String => Unit) {
    subscribers = handler :: subscribers
  }

  private def pollForData(sock: DatagramSocket): Unit = Future {
    println("Polling for data.")
    val pattern = """-?\d+,-?\d+,-?\d+""".r
    val buf = Array.fill[Byte](128)(0)
    val packet = new DatagramPacket(buf, buf.length)
    while (isOpen) {
      sock.receive(packet)
      val line = new String(packet.getData.takeWhile(_ != '\n'))
      line match {
        case pattern() => subscribers foreach (handle => handle(line))
        case _ => println("Bad packet: " + line)
      }
    }
    println("Stopped polling for data.")
  }
}
