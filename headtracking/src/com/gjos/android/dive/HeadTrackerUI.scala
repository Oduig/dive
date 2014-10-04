package com.gjos.android.dive

import android.os.Bundle
import android.view.{MenuItem, Menu, View}
import android.widget.{TextView, EditText, RadioGroup, Button}
import com.gjos.android.dive.calc.Vec
import com.gjos.android.dive.connectivity._
import com.gjos.android.dive.persistence.{UiSettings, SQLite}
import com.gjos.android.dive.sensing.{Gesture, SensorBroker}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Try, Failure, Success}

class HeadTrackerUI extends RichActivity {
  lazy val sensorBroker = new SensorBroker(this)
  lazy val database = new SQLite(getString(R.string.dbName), getString(R.string.dbVersion).toInt, getApplicationContext)
  lazy val defaultSettings = UiSettings(
    getString(R.string.defaultConnectionType).toInt,
    getString(R.string.defaultIpAddress),
    getString(R.string.defaultPort).toInt,
    getString(R.string.defaultBluetoothAddress)
  )

  override def onCreate(savedState: Bundle) {
    super.onCreate(savedState)
    this.setContentView(R.layout.main)
    defaultSettings

    database.fetchUiSettings() onComplete restoreSettings

    radioGroup.setOnCheckedChangeListener(connectionTypeChanged)
    connectButton.setOnClickListener(connectBtnClick)
    sensorBroker.accelerometer.subscribe(onAcceleration)
    sensorBroker.gyroscope.subscribe(onOrientationChanged)
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater.inflate(R.menu.main, menu)
    true
  }

  override def onOptionsItemSelected(item: MenuItem) = item.getItemId match {
    case R.id.resetDefaults =>
      restoreSettings(Success(Some(defaultSettings)))
      super.onOptionsItemSelected(item)
  }

  private def restoreSettings(settingsOpt: Try[Option[UiSettings]]): Unit = inUiThread {
    settingsOpt match {
      case Success(uiSettingsOpt) =>
        val UiSettings(connectionType, ipAddress, port, btAddress) = uiSettingsOpt getOrElse defaultSettings
        radioGroup.check(radioGroup.getChildAt(connectionType).getId)
        ipEdit.setText(ipAddress)
        portEdit.setText(port.toString)
        bluetoothEdit.setText(btAddress)
      case Failure(ex) => toast("Error while restoring settings: " + ex.getMessage)
    }
  }

  private def saveSettings(): Unit = {
    database.saveUiSettings(UiSettings(
      currentConnectionTypeIndex,
      currentIpAddress,
      currentPort,
      currentBluetoothAddress
    )) onFailure {
      case ex => inUiThread(toast("Error while saving settings: " + ex.getMessage))
    }
  }

  private def connectButton: Button = find(R.id.connectButton)
  private def radioGroup: RadioGroup = find(R.id.connectionTypeGroup)
  private def statusText: TextView = find(R.id.statusText)
  private def ipEdit: EditText = find(R.id.ipEdit)
  private def ipLabel: TextView = find(R.id.ipLabel)
  private def portEdit: EditText = find(R.id.portEdit)
  private def portLabel: TextView = find(R.id.portLabel)
  private def bluetoothEdit: EditText = find(R.id.bluetoothEdit)
  private def bluetoothLabel: TextView = find(R.id.bluetoothLabel)

  private def currentConnectionTypeIndex = radioGroup.indexOfChild(radioGroup.findViewById(radioGroup.getCheckedRadioButtonId))
  private def currentIpAddress: String = ipEdit.getText.toString
  private def currentBluetoothAddress: String = bluetoothEdit.getText.toString

  private def currentPort: Int = Try(portEdit.getText.toString.toInt) getOrElse 0

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
    saveSettings()
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
        learnGesturesFor(3000)
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

  var currentGesture = Gesture.None
  var lastGestureTime = 0l
  def learnGesturesFor(duration: Long) {
    toast("Learning!")
    lastGestureTime = System.currentTimeMillis + duration
  }
  var (tresholdX, tresholdY, tresholdZ) = (0f, 0f, 0f)
  def onAcceleration(values: Array[Float]): Unit = {
    val Array(x, y, z) = values
    val now = System.currentTimeMillis
    if (now < lastGestureTime) { // learning mode
      tresholdX = if (x > tresholdX) x * 1.5f else if (-x > tresholdX) -x * 1.5f else tresholdX
      tresholdY = if (y > tresholdY) y * 1.5f else if (-y > tresholdY) -y * 1.5f else tresholdY
      tresholdZ = if (z > tresholdZ) z * 1.5f else if (-z > tresholdZ) -z * 1.5f else tresholdZ
    } else {
      val gesture =
        if (now < lastGestureTime + 2000) {
          Gesture.None
        } else if (x > tresholdX) {
          toast("Jump!")
          Gesture.Jump
        } else if (-x > tresholdX) {
          toast("Crouch!")
          Gesture.Crouch
        } else if (z > tresholdZ) {
          toast("North!")
          if (y > tresholdY) Gesture.MoveNE
          else if (-y > tresholdY) Gesture.MoveNW
          else Gesture.MoveN
        } else if (-z > tresholdZ) {
          toast("South!")
          if (y > tresholdY) Gesture.MoveSE
          else if (-y > tresholdY) Gesture.MoveSW
          else Gesture.MoveS
        } else if (y > tresholdY) {
          toast("East!")
          Gesture.MoveE
        } else if (-y > tresholdY) {
          toast("West!")
          Gesture.MoveW
        } else {
          Gesture.None
        }
      currentGesture = gesture
      if (gesture != Gesture.None) {
        lastGestureTime = now
      }
    }
  }

  var lastGyroMeasurement = currentMicros
  def onOrientationChanged(values: Array[Float]): Unit = {
    val muSec = currentMicros
    val dt = muSec - lastGyroMeasurement
    lastGyroMeasurement = muSec

    val radiansMoved = Vec(values(0) * dt / 1000000f, values(1) * dt / 1000000f, currentGesture)

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
