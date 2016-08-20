package it.unipi.di.acubelab.wikipediarelatedness.utils

import java.util.Locale

import it.unimi.dsi.fastutil.objects.{Object2DoubleArrayMap, ObjectArrayList}

import scala.util.parsing.json.JSON
import scalaj.http.Http

class CoSimRank(val algorithm: String, val iters: Int, val decay: Double) {

  def computeSimilarity(weightedEdges: List[(Int, Int, Double)], srcWikiID: Int, dstWikiID: Int)
    : Double = {
    computeSimilarity(weightedEdges, List((srcWikiID, dstWikiID))).getDouble((srcWikiID, dstWikiID))
  }

  def computeSimilarity(weightedEdges: List[(Int, Int, Double)], simPairs: List[(Int, Int)])
    : Object2DoubleArrayMap[(Int, Int)] = {

    // Parameter configuration.
    val simParams = Seq("graph" -> edgesToString(weightedEdges),
                        "algorithm" -> algorithm.toString,
                        "iters" -> iters.toString,
                        "decay" -> "%1.3f".formatLocal(Locale.US, decay),
                        "pairs" -> simPairString(simPairs)
                       )

    // Server request.
    val strResponse = Http(Configuration.cosimrank).postForm(simParams)
      .timeout(1000000000, 10000000).asString.body.toString

    // JSON response parsing
    val jsonResponse = JSON.parseFull(strResponse)
    jsonResponse match {
      case Some(jsonMap: Map[String, Any]@unchecked) =>

        if (jsonMap("Status") == "Error") {
          val message = jsonMap("Message").toString

          if (message.startsWith("Graph too big.")) {
            throw new IllegalArgumentException(message)
          } else {
            throw new RuntimeException(message)
          }
        }

        jsonMap("Sims") match {
          case jsonSims: List[Any]@unchecked =>

            val simMap = new Object2DoubleArrayMap[(Int, Int)]

            for(sim <- jsonSims) {
              sim match {
                case jsonSim: Map[String, Any] @unchecked =>

                  val src = jsonSim("src").asInstanceOf[Double].toInt
                  val dst = jsonSim("dst").asInstanceOf[Double].toInt
                  val sim = jsonSim("sim").asInstanceOf[Double]

                  simMap.put((src, dst), sim)

                case _ => ;
              }
            }

            return simMap

          case _ => ;
        }

      case _ => ;
    }

    throw new RuntimeException("General error while parsing JSON response: %s".format(jsonResponse))
  }

  def edgesToString(weightedEdges: List[(Int, Int, Double)]) : String = {
    val jsonObjs = weightedEdges.map {
      edge => """{"src": %d, "dst": %d, "weight": %1.3f}"""
        .formatLocal(Locale.US, edge._1, edge._2, edge._3)
    }.mkString(", ")

    "[%s]".format(jsonObjs)
  }

  def simPairString(simPairs: List[(Int, Int)]) : String = {
    val jsonObjs = simPairs.map {
      pair => """{"src": %d, "dst": %d}""".format(pair._1, pair._2)
    }.mkString(", ")

    "[%s]".format(jsonObjs)
  }

  override def toString: String = {
    "%s-iters_%d-decay_%1.3f".formatLocal(Locale.US, algorithm, iters, decay)
  }
}

object CoSimRank {
  def make(options: Map[String, Any]) : CoSimRank = {
    val algorithm: String = options("relatedness").toString
    val iters = if (options.contains("iters")) options("iters").asInstanceOf[Double].toInt else 5
    val decay = if (options.contains("decay")) options("decay").asInstanceOf[Double] else 0.8

    new CoSimRank(algorithm, iters, decay)
  }
}
