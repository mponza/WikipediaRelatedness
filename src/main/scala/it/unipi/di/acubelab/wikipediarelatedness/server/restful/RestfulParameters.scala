package it.unipi.di.acubelab.wikipediarelatedness.server.restful

import com.twitter.finagle.http.Request
import it.unipi.di.acubelab.wikipediarelatedness.dataset.{WikiEntity, WikiRelateTask}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping.WikiTitleID
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.slf4j.LoggerFactory


case class RelParameters(srcWikiID: Int, dstWikiID: Int = -1, method: String = "")
case class TextParameters(text: String, method: String = "esa")


object RestfulParameters {

  val logger = LoggerFactory.getLogger("RestfulParam")

  def request2RelParameters(request: Request) : RelParameters = {
    /*implicit val formats = DefaultFormats

    val content = request.getContentString()
    val json = parse(content)

    if (content.contains("srcWikiID") && content.contains("dstWikiID") && content.contains("method")) {
      return json.extract[RelParameters]
    }*/

    if(request.containsParam("srcWikiID") && request.containsParam("dstWikiID") && request.containsParam("method") ) {
      val p = new RelParameters(request.getIntParam("srcWikiID"), request.getIntParam("dstWikiID"), request.getParam("method"))
      logger.info(p.toString)

      return p
    }

    // wrong json
    throw new IllegalArgumentException("{srcWikiID, dstWikiID, method} parameters not present in the json request.")
  }

  def request2RelParameters4Rank(request: Request) : RelParameters = {



    if(request.containsParam("srcWikiID") && request.containsParam("method")){
      return new RelParameters(request.getIntParam("srcWikiID"), -1, request.getParam("method"))
    }


    /*
    implicit val formats = DefaultFormats

    val content = request.getContentString()
    val json = parse(content)

    if (content.contains("srcWikiID") && content.contains("method")) {
      return json.extract[RelParameters]
    }*/

    // wrong json
    throw new IllegalArgumentException("{srcWikiID, method} parameters not present in the json request.")
  }


  def request2TextParameters(request: Request) : TextParameters = {

    if (request.containsParam("method")) {

      return new TextParameters(request.getParam("text"), request.getParam("method"))

    } else {
      return new TextParameters(request.getParam("text"), "esa")
    }

    /*
    json string
    implicit val formats = DefaultFormats

    val content = request.getContentString()

    logger.warn("Content of the request is")
    logger.warn(content)
    logger.warn("--------------")
    val json = parse(content)

    if (content.contains("text")) {
      return json.extract[TextParameters]
    }*/

    throw new IllegalArgumentException("{text} parameters not present in the json request.")
  }

}