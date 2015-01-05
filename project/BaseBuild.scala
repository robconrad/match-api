/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 3:13 PM
 */

import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import org.scalastyle.sbt.ScalastylePlugin
import sbt.Keys._
import sbt._
import sbtassembly.Plugin.AssemblyKeys
import scoverage.ScoverageSbtPlugin
import scoverage.ScoverageSbtPlugin.ScoverageKeys

import scalariform.formatter.preferences._

/**
 * Top level build configuration
 */
object BaseBuild extends Build {

  object Names {
    final val common = "common"
    final val entity = "entity"
    final val rest = "rest"
    final val server = "server"
    final val socket = "socket"
    final val slick = "slick"
    final val top = "base-api"
  }

  object Dirs {
    private def project(name: String) = s"project-$name"
    final val common = project(Names.common)
    final val entity = project(Names.entity)
    final val rest = project(Names.rest)
    final val server = project(Names.server)
    final val socket = project(Names.socket)
    final val slick = project(Names.slick)
    final val top = "."
  }

  /**
   * Base project, provides base.common libraries to all base-api projects
   */
  lazy val commonProject = Project(
    id = Names.common,
    base = file(Dirs.common),
    settings = baseSettings ++ Seq(
      name := Names.common,
      libraryDependencies ++=
        Dependencies.coreBundle ++
        Dependencies.guava ++
        Dependencies.jodaTime ++
        Dependencies.log
    )
  )

  /**
   * Standalone project invoked only during development to generate tables code for Slick
   */
  lazy val slickCodegenProject = Project(
    id = Names.slick,
    base = file(Dirs.slick),
    settings = baseSettings ++ Seq(
      name := Names.slick,
      libraryDependencies ++= Dependencies.slick
    )
  )

  /**
   * Base business logic layer
   */
  lazy val entityProject = Project(
    id = Names.entity,
    base = file(Dirs.entity),
    settings = baseSettings ++ Seq(
      name := Names.entity,
      slickTaskKey <<= slickTask,
      libraryDependencies ++=
        Dependencies.coreBundle ++
        Dependencies.json ++
        Dependencies.redis ++
        Dependencies.slick ++
        Dependencies.sprayCaching ++
        Dependencies.sprayHttp ++
        Dependencies.swaggerAnnotations
    )
  ).dependsOn(
      slickCodegenProject,
      commonProject % "test->test;compile->compile")

  /**
   * REST API and invocation of business logic
   */
  lazy val restProject = Project(
    id = Names.rest,
    base = file(Dirs.rest),
    settings = baseSettings ++ Seq(
      name := Names.rest,
      resolvers ++= Dependencies.Resolvers.spray,
      libraryDependencies ++=
        Dependencies.coreBundle ++
        Dependencies.json ++
        Dependencies.slick ++
        Dependencies.spray ++
        Dependencies.spraySwagger
    )
  ).dependsOn(
      commonProject % "test->test;compile->compile",
      entityProject % "test->test;compile->compile")

  /**
   * Socket API
   */
  lazy val socketProject = Project(
    id = Names.socket,
    base = file(Dirs.socket),
    settings = baseSettings ++ Seq(
      name := Names.socket,
      libraryDependencies ++=
        Dependencies.coreBundle ++
        Dependencies.json ++
        Dependencies.netty
    )
  ).dependsOn(
      commonProject % "test->test;compile->compile",
      entityProject % "test->test;compile->compile")

  /**
   * Server to run REST and/or Socket APIs
   */
  lazy val serverProject = Project(
    id = Names.server,
    base = file(Dirs.server),
    settings = baseSettings ++ Seq(
      name := Names.server,
      libraryDependencies ++=
        Dependencies.coreBundle
    )
  ).dependsOn(
      commonProject % "test->test;compile->compile",
      entityProject % "test->test;compile->compile",
      restProject % "test->test;compile->compile",
      socketProject % "test->test;compile->compile")

  /**
   * Top level project (integrates all sub-projects, builds fatjar, etc.)
   */
  lazy val integrationProject = Project(
    id = Names.top,
    base = file(Dirs.top),
    settings = baseSettings ++ Seq(
      name := Names.top,
      mainClass := Some("base.server.Server"),
      AssemblyKeys.jarName in AssemblyKeys.assembly := Names.top + "-" + ("git describe --always" !!).trim + ".jar"
    )
  ).dependsOn(
      // the reason for all the dependencies is to get scalatest to run tests from every sub-project (should be a way around this but I haven't looked into it yet)
      commonProject % "test->test;compile->compile",
      entityProject % "test->test;compile->compile",
      restProject % "test->test;compile->compile",
      socketProject % "test->test;compile->compile",
      serverProject % "test->test;compile->compile")
    .aggregate(commonProject, entityProject, restProject, socketProject, serverProject)

  // code formatting preferences
  lazy val scalariformPreferences = FormattingPreferences()
    .setPreference(AlignParameters, true)
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(DoubleIndentClassDeclaration, true)
    .setPreference(PreserveDanglingCloseParenthesis, true)
    .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)

  // To make the logger work in tests
  lazy val standardTestOptions = Tests.Setup(cl =>
    cl.loadClass("org.slf4j.LoggerFactory")
      .getMethod("getLogger", cl.loadClass("java.lang.String"))
      .invoke(null, "ROOT"))

  lazy val baseSettings =
    Defaults.coreDefaultSettings ++
    SbtScalariform.scalariformSettings ++
    sbtassembly.Plugin.assemblySettings ++
    ScoverageSbtPlugin.instrumentSettings ++
    ScalastylePlugin.projectSettings ++
    net.virtualvoid.sbt.graph.Plugin.graphSettings ++ Seq(
      scalaVersion := "2.11.2",
      organization := "base",
      autoCompilerPlugins := true,
      scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:implicitConversions"),
      ScalariformKeys.preferences := scalariformPreferences,
      ScalastylePlugin.scalastyleFailOnError := true,
      (compile in Compile) <<= (compile in Compile) dependsOn (ScalastylePlugin.scalastyle in Compile).toTask(""),
      (test in Test) <<= (test in Test) dependsOn (ScalastylePlugin.scalastyle in Test).toTask(""),
      (products in Test) <<= (products in Test) dependsOn (ScalastylePlugin.scalastyle in Test).toTask(""),
      ScoverageKeys.minimumCoverage := 80,
      ScoverageKeys.highlighting := true,
      resolvers ++= Dependencies.Resolvers.standard,
      testOptions in Test += standardTestOptions,
      parallelExecution in Test := false,
      parallelExecution in ScoverageSbtPlugin.ScoverageTest := false,
      parallelExecution := false
    )

  /**
   * Task to generate Slick code from the evolution files via gen-slick
   */
  lazy val slickTaskKey = TaskKey[Seq[File]]("gen-slick")
  lazy val slickTask = (dependencyClasspath in Compile, runner in Compile, streams) map { (cp, r, s) =>
    val outputDir = "project-entity/src/main/scala"
    toError(r.run("scala.slick.model.codegen.DatabaseCodeGenerator", cp.files, Array(outputDir), s.log))
    Seq(file(outputDir + "Tables.scala"))
  }

}