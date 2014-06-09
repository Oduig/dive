package com.gjos.android.dive.sensing

import android.hardware.{Sensor, SensorManager}
import android.content.Context
import rx.lang.scala.Observable
import android.util.Log

class LinearAccelerationSensor() extends ActiveSensor {
  val sensorType = Sensor.TYPE_LINEAR_ACCELERATION

  //def observable = Observable(subscriber => subscriber.)

  def onChange(values: Array[Float]) {
    require(values.size == 3, "LinearAccelerationSensor cannot take more than 3 values on change")
    val Array(dx, dy, dz) = values
    Log.e("DiveHeadTracking", s"Sensor moved ($dx, $dy, $dz)")
  }
}