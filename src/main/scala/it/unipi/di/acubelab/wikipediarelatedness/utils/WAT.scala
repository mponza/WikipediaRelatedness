package it.unipi.di.acubelab.wikipediarelatedness.utils

import org.slf4j.LoggerFactory

import scala.util.parsing.json.JSON
import scalaj.http.Http


object WAT {
  val logger = LoggerFactory.getLogger("WAT")

  def redirect(title: String) : (String, Int) = {
    val strResponse = Http(Configuration.wat).param("title", title).timeout(1000000000, 10000000).asString.body.toString

    val jsonResponse = JSON.parseFull(strResponse)

    jsonResponse match {
      case Some(jsonMap: Map[String, Any] @unchecked) =>

        jsonMap("title") match {
          case jsonTitle: Map[String, Any] @unchecked =>

            if (jsonTitle.contains("redirect_title")) {
              jsonTitle("redirect_title") match {
                case jsonWiki: Map[String, Any] @unchecked  =>

                  val redirectedTitle = jsonWiki("wiki_title").asInstanceOf[String]
                  val redirectedWikiID = jsonWiki("wiki_id").asInstanceOf[Double].toInt

                  logger.warn("Redirected %s with %s.".format(title, redirectedTitle))
                  return (redirectedTitle, redirectedWikiID)

                case _ => ;
              }
            }

            jsonTitle("title") match {
              case jsonWiki: Map[String, Any] @unchecked =>

                val wikiID = jsonWiki("wiki_id").asInstanceOf[Double].toInt
                return (title, wikiID)

              case _ => ;
            }

          case _ => ;
        }

      case _ => ;
    }

    throw new IllegalArgumentException("Error parsing JSON response with title %s: root not found.".format(title))
  }
}
