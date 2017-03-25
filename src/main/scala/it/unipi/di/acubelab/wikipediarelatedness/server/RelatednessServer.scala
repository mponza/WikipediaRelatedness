package it.unipi.di.acubelab.wikipediarelatedness.server


import com.twitter.util.Await
import it.unipi.di.acubelab.wikipediarelatedness.server.service.WikiRelateService
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{RelatednessFactory, RelatednessOptions}

object RelatednessServer {


  def main(args: Array[String]) {

    val fastAlgoScheme = RelatednessFactory.make(new RelatednessOptions(name="algo:uncom.mw+uncom.dw"))
    val service = new WikiRelateService(fastAlgoScheme)
    service.run()



  }

}