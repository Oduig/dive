package com.gjos.android.dive.sensing

import android.hardware.{Sensor, SensorManager}
import android.content.Context

class LinearAccelerationSensor(protected val context: Context) extends ActiveSensor {
  val sensorType = Sensor.TYPE_LINEAR_ACCELERATION

  def onChange(values: Array[Float]) {
    require(values.size == 3, "LinearAccelerationSensor cannot take more than 3 values on change")
    val Array(dx, dy, dz) = values
    
  }
}