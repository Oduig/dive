package com.gjos.android.dive

import scala.collection.mutable
import scala.annotation.tailrec

/**
 * I was in an interesting mood while writing this class
 * Beware for variably entertaining comments (Vec)
 */
class Vec(val coordinates: mutable.Buffer[Float]) {
  def x = coordinates(0)
  def y = coordinates(1)
  def z = coordinates(2)

  def numDimensions = coordinates.size

  def *(scale: Float) = {
    coordinates foreach (_ * scale)
    this
  }

  def +(that: Vec) = (this bond that)(+=)
  def *(that: Vec) = (this bond that)(*=)

  def +=(that: Vec) = (this fuse that) (_ + _)
  def *=(that: Vec) = (this fuse that) (_ * _)

  /*
   * Fuse merges that into this
   */
  @tailrec private def fuse(that: Vec)(fn: (Float, Float) => Float, i: Int = 0): Unit = {
    if (i < numDimensions) {
      this.coordinates(i) = fn(this.coordinates(i), that.coordinates(i))
      fuse(that)(fn, i + 1)
    }
  }

  /*
   * Bond leaves this and that intact and makes a third
   * Despite this, some say that bonding does not leave the two intact
   */
  private def bond(that: Vec)(fn: Vec => Unit) = {
    val buffer = mutable.Buffer.empty[Float]
    coordinates copyToBuffer buffer
    val v = new Vec(buffer)
    fn(that)
    v
  }

  // The first rule of f is, you do not talk about f
  // After you try google, please ask them to support special characters!
  override def toString() = coordinates.map(f => f"$f%.1f").mkString("(", ", ", ")")
}

object Vec {
  def apply(coords: Float*): Vec = new Vec(coords.toBuffer)

  def origin = Vec(0) // Because I can
  def origin2D = Vec(0, 0)
  def origin3D = Vec(0, 0, 0)
}