package com.gjos.scala.dive.remotecontrol

import scala.annotation.tailrec
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import com.gjos.scala.dive.remotecontrol.connectivity.{Listener, TcpListener, UdpListener, BluetoothListener}
import com.gjos.scala.dive.remotecontrol.control.{KeyboardTyper, MouseMover}

import scala.concurrent.duration._
import scala.util.{Success, Failure}

object RcServerConsole extends App {
  println("Remote control server app for Durovis Dive.")

  private var connection: Option[Listener] = None
  private val mouse = new MouseMover()
  private val keyboard = new KeyboardTyper()

  handleInput("h".toList)

  @tailrec private def handleInput(cmd: List[Char]): Unit = {
    cmd match {
      case 'h' :: Nil => println(
        """Available commands:
          |t <port> - listen for TCP connection
          |u <port> - listen for UDP connection
          |b - listen for Bluetooth connection
          |d - disconnect
          |p - increase sensitivity
          |m - decrease sensitivity
          |z - toggle roll (aka tilt) functionality via the scrollwheel (default off)
          |j <key> - bind <key> to jump (default spacebar)
          |c <toggle|hold> <key> - bind <key> to crouch (default none), with mode <toggle|hold> (default toggle)
          |h - help
          |q - quit""".stripMargin)
      case 't' :: cs => listenTcp(cs.mkString)
      case 'u' :: cs => listenUdp(cs.mkString)
      case 'b' :: cs => listenBluetooth()
      case 'd' :: Nil => disconnect()
      case 'p' :: Nil => increaseSensitivity()
      case 'm' :: Nil => decreaseSensitivity()
      case 'z' :: Nil => toggleRoll()
      case 'j' :: cs => setJumpKey(cs.mkString)
      case 'c' :: cs => setCrouchKey(cs.mkString)
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

  private def listenBluetooth() {
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
        mouse.start()
        println("Listening.")
      case Some(Failure(ex)) =>
        connection = None
        println("Failed to start server!\n Message: " + ex.getMessage)
      case None =>
        connection = None
        println("Timeout when attempting to start server.")
    }
  }

  var rollEnabled = false
  var calibrated = false
  private def handleMessage(content: String) {
    if (calibrated) {
      println("Received: " + content)
      val Array(x, y, z) = content.split(",")
      val yaw = -x.toInt
      val pitch = y.toInt
      val roll = if (rollEnabled) z.toInt else 0
      mouse.move(yaw, pitch, roll)
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
    mouse.stop()
    connection map (_.close())
    println("Disconnected.")
  }

  private def increaseSensitivity() = println("New sensitivity: " + mouse.moreSensitive())
  private def decreaseSensitivity() = println("New sensitivity: " + mouse.lessSensitive())

  private def toggleRoll() {
    println((if (rollEnabled) "Disabled" else "Enabled") + " camera roll.")
    rollEnabled = !rollEnabled
  }

  private def setJumpKey(s: String) {
    val trimmed = s.trim()
    val key = if (trimmed.isEmpty) " " else trimmed
    keyboard.setJumpKey(key)
    println("Set jump key to " + keyboard.jumpKeyName + ".")
  }

  private def setCrouchKey(s: String) {
    val words = s.split(" ").filter(_.nonEmpty)
    if (words.size == 1) {
      val hold = words(0).toLowerCase == "hold"
      val key = " "
      keyboard.setCrouchKey(key, hold)
      println("Set crouch key to " + keyboard.crouchKeyName + " (" + (if (hold) "hold" else "toggle") + ").")
    } else if (words.size > 1) {
      val hold = words(0).toLowerCase == "hold"
      val key = words(1)
      keyboard.setCrouchKey(key, hold)
      println("Set crouch key to " + keyboard.crouchKeyName + " (" + (if (hold) "hold" else "toggle") + ").")
    } else {
      println("Invalid syntax, type 'h' for details.")
    }
  }
}
