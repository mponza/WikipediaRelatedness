package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.clique

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph.WikiJungCliqueGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.similarity.CoSimRanker
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph.SubNodeCreatorFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessFactory, RelatednessOptions}


/**
  * Relatedness method that uses subNodeCreator to generate a cliqued Wikipedia subgraph
  * weighted with the weighter relatedness.
  *
  * @param options
  */
class CliqueRelatedness(val options: RelatednessOptions) extends Relatedness {
  protected val subNodeCreator = SubNodeCreatorFactory.make(options.subNodes, options.subSize)
  protected val weighter = getWeighter
  protected val simRanker = new CoSimRanker(options.iterations, options.pprDecay, options.csrDecay)


  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = {

    val nodes = subNodeCreator.subNodes(srcWikiID, dstWikiID)
    val subGraph = new WikiJungCliqueGraph(nodes, weighter)

    simRanker.similarity(subGraph, srcWikiID, dstWikiID)
  }


  override def toString = {
    "Clique_subNodes:%s,subSize:%d,iterations:%d,pprDecay:%1.2f,csrDecay:%1.2f,weighter:[%s]"
      .format(options.subNodes, options.subSize,
        options.iterations, options.pprDecay, options.csrDecay,
        weighter.toString)
  }


  /**
    * Returns a Relatedness method that will be subsequently used to weight the subgraph.
    *
    */
  protected def getWeighter : Relatedness = {
    val weightOptions = new RelatednessOptions(
                              name = options.weighter,
                              graph = options.weighterGraph,
                              model = options.weighterModel,
                              threshold = options.weighterThreshold
                            )

    RelatednessFactory.make(weightOptions)
  }

}
