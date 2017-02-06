package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.service

import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import org.slf4j.LoggerFactory

import scala.util.parsing.json.JSON
import scalaj.http.{Http, HttpOptions}

object CoSimRankServer {
  protected val logger = LoggerFactory.getLogger("CoSimRankServer")

  protected val url = Config.getString("service.cosimrank")


  /**
    * Computes the similarity between srcWikiID and dstWikiID by running CoSimRank over the whole Wikipedia graph.
    * This function uses a RESTful server which wraps the native C++ implementation of CoSimRank.
    *
    * The returned similarities are respectively CoSimRank and PPR+Cos.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @param iterations
    * @param decay
    * @return
    */
  def similarities(srcWikiID: Int, dstWikiID: Int, iterations: Int = 30, decay: Float = 0.8f) : (Float, Float) = {

    val data = """{"src": "%d", "dst": "%d"}""".format(srcWikiID, dstWikiID)


    // Dunnou why it does not work...
    //val strResponse = Http(Config.getString("service.cosimrank")).postForm(Seq( ("src", srcWikiID.toString) , ("dst", dstWikiID.toString)) )
    //  .header("Content-type", "application/json").asString.toString


    // i.e. This works without problems:
    //
    //          curl -H "Content-Type: application/json" -X POST -d '{"src": 30075, "dst": 16217}' http:localhost:9080
    // So... lol
    import scala.sys.process._
    val cmd = Seq("curl", "-L", "-X", "POST", "-H", "'Content-Type: application/json'", "-d " + data,
                   Config.getString("service.cosimrank"))
    val strResponse = cmd.!!


    JSON.parseFull(strResponse) match {
      case Some(jsonMap: Map[String, Any] @unchecked) =>

        val cosimrank = jsonMap.get("CoSimRank").get.asInstanceOf[Double].toFloat
        val pprcos = jsonMap.get("PPRCos").get.asInstanceOf[Double].toFloat

        return (cosimrank, pprcos)

      case _ => ;
    }

    throw new IllegalArgumentException("Error parsing JSON response with src %d and dst %d".format(srcWikiID, dstWikiID))
  }

}
