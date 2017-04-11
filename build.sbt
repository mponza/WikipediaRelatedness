name := "WikipediaRelatedness"

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions += "-target:jvm-1.8"

scalacOptions ++= Seq("-feature", "-deprecation")


//scalacOptions ++= Seq("-country", "us")
//scalacOptions ++= Seq("-language", "us")


fork in run := true
fork in Test := true

javaOptions in run += "-Xmx60G"
javaOptions in Test += "-Xmx60G"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.1",
  "com.github.tototoshi" %% "scala-csv" % "1.3.3",
  "com.github.scopt" %% "scopt" % "3.5.0",
  "org.scalaj" % "scalaj-http_2.11" % "2.3.0",


  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "com.google.guava" % "guava" % "19.0",

  "it.unimi.dsi" % "webgraph" % "3.5.2",
  "it.unimi.dsi" % "fastutil" % "7.0.12",
  "it.unimi.dsi" % "sux4j" % "4.0.0",

  "net.sf.jung" % "jung2" % "2.0.1",
  "net.sf.jung" % "jung-api" % "2.0.1",
  "net.sf.jung" % "jung-graph-impl" % "2.0.1",
  "net.sf.jung" % "jung-algorithms" % "2.0.1",

  "edu.berkeley.nlp" % "berkeleylm" % "1.1.2",

  "org.apache.commons" % "commons-math3" % "3.6.1",

  "org.deeplearning4j" % "deeplearning4j-core" % "0.7.1",
  "org.deeplearning4j" % "deeplearning4j-nlp" % "0.7.1",
  "org.deeplearning4j" % "deeplearning4j-graph" % "0.7.1",
  "org.nd4j" % "nd4j-native" % "0.7.1" classifier "" classifier "macosx-x86_64",
  "org.nd4j" % "nd4j-native" % "0.7.1" classifier "" classifier "linux-x86_64",
  "org.bytedeco" % "javacpp" % "1.2.3",

  "org.apache.lucene" % "lucene-core" % "6.1.0",
  "org.apache.lucene" % "lucene-queryparser" % "6.1.0",
  "org.apache.lucene" % "lucene-analyzers-common" % "6.1.0",

  "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0" classifier "models",

  "org.scalatest" % "scalatest_2.11" % "3.0.0",

  "gov.nist.math" % "jama" % "1.0.3",

  "com.twitter" %% "finagle-core" % "6.41.0",
  "com.twitter" %% "finagle-stats" % "6.41.0",
  "com.twitter" % "twitter-server_2.11" % "1.28.0",

  "com.madhukaraphatak" % "java-sizeof_2.11" % "0.1",

  "org.json4s" % "json4s-native_2.11" % "3.5.1",
  "org.json4s" % "json4s-jackson_2.11" % "3.5.1"




)

// http://www.scala-sbt.org/0.13.5/docs/Detailed-Topics/Library-Management.html
unmanagedJars in Compile ++= {
  val base = baseDirectory.value / "lib"
  val baseDirectories = (base / "law-2.3") +++ (base / "law-deps") +++ (base / "triangles")
  val customJars = (baseDirectories ** "*.jar")
  customJars.classpath
}

mainClass in (Compile, run) := Some("it.unipi.di.acubelab.wikipediarelatedness.Main")