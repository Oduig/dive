package com.gjos.scala.dive.remotecontrol.control

import java.awt.Robot
import scala.util.Try

class MouseMover() {

  private val robot = new Robot()

  def move(x: Int, y: Int) = Try {
    robot.mouseMove(x, y)
  }
}
