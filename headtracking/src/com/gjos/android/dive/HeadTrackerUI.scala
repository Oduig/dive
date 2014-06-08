package com.gjos.android.dive

import android.os.Bundle
import android.widget.{EditText, RadioGroup, Button}
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global
import com.gjos.android.dive.connectivity._
import scala.util.Success
import scala.util.Failure
import scala.Some

class HeadTrackerUI extends RichActivity {
  override def onCreate(savedState: Bundle) {
    super.onCreate(savedState)
    this.setContentView(R.layout.main)

    connectButton.setOnClickListener(connectBtnClick)
  }

  def connectButton: Button = find(R.id.connectButton)
  def radioGroup: RadioGroup = find(R.id.connectionTypeGroup)
  def ipEdit: Button = find(R.id.ipEdit)
  def portEdit: EditText = find(R.id.portEdit)

  var currentConnection: Option[Connection] = None

  def connectBtnClick() = currentConnection match {
    case Some(connection) if connection.isOpen => startDisconnect()
    case _ => startConnect()
  }

  def startConnect() {
    currentConnection = Some(createConnection())
    connectButton.setEnabled(false)
    toast("Connecting...")
    currentConnection.get.open() onComplete connectComplete
  }

  def createConnection() = radioGroup.getCheckedRadioButtonId match {
    case R.id.tcp => new TcpConnection
    case R.id.udp => new UdpConnection
    case R.id.bluetooth => new BluetoothConnection
  }

  def startDisconnect() {
    connectButton.setEnabled(false)
    toast("Disconnecting...")
    currentConnection.get.close() onComplete disconnectComplete
  }

  def connectComplete(result: Try[Unit]) = inUiThread {
    result match {
      case Success(_) =>
        toast("Connected!")
        connectButton.setText("Disconnect")
      case Failure(ex) =>
        toast("Failed to connect: " + ex.getMessage)
    }
    connectButton.setEnabled(true)
  }

  def disconnectComplete(result: Try[Unit]) = inUiThread {
    result match {
      case Success(_) =>
        toast("Disconnected.")
        connectButton.setText("Connect")
      case Failure(ex) =>
        toast("Failed to disconnect: " + ex.getMessage)
    }
    connectButton.setEnabled(true)
  }

}
