package com.gjos.android.dive

import android.app.Activity
import android.os.Bundle
import android.widget.{Button, Toast}
import scala.concurrent.duration._
import android.view.View

class HeadTrackerUI extends RichActivity {
  override def onCreate(savedState: Bundle) {
    super.onCreate(savedState)
    this.setContentView(R.layout.main)

    val connectButton: Button = find(R.id.connectButton)
    connectButton.setOnClickListener(connectToServer)
  }

  def connectToServer() = {
    toast("Connecting...")
  }
}
