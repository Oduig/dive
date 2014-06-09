package com.gjos.android.dive

import android.os.Bundle
import android.widget.{TextView, EditText, RadioGroup, Button}
import com.gjos.android.dive.connectivity._
import scala.Some
import com.gjos.android.dive.sensing.SensorBroker
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Try, Failure, Success}

class HeadTrackerUI extends RichActivity {
  lazy val sensorBroker = new SensorBroker(this)

  override def onCreate(savedState: Bundle) {
    super.onCreate(savedState)
    this.setContentView(R.layout.main)

    connectButton.setOnClickListener(connectBtnClick)
    sensorBroker.linearAcceleration.observable.subscribe(onAcceleration _)
  }

  private def connectButton: Button = find(R.id.connectButton)
  private def radioGroup: RadioGroup = find(R.id.connectionTypeGroup)
  private def statusText: TextView = find(R.id.statusText)

  private def currentIp: String = find[EditText](R.id.ipEdit).getText.toString

  // Always works since EditText type is set to number
  private def currentPort: Int = find[EditText](R.id.portEdit).getText.toString.toInt

  var currentConnection: Option[Connection] = None

  def connectBtnClick() = currentConnection match {
    case Some(connection) if connection.isOpen => startDisconnect()
    case _ => startConnect()
  }

  protected def startConnect() {
    val connection = createConnection()
    currentConnection = Some(connection)
    uponConnecting()
    connection.open() onComplete uponConnect
  }

  protected def startDisconnect() {
    require(currentConnection.nonEmpty && currentConnection.get.isOpen, "Cannot disconnect, no connection is open.")
    uponDisconnecting()
    currentConnection.get.close() onComplete uponDisconnect
    currentConnection = None
  }

  private def uponConnecting() {
    connectButton.setEnabled(false)
    toast("Connecting...")
  }

  private def uponDisconnecting() {
    connectButton.setEnabled(false)
    toast("Disconnecting...")
  }

  private def createConnection() = radioGroup.getCheckedRadioButtonId match {
    case R.id.tcp => new TcpConnection(currentIp, currentPort)
    case R.id.udp => new UdpConnection(currentIp, currentPort)
    case R.id.bluetooth => new BluetoothConnection
  }

  private def uponConnect(result: Try[Unit]) = inUiThread {
    result match {
      case Success(_) =>
        toast("Connected!")
        connectButton.setText("Disconnect")
        sensorBroker.startListening()
      case Failure(ex) =>
        toast("Failed to connect: " + ex.getMessage)
    }
    connectButton.setEnabled(true)
  }

  private def uponDisconnect(result: Try[Unit]) = inUiThread {
    result match {
      case Success(_) =>
        sensorBroker.stopListening()
        toast("Disconnected.")
        connectButton.setText("Connect")
      case Failure(ex) =>
        toast("Failed to disconnect: " + ex.getMessage)
    }
    connectButton.setEnabled(true)
  }

  def onAcceleration(values: Array[Float]) = values match {
    case Array(dx, dy, dz) =>
      val treshold = 2
      if (dx > treshold || dy > treshold || dz > treshold) {
        val (absX, absY, absZ) = (Math.abs(dx), Math.abs(dy), Math.abs(dz))
        val movement = Math.max(absX, Math.max(absY, absZ)) match {
          case `absX` => if (dx > 0) "->" else "<-"
          case `absY` => if (dy > 0) "^" else "v"
          case `absZ` => if (dz > 0) "x" else "o"
        }
        statusText.setText(f"Last movement: $movement ($dx%.1f, $dy%.1f, $dz%.1f)")
      }
  }
}
