name := "remotecontrol"

version := "1.0"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.10" % "2.2.0" % "test"
)

unmanagedJars in Compile <++= baseDirectory map { base =>
  val libFolder = base / "lib"
  (libFolder ** "*.jar").classpath
}

javaOptions ++= Seq(
  "-Dbluecove.native.path=" + baseDirectory.toString + "/resources"
)

fork in run := true

connectInput in run := true