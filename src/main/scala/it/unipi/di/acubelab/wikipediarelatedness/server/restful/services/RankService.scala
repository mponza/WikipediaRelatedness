package it.unipi.di.acubelab.wikipediarelatedness.server.restful.services

import com.twitter.finagle.http.Method._
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.server.restful.RestfulParameters
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.json4s.jackson.JsonMethods._
import org.slf4j.LoggerFactory

class RankService extends RelService(port=8000) {

  val logger = LoggerFactory.getLogger("WikiRank")
  lazy val wikiOutGraph = WikiBVGraphFactory.make("un.out")


  override def apply(request: Request): Future[Response] = {

    request.method match {
      case Post =>

        val relParams = RestfulParameters.request2RelParameters(request)

        val rel = getRelatedness(relParams.method)

        val outEntities = wikiOutGraph.successorArray(relParams.srcWikiID).distinct.filter(_ != relParams.srcWikiID)
        val rankedEntities = outEntities.map {
          case dstWikiID =>
            try {

              val wikiRelTask = getWikiRelTask(relParams.srcWikiID, dstWikiID)
              rel.computeRelatedness(wikiRelTask)

              (dstWikiID, wikiRelTask.machineRelatedness)

            } catch {
              case e: Exception => logger.warn("Error while computing relatedness between %d and %d"
                .format(relParams.srcWikiID, dstWikiID))

                (dstWikiID, 0.0f)
          }
        }.sortBy(-_._2)


        Future.apply(rankedEntities2Response(relParams.srcWikiID, relParams.method, rankedEntities) )
    }

  }


  protected def rankedEntities2Response(srcWikiID:Int, relName: String, rankedEntities: Array[(Int, Float)]) : Response = {
    import org.json4s.JsonDSL._

    val mappedRankedEntities = rankedEntities.map(scoredEntity => ("dstWikiID" -> scoredEntity._1) ~ ("relatedness" -> scoredEntity._2) )
    val json = ("srcWikiID" -> srcWikiID) ~ ("rankedEntities" -> mappedRankedEntities) ~ ("method" -> relName)

    val strJson = compact( render(json) )

    val response = Response(Status.Ok)
    response.contentString = strJson
    response
  }

}
