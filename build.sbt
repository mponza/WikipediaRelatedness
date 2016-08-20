name := "WikipediaRelatedness"

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions += "-target:jvm-1.8"

scalacOptions ++= Seq("-feature")

fork in run := true

javaOptions in run += "-Xmx2G"

libraryDependencies ++= Seq(
  "com.github.tototoshi" %% "scala-csv" % "1.3.3",
  "org.scalaj" % "scalaj-http_2.11" % "2.3.0",

  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "com.google.guava" % "guava" % "19.0",

  "it.unimi.dsi" % "webgraph" % "3.5.2",
  "it.unimi.dsi" % "fastutil" % "7.0.12",
  "it.unimi.dsi" % "sux4j" % "4.0.0",

  "edu.berkeley.nlp" % "berkeleylm" % "1.1.2",

  "org.apache.commons" % "commons-math3" % "3.6.1",

  "org.deeplearning4j" % "deeplearning4j-core" % "0.4-rc3.10",
  "org.deeplearning4j" % "deeplearning4j-nlp" % "0.4-rc3.10",
  "org.deeplearning4j" % "deeplearning4j-graph" % "0.4-rc3.10",
  "org.nd4j" % "nd4j-native" % "0.4-rc3.10" classifier "" classifier "macosx-x86_64",
  "org.nd4j" % "nd4j-native" % "0.4-rc3.10" classifier "" classifier "linux-x86_64",
  "org.bytedeco" % "javacpp" % "1.2.3",

  "org.apache.lucene" % "lucene-core" % "6.1.0",
  "org.apache.lucene" % "lucene-queryparser" % "6.1.0",
  "org.apache.lucene" % "lucene-analyzers-common" % "6.1.0"
)

// http://www.scala-sbt.org/0.13.5/docs/Detailed-Topics/Library-Management.html
unmanagedJars in Compile ++= {
  val base = baseDirectory.value / "lib"
  val baseDirectories = (base / "law-2.3") +++ (base / "law-deps")
  val customJars = (baseDirectories ** "*.jar")
  customJars.classpath
}