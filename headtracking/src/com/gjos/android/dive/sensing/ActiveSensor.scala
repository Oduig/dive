package com.gjos.android.dive.sensing

import android.content.Context
import android.hardware.{Sensor, SensorManager}

trait ActiveSensor {
  protected val context: Context
  val sensorType: Int

  protected val sensorManager = context.getSystemService(Context.SENSOR_SERVICE).asInstanceOf[SensorManager]
  protected val sensor = sensorManager.getDefaultSensor(sensorType)

  def onChange(values: Array[Float]): Unit
}
