package it.unipi.di.acubelab.wikipediarelatedness.server.restful.services

import com.twitter.finagle.Service
import com.twitter.finagle.http.Method.Post
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future
import it.unipi.di.acubelab.wikipediarelatedness.dataset.{WikiEntity, WikiRelateTask}
import it.unipi.di.acubelab.wikipediarelatedness.server.restful.RestfulParameters
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping.WikiTitleID
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessFactory, RelatednessOptions}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.slf4j.LoggerFactory


class RelService extends Service[Request, Response] {

  def logger = LoggerFactory.getLogger("RelService")

  lazy val wikiOutGraph = WikiBVGraphFactory.make("un.out")

  lazy val jacc = RelatednessFactory.make(new RelatednessOptions(name="uncom.jacc", graph="in"))
  lazy val mw = RelatednessFactory.make(new RelatednessOptions(name="uncom.mw", graph="in"))

  lazy val mwFastAlgoScheme = RelatednessFactory.make(new RelatednessOptions(name="algo:uncom.mw"))
  lazy val mwDWFastAlgoScheme = RelatednessFactory.make(new RelatednessOptions(name="algo:uncom.mw+uncom.dw"))

  // lazy val esa = RelatednessFactory.make(new RelatednessOptions(name="esa"))


  def apply(request: Request): Future[Response] = {

    request.method match {
      case Post =>

        val relParams = RestfulParameters.request2RelParameters(request)


        val rel = getRelatedness(relParams.method)
        val wikiRelTask = getWikiRelTask(relParams.srcWikiID, relParams.dstWikiID)

        try {

          wikiRelTask.machineRelatedness = rel.computeRelatedness(wikiRelTask)

        } catch {
          case e: Exception =>

            if(!wikiOutGraph.contains(relParams.srcWikiID)) logger.warn("%d not in Wikipedia".format(relParams.srcWikiID))
            if(!wikiOutGraph.contains(relParams.dstWikiID)) logger.warn("%d not in Wikipedia".format(relParams.dstWikiID))

            wikiRelTask.machineRelatedness = 0f
        }

        logger.info("Relatedness [%s] between %s and %s is %1.2f".format(relParams.method, wikiRelTask.src, wikiRelTask.dst, wikiRelTask.machineRelatedness))

        Future.apply(wikiRelTask2Response(wikiRelTask))
    }

  }


  protected def getRelatedness(name: String) : Relatedness = name match {
    case "jaccard" => jacc
    case "milnewitten" => mw

    case "2stage-mw" => mwFastAlgoScheme
    case "2stage-mwdw" => mwDWFastAlgoScheme
  }


  protected def getWikiRelTask(srcWikiID: Int, dstWikiID: Int) = {
    val srcEntity =  new WikiEntity(srcWikiID, WikiTitleID.map(srcWikiID))
    val dstEntity = new WikiEntity(dstWikiID, WikiTitleID.map(dstWikiID))

    new WikiRelateTask(srcEntity, dstEntity, -1f)
  }


  protected def wikiRelTask2Response(wikiRelateTask: WikiRelateTask) : Response = {
    import org.json4s.JsonDSL._

    val json = ("srcWikiID" -> wikiRelateTask.src.wikiID) ~ ("dstWikiID" -> wikiRelateTask.dst.wikiID) ~
      ("srcWikiTitle" -> wikiRelateTask.src.wikiTitle) ~ ("dstWikiTitle" -> wikiRelateTask.dst.wikiTitle) ~
      ("relatedness" -> wikiRelateTask.machineRelatedness)

    val strJson = compact( render(json) )

    val response = Response(Status.Ok)
    response.contentString = strJson
    response
  }

}
