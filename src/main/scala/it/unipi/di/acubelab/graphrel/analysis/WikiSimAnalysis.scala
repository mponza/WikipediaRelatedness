package it.unipi.di.acubelab.graphrel.analysis

import java.io.File
import java.nio.file.Paths

import com.github.tototoshi.csv.CSVWriter
import it.unipi.di.acubelab.graphrel.analysis.bucket.{BucketAnalyzer, BucketAnalyzerFactory}
import it.unipi.di.acubelab.graphrel.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.graphrel.utils.Configuration
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

class WikiSimAnalysis(options: Map[String, Any]) {
  val logger = LoggerFactory.getLogger(classOf[WikiSimAnalysis])
  val analysisName = options.getOrElse("analysis", "Relatedness").toString

  def computeAnalysis() = {
    logger.info("Reading data.csv files...")
    val benchmarkPaths = benchmarkDataCSVPaths()
    logger.info("%d CSV datasets loaded!".format(benchmarkPaths.length))

    logger.info("Analyzing data...")
    val analyzers = computeAnalyzers(benchmarkPaths)

    logger.info("Storing analyzed data...")
    storeAnalyzers(analyzers, analysisPath())
  }

  def storeAnalyzers(analyzers: List[BucketAnalyzer], path: String) = {
    new File(path).mkdirs()

    val buckets = analyzers(0).buckets
    for((bucket, index) <- buckets.zipWithIndex) {

      // Number of elements in the index-th bucket of bucketTasks.
      val size = analyzers(0).bucketTasks.get(index).size

      val bucketPath = Paths.get(path, "bucket_%1.2f-%1.2f_size-%d.csv"
        .format(bucket._1, bucket._2, size)).toString

      val csvWriter = CSVWriter.open(bucketPath)

      csvWriter.writeRow(List("Method", "Pearson", "Spearman"))
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
  def computeAnalyzers(benchmarkPaths: List[String]): List[BucketAnalyzer] = {
    val analyzers = ListBuffer.empty[BucketAnalyzer]

    benchmarkPaths.foreach {
      path =>
        val wikiSimDataset = new WikiSimDataset(path)
        val relName = relatednessName(path)

        val analyzer = BucketAnalyzerFactory.make(analysisName, relName, wikiSimDataset)
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
    Paths.get(Configuration.analysis("correlation"), analysisName).toString
  }
}
