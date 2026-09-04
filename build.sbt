lazy val cafesat = taskKey[File]("Create the main run script")

lazy val runnerScriptTemplate = 
"""#!/bin/sh
java -classpath "%s" %s "$@"
"""

cafesat := {
  val cp = (Runtime / fullClasspath).value
  val mainClass = "cafesat.Main"
  val contents = runnerScriptTemplate.format(cp.files.absString, mainClass)
  val out = target.value / "cafesat"
  IO.write(out, contents)
  out.setExecutable(true)
  out
}

lazy val root = (project in file(".")).
  settings(
    name := "CafeSat",
    version := "0.01",
    scalaVersion := "2.13.18",
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),

    IntegrationTest / javaOptions ++= Seq("-Xss10M"),
    IntegrationTest / fork := true,
    IntegrationTest / logBuffered := false,
    Test / parallelExecution := true,

    libraryDependencies += "com.regblanc" %% "scala-smtlib" % "0.2.1-42-gc68dbaa",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.20" % "test,it"
  ).
  configs( IntegrationTest ).
  settings( Defaults.itSettings : _*)
