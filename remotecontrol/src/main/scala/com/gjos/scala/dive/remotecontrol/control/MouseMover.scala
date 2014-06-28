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
 * Will smoothen out the mouse when move(x, y) is called, slowing the mouse to pixelsPerSecond
 * Move commands are queued if they arrive while a move is in progress (TODO make it adjust to new move immediately)
 * If there are no more move commands, will go idle for slackMs
 */
class MouseMover(val slackMs: Long = 10.millis.toMillis, val pixelsPerSecond: Int = 1000) {

  private var sensitivity = 1f
  private var running = false
  private val robot = new Robot()

  private val moveDelayNanos = Math.max(1, 1000000000 / pixelsPerSecond)

  private val dx = new AtomicInteger()
  private val dy = new AtomicInteger()

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
    val startPos = MouseInfo.getPointerInfo.getLocation
    val maxStep = .05

    def step(move: Int) = if (move == 0) 0 else if (move > 0) Math.max((maxStep * move).toInt, 1) else Math.min((maxStep * move).toInt, 1)

    @tailrec def iter(curX: Int, curY: Int, moveX: Int, moveY: Int): Unit = {
      val (stepX, stepY) = if (moveX != 0 && moveY != 0) {
        Math.abs(moveX).toFloat / math.abs(moveY) match {
          case ratio if ratio > 2 => (step(moveX), 0)
          case ratio if ratio > 0.5 => (step(moveX), step(moveY))
          case _ => (0, step(moveY))
        }
      } else {
        (step(moveX), step(moveY))
      }
      if (stepX != 0 || stepY != 0) {
        robot.mouseMove(curX + stepX, curY + stepY)
        nanosleep(moveDelayNanos)
        val todoX = dx.addAndGet(-stepX)
        val todoY = dy.addAndGet(-stepY)
        iter(curX + stepX, curY + stepY, todoX, todoY)
      }
    }

    iter(startPos.x, startPos.y, dx.get, dy.get)
  }

  @tailrec final def nanosleep(remaining: Long, previous: Long = System.nanoTime): Unit = {
    if (remaining > 0) {
      val _ = 3.14 * 9.1
      val t = System.nanoTime
      val dt = Math.max(t - previous, 0)
      nanosleep(t, remaining - dt)
    }
  }
}
