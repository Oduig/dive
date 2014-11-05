package com.gjos.scala.dive.remotecontrol.control

trait Gesture {
  val value: Int
  val name: Option[String]
}

object Gesture {
  case object Empty extends Gesture {
    val value = 0
    val name = None
  }
  case object Jump extends Gesture {
    val value = 1
    val name = Some("Jump")
  }
  case object Crouch extends Gesture {
    val value = 2
    val name = Some("Crouch")
  }
  case object Uncrouch extends Gesture {
    val value = 3
    val name = Some("Uncrouch")
  }
}
