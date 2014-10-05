package com.gjos.android.dive.sensing

/**
 * Detects gestures from accelerometer
 */
class GestureDetector() {
  private val MinGestureInterval = 1000
  private val CrouchTreshold = 1.7f
  private val JumpTreshold = 3f
  private val FramesInGesture = 4

  private var jumpFrames = 0
  private var crouchFrames = 0
  private var uncrouchFrames = 0
  var crouched = false

  private var _currentGesture = Gesture.Empty
  private var latestGestureTime = 0L

  def popGesture() = {
    val r = _currentGesture
    _currentGesture = Gesture.Empty
    r
  }

  def learnGesturesFor(duration: Long) {
    //latestGestureTime = System.currentTimeMillis + duration
  }

  //private var tresholdXmax = 0f
  //private var tresholdXmin = 0f

  private def resetFrames(): Unit = {
    jumpFrames = 0
    crouchFrames = 0
    uncrouchFrames = 0
  }

  def detectGesture(x: Float, y: Float, z: Float) {
    val now = System.currentTimeMillis

    _currentGesture = {
      if (now < latestGestureTime + MinGestureInterval) {
        // Skip mode (to avoid flooding gestures)
        Gesture.Empty
      } else if (x < -CrouchTreshold && uncrouchFrames >= FramesInGesture && crouched) {
        crouched = false
        resetFrames()
        Gesture.Uncrouch
      } else if (x < -JumpTreshold && jumpFrames >= FramesInGesture && !crouched) {
        resetFrames()
        Gesture.Jump
      } else if (x > CrouchTreshold && crouchFrames >= FramesInGesture && !crouched) {
        crouched = true
        resetFrames()
        Gesture.Crouch
      } else if (x > CrouchTreshold && crouched && jumpFrames == 0 && crouchFrames == 0) {
        uncrouchFrames += 1
        Gesture.Empty
      } else if (x < -CrouchTreshold && !crouched && jumpFrames == 0 && uncrouchFrames == 0) {
        crouchFrames += 1
        Gesture.Empty
      } else if (x > JumpTreshold && !crouched && uncrouchFrames == 0 && crouchFrames == 0) {
        jumpFrames += 1
        Gesture.Empty
      } else {
        Gesture.Empty
      }
    }

    if (_currentGesture != Gesture.Empty) {
      latestGestureTime = now
    }
  }
}
