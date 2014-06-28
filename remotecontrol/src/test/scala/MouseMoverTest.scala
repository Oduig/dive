import com.gjos.scala.dive.remotecontrol.control.MouseMover
import scala.concurrent.duration._

object MouseMoverTest extends App {
  val mouseMover = new MouseMover()

  mouseMover.nanosleep(5000000000L)
//  mouseMover.start()
//  mouseMover.move(100, 100)
//  Thread sleep 400.millis.toMillis
//  mouseMover.move(-200, 0)
//  Thread sleep 400.millis.toMillis
//  mouseMover.move(200, 500)
//
//  Thread sleep 5000.millis.toMillis
//  mouseMover.stop()
}
