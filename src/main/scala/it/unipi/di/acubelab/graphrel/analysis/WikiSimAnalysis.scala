package it.unipi.di.acubelab.graphrel.analysis

import java.io.File
import java.nio.file.Paths

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unipi.di.acubelab.graphrel.analysis.wikisim.WikiBenchDataset
import it.unipi.di.acubelab.graphrel.utils.Configuration

import scala.collection.mutable.ListBuffer

class WikiSimAnalysis {
  // One analyzer per file (relatedness function)
  val analyzers = ListBuffer[BucketRelAnalyzer]

  def computeAnalysis() = {
    val benchmarkPaths = benchmarkDataCSVPaths()

    // Generate one analyzer for each file
    benchmarkPaths.foreach {
      path =>
        val wikiSimBenchmark = new WikiBenchDataset(path)

        val relName = relatednessName(path)
        val analyzer = new BucketRelAnalyzer(relName)

        wikiSimBenchmark.wikiSimPairs.foreach {
          wikiBenchTask => Int2ObjectOpenHashMap[ObjectArrayList]
        }
    }


  }

  def benchmarkDataCSVPaths(): List[String] = {
    val dirs = new File(Configuration.benchmark).listFiles.filter(_.isDirectory)

    // Get all absolute path of the *.data.csv file of each benchmark directory
    val paths = dirs.map(_.listFiles().filter(_.getName.endsWith(".data.csv"))(0).getAbsolutePath)
    paths.toList
  }

  def relatednessName(path: String): String  ={
    Paths.get(path).getFileName.toString.slice(path.lastIndexOf("/") + 1, path.lastIndexOf("."))
  }
}
