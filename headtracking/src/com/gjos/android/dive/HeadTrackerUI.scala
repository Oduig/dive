package com.gjos.android.dive

import android.os.Bundle
import android.widget.{EditText, RadioGroup, Button}
import com.gjos.android.dive.connectivity._
import scala.Some
import com.gjos.android.dive.sensing.LinearAccelerationSensor
import android.hardware.{SensorManager, Sensor, SensorEvent, SensorEventListener}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Try, Failure, Success}
import android.util.Log
import android.content.Context

class HeadTrackerUI extends RichActivity with SensorEventListener {
  override def onCreate(savedState: Bundle) {
    super.onCreate(savedState)
    this.setContentView(R.layout.main)

    connectButton.setOnClickListener(connectBtnClick)
  }

  private lazy val sensorManager = getSystemService(Context.SENSOR_SERVICE).asInstanceOf[SensorManager]
  private lazy val sensors = Set(
    new LinearAccelerationSensor()
  )

  private def registerSensorListeners() {
    for (sensor <- sensors) yield {
      val sensorDevice = sensorManager.getDefaultSensor(sensor.sensorType)
      sensorManager.registerListener(this, sensorDevice, SensorManager.SENSOR_DELAY_FASTEST)
    }
  }

  private def unregisterSensorListeners() {
    for (sensor <- sensors) {
      val sensorDevice = sensorManager.getDefaultSensor(sensor.sensorType)
      sensorManager.unregisterListener(this, sensorDevice)
    }
  }

  private def connectButton: Button = find(R.id.connectButton)
  private def radioGroup: RadioGroup = find(R.id.connectionTypeGroup)

  private def currentIp: String = find[EditText](R.id.ipEdit).getText.toString

  // Always works since EditText type is set to number
  private def currentPort: Int = find[EditText](R.id.portEdit).getText.toString.toInt

  var currentConnection: Option[Connection] = None

  def connectBtnClick() = currentConnection match {
    case Some(connection) if connection.isOpen => startDisconnect()
    case _ => startConnect()
  }

  def onSensorChanged(event: SensorEvent) = currentConnection match {
    case Some(connection) if connection.isOpen =>
      for (sensor <- sensors if sensor.sensorType == event.sensor.getType) {
        sensor.onChange(event.values)
      }
    case _ =>
  }

  def onAccuracyChanged(event: Sensor, accuracy: Int) {}

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
        registerSensorListeners()
      case Failure(ex) =>
        toast("Failed to connect: " + ex.getMessage)
    }
    connectButton.setEnabled(true)
  }

  private def uponDisconnect(result: Try[Unit]) = inUiThread {
    result match {
      case Success(_) =>
        unregisterSensorListeners()
        toast("Disconnected.")
        connectButton.setText("Connect")
      case Failure(ex) =>
        toast("Failed to disconnect: " + ex.getMessage)
    }
    connectButton.setEnabled(true)
  }

}
