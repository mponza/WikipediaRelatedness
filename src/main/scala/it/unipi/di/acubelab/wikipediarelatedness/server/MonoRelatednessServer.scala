package it.unipi.di.acubelab.wikipediarelatedness.server

import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.Method._
import com.twitter.finagle.http.Version.{apply => _, _}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.server.TwitterServer
import com.twitter.util.{Await, Future}
import it.unipi.di.acubelab.wikipediarelatedness.server.service.{LambdaWikiRelateService, WikiRelateService}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast.relatedness.FastMWDWRelatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{RelatednessFactory, RelatednessOptions}
import org.slf4j.LoggerFactory



object MonoRelatednessServer extends TwitterServer {

  private val logger = LoggerFactory.getLogger("RelatednessServer")


  lazy val fastAlgoScheme = new WikiRelateService( RelatednessFactory.make(new RelatednessOptions(name="algo:uncom.mw+uncom.dw")) )
  lazy val lambdaFastAlgoScheme = new LambdaWikiRelateService( RelatednessFactory.make(new RelatednessOptions(name="lambdaAlgo:uncom.mw+uncom.dw"))
                                                                .asInstanceOf[FastMWDWRelatedness] )
  lazy val mwFastAlgoScheme = new WikiRelateService( RelatednessFactory.make(new RelatednessOptions(name="algo:uncom.mw")) )
  lazy val mw = new WikiRelateService( RelatednessFactory.make(new RelatednessOptions(name="uncom.mw", graph="in")) )
  lazy val jaccard = new WikiRelateService( RelatednessFactory.make(new RelatednessOptions(name="uncom.jacc", graph="in")) )


  def main() {

    val routingService = new Service[Request, Response] {

      override def apply(request: Request): Future[Response] = {
        try {

          request.method match {
            case Post =>

              request.path match {

                case "/algoMW+DW" => fastAlgoScheme(request)
                case "/algoLambdaMW+DW" => lambdaFastAlgoScheme(request)

                case "/algoMW" => mwFastAlgoScheme(request)

                case "/milnewitten" => mw(request)
                case "/jaccard" => jaccard(request)
              }

            case _ =>
              Future.value(Response.apply(Http11, Status.NotFound))
          }

        } catch {
          // errors & co.
          case e: Exception => throw e

        }
      }
    }

    val routingServer = Http.serve(":9090", routingService)
    logger.info("Server up!")
    Await.all(routingServer, adminHttpServer)
  }


}