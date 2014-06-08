package com.gjos.android.dive.connectivity

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.blocking
import scala.concurrent.Future
import scala.concurrent.duration._

class BluetoothConnection extends ConnectionImpl {

  protected def openSafely() {
    blocking(Thread sleep 2.seconds.toMillis)
  }

  protected def closeSafely() {
    blocking(Thread sleep 1.second.toMillis)
  }


  def send(b: Byte) = Future {

  }

  def onReceive(handler: Byte => Unit) {

  }
}
