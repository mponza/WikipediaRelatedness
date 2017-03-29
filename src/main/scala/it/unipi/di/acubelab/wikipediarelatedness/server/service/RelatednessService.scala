package it.unipi.di.acubelab.wikipediarelatedness.server.service

import java.util.Locale

import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.Method.Post
import com.twitter.finagle.http.Version.Http11
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.{Await, Future}
import it.unipi.di.acubelab.wikipediarelatedness.server.utils.ResponseParsing
import org.slf4j.Logger

trait RelatednessService[T]  extends Service[Request, Response] {

  protected val logger: Logger
  protected val port: String
  protected val responseParser: T


  def apply(request: Request): Future[Response] = {
    try {

      request.method match {
        case Post =>
          // logger.info("Parsing request %s" format request.getContentString())
          val task  =

          val wikiRelateTask = ResponseParsing(request)
          val queryString = "%s (%d) and %s (%d)" format(wikiRelateTask.src.wikiTitle, wikiRelateTask.src.wikiID,
            wikiRelateTask.dst.wikiTitle, wikiRelateTask.dst.wikiID)


          wikiRelateTask.machineRelatedness = relatedness.computeRelatedness(wikiRelateTask)
          logger.info("Computing relatedness between %s... " format queryString)
          logger.info("Relatedness between %s is %1.3f" formatLocal(Locale.US, wikiRelateTask.machineRelatedness))

          val response = ResponseParsing(wikiRelateTask)
          Future.apply(response)


        case _ => Future.value(Response.apply(Http11, Status.NotFound))
      }

    } catch {
      // errors & co.
      case e: Exception => throw e

    }
  }

  def process(task: T)

  def getPort = port
  def run() = Await.ready( getServer )
  def getServer = Http.serve(getPort, this)
}
