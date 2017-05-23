package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.service

import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import org.slf4j.LoggerFactory

import scala.util.parsing.json.JSON
import scalaj.http.Http




object RemoteWikipedia {

  protected val logger = LoggerFactory.getLogger("RemoteWikipedia")
  protected val extraMap = Map(
    "MySpace" -> "Myspace",
    "Revolutionary_Armed_Forces_of_Colombia" -> "FARC",
    "NBC_Universal"-> "NBCUniversal",
    "Thomas_Watson,_ Jr." -> "Thomas_Watson_Jr",
    "Academy_Award" -> "Academy_Awards",
    "Fight_Club_(film)" -> "Fight_Club",
    "Anne_Hathaway_actress" -> "Anne_Hathaway",
    "Burma" -> "Myanmar",
    "Inception_(film)" -> "Inception",

    "Liberty_City_(Grand_Theft_Auto)" -> "",
    "Peter_Moore_(business)" -> "",
    "John_D._Carmack" -> "",
    "Team_Fortress" -> "",
    "Doom_(video_game)" -> "",
    "JC_Denton" -> "",
    "Ion_Storm_Inc." -> "",
    "Game_of_the_Year" -> "",
    "Steam_(content_delivery)" -> "",
    "Harmonix_Music_Systems" -> "",
    "Tomb_Raider_(video_game)" -> "",
    "The_Cannon_Group" -> "",
    "Brazilian_Jiu-Jitsu" -> "",
    "H._M._Murdock" -> "",
    "Templeton_\"Faceman\"_Peck" -> "",
    "Robert_Ehrlich" -> "Robert Ehrlich",
    "Salon.com" -> "Salon_(website)",
    "Banana_Republic_(clothing_retailer)" -> ""
  )


  def title2ID(wikiTitle: String) : Int = {





    val strResponse = Http(getWikiURL(wikiTitle)).timeout(1000000000, 10000000).asString.body.toString



    val jsonResponse = JSON.parseFull(strResponse)

    jsonResponse match {
      case Some(jsonMap: Map[String, Any]@unchecked) =>

        jsonMap("query") match {
          case jsonQuery: Map[String, Any]@unchecked =>

            if (jsonQuery.contains("pages")) {
              jsonQuery("pages") match {
                case jsonPages: Map[String, Any]@unchecked =>

                  return jsonPages.keys.map(_.toInt).toSeq.head

                case _ => ;
              }
            }

          case _ => ;
        }

      case _ => ;
    }

    logger.error("Failing using Remote Wikipedia with %s".format(wikiTitle))
     -1
  }



  def getWikiURL(wikiTitle: String) = {
    "https://en.wikipedia.org/w/api.php?action=query&prop=info&titles=%s&inprop=url&format=json".format(wikiTitle)
  }

}
