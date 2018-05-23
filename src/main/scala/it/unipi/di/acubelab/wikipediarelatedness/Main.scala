package it.unipi.di.acubelab.wikipediarelatedness

import it.unipi.di.acubelab.wikipediarelatedness.evaluation.benchmark.WikiRelBenchmark
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.dataset.WikiRelDatasetFactory
import it.unipi.di.acubelab.wikipediarelatedness.utils.{Data2Rel2TSV, TwoStageArgs}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.graph.{WikiGraph, WikiGraphProcessor}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.WikiRelatednessFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.firststage.nodes.RelatedWikiNeighbourNodesOfSubGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.firststage.nodes.cache.CachedNodesOfSubGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.secondstage.weights.RelatednessWeightsOfSubGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.secondstage.weights.cache.CachedWeightsOfSubGraph


/**
  * Take as input a graph of out-edges in tsv format and save it in FastUtil format.
  */
object WikiGraphProcessing {

  def main(args: Array[String]): Unit = {
    val wikiGraphTSV = args(0)
    val wikiOutBin = args(1)
    val wikiInBin = args(2)
    val wikiSymBin = args(3)

    new WikiGraphProcessor().process( wikiGraphTSV, wikiOutBin, wikiInBin, wikiSymBin )
  }

}


/**
  * Generates cache of top (most related) nodes for each node in the graph.
  */
object TopNodesCaching {

  def main(args: Array[String]) : Unit = {
    val outGraphFilename = args(0)
    val inGraphFilename = args(1)
    val cacheFilename = args(2)

    val mwRel = WikiRelatednessFactory.makeMilneWitten(inGraphFilename)
    val nodesSubGraph = new RelatedWikiNeighbourNodesOfSubGraph(WikiGraph(outGraphFilename), mwRel)

    CachedNodesOfSubGraph.generateWikiRelatednessCache(nodesSubGraph, cacheFilename)

  }

}


/**
  * Generates cache of top (most related) nodes for each node in the graph.
  */
object WeightCaching {

  def main(args: Array[String]) : Unit = {
    val outGraphFilename = args(0)
    val inGraphFilename = args(1)

    val graphFilename = args(2)  // computes M&W relatedness between the neighbors of the nodes in this graph.
                                 //  If you have a lot of memory you can use the sym-graph.bin, otherwiser use out-graph.bin

    val cacheFilename = args(3)

    val mwRel = WikiRelatednessFactory.makeMilneWitten(inGraphFilename)
    val weightSubGraph = new RelatednessWeightsOfSubGraph(mwRel)

    CachedWeightsOfSubGraph.generateWeightsCache(WikiGraph(graphFilename), weightSubGraph, cacheFilename)
  }

}


/**
  * Computes the Two-Stage Framework relatedness on a set of pairs.
  */
object ApplyTwoStageFramework {

  def main(args: Array[String]) : Unit = {
    val conf = new TwoStageArgs(args)

    val twoStageFramework = WikiRelatednessFactory
      .makeCachedTwoStageFrameworkRelatedness(
        conf.cachetopnodes(), conf.cacheweights(), conf.outgraph(), conf.ingraph(), conf.k()
      )

    val data2rel2csv = new Data2Rel2TSV(twoStageFramework)
    data2rel2csv.apply(conf.querypairs(), conf.output())
  }

}



/**
  * Benchmark Milne&Witten on WiRe dataset.
  */
object MilneWittenBenchmarking {

  def main(args: Array[String]) : Unit = {
    val wire = WikiRelDatasetFactory.apply("WiRe")

    val graphFilename = args(0)
    val rel = WikiRelatednessFactory.makeMilneWitten(graphFilename)

    val benchmark = new WikiRelBenchmark(wire, rel)
    benchmark.run()

  }
}


/**
  * Benchmark TwoStageFramework on WiRe dataset.
  */
object TwoStageFrameworkBenchmarking {

  def main(args: Array[String]) : Unit = {
    val wire = WikiRelDatasetFactory.apply("WiRe")

    val outGraphFilename = args(0)
    val inGraphFilename = args(1)
    val k = args(2).toInt
    val cacheFilename = args(3)

    // everything slowly on-the-fly
//    val rel = WikiRelatednessFactory.makeTwoStageFrameworkRelatedness(outGraphFilename, inGraphFilename, 30)

    // with cache
//    val rel = WikiRelatednessFactory.makeCachedTwoStageFrameworkRelatedness(cacheFilename, outGraphFilename, inGraphFilename, 30)

//    val benchmark = new WikiRelBenchmark(wire, rel)
//    benchmark.run()

  }
}