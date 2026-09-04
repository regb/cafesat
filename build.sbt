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

lazy val commonSettings = Seq(
  version := "0.01",
  scalaVersion := "2.13.18",
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
  crossScalaVersions := Seq("2.13.18", "3.3.8")
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    name := "CafeSat",

    Test / parallelExecution := true,

    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.20" % Test
  )

lazy val it = (project in file("it"))
  .dependsOn(root)
  .settings(commonSettings)
  .settings(
    name := "CafeSat-it",

    // Stick to the original src/it directory layout.
    Test / scalaSource := baseDirectory.value / ".." / "src" / "it" / "scala",
    Test / resourceDirectory := baseDirectory.value / ".." / "src" / "it" / "resources",

    Test / javaOptions += "-Xss10M",
    Test / fork := true,
    Test / logBuffered := false,

    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.20" % Test
  )
