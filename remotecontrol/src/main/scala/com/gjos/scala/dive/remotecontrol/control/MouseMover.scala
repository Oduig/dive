package com.gjos.scala.dive.remotecontrol.control

import java.awt.{MouseInfo, GraphicsEnvironment, Robot}
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.blocking
import scala.annotation.tailrec

/**
 * Relatively move mouse position
 * If there are no more move commands, will go idle for slackMs to save the whale
 */
class MouseMover(val slackMs: Long = 10.millis.toMillis) {

  private var sensitivityX = 1f
  private var sensitivityY = 1f
  private var sensitivityZ = 1f
  private var running = false
  private val robot = new Robot()

  private val dx = new AtomicInteger()
  private val dy = new AtomicInteger()
  private val dz = new AtomicInteger()

  def position = MouseInfo.getPointerInfo.getLocation

  def start() {
    if (!running) {
      running = true
      Future {
        while(running) {
          val ms = System.currentTimeMillis
          update()
          val left = slackMs - (System.currentTimeMillis - ms)
          if (left > 0) blocking(Thread sleep left)
        }
      }
    }
  }

  def stop() {
    running = false
  }

  def move(x: Int, y: Int, z: Int) {
    dx.addAndGet((x * sensitivityX).toInt)
    dy.addAndGet((y * sensitivityY).toInt)
    dz.addAndGet((z * sensitivityZ).toInt)
  }

  def moreSensitive(c: Option[Char]): (Float, Float, Float) = changeSensitivity(1.5f)(c getOrElse ' ')
  def lessSensitive(c: Option[Char]): (Float, Float, Float) = changeSensitivity(1/1.5f)(c getOrElse ' ')

  private def changeSensitivity(factor: Float)(c: Char): (Float, Float, Float) = {
    if (c == 'x' || c == ' ') sensitivityX *= factor
    if (c == 'y' || c == ' ') sensitivityY *= factor
    if (c == 'z' || c == ' ') sensitivityZ *= factor
    (sensitivityX, sensitivityY, sensitivityZ)
  }

  private def update() {
    @tailrec def iter(moveX: Float, moveY: Float, moveZ: Float, restX: Float=0, restY: Float=0, restZ: Float=0): Unit = {
      if (moveX != 0 || moveY != 0 || moveZ != 0) {
        // Deal with yaw and pitch by gradually approaching the target
        val largest = Math max (Math abs moveX, Math abs moveY)
        // Normalize
        val stepX = if (largest == 0) 0 else moveX / largest
        val stepY = if (largest == 0) 0 else moveY / largest
        // The actual mousemove is in discrete steps
        val offset = position
        val discreteX = (restX + stepX).toInt
        val discreteY = (restY + stepY).toInt

        robot.mouseMove(offset.x + discreteX, offset.y + discreteY)

        // Deal with roll by mouse scroll. Take a step of 1 or -1
        val stepZ = if (moveZ == 0) 0 else moveZ / Math.abs(moveZ)
        val discreteZ = (restZ + stepZ).toInt

        robot.mouseWheel(discreteZ)

        // Calculate new position and remaining move
        iter(
          moveX - stepX + dx.getAndSet(0),
          moveY - stepY + dy.getAndSet(0),
          moveZ - stepZ + dz.getAndSet(0),
          restX + stepX - discreteX,
          restY + stepY - discreteY,
          restZ + stepZ - discreteZ)
      }
    }

    iter(dx.getAndSet(0), dy.getAndSet(0), dz.getAndSet(0))
  }
}
