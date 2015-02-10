/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/10/15 12:33 PM
 */

import sbt._


object Dependencies {

  object Resolvers {

    lazy val standard = Seq(
      "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
      Resolver.sonatypeRepo("snapshots"),
      Resolver.typesafeRepo("releases"),
      "Typesafe Ivy Repo" at "http://repo.typesafe.com/typesafe/ivy-releases",
      "Java.net Maven2 Repository" at "http://download.java.net/maven/2/",
      "Maven.org" at "http://repo1.maven.org/maven2/")

    lazy val spray = Seq(
      "spray repo" at "http://repo.spray.io")

  }


  lazy val coreBundle = akka ++ config ++ test


  private val akkaVersion = "2.3.4"
  lazy val akka = Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-remote" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test")

  lazy val config = Seq(
    "com.typesafe" % "config" % "1.2.1")

  lazy val facebook = Seq(
    "com.restfb" % "restfb" % "1.7.1")

  lazy val guava = Seq(
    "com.google.guava" % "guava" % "18.0")

  lazy val jodaTime = Seq(
    "joda-time" % "joda-time" % "2.4")

  lazy val json = Seq(
    "org.json4s" %% "json4s-native" % "3.2.10",
    "org.json4s" %% "json4s-jackson" % "3.2.10",
    "org.json4s" %% "json4s-ext" % "3.2.10")

  lazy val log = Seq(
    "ch.qos.logback" % "logback-classic" % "1.1.2" % "compile")

  lazy val netty = Seq(
    "io.netty" % "netty-all" % "4.0.12.Final")

  lazy val redis = Seq(
    "com.github.spullara.redis" % "client" % "0.7",
    "com.github.robconrad" %% "scredis" % "2.1.0-SNAPSHOT" changing())

  private val sprayVersion = "1.3.1"
  lazy val sprayClient = Seq(
    "io.spray" %% "spray-client" % sprayVersion)
  lazy val sprayCaching = Seq(
    "io.spray" %% "spray-caching" % sprayVersion)
  lazy val sprayHttp = Seq(
    "io.spray" %% "spray-http" % sprayVersion)
  lazy val spray = sprayClient ++ sprayCaching ++ Seq(
    "io.spray" %% "spray-can" % sprayVersion,
    "io.spray" %% "spray-routing" % sprayVersion,
    "io.spray" %% "spray-testkit" % sprayVersion % "test")

  lazy val spraySwagger = Seq(
    "com.gettyimages" %% "spray-swagger" % "0.4.4" excludeAll(ExclusionRule(organization = "org.json4s"), ExclusionRule(organization = "org.slf4j")))

  lazy val swaggerAnnotations = Seq(
    "com.wordnik" % "swagger-annotations" % "1.3.5")

  lazy val test = Seq(
    "org.scalamock" %% "scalamock-scalatest-support" % "3.2.1" % "test")

  lazy val twilio = Seq(
    "com.twilio.sdk" % "twilio-java-sdk" % "3.4.5")

}