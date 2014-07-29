package com.gjos.android.dive.connectivity

import java.io.{IOException, DataOutputStream}
import java.util.UUID

import android.bluetooth.{BluetoothSocket, BluetoothAdapter}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.blocking
import scala.concurrent.Future

class BluetoothConnection(val bluetoothAddress: String) extends ConnectionImpl {

  var connection: Option[(BluetoothSocket, DataOutputStream)] = None

  protected def openSafely() {
    val adapter = BluetoothAdapter.getDefaultAdapter
    val device = adapter.getRemoteDevice(bluetoothAddress.toUpperCase)
    // Android bluetooth UUID that works
    val uuid = "00001101-0000-1000-8000-00805F9B34FB"
    val btSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid))
    adapter.cancelDiscovery()
    btSocket.connect()
    val outStream = new DataOutputStream(btSocket.getOutputStream)
    connection = Some(btSocket, outStream)
  }

  protected def closeSafely() = connection match {
    case Some((sock, _)) =>
      blocking(sock.close())
      connection = None
    case _ =>
  }

  def send(line: String) = connection match {
    case Some((_, stream)) =>
      Future(stream writeBytes (line + '\n'))
    case None =>
      throw new IOException("Cannot send Bluetooth data when disconnected")
  }

  def onReceive(handler: Byte => Unit) {

  }

}
