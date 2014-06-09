package com.gjos.android.dive.sensing

import rx.lang.scala.{Subscription, Subscriber, Observable}
import scala.collection.mutable

trait ActiveSensor {
  val sensorType: Int

  private val subscribers = mutable.Buffer.empty[Subscriber[Array[Float]]]

  def observable = Observable[Array[Float]] {
    subscriber => {
      subscribers += subscriber
      Subscription { subscribers -= subscriber }
    }
  }

  final def onChange(values: Array[Float]) {
    safeOnChange(values)
    subscribers foreach (_.onNext(values))
  }

  // Override this to add behavior on change
  protected def safeOnChange(values: Array[Float]) {}
}
