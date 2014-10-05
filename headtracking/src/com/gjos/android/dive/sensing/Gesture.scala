package com.gjos.android.dive.sensing

object Gesture {
  val Empty = 0
  val Jump = 1
  val Crouch = 2
  val Uncrouch = 3

  def toString(gesture: Int) = gesture match {
    case Empty => None
    case Jump => Some("Jump")
    case Crouch => Some("Crouch")
    case Uncrouch => Some("Uncrouch")
  }
}
