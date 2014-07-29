package com.gjos.android.dive

import android.app.Activity
import android.widget.RadioGroup.OnCheckedChangeListener
import android.widget.{RadioGroup, Toast}
import scala.concurrent.duration._
import android.view.{Gravity, ViewGroup, View}
import scala.language.implicitConversions

class RichActivity extends Activity {

  private lazy val toaster = {
    val toast = Toast.makeText(this, "", 1.second.toMillis.toInt)
    toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0)
    toast
  }

  protected def find[T](viewId: Int): T = findViewById(viewId).asInstanceOf[T]

  protected def toast(msg: String) = {
    toaster.setText(msg)
    toaster.show()
  }

  implicit protected def callbackToClickListener(callback: => Unit) = new View.OnClickListener() {
    def onClick(v: View) = callback
  }

  implicit protected def callbackToCheckedChangeListener(callback: Int => Unit) = new OnCheckedChangeListener() {
    def onCheckedChanged(rg: RadioGroup, index: Int): Unit = callback(index)
  }

  protected def children(v: ViewGroup): Vector[View] = {
    (0 until v.getChildCount).toVector map v.getChildAt
  }

  protected def inUiThread(statements: => Unit) = runOnUiThread {
    new Runnable() {
      def run() = statements
    }
  }
}
