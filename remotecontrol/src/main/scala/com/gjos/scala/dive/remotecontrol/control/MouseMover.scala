package com.gjos.scala.dive.remotecontrol.control

import java.awt.{MouseInfo, Robot}
import scala.util.Try

class MouseMover() {

  private val robot = new Robot()

  def move(x: Int, y: Int) = Try {
    val point = MouseInfo.getPointerInfo().getLocation()
    robot.mouseMove(point.x + x, point.x + y)
  }
}
