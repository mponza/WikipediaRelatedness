package it.unipi.di.acubelab.wikipediarelatedness.server.restful.services

import com.twitter.finagle.http.Method._
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future
import it.unipi.di.acubelab.wikipediarelatedness.server.restful.RestfulParameters
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping.WikiTitleID
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.TopKFactory
import org.slf4j.LoggerFactory

import org.json4s._
import org.json4s.jackson.JsonMethods._


class RankService extends RelService {

  override def logger = LoggerFactory.getLogger("WikiRank")

  lazy val esaTopK = TopKFactory.make("esa")


  override def apply(request: Request): Future[Response] = {

    request.method match {
      case Post =>


        val relParams = RestfulParameters.request2RelParameters4Rank(request)
        var rankedEntities = Array.empty[(Int, Float)]


        logger.info("Ranking entities for entity %d with method %s".format(relParams.srcWikiID, relParams.method))

        //
        // ESA-specific ranking
        if(relParams.method == "esa") {
          rankedEntities = esaTopK.topKScoredEntities(relParams.srcWikiID, 10000).filter(_ != relParams.srcWikiID).filter(_._2 != 0f).toArray
        }


        //
        // General-purpose relatedness ranking
        else {

          val rel = getRelatedness(relParams.method)

          val outEntities = wikiOutGraph.successorArray(relParams.srcWikiID).distinct.filter(_ != relParams.srcWikiID)
          rankedEntities = outEntities.map {
            case dstWikiID =>
              try {

                val wikiRelTask = getWikiRelTask(relParams.srcWikiID, dstWikiID)
                wikiRelTask.machineRelatedness = rel.computeRelatedness(wikiRelTask)

                (dstWikiID, wikiRelTask.machineRelatedness)

              } catch {
                case e: Exception => logger.warn("Error while computing relatedness between %d and %d"
                  .format(relParams.srcWikiID, dstWikiID))

                  (dstWikiID, 0.0f)
              }
          }.filter(_._2 != 0f).sortBy(-_._2)

        }


        //
        // JSON response
        Future.apply(rankedEntities2Response(relParams.srcWikiID, relParams.method, rankedEntities) )
    }

  }


  protected def rankedEntities2Response(srcWikiID:Int, relName: String, rankedEntities: Array[(Int, Float)]) : Response = {
    import org.json4s.JsonDSL._

    val mappedRankedEntities = rankedEntities.map(scoredEntity =>
                                                          ("dstWikiID" -> scoredEntity._1) ~
                                                          ("dstWikiTitle" -> WikiTitleID.map(scoredEntity._1)) ~
                                                          ("relatedness" -> scoredEntity._2)
                                                  ).toList

    val json = ("srcWikiID" -> srcWikiID) ~ ("rankedEntities" -> mappedRankedEntities) ~ ("method" -> relName)

    val strJson = compact( render(json) )

    val response = Response(Status.Ok)
    response.contentString = strJson
    response
  }

}
