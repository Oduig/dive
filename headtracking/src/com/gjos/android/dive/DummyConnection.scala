package com.gjos.android.dive

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.blocking
import scala.concurrent.Future
import scala.concurrent.duration._

class DummyConnection extends Connection {

  private var connectionOpen = false

  def open() = Future {
    blocking(Thread sleep 2.second.toMillis)
    connectionOpen = true
  }

  def close() = Future {
    blocking(Thread sleep 1.second.toMillis)
    connectionOpen = false
  }

  def isOpen() = connectionOpen

  def send(b: Byte) = Future {

  }

  def onReceive(handler: Byte => Unit) {

  }
}
