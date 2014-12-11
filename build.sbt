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
    scalaVersion := "2.11.4",
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),

    libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % Test
  ).
  dependsOn(scalaSmtLib)

lazy val scalaSmtLib = {
  val commit = "004fab30fc294677a14429fad2cd95ab4d366416"
  val githubLink = s"git://github.com/regb/scala-smtlib.git#$commit"
  RootProject(uri(githubLink))
}
