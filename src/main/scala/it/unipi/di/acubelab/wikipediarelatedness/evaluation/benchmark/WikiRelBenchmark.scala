package it.unipi.di.acubelab.wikipediarelatedness.evaluation.benchmark

import it.unimi.dsi.logging.ProgressLogger
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.dataset.WikiRelDataset
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.metrics.Correlation
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.WikiRelatedness
import org.slf4j.LoggerFactory

class WikiRelBenchmark(val wikiRelDataset: WikiRelDataset, val wikiRelatedness: WikiRelatedness) {

  private val logger = LoggerFactory.getLogger(classOf[WikiRelBenchmark])

  def run() = {

    val pl = new ProgressLogger(logger)
    pl.start(s"Benchmarking ${wikiRelatedness.name()} on ${wikiRelDataset.name()} dataset")

    wikiRelDataset.foreach {
      wikiRelTask =>

        wikiRelTask.start()
        wikiRelTask.machineRelatedness = wikiRelatedness.relatedness( wikiRelTask.src.wikiID,  wikiRelTask.dst.wikiID )
        wikiRelTask.end()

        pl.update()
    }
    pl.done()

    logger.info(s"Correlation are: [Person ${Correlation.pearson(wikiRelDataset)}]" +
                s"and [Sperman ${Correlation.spearman(wikiRelDataset)}]" )
  }
}
