package it.unipi.di.acubelab.wikipediarelatedness.server


import com.twitter.finagle.Http
import com.twitter.util.Await
import it.unipi.di.acubelab.wikipediarelatedness.server.service.WikiRelateService

object RelatednessServer {


  def main(args: Array[String]) {

    val service = new WikiRelateService
    service.run()

  }

}