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

  private var sensitivity = 1f
  private var running = false
  private val robot = new Robot()

  private val dx = new AtomicInteger()
  private val dy = new AtomicInteger()

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

  def move(x: Int, y: Int) {
    dx.addAndGet((x * sensitivity).toInt)
    dy.addAndGet((y * sensitivity).toInt)
  }

  def moreSensitive(): Float = {
    sensitivity *= 2
    sensitivity
  }

  def lessSensitive(): Float = {
    sensitivity /= 2
    sensitivity
  }

  private def update() {
    @tailrec def iter(moveX: Float, moveY: Float, restX: Float=0, restY: Float=0): Unit = {
      if (moveX != 0 || moveY != 0) {
        val largest = Math max (Math abs moveX, Math abs moveY)
        // Normalize
        val stepX = moveX / largest
        val stepY = moveY / largest
        // The actual mousemove is in discrete steps
        val offset = position
        val discreteX = (restX + stepX).toInt
        val discreteY = (restY + stepY).toInt

        robot.mouseMove(offset.x + discreteX, offset.y + discreteY)
        // Calculate new position and remaining move
        iter(moveX - stepX + dx.getAndSet(0), moveY - stepY + dy.getAndSet(0), restX + stepX - discreteX, restY + stepY - discreteY)
      }
    }

    iter(dx.getAndSet(0), dy.getAndSet(0))
  }
}
