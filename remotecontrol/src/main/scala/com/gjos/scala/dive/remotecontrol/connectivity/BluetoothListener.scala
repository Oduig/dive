package com.gjos.scala.dive.remotecontrol.connectivity

import java.io.{InputStreamReader, BufferedReader}
import javax.bluetooth.{DiscoveryAgent, LocalDevice, UUID}
import javax.microedition.io.{StreamConnectionNotifier, Connector}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.blocking
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Random

class BluetoothListener() extends ListenerImpl {

  var connection: Option[(StreamConnectionNotifier, BufferedReader)] = None
  var subscribers = List[String => Unit]()

  def send(bs: String) = Future {
  }

  def onReceive(handler: String => Unit) {
    subscribers = handler :: subscribers
  }

  protected def openSafely() {
    val server = makeServer()
    pollForClients(server)
    pollForData()
  }

  protected def closeSafely() = connection match {
    case Some((server, _)) =>
      blocking(server.close())
      connection = None
    case _ =>
  }

  private def makeServer() = {
    val uuid = new UUID(randomHex(32), false)

    val name = "Durovis Headtracking Server"
    val url = s"btspp://localhost:$uuid;name=$name;authenticate=false;encrypt=false;"

    val local = LocalDevice.getLocalDevice()
    local.setDiscoverable(DiscoveryAgent.GIAC)
    val server = Connector.open(url).asInstanceOf[StreamConnectionNotifier]
    server
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

  private def pollForClients(server: StreamConnectionNotifier): Unit = Future {
    while (isOpen) {
      println("Waiting for client.")
      val conn = server.acceptAndOpen()
      val stream = new BufferedReader(new InputStreamReader(conn.openInputStream()))
      connection map (_._2.close)
      connection = Some((server, stream))
      blocking(Thread sleep 1.second.toMillis)
    }
    println("Stopped polling for clients.")
  }


  private def randomHex(length: Int) = {
    val legalCharacters = "abcdef0123456789".toVector
    val generated = List.fill(length)(legalCharacters(Random.nextInt(legalCharacters.size)))
    generated.mkString
  }
}
