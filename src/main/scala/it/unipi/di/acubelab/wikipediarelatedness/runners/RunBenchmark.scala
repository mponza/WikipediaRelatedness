package it.unipi.di.acubelab.wikipediarelatedness.runners

import it.unipi.di.acubelab.wikipediarelatedness.benchmark.RelatednessBenchmark
import it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.wikipediarelatedness.dataset.wire.WiReDataset
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.set.MilneWittenRelatedness


class RunBenchmark {

  val wikisim = new WikiSimDataset(Configuration.dataset("procWikiSim"))
  val wire = new WiReDataset(Configuration.wirePiPz("ss"))

  def run() = {
    for (relatedness <- methods()) {
      val benchmark = new RelatednessBenchmark(wikisim, relatedness)
      benchmark.runBenchmark()
    }
  }



  def methods() = {
    List(
      new MilneWittenRelatedness()
    )
  }

}
