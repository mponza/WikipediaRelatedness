package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.neighbors

import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.set.MilneWittenRelatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.TopKCached
import org.slf4j.LoggerFactory


/**
  * Ranks neighborhood of a node by their MilneWitten score.
  *
  * @param graph
  */
class MilneWittenTopK(graph: String) extends TopKCached {
  override protected def logger = LoggerFactory.getLogger(getClass)

  override protected def cachePath = Config.getString("wikipedia.cache.topk.milnewitten." + graph)
  protected val milnewitten = new MilneWittenRelatedness(new RelatednessOptions(graph = graph))


  /**
    * Pure computation of the top-k most similar entities without using cache.
    *
    * @param wikiID
    * @param k
    * @return
    */
  override protected def nonCachedTopKScoredEntities(wikiID: Int, k: Int): Seq[(Int, Float)] = {
    val neighborhood = milnewitten.wikiGraph.successorArray(wikiID)

    logger.warn("%d not cached. Computing MilneWitten top-k over %d entities...".format(wikiID, neighborhood.length))

    neighborhood.filter(_ != wikiID).map {
      case wID => Tuple2(wID, milnewitten.computeRelatedness(wikiID, wID))
    }.sortBy(_._2).slice(0, k)

  }
}
