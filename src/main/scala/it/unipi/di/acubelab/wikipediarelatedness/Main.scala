package it.unipi.di.acubelab.wikipediarelatedness

import it.unipi.di.acubelab.wikipediarelatedness.benchmark.{Benchmark, MultipleBenchmark}
import it.unipi.di.acubelab.wikipediarelatedness.dataset.DatasetFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{RelatednessFactory, RelatednessOptions}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.graph.fast.FastWikiGraphProcessor
import org.slf4j.LoggerFactory


/**
  * Take as input a graph of out-edges in tsv format and save it in FastUtil format.
  */
object WikiGraphProcessing {

  val logger = LoggerFactory.getLogger("WikiGraphProcessing")

  def main(args: Array[String]): Unit = {

    val wikiGraphTSV = args(0)

    val wikiOutBin = args(1)
    val wikiInBin = args(2)
    val wikiSymBin = args(3)

    new FastWikiGraphProcessor().process( wikiGraphTSV, wikiOutBin, wikiInBin, wikiSymBin )
  }

}





/**
  * Experiments a relatedness algorithm over all available datasets.
  *
  */
//object Main {
//  val logger = LoggerFactory.getLogger("Main")
//
//  def main(args: Array[String]) {
//
//    val options = RelatednessOptions.make(args)
//    val relatedness = RelatednessFactory.make(options)
//
//    DatasetFactory.datasets().foreach {
//      dataset =>
//            val benchmark = new MultipleBenchmark(dataset, relatedness)
//            benchmark.run()
//        //val benchmark = new Benchmark(dataset, relatedness)
//        //benchmark.run()
//    }
//  }
//
//}


/*
object MilneWittenCacher {
  def main(args: Array[String]) {
    val topk = new MilneWittenTopK("sym")
    val dataset = DatasetFactory.datasets()

    TopKCacher.generate(topk, dataset.flatten, topk.getCachePath)
  }
}
*/

/*
object W2VCacher {
  def main(args: Array[String]) {
    val topk = new W2VTopK(
      Config.getString("wikipedia.neural.w2v.corpus400"),
      Config.getString("wikipedia.cache.topk.neural.corpus400.entity")
      //Config.getString("wikipedia.neural.deepwalk.dw10"),
      //Config.getString("wikipedia.cache.topk.neural.dw10.entity2entities")
    )


    val dataset = DatasetFactory.datasets()
    TopKCacher.generate(topk, dataset.flatten, topk.getCachePath)
  }

}*/

/*
object Statistics{


  def main (args: Array[String] ) {
    val logger = LoggerFactory.getLogger("Statistics")

    val graph = WikiBVGraphFactory.make("sym")

    val n = NeighbourhoodFunction.compute(graph.graph, 24, null)
    logger.info("Average distance %1.2f".format( NeighbourhoodFunction.averageDistance(n) ))

    logger.info("NumNodes %d".format(graph.numNodes()))
    logger.info("Sym NumArcs %d".format(graph.numNodes()))
    logger.info("Directed NumArcs %d".format(WikiBVGraphFactory.make("out").numArcs()))

    val cc = ConnectedComponents.compute(graph.graph, 24, null)
    logger.info("Number of CC: %d".format(cc.numberOfComponents ))

    val lcc = ConnectedComponents.getLargestComponent(graph.graph, 24, null)
    logger.info("Number of Largest CC. NumNodes: %d.".format( lcc.numNodes()))


    val diameter = new SumSweepUndirectedDiameterRadius( graph.graph , SumSweepUndirectedDiameterRadius.OutputLevel.DIAMETER, null )
    diameter.compute()
    logger.info("Diameter: %d".format( diameter.getDiameter ))

  }

}
*/

/*
object Jung {
  def main(args: Array[String]) {
    val wjg = new WikiJungBVGraph("out")
    val ppr = new PPRRanker(5, 0.1)

    println( ppr.similarity(17547, 27546, wjg) )
  }
}*/

/*
object CSR {

  def main(args: Array[String]) {

    CoSimRankCache.generateCache( DatasetFactory.datasets().flatten.toList, 5, 0.8f )
    CoSimRankCache.generateCache( DatasetFactory.datasets().flatten.toList, 10, 0.8f )

    CoSimRankCache.generateCache( DatasetFactory.datasets().flatten.toList, 5, 0.9f )
    CoSimRankCache.generateCache( DatasetFactory.datasets().flatten.toList, 10, 0.9f )
  }
}*/