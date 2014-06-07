import android.Keys._

android.Plugin.androidBuild

name := "Dive head tracker"

scalaVersion := "2.11.0"

proguardCache in Android ++= Seq(
)

proguardOptions in Android ++= Seq("-dontobfuscate", "-dontoptimize", "-dontwarn scala.collection.mutable.**")

libraryDependencies ++= Seq(
)

scalacOptions in Compile += "-feature"

run <<= run in Android

install <<= install in Android
