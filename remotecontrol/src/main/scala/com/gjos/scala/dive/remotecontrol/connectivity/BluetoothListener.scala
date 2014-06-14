package com.gjos.android.dive.connectivity

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.blocking
import scala.concurrent.Future
import scala.concurrent.duration._

class BluetoothListener extends ListenerImpl {

  protected def openSafely() {
    blocking(Thread sleep 2.seconds.toMillis)
  }

  protected def closeSafely() {
    blocking(Thread sleep 1.second.toMillis)
  }


  def send(bs: String) = Future {

  }

  def onReceive(handler: String => Unit) {

  }
}
