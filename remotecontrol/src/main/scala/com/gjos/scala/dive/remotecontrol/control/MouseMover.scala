package com.gjos.scala.dive.remotecontrol.control

import java.awt.{MouseInfo, GraphicsEnvironment, Robot}
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.duration._
import scala.concurrent.Future

class MouseMover(val throttle: Long = 10.millis.toMillis) {

  private var sensitivity = 8f
  private var running = false
  private val robot = new Robot()

//  private val width = GraphicsEnvironment.getLocalGraphicsEnvironment.getDefaultScreenDevice.getDisplayMode.getWidth
//  private val height = GraphicsEnvironment.getLocalGraphicsEnvironment.getDefaultScreenDevice.getDisplayMode.getHeight
//  private val xCenter = width / 2
//  private val yCenter = height / 2

  private val dx = new AtomicInteger()
  private val dy = new AtomicInteger()

  def start() {
    if (!running) {
      running = true
      Future {
        while(running) {
          val ms = System.currentTimeMillis
          update()
          val left = throttle - (System.currentTimeMillis - ms)
          if (left > 0) Thread sleep left
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
    val totalX = dx.getAndSet(0)
    val totalY = dy.getAndSet(0)
    val pos = MouseInfo.getPointerInfo.getLocation
    robot.mouseMove(pos.x + totalX, pos.y + totalY)
//  TODO: fix smoothening
//    var currentX = pos.x
//    var currentY = pos.y
//    for (i <- 1 to totalX * totalY) {
//      val stepX = if (i % totalY == 0) 1 else 0
//      val stepY = if (i % totalX == 0) 1 else 0
//      if (stepX > 0 || stepY > 0) {
//        currentX += stepX
//        currentY += stepY
//        robot.mouseMove(currentX, currentY)
//      }
//    }
  }
}
