package it.unipi.di.acubelab.wikipediarelatedness.server


import com.twitter.util.Await
import it.unipi.di.acubelab.wikipediarelatedness.server.service.WikiRelateService
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{RelatednessFactory, RelatednessOptions}
import com.twitter.server.TwitterServer
import org.slf4j.LoggerFactory






object RelatednessServer extends TwitterServer {

  private val logger = LoggerFactory.getLogger("RelatednessServer")

  def main() {

    val fastAlgoScheme = RelatednessFactory.make(new RelatednessOptions(name="algo:uncom.mw"))//+uncom.dw"))
    val algoService = new WikiRelateService(fastAlgoScheme, 9070)
    //algoService.run()

    val mw = RelatednessFactory.make(new RelatednessOptions(name="uncom.mw", graph="in"))
    val mwService = new WikiRelateService(mw)
    //mwService.run()

    onExit {
      algoService.close()
      mwService.close()
    }

    logger.info("Server up!")
    Await.all(algoService.getServer, mwService.getServer, adminHttpServer)
  }

}