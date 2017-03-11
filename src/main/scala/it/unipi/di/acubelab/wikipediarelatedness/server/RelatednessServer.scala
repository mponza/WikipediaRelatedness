package it.unipi.di.acubelab.wikipediarelatedness.server

import com.twitter.finagle.http.Method.Post
import com.twitter.finagle.http.Version.Http11
import com.twitter.finagle.http.path.{Path, Root}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.{Http, Service}
import com.twitter.util.{Await, Future}

object RelatednessServer {

  // Code adapted from https://gist.github.com/stonegao/1273845
  def main(args: Array[String]) {

    val service = new Service[Request, Response] {

      def apply(request: Request): Future[Response] = {
        try {
          request.method match { // -> Path(request.path) match {
            case Post => // -> Root / Path("relate/graph") =>
              val c = request.contentString
              //val json = c.js
              null

            case _ =>
              Future.value(Response.apply(Http11, Status.NotFound))
          }
        }
      }
    }


    val server = Http.serve(":8080", service)
    Await.ready(server)
  }

}


/*
*
* https://gist.github.com/stonegao/1273845
*
* class Respond extends Service[Request, Response] with Logger {
  def apply(request: Request) = {
    try {
      request.method -> Path(request.path) match {
        case GET -> Root / "todos" => Future.value {
          val data = Todos.allAsJson
          debug("data: %s" format data)
          Responses.json(data, acceptsGzip(request))
        }
        case GET -> Root / "todos" / id => Future.value {
          val todo = Todos get id
          val data = todo.toJson
          debug("data: %s" format data)
          Responses.json(data, acceptsGzip(request))
        }
        case POST -> Root / "todos" => Future.value {
          val content = request.contentString
          val todo    = Todos.fromJson(content, create = true)
          val data    = todo.toJson
          Responses.json(data, acceptsGzip(request))
        }
        case PUT -> Root / "todos" / id => Future.value {
          val content = request.contentString
          val todo    = Todos.fromJson(content, update = true)
          val data    = todo.toJson
          debug("data: %s" format data)
          Responses.json(data, acceptsGzip(request))
        }
        case DELETE -> Root / "todos" / id => Future.value {
          Todos remove id
          debug("data: %s" format id)
          Response()
        }
        case _ =>
          Future value Response(Http11, NotFound)
      }
    } catch {
      case e: NoSuchElement => Future value Response(Http11, NotFound)
      case e: Exception => Future.value {
        val message = Option(e.getMessage) getOrElse "Something went wrong."
        error("\nMessage: %s\nStack trace:\n%s"
          .format(message, e.getStackTraceString))
        Responses.error(message, acceptsGzip(request))
      }
    }
  }
}
* */

/*
*
* import com.twitter.finagle.Http

val server = Http.server
  .withAdmissionControl.concurrencyLimit(
    maxConcurrentRequests = 10,
    maxWaiters = 0
  )
  .serve(":8080", service)




  import com.twitter.conversions.time._
import com.twitter.finagle.Http

val twitter = Http.server
  .withSession.maxLifeTime(20.seconds)
  .withSession.maxIdleTime(10.seconds)
  .newService("twitter.com")
*
* */