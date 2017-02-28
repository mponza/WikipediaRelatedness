package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.subgraph

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.similarity.SimRanker
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph.SubNodeCreatorFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessFactory, RelatednessOptions}


/**
  * Abstract class for relatedness based on subgraph computation.
  *
  * @param options
  */
abstract class SubGraphRelatedness(val options: RelatednessOptions) extends Relatedness {
  protected val subNodeCreator = SubNodeCreatorFactory.make(options.subNodes, options.subSize)
  protected val weighter = getWeighter
  protected val simRanker = SimRanker.make(options)

  protected def getWeighter : Relatedness = RelatednessFactory.make( options.getWeighterRelatednessOptions )

  abstract def toString
  abstract def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float
}