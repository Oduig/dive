package com.gjos.android.dive.calc

import scala.collection.mutable
import scala.annotation.tailrec


class Vec(private val coordinate: mutable.Buffer[Float]) {
  def x = coordinate(0)
  def y = coordinate(1)
  def z = coordinate(2)

  def coordinates = coordinate.toList

  def numDimensions = coordinate.size

  def length = Math sqrt coordinate.map(x => x * x).sum

  // Mutable ops
  def *=(scale: Float) = transform(coordinate(_) *= scale)
  def /=(scale: Float) = transform(coordinate(_) /= scale)
  def +=(that: Vec) = (this fuse that)(_ + _)
  def *=(that: Vec) = (this fuse that)(_ * _)
  def /=(that: Vec) = (this fuse that)(_ / _)

  // Immutable ops
  def *(scale: Float) = copy() *= scale
  def /(scale: Float) = copy() /= scale
  def +(that: Vec) = copy() += that
  def *(that: Vec) = copy() *= that
  def /(that: Vec) = copy() /= that

  def copy() = {
    val buffer = mutable.Buffer.empty[Float]
    coordinate copyToBuffer buffer
    new Vec(buffer)
  }

  private def fuse(that: Vec)(fn: (Float, Float) => Float) = transform { i =>
    this coordinate i = fn(this coordinate i, that coordinate i)
  }

  @tailrec private def transform(fn: Int => Unit, i: Int = numDimensions): Vec = i match {
    case 0 => this
    case _ =>
      fn(i - 1)
      transform(fn, i - 1)
  }

  // The first rule of f is, you do not talk about f
  // After you try google, please ask them to support special characters!
  override def toString() = coordinate.map(f => f"$f%.1f").mkString("(", ", ", ")")

  def toCsv(scale: Int = 100) = coordinate.map(c => (c * scale).toInt).mkString(",")
}

object Vec {
  def apply(coords: Float*): Vec = new Vec(coords.toBuffer)

  def origin = Vec(0) // Because I can
  def origin2D = Vec(0, 0)
  def origin3D = Vec(0, 0, 0)
}