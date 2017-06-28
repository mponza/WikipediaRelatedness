package it.unipi.di.acubelab.wikipediarelatedness.server.restful.services

import com.twitter.finagle.http.Method._
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future
import it.unipi.di.acubelab.wikipediarelatedness.server.restful.RestfulParameters
import it.unipi.di.acubelab.wikipediarelatedness.utils.{CoreNLP, StopWords}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.esa.ESA
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping.WikiTitleID
import org.json4s.jackson.JsonMethods._
import org.slf4j.LoggerFactory


class TextService extends RankService {

  override def logger = LoggerFactory.getLogger("TextRank")


  override def apply(request: Request): Future[Response] = {

    request.method match {
      case Post =>

        val textParams = RestfulParameters.request2TextParameters(request)

        val lemmaText = text2CleanedLemmas(textParams.text)

        val rankedEntities = ESA.wikipediaConcepts(lemmaText, 10000).filter(_._2 != 0).sortBy(-_._2).toArray

        Future.apply(rankedEntities2Response(textParams.text, textParams.method, rankedEntities))
    }
  }


  protected def text2CleanedLemmas(text: String) : String = {
    val lowText = text.toLowerCase
    val noPunctText = lowText.replaceAll("""[\p{Punct}]""", "")

    val lemmas = CoreNLP.lemmatize(noPunctText)
    lemmas.filter(!StopWords.isStopWord(_)) mkString " "
  }


  protected def rankedEntities2Response(text: String, method: String, rankedEntities: Array[(Int, Float)]) : Response = {
    import org.json4s.JsonDSL._

    val mappedRankedEntities = rankedEntities.map(scoredEntity =>

      ("dstWikiID" -> scoredEntity._1) ~
        ("dstWikiTitle" -> WikiTitleID.map(scoredEntity._1)) ~
        ("relatedness" -> scoredEntity._2)

    ).toList

    val json = ("text" -> text) ~ ("rankedEntities" -> mappedRankedEntities) ~ ("method" -> method)

    val strJson = compact( render(json) )

    val response = Response(Status.Ok)
    response.contentString = strJson
    response
  }

}