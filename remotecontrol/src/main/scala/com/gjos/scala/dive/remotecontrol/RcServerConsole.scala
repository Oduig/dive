package com.gjos.scala.dive.remotecontrol

import scala.annotation.tailrec
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import com.gjos.scala.dive.remotecontrol.connectivity.{Listener, TcpListener, UdpListener, BluetoothListener}
import com.gjos.scala.dive.remotecontrol.control.MouseMover

import scala.concurrent.duration._
import scala.util.{Success, Failure}

object RcServerConsole extends App {
  println("Remote control server app for Durovis Dive.")

  private var connection: Option[Listener] = None
  private val mouseMover = new MouseMover()

  handleInput("h".toList)

  @tailrec private def handleInput(cmd: List[Char]): Unit = {
    cmd match {
      case 'h' :: Nil => println(
        """Available commands:
          |t - listen for TCP connection
          |u - listen for UDP connection
          |b - listen for Bluetooth connection
          |d - disconnect
          |p - increase sensitivity
          |m - decrease sensitivity
          |h - help
          |q - quit""".stripMargin)
      case 't' :: cs => listenTcp(cs.mkString)
      case 'u' :: cs => listenUdp(cs.mkString)
      case 'b' :: cs => listenBluetooth(cs.mkString)
      case 'd' :: Nil => disconnect()
      case 'p' :: Nil => increaseSensitivity()
      case 'm' :: Nil => decreaseSensitivity()
      case 'q' :: Nil => quit()
      case _ => println("Say what?")
    }
    if (cmd != List('q')) {
      println("""What would you like to do?""")
      val newCmd = readLine()
      if (newCmd != null) handleInput(newCmd.toList)
    }
  }

  private def listenTcp(args: String) {
    val port = if (args.trim.size > 0) args.trim.toInt else 13337
    connect(new TcpListener(port))
  }

  private def listenUdp(args: String) {
    val port = if (args.trim.size > 0) args.trim.toInt else 13337
    connect(new UdpListener(port))
  }

  private def listenBluetooth(args: String) {
    connect(new BluetoothListener())
  }

  private def connect(listener: Listener) {
    println("Starting connection listener...")
    val startListening = listener.open()
    Await.ready(startListening, 3.seconds)
    startListening.value match {
      case Some(Success(_)) =>
        listener onReceive handleMessage
        connection = Some(listener)
        mouseMover.start()
        println("Listening.")
      case Some(Failure(ex)) =>
        connection = None
        println("Failed to start server!\n Message: " + ex.getMessage)
      case None =>
        connection = None
        println("Timeout when attempting to start server.")
    }
  }

  var calibrated = false
  private def handleMessage(content: String) {
    if (calibrated) {
      println("Received: " + content)
      val Array(x, y, _) = content.split(",")
      mouseMover.move(x.toInt, -y.toInt)
    } else {
      println("Skipped: " + content)
      calibrated = true
    }
  }

  private def quit() {
    disconnect()
    println("Done.")
  }

  private def disconnect() = {
    mouseMover.stop()
    connection map (_.close)
    println("Disconnected.")
  }

  private def increaseSensitivity() = println("New sensitivity: " + mouseMover.moreSensitive())
  private def decreaseSensitivity() = println("New sensitivity: " + mouseMover.lessSensitive())
}
