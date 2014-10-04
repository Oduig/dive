package com.gjos.android.dive.sensing

import android.hardware.{SensorManager, Sensor}

class GyroscopeSensor() extends ActiveSensor {
  val sensorType = Sensor.TYPE_GYROSCOPE
  val pollRate = SensorManager.SENSOR_DELAY_FASTEST
}
