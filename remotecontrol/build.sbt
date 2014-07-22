name := "remotecontrol"

version := "1.0"

libraryDependencies ++= Seq(
  "net.sf.bluecove" % "bluecove" % "2.1.0",
  "org.scalatest" % "scalatest_2.10" % "2.2.0" % "test"
)

javaOptions ++= Seq(
  "-Dbluecove.native.path=/Localdata/git/dive/remotecontrol/resources"
)

fork in run := true

connectInput in run := true