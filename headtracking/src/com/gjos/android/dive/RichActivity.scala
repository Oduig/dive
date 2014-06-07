package com.gjos.android.dive

import android.app.Activity
import android.widget.Toast
import scala.concurrent.duration._
import android.view.View

class RichActivity extends Activity {

  protected def find[T](viewId: Int): T = findViewById(viewId).asInstanceOf[T]

  protected def toast(msg: String) = {
    Toast.makeText(this, msg, 1.second.toMillis.toInt).show()
  }

  implicit protected def callbackToClickListener(callback: => Unit) = new View.OnClickListener() {
    def onClick(v: View) = callback
  }
}
