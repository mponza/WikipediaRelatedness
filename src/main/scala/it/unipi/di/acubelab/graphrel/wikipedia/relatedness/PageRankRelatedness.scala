package it.unipi.di.acubelab.graphrel.wikipedia.relatedness

import java.util.Locale

import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap
import it.unipi.di.acubelab.graphrel.utils.Configuration

import scala.util.parsing.json.JSON
import scalaj.http.Http

trait PageRankRelatedness extends Relatedness {
  // CoSimRank Library Parameters.
  val algorithm: String
  val iters: Int
  val decay: Double

  def getSimRequest(weightedEdges: List[(Int, Int, Double)], simPairs: List[(Int, Int)])
    : Object2DoubleArrayMap[(Int, Int)] = {

    // Parameter configuration.
    val simParams = Seq("graph" -> edgesToString(weightedEdges),
                        "algorithm" -> algorithm,
                        "iters" -> iters.toString,
                        "decay" -> "%1.3f".formatLocal(Locale.US, decay),
                        "pairs" -> simPairString(simParams)
                       )

    // Server request.
    val strResponse = Http(Configuration.cosimrank).postForm(simParams)
      .timeout(1000000000, 10000000).asString.body.toString

    // JSON response parsing
    val jsonResponse = JSON.parseFull(strResponse)
    jsonResponse match {
      case Some(jsonMap: Map[String, Any]@unchecked) =>

        if (jsonMap("Status") == "Error") {
          throw new IllegalArgumentException("Exception while parsing JSON: %s"
                                              .format(jsonMap("Message").toString))
        }

        jsonMap("Sims") match {
          case jsonSims: List[Any]@unchecked =>

            val simMap = new Object2DoubleArrayMap[(Int, Int)]

            for(sim <- jsonSims) {
              sim match {
                case jsonSim: Map[String, Any]@unchecked =>

                  val src = jsonSim("src").asInstanceOf[Int]
                  val dst = jsonSim("dst").asInstanceOf[Int]
                  val sim = jsonSim("sim").asInstanceOf[Double]

                  simMap.put((src, dst), sim)

                case _ => ;
              }
            }

            simMap

          case _ => ;
        }

      case _ => ;
    }

    throw new IllegalArgumentException("General error while parsing JSON response.")
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
}
