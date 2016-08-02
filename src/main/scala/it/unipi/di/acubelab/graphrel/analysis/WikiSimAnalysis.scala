package it.unipi.di.acubelab.graphrel.analysis

import java.io.File
import java.nio.file.Paths

import com.github.tototoshi.csv.CSVWriter
import it.unipi.di.acubelab.graphrel.analysis.bucket.BucketRelAnalyzer
import it.unipi.di.acubelab.graphrel.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.graphrel.utils.Configuration
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

class WikiSimAnalysis(analysisName: String = "BucketRelatedness") {
  val logger = LoggerFactory.getLogger(classOf[WikiSimAnalysis])

  def computeAnalysis() = {
    logger.info("Reading data.csv files...")
    val benchmarkPaths = benchmarkDataCSVPaths()
    logger.info("%d CSV datasets loaded!".format(benchmarkPaths.length))

    logger.info("Analyzing data...")
    val analyzers = computeAnalyzers(benchmarkPaths)

    logger.info("Storing analyzed data...")
    storeAnalyzers(analyzers, analysisPath())
  }

  def storeAnalyzers(analyzers: List[BucketRelAnalyzer], path: String) = {
    new File(path).mkdirs()

    val buckets = analyzers(0).buckets
    for((bucket, index) <- buckets.zipWithIndex) {

      val bucketPath = Paths.get(path, "bucket_%1.2f-%1.2f.csv".format(bucket._1, bucket._2)).toString
      val csvWriter = CSVWriter.open(bucketPath)

      analyzers.foreach {
        case bucketAnalyzer => csvWriter.writeRow(bucketAnalyzer.toCSVList(index))
      }

      csvWriter.close
    }
  }

  /**
    *
    * @param benchmarkPaths
    * @return One analyzer per relatedness function.
    */
  def computeAnalyzers(benchmarkPaths: List[String]): List[BucketRelAnalyzer] = {
    val analyzers = ListBuffer.empty[BucketRelAnalyzer]

    benchmarkPaths.foreach {
      path =>
        val wikiSimDataset = new WikiSimDataset(path)
        val relName = relatednessName(path)

        val analyzer = new BucketRelAnalyzer(relName, wikiSimDataset)
        analyzers.append(analyzer)
    }

    analyzers.toList
  }

  def benchmarkDataCSVPaths(): List[String] = {
    val dirs = new File(Configuration.benchmark).listFiles.filter(_.isDirectory)

    // Get all absolute path of the *.data.csv file of each benchmark directory
    val paths = dirs.map(_.listFiles().filter(_.getName.endsWith(".data.csv"))(0).getAbsolutePath)
    paths.toList.sortWith(_ < _)
  }

  def relatednessName(path: String): String  = {
    path.slice(path.lastIndexOf("/") + 1, path.lastIndexOf(".data.csv")).toString
  }

  def analysisPath(): String = {
    Paths.get(Configuration.analysis, analysisName).toString
  }
}
