package it.unipi.di.acubelab.wikipediarelatedness.analysis

import java.io.File
import java.nio.file.Paths
import java.util.Locale

import com.github.tototoshi.csv.CSVWriter
import it.unipi.di.acubelab.wikipediarelatedness.analysis.bucket.{BucketAnalyzer, BucketAnalyzerFactory}
import it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

/**
  * TODO: Find time to refactor analsyis. Too many changes during time.
  *
  *   {
  *     "analysis": "Relatedness,inDegree,outDegree,inDistance,outDistance",
  *     "eval":"correlation,classification"
  *   }
  */
class WikiSimAnalysis(options: Map[String, Any]) {
  Locale.setDefault(Locale.US)

  val logger = LoggerFactory.getLogger(classOf[WikiSimAnalysis])

  val analysisNames = options.getOrElse("analysis", "Relatedness").toString.split(",")
  val evalNames = options.getOrElse("eval", "correlation").toString.split(",")

  def computeAnalysis() = {
    logger.info("Reading data.csv files...")
    val benchmarkPaths = benchmarkDataCSVPaths()
    logger.info("%d CSV datasets loaded!".format(benchmarkPaths.length))

    logger.info("Analyzing data...")

    for {
      analysis <- analysisNames
      eval <- evalNames
    } {
        val analyzers = computeAnalyzers(analysis, eval, benchmarkPaths)

        logger.info("Storing analyzed data...")
        storeAnalyzers(analyzers, analysisPath(analysis, eval))
    }
  }

  def storeAnalyzers(analyzers: List[BucketAnalyzer], path: String) = {
    new File(path).mkdirs()

    val buckets = analyzers(0).buckets
    val columnNames = "Method" +: analyzers(0).wikiSimPerformance(0).csvFields()

    for((bucket, index) <- buckets.zipWithIndex) {

      if(analyzers(0).bucketTasks.contains(index)) {

        // Number of elements in the index-th bucket of bucketTasks.
        val size = analyzers(0).bucketTasks(index).size

        var bucketPath = ""
        if (path.contains("Rank") || path.contains("Jaccard")) {
          bucketPath = Paths.get(path, "bucket_%1.8f-%1.8f_size-%d.csv"
            .format(bucket._1, bucket._2, size)).toString
        } else {
          bucketPath = Paths.get(path, "bucket_%1.2f-%1.2f_size-%d.csv"
            .format(bucket._1, bucket._2, size)).toString
        }

        val csvWriter = CSVWriter.open(bucketPath)

        csvWriter.writeRow(columnNames)

        analyzers.foreach {
          case bucketAnalyzer =>
            csvWriter.writeRow(bucketAnalyzer.bucketToCSVRow(index))
        }

        csvWriter.close
      }
    }
  }

  /**
    *
    * @param benchmarkPaths
    * @return One analyzer for each relatedness function.
    */
  def computeAnalyzers(analysisName: String, evalName: String, benchmarkPaths: List[String]): List[BucketAnalyzer] = {
    val analyzers = ListBuffer.empty[BucketAnalyzer]

    benchmarkPaths.foreach {
      path =>
        val wikiSimDataset = new WikiSimDataset(path)
        val relName = relatednessName(path)

        val analyzer = BucketAnalyzerFactory.make(analysisName, evalName, relName, wikiSimDataset)
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

  def analysisPath(analysis: String, eval: String): String = {
    Paths.get(Configuration.analysis(eval), analysis).toString
  }
}