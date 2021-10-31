lazy val cafesat = taskKey[File]("Create the main run script")

lazy val runnerScriptTemplate = 
"""#!/bin/sh
java -classpath "%s" %s "$@"
"""

cafesat := {
  val cp = (fullClasspath in Runtime).value
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
    scalaVersion := "2.13.1",
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),

    javaOptions in IntegrationTest ++= Seq("-Xss10M"),
    fork in IntegrationTest := true,
    logBuffered in IntegrationTest := false,
    parallelExecution in Test := true,

    libraryDependencies += "com.regblanc" %% "scala-smtlib" % "0.2.1-42-gc68dbaa",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "test,it"
  ).
  configs( IntegrationTest ).
  settings( Defaults.itSettings : _*)
