package com.gjos.android.dive.connectivity

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.blocking
import scala.concurrent.Future
import java.net.ServerSocket
import java.io.{InputStreamReader, BufferedReader}
import scala.annotation.tailrec
import scala.concurrent.duration._

class TcpListener(protected val port: Int) extends ListenerImpl {

  var connection: Option[(ServerSocket, BufferedReader)] = None
  var subscribers = List[String => Unit]()

  // TODO allow accepting socket a second time
  protected def openSafely() {
    val sock = new ServerSocket(port)
    val stream = new BufferedReader(new InputStreamReader(sock.accept().getInputStream))
    connection = Some((sock, stream))
    pollWhileOpen
  }

  protected def closeSafely() = connection match {
    case Some((sock, _)) =>
      blocking(sock.close())
      connection = None
    case _ =>
  }

  def send(bs: String) = Future {
  }

  def onReceive(handler: String => Unit) {
    subscribers = handler :: subscribers
  }

  @tailrec private def pollWhileOpen(): Unit = connection match {
    case Some((_, stream)) =>
      if (stream.ready()) {
        val line = stream.readLine()
        subscribers foreach (handle => handle(line))
      } else {
        blocking(Thread sleep 5.millisecond.toMillis)
      }
      pollWhileOpen()
    case _ =>
  }
}
