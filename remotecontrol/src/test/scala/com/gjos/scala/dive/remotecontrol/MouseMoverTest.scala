package com.gjos.scala.dive.remotecontrol

import com.gjos.scala.dive.remotecontrol.control.MouseMover
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._

class MouseMoverTest extends WordSpec with Matchers {

  "MouseMover" should {
    "move the mouse cumulatively" in {
      val mouseMover = new MouseMover()

      val startpos = mouseMover.position

      mouseMover.start()
      mouseMover.move(10, 10)
      Thread sleep 40.millis.toMillis
      mouseMover.move(-20, 0)
      Thread sleep 40.millis.toMillis
      mouseMover.move(20, 50)

      Thread sleep 100.millis.toMillis
      mouseMover.stop()

      val dx = mouseMover.position.x - startpos.x
      val dy = mouseMover.position.y - startpos.y
      dx should be (10)
      dy should be (60)
    }
  }
}
