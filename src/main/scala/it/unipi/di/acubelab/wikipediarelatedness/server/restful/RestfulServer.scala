package it.unipi.di.acubelab.wikipediarelatedness.server.restful

import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Method, Request, Response}
import com.twitter.server.TwitterServer
import com.twitter.util.{Await, Future}
import it.unipi.di.acubelab.wikipediarelatedness.server.restful.services.{RankService, RelService, TextService}
import org.slf4j.LoggerFactory


object RestfulServer extends TwitterServer {

  override def defaultHttpPort: Int = 9999

  private val logger = LoggerFactory.getLogger("RestfulWikiRelServer")

  val relService = new RelService
  val rankService = new RankService
  val textService = new TextService


  def main() {
    val routingService = new Service[Request, Response] {

      override def apply(request: Request): Future[Response] = {

        request.method match {
          case Method.Post =>

            request.path match {
              case "/rel" => relService(request)
              case "/rank" => rankService(request)
              case "/text" => textService(request)

              //case "/rank" =>
            }
        }
      }
    }

    val routingServer = Http.serve(":7000", routingService)
    logger.info("Server up!")
    Await.all(routingServer)
  }

}

