package it.unipi.di.acubelab.wikipediarelatedness.server.restful.services

import com.twitter.finagle.Service
import com.twitter.finagle.http.Method.Post
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future
import it.unipi.di.acubelab.wikipediarelatedness.dataset.{WikiEntity, WikiRelateTask}
import it.unipi.di.acubelab.wikipediarelatedness.server.restful.RestfulParameters
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping.WikiTitleID
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessFactory, RelatednessOptions}
import org.json4s.jackson.JsonMethods._


class RelService(val port: Int = 7000) extends Service[Request, Response] {

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
        val wikiRelTask = getWikiRelTask( relParams.srcWikiID, relParams.dstWikiID )
        rel.computeRelatedness(wikiRelTask)

        Future.apply(wikiRelTask2Response(wikiRelTask) )
    }

  }


  protected def getRelatedness(name: String) : Relatedness = name match {
    case "jaccard" => jacc
    case "milnewitten" => mw

    case "2Stage-MW" => mwFastAlgoScheme
    case "2Stage-MWDW" => mwDWFastAlgoScheme

      // esa?
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
