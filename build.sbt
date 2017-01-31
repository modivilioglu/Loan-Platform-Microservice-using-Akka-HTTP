//name := "akka-http-microservice"
//organization := "com.theiterators"
//version := "1.0"
//scalaVersion := "2.11.8"
//
//libraryDependencies ++= {
//  val akkaVersion       = "2.4.12"
//  val akkaHttpVersion   = "3.0.0-RC1"
//  val scalaTestVersion  = "3.0.1"
//  Seq(
//    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
//    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
//    "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
//    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion//,
//    //"com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion//,
//    //"com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
//    //"org.scalatest"     %% "scalatest" % scalaTestVersion % "test"
//  )
//}

name := "Crowd-Sourcing Loan Platform"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

import sbt._

lazy val root = (project in file("."))
  .settings(libraryDependencies ++= Seq(
    "com.typesafe.akka" % "akka-actor_2.11" % "2.4-SNAPSHOT",
    "com.typesafe.akka" %% "akka-http" % "3.0.0-RC1",
    "com.typesafe.akka" %% "akka-http-spray-json" % "3.0.0-RC1",
    "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % "3.0.0-RC1"
  ))