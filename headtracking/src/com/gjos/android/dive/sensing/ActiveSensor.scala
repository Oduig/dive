package com.gjos.android.dive.sensing

import android.content.Context
import android.hardware.{Sensor, SensorManager}

trait ActiveSensor {
  val sensorType: Int

  def onChange(values: Array[Float]): Unit
}
