package com.gjos.android.dive.sensing

import android.hardware.{SensorManager, Sensor}

class LinearAccelerationSensor() extends ActiveSensor {
  val sensorType = Sensor.TYPE_LINEAR_ACCELERATION
  val pollRate = SensorManager.SENSOR_DELAY_GAME
}