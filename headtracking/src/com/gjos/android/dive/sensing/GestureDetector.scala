package com.gjos.android.dive.sensing

/**
 * Detects gestures from accelerometer
 */
class GestureDetector() {
  private val GestureTreshold = 3f
  private val MinGestureInterval = 2000

  private var _currentGesture = Gesture.Empty
  private var latestGestureTime = 0L

  def popGesture() = {
    val r = _currentGesture
    _currentGesture = Gesture.Empty
    r
  }

  def learnGesturesFor(duration: Long) {
    latestGestureTime = System.currentTimeMillis + duration
  }

  private var (tresholdXmax, tresholdYMax, tresholdZmax) = (0f, 0f, 0f)
  private var (tresholdXmin, tresholdYmin, tresholdZmin) = (0f, 0f, 0f)

  def detectGesture(x: Float, y: Float, z: Float) {
    val now = System.currentTimeMillis

    _currentGesture = {
      if (now < latestGestureTime) {
        // learning mode
        if (x > tresholdXmax) tresholdXmax = x * GestureTreshold
        else if (x < tresholdXmin) tresholdXmin = x * GestureTreshold

        if (y > tresholdYMax) tresholdYMax = y * GestureTreshold
        else if (y < tresholdYmin) tresholdYmin = y * GestureTreshold

        if (z > tresholdZmax) tresholdZmax = z * GestureTreshold
        else if (z < tresholdZmin) tresholdZmin = z * GestureTreshold

        Gesture.Empty
      } else if (now < latestGestureTime + MinGestureInterval) {
        // Skip mode (to avoid flooding gestures)
        Gesture.Empty
      } else if (x > tresholdXmax) {
        Gesture.Jump
      } else if (x < tresholdXmin) {
        Gesture.Crouch
      } else if (z < tresholdZmin) {
        if (y < tresholdYmin) {
          Gesture.MoveNE
        } else if (y > tresholdYMax) {
          Gesture.MoveNW
        } else {
          Gesture.MoveN
        }
      } else if (z > tresholdZmax) {
        if (y < tresholdYmin) {
          Gesture.MoveSE
        } else if (y > tresholdYMax) {
          Gesture.MoveSW
        } else {
          Gesture.MoveS
        }
      } else if (y < tresholdYmin) {
        Gesture.MoveE
      } else if (y > tresholdYMax) {
        Gesture.MoveW
      } else {
        Gesture.Empty
      }
    }

    if (_currentGesture != Gesture.Empty) {
      latestGestureTime = now
    }
  }
}
