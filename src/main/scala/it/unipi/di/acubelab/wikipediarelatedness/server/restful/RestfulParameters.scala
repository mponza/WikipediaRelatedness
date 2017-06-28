package it.unipi.di.acubelab.wikipediarelatedness.server.restful

import com.twitter.finagle.http.Request
import it.unipi.di.acubelab.wikipediarelatedness.dataset.{WikiEntity, WikiRelateTask}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping.WikiTitleID
import org.json4s._
import org.json4s.jackson.JsonMethods._


case class RelParameters(srcWikiID: Int, dstWikiID: Int, method: String = "")


object RestfulParameters {

  def request2RelParameters(request: Request) : RelParameters = {
    val content = request.getContentString()
    val json = parse(content)

    if (content.contains("srcWikiID") && content.contains("dstWikiID") && content.contains("method")) {
      return json.extract[RelParameters]
    }

    // wrong json
    throw new IllegalArgumentException("{srcWikiID, dstWikiID, method} parameters not present in the json request.")
  }

}