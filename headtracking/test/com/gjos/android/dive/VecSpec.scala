package com.gjos.android.dive

import org.scalatest.{WordSpec, Matchers}
import com.gjos.android.dive.calc.Vec

class VecSpec extends WordSpec with Matchers {

  "Vec" should {
    "have dimension equal to the number of floats" in {
      val v = Vec(1, 2, 3)
      v.numDimensions should be (3)
    }
  }
}
