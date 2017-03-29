package it.unipi.di.acubelab.wikipediarelatedness.server.utils

import com.twitter.finagle.http.{Request, Response, Status}
import it.unipi.di.acubelab.wikipediarelatedness.dataset.{WikiEntity, WikiRelateTask}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping.WikiTitleID
import org.json4s._
import org.json4s.jackson.JsonMethods._

// json response -> WikiRelateTask
case class WikiIDs(srcWikiID: Int, dstWikiID: Int)
case class WikiTitles(srcWikiTitle: String, dstWikiTitle: String)

// WikiRelateTask -> json response
case class WikiRelateResponse(srcWikID: Int, dstWikiID: Int, srcWikiTitle: String, dstWikiTile: String, relatedndess: Float)


/**
  * Parse response to WikiRelateTask and viceversa.
  *
  */
object ResponseParsing {

  /**
    * Parses request and returns the corresponding WikiRelateTask.
    *
    * @param request
    * @return
    */
  def apply(request: Request) : WikiRelateTask = {
    implicit val formats = DefaultFormats

    val content = request.getContentString()
    val json = parse(content)


    // wikiIDs
    if (content.contains("srcWikiID") && content.contains("dstWikiID")) {

        val wikiIDs = json.extract[WikiIDs]
        val srcWikiEntity = param2WikiEntity(wikiIDs.srcWikiID)
        val dstWikiEntity = param2WikiEntity(wikiIDs.dstWikiID)

        return new WikiRelateTask(srcWikiEntity, dstWikiEntity, -1f)


    // wikiTitles
    } else if(content.contains("srcWikiTitle") && content.contains("dstWikiTitle")) {
      val wikiTitles = json.extract[WikiTitles]
      val srcWikiEntity = param2WikiEntity(wikiTitles.srcWikiTitle)
      val dstWikiEntity = param2WikiEntity(wikiTitles.dstWikiTitle)

      return new WikiRelateTask(srcWikiEntity, dstWikiEntity, -1f)
    }


    // wrong json
    throw new IllegalArgumentException("{src, dst}{WikiID, WikiTitle} parameters not present in the json request.")
  }

  protected def param2WikiEntity(wikiID: Int) = new WikiEntity(wikiID, WikiTitleID.map(wikiID))
  protected def param2WikiEntity(wikiTitle: String) = new WikiEntity(WikiTitleID.map(wikiTitle), wikiTitle)



  def apply(wikiRelateTask: WikiRelateTask) : Response = {
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
