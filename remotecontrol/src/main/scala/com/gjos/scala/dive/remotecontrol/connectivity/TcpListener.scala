package com.gjos.scala.dive.remotecontrol.connectivity

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, blocking, Future}
import java.net.ServerSocket
import java.io.{InputStreamReader, BufferedReader}
import scala.annotation.tailrec
import scala.concurrent.duration._

class TcpListener(protected val port: Int) extends ListenerImpl {

  var connection: Option[(ServerSocket, BufferedReader)] = None
  var subscribers = List[String => Unit]()

  protected def openSafely() {
    val sock = new ServerSocket(port)
    pollForClients(sock)
    pollForData()
  }

  protected def closeSafely() = connection match {
    case Some((sock, _)) =>
      blocking(sock.close())
      connection = None
    case _ =>
  }

  def send(bs: String) = {
  }

  def onReceive(handler: String => Unit) {
    subscribers = handler :: subscribers
  }

  private def pollForData(): Unit = Future {
    println("Polling for data.")
    while (isOpen) {
      connection match {
        case Some((_, stream)) if stream.ready() =>
          val line = stream.readLine()
          subscribers foreach (handle => handle(line))
        case _ =>
          blocking(Thread sleep 5.millisecond.toMillis)
      }
    }
    println("Stopped polling for data.")
  }

  private def pollForClients(sock: ServerSocket): Unit = Future {
    while (isOpen) {
      println("Waiting for client.")
      val stream = new BufferedReader(new InputStreamReader(sock.accept().getInputStream))
      connection map (_._2.close)
      connection = Some((sock, stream))
      blocking(Thread sleep 1.second.toMillis)
    }
    println("Stopped polling for clients.")
  }
}
