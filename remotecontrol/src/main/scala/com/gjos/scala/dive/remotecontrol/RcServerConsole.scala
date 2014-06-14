package com.gjos.scala.dive.remotecontrol

import scala.annotation.tailrec
import com.gjos.android.dive.connectivity.{Listener, TcpListener, UdpListener, BluetoothListener}

object RcServerConsole extends App {
  println("Remote control server app for Durovis Dive.")

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
      case 'q' :: Nil => println("Done.")
      case _ => println("Say what?")
    }
    if (cmd != List('q')) {
      println("""What would you like to do?""")
      handleInput(readLine().toList)
    }
  }

  private var connection: Option[Listener] = None

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
  }

  private def disconnect() = connection map (_.close)
}
