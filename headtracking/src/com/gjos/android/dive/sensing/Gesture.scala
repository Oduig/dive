package com.gjos.android.dive.sensing

object Gesture {
  val Empty = 0
  val Jump = 1
  val Crouch = 2
  val MoveN = 3
  val MoveNE = 4
  val MoveE = 5
  val MoveSE = 6
  val MoveS = 7
  val MoveSW = 8
  val MoveW = 9
  val MoveNW = 10
  val Stop = 11

  def toString(gesture: Int) = gesture match {
    case Empty => None
    case Jump => Some("Jump")
    case Crouch => Some("Crouch")
    case MoveN => Some("MoveN")
    case MoveNE => Some("MoveNE")
    case MoveE => Some("MoveE")
    case MoveSE => Some("MoveSE")
    case MoveS => Some("MoveS")
    case MoveSW => Some("MoveSW")
    case MoveW => Some("MoveW")
    case MoveNW => Some("MoveNW")
    case Stop => Some("Stop")
  }
}
