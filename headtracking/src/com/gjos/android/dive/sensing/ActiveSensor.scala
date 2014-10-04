package com.gjos.android.dive.sensing

trait ActiveSensor {
  val sensorType: Int
  val pollRate: Int

  private var handleChange: Option[Array[Float] => Unit] = None
  def subscribe(handle: Array[Float] => Unit) {
    handleChange = Some(handle)
  }

  def unsubscribe() {
    handleChange = None
  }

  final def onChange(values: Array[Float]) {
    safeOnChange(values)
    handleChange map (_(values))
  }

  // Override this to add behavior on change
  protected def safeOnChange(values: Array[Float]) {}
}
