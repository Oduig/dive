package com.gjos.scala.dive.remotecontrol

import scala.annotation.tailrec
import com.gjos.android.dive.connectivity.{Listener, TcpListener, UdpListener, BluetoothListener}
import com.gjos.scala.dive.remotecontrol.control.MouseMover

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
          |h - help
          |q - quit""".stripMargin)
      case 't' :: cs => listenTcp(cs.mkString)
      case 'u' :: cs => listenUdp(cs.mkString)
      case 'b' :: cs => listenBluetooth(cs.mkString)
      case 'd' :: Nil => disconnect()
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
    println("Not implemented.")
  }

  private def listenBluetooth(args: String) {
    println("Not implemented.")
  }

  private def connect(listener: Listener) {
    println("Opening server socket...")
    connection = Some(listener)
    listener.open()
    listener onReceive handleMessage
    println("Listening.")
  }

  private def handleMessage(content: String) {
    println("Received: " + content)
    val Array(x, y, _) = content.split(",")
    mouseMover.move(x.toInt / 10, y.toInt / 10)
  }

  private def quit() {
    disconnect()
    println("Done.")
  }

  private def disconnect() = {
    connection map (_.close)
    println("Disconnected.")
  }
}
