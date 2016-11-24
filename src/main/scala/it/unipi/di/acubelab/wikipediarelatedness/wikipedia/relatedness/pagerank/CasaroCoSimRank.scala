package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.pagerank

import it.unipi.di.acubelab.wikipediarelatedness.options.CasaroCoSimRankOptions
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory

import scala.util.parsing.json.JSON
import scalaj.http.Http

class CasaroCoSimRank(options: CasaroCoSimRankOptions) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[CasaroCoSimRank])

  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    val params = Seq("src" -> srcWikiID.toString, "dst" -> dstWikiID.toString)

    // Send query to Casaro CoSimRank
    try {
      val response = Http(Configuration.cosimrank).postForm(params).timeout(Int.MaxValue, Int.MaxValue).asString.body.toString

      JSON.parseFull(response) match {
        case Some(jsonMap: Map[String, Any] @unchecked) =>

          jsonMap("CoSimRank").toString.toFloat

        case _ => throw new IllegalArgumentException("Error while parsing CasaroCoSimRank response.")
      }
    } catch {
      case e: Exception =>
        logger.error("Error while computing relatedness between %d and %d".format(srcWikiID, dstWikiID))
        -1f
    }
  }


  override def toString() = "CasaroCoSimRank"
}

/*
* class CoSimRankRelatedness(options: CoSimRankOptions) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[CoSimRankRelatedness])

  val csr = new CoSimRankParallelGaussSeidel(WikiGraphFactory.inGraph, options.iterations, options.pprDecay, options.csrDecay)


  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    csr.similarity(srcWikiID, dstWikiID)
  }

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






  override def toString(): String = {
    "CoSimRank_%s".format(options)
  }
}
*
* */

