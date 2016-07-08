name := "GraphRel"

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions += "-target:jvm-1.8"

libraryDependencies ++= Seq(
  "com.github.tototoshi" %% "scala-csv" % "1.3.3",
  "it.unimi.dsi" % "webgraph" % "3.5.2",
  "it.unimi.dsi" % "fastutil" % "7.0.12"
)