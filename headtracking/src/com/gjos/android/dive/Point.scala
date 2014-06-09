package com.gjos.android.dive

class Point(val x: Short, val y: Short) {
  def +(that: Point) = Point(this.x + that.x, this.y + that.y)

  override def toString() = s"($x, $y)"
}

object Point {
  def origin = new Point(0, 0)
  def apply(x: Short, y: Short): Point = new Point(x, y)
  def apply(x: Int, y: Int): Point = Point(intToShort(x), intToShort(y))
  def apply(x: Float, y: Float): Point = Point(x.toInt, y.toInt)

  private def intToShort(i: Int): Short = Math.max(Short.MinValue, Math.min(Short.MaxValue, i)).toShort
}