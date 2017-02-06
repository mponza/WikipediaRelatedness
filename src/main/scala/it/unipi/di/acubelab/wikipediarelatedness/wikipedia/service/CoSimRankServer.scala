package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.service

import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import org.slf4j.LoggerFactory

import scala.util.parsing.json.JSON
import scalaj.http.Http

object CoSimRankServer {
  protected val logger = LoggerFactory.getLogger("CoSimRankServer")

  protected val url = Config.getString("service.cosimrank")

  /**
    * Computes the similarity between srcWikiID and dstWikiID by running CoSimRank over the whole Wikipedia graph.
    * This function uses a RESTful server which wraps the native C++ implementation of CoSimRank.
    *
    * The returned similarities are respectively CoSimRank and PPR+Cos.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @param iterations
    * @param decay
    * @return
    */
  def similarities(srcWikiID: Int, dstWikiID: Int, iterations: Int = 30, decay: Float = 0.8f) : (Float, Float) = {
    val arguments = Seq( ("src", srcWikiID.toString), ("dst", dstWikiID.toString),
                       ("iterations", iterations.toString), ("decay", decay.toString)
                    )

    val strResponse = Http(Config.getString("service.cosimrank")).params(arguments)
                                .timeout(1000000000, 10000000).asString.body.toString
    val jsonResponse = JSON.parseFull(strResponse)


    jsonResponse match {
      case Some(jsonMap: Map[String, Any] @unchecked) =>

        val cosimrank = jsonMap.get("CoSimRank").asInstanceOf[Double].toFloat
        val pprcos = jsonMap.get("PPRCos").asInstanceOf[Double].toFloat

        return (cosimrank, pprcos)

      case _ => ;
    }

    throw new IllegalArgumentException("Error parsing JSON response with src %d and dst %d".format(srcWikiID, dstWikiID))
  }

}
