name := "WikipediaRelatedness"
version := "0.1"
scalaVersion := "2.12.6"
scalacOptions += "-target:jvm-1.8"
scalacOptions ++= Seq("-feature", "-deprecation")
fork in run := true

javaOptions in run += "-Xmx35G"
javaOptions in Test += "-Xmx35G"


libraryDependencies ++= Seq(
  "it.unimi.dsi" % "fastutil" % "8.1.1",
  "it.unimi.dsi" % "dsiutils" % "2.4.2",

  "com.jsoniter" % "jsoniter" % "0.9.22",

  "org.scalatest" %% "scalatest" % "3.0.5" % "test",

  "com.typesafe" % "config" % "1.3.3",

  "org.apache.commons" % "commons-compress" % "1.16.1",
  "org.apache.commons" % "commons-math3" % "3.6.1",

  "com.github.tototoshi" %% "scala-csv" % "1.3.5",

  "org.rogach" %% "scallop" % "3.1.2"
)