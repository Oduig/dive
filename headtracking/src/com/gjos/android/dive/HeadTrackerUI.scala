package com.gjos.android.dive

import android.os.Bundle
import android.view.View
import android.widget.{TextView, EditText, RadioGroup, Button}
import com.gjos.android.dive.calc.Vec
import com.gjos.android.dive.connectivity._
import com.gjos.android.dive.persistence.{UiSettings, SQLite}
import com.gjos.android.dive.sensing.SensorBroker
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Try, Failure, Success}

class HeadTrackerUI extends RichActivity {
  lazy val sensorBroker = new SensorBroker(this)
  lazy val database = new SQLite(getString(R.string.dbName), getString(R.string.dbVersion).toInt, getApplicationContext)

  override def onCreate(savedState: Bundle) {
    super.onCreate(savedState)
    this.setContentView(R.layout.main)

    database.fetchUiSettings() map (_ map restoreSettings)

    radioGroup.setOnCheckedChangeListener(connectionTypeChanged)
    connectButton.setOnClickListener(connectBtnClick)
    sensorBroker.gyroscope.observable.buffer(3).subscribe(onOrientationChanged _)
  }

  private def restoreSettings(settings: UiSettings): Unit = settings match {
    case UiSettings(connectionType, ipAddress, port, btAddress) =>
      radioGroup.check(connectionType)
      ipEdit.setText(ipAddress)
      portEdit.setText(port)
      bluetoothEdit.setText(btAddress)
  }

  private def saveSettings(): Unit = {
    database.saveUiSettings(UiSettings(
      radioGroup.getCheckedRadioButtonId,
      currentIpAddress,
      currentPort,
      currentBluetoothAddress
    ))
  }

  private def connectButton: Button = find(R.id.connectButton)
  private def radioGroup: RadioGroup = find(R.id.connectionTypeGroup)
  private def statusText: TextView = find(R.id.statusText)
  private def ipEdit: EditText = find(R.id.ipEdit)
  private def ipLabel: TextView = find(R.id.ipLabel)
  private def portEdit: EditText = find(R.id.portEdit)
  private def portLabel: TextView = find(R.id.portLabel)
  private def bluetoothEdit: EditText = find(R.id.ipEdit)
  private def bluetoothLabel: TextView = find(R.id.bluetoothLabel)

  private def currentIpAddress: String = ipEdit.getText.toString
  private def currentBluetoothAddress: String = bluetoothEdit.getText.toString

  // Casting to Int always works since EditText type is set to number
  private def currentPort: Int = portEdit.getText.toString.toInt

  var currentConnection: Option[Connection] = None

  def connectionTypeChanged: Int => Unit = {
    case R.id.tcp | R.id.udp => toggleIpUi()
    case R.id.bluetooth => toggleBluetoothUi()
  }

  private def toggleIpUi() = toggleConnectUi(ipMode=true)
  private def toggleBluetoothUi() = toggleConnectUi(ipMode=false)
  private def toggleConnectUi(ipMode: Boolean) {
    val (ipVisibility, bluetoothVisibility) = if (ipMode) (View.VISIBLE, View.GONE) else (View.GONE, View.VISIBLE)
    bluetoothEdit.setVisibility(bluetoothVisibility)
    bluetoothLabel.setVisibility(bluetoothVisibility)
    ipEdit.setVisibility(ipVisibility)
    ipLabel.setVisibility(ipVisibility)
    portEdit.setVisibility(ipVisibility)
    portLabel.setVisibility(ipVisibility)
  }

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

  private def uponConnecting() = inUiThread {
    connectButton.setEnabled(false)
    toast("Connecting...")
  }

  private def uponDisconnecting() = inUiThread {
    connectButton.setEnabled(false)
    toast("Disconnecting...")
  }

  private def createConnection() = radioGroup.getCheckedRadioButtonId match {
    case R.id.tcp => new TcpConnection(currentIpAddress, currentPort)
    case R.id.udp => new UdpConnection(currentIpAddress, currentPort)
    case R.id.bluetooth => new BluetoothConnection(currentBluetoothAddress)
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

  var lastMeasurement = currentMicros
  def onOrientationChanged(buffer: Seq[Array[Float]]): Unit = {
    val muSec = currentMicros
    val dt = muSec - lastMeasurement
    lastMeasurement = muSec

    val radiansMoved = Vec(0, 0, 0)
    for (values <- buffer) {
      radiansMoved += Vec(values(0), values(1), values(2))
    }
    radiansMoved *= dt / 1000000f
    radiansMoved /= buffer.size

    val csv = radiansMoved.toCsv()
    statusText.setText(s"Radians moved: $csv")
    currentConnection foreach (_.send(csv) onFailure handleFailure)
  }

  def currentMicros = System.nanoTime / 1000

  def handleFailure: PartialFunction[Throwable, Unit] = {
    case ex: RuntimeException =>
      startDisconnect()
      statusText.setText(s"Failed sending: ${ex.getMessage}")
  }
}
