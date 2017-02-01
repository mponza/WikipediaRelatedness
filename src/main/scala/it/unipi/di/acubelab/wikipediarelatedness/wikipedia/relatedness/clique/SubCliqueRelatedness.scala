package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.clique

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessFactory, RelatednessOptions}

class SubCliqueRelatedness(val options: RelatednessOptions) extends Relatedness {
  protected val subNoder =
  protected val weighter = getWeightRelatedness


  /**
    * Returns a Relatedness method that will be subsequently used to weight the subgraph.
    *
    */
  protected def getWeightRelatedness = {
    val weightOptions = new RelatednessOptions(
                            name = options.weighter,
                            graph = options.weighterGraph,
                            model = options.weighterModel,
                            threshold = options.weighterThreshold
                          )
    RelatednessFactory.make(weightOptions)
  }


  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = {

    val nodes =



    val graph = new WikiJungCiqueGraph(nodes, relat4edness)

    val cosimranker =

      computecosimrank
  }
}
