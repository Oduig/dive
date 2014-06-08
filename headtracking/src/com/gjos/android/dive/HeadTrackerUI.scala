package com.gjos.android.dive

import android.os.Bundle
import android.widget.{EditText, RadioGroup, Button}
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global

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
  val connection = new DummyConnection()

  def connectBtnClick() = if (connection.isOpen) startDisconnect else startConnect


  def startConnect() {
    connectButton.setEnabled(false)
    toast("Connecting...")
    connection.open() onComplete connectComplete
  }

  def startDisconnect() {
    connectButton.setEnabled(false)
    toast("Disconnecting...")
    connection.close() onComplete disconnectComplete
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
