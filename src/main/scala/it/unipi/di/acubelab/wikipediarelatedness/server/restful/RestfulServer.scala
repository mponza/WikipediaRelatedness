package it.unipi.di.acubelab.wikipediarelatedness.server.restful

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Method, Request, Response}
import com.twitter.server.TwitterServer
import com.twitter.util.Future
import it.unipi.di.acubelab.wikipediarelatedness.server.restful.services.{RankService, RelService}
import org.slf4j.LoggerFactory


class RestfulServer extends TwitterServer {

  override def defaultHttpPort: Int = 9999

  private val logger = LoggerFactory.getLogger("RestfulWikiRelServer")

  val relService = new RelService()
  val rankService = new RankService


  def main() {
    val restfulService = new Service[Request, Response] {

      override def apply(request: Request): Future[Response] = {

        request.method match {
          case Method.Post =>

            request.path match {
              case "/rel" => relService(request)
              case "/rank" => rankService(request)

              //case "/rank" =>
            }
        }
      }


    }
  }

}

