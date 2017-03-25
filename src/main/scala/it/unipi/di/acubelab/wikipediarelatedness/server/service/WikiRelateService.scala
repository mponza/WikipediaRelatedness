package it.unipi.di.acubelab.wikipediarelatedness.server.service

import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.Method.Post
import com.twitter.finagle.http.Version.Http11
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.{Await, Future}
import it.unipi.di.acubelab.wikipediarelatedness.server.utils.ResponseParsing
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessFactory, RelatednessOptions}
import org.slf4j.LoggerFactory


/**
  * Service for computing relatedness between two Wikipedia entities.
  *
  * Original code adapted from https://gist.github.com/stonegao/1273845
  *
  */
class WikiRelateService(val relatedness: Relatedness) extends Service[Request, Response] {

  protected val logger = LoggerFactory.getLogger(getClass)


  def apply(request: Request): Future[Response] = {
    try {
      request.method match {
        case Post =>

          val wikiRelateTask = ResponseParsing(request)
          val queryString = "%s (%d) and %s (%d)"  format  (wikiRelateTask.src.wikiTitle, wikiRelateTask.src.wikiID,
                                                               wikiRelateTask.dst.wikiTitle, wikiRelateTask.dst.wikiID)


          wikiRelateTask.machineRelatedness = relatedness.computeRelatedness(wikiRelateTask)
          logger.info("Computing elatedness between %s... " format queryString)
          logger.info("Relatedness between %s is %1.3f" format (queryString, wikiRelateTask.machineRelatedness) )

          val response = ResponseParsing(wikiRelateTask)
          Future.apply(response)

        case _ => Future.value(Response.apply(Http11, Status.NotFound))
      }

    } catch {
      // errors & co.
      case e: Exception => throw e

    }
  }


  def run() = Await.ready( getServer )

  def getServer = Http.serve(getURL, this)

  def getURL = ":8080"
}
