package it.unipi.di.acubelab.wikipediarelatedness.server.service

import com.twitter.finagle.http.{Request, Response, Status}
import org.json4s.jackson.JsonMethods._
import org.json4s._

trait SerivceParser[T] {

  /**
    * Parses request and returns the corresponding task of type T.
    * @param request
    * @return
    */
  def apply(request: Request) : T  = {
    implicit val formats = DefaultFormats

    val content = request.getContentString()
    val json = parse(content)
  }


  /**
    * Parses task and returns a response.
    * @param task
    * @return
    */
  def apply(task: T) : Response = {
    val strTask = task2JsonMap(task)
    val strJson = compact( render(strTask) )

    val response = Response(Status.Ok)
    response.contentString = strJson
    response
  }


  // Design it better for service and the corresponding parser.


  /**
    * From Task to JObject
    * @param task
    * @return
    */
  protected def task2JsonMap(task: T) : JObject
}
