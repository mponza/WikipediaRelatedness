package it.unipi.di.acubelab.wikipediarelatedness.server.service

import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.Method.Post
import com.twitter.finagle.http.Version.Http11
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.{Await, Future}
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.server.utils.ResponseParsing
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{RelatednessFactory, RelatednessOptions}
import org.slf4j.LoggerFactory


/**
  * Service for computing relatedness between two Wikipedia entities.
  *
  * Original code adapted from https://gist.github.com/stonegao/1273845
  *
  */
class WikiRelateService extends Service[Request, Response] {

  protected val logger = LoggerFactory.getLogger(getClass)
  protected lazy val relatedness = RelatednessFactory.make(new RelatednessOptions(name="algo:uncom.mw+uncom.dw"))


  def apply(request: Request): Future[Response] = {
    try {
      request.method match {
        case Post =>

          val wikiRelateTask = ResponseParsing(request)

          logger.info("Computing Relatedness for WikiTask: %s..." format wikiRelateTask.toString())
          wikiRelateTask.machineRelatedness = relatedness.computeRelatedness(wikiRelateTask)

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
