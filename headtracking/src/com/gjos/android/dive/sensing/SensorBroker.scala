package com.gjos.android.dive.sensing

import android.content.Context
import android.hardware.{Sensor, SensorManager, SensorEvent, SensorEventListener}

class SensorBroker(context: Context) extends SensorEventListener {
  private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE).asInstanceOf[SensorManager]

  val accelerometer = new LinearAccelerationSensor()
  val gyroscope = new GyroscopeSensor()

  private val sensors = Set(gyroscope)

  def startListening() = registerSensorListeners()
  def stopListening() = unregisterSensorListeners()

  private def registerSensorListeners() {
    for (sensor <- sensors) yield {
      val sensorDevice = sensorManager.getDefaultSensor(sensor.sensorType)
      sensorManager.registerListener(this, sensorDevice, SensorManager.SENSOR_DELAY_FASTEST)
    }
  }

  private def unregisterSensorListeners() {
    for (sensor <- sensors) {
      val sensorDevice = sensorManager.getDefaultSensor(sensor.sensorType)
      sensorManager.unregisterListener(this, sensorDevice)
    }
  }
  
  protected def onSensorChanged(event: SensorEvent) = {
    for (sensor <- sensors if sensor.sensorType == event.sensor.getType) {
      sensor.onChange(event.values)
    }
  }

  protected def onAccuracyChanged(event: Sensor, accuracy: Int) {}

}
