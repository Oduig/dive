name := "remotecontrol"

version := "1.0"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"
)

unmanagedJars in Compile <++= baseDirectory map { base =>
  val libFolder = base / "lib"
  (libFolder ** "*.jar").classpath
}

javaOptions ++= Seq(
  "-Dbluecove.native.path=" + baseDirectory.toString + "/resources"
)

scalacOptions ++= Seq(
  "-target:jvm-1.7",
  "-feature",
  "-deprecation",
  "-unchecked"
)

fork in run := true

connectInput in run := true