package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk

import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.esa.ESA
import org.slf4j.LoggerFactory


class ESATopK extends TopKCached {
  override protected def logger = LoggerFactory.getLogger(getClass)
  override protected def cacheTopKPath = Config.getString("wikipedia.cache.topk.esa.entity")


  /**
    * Pure computation of the top-k most similar entities without using cache.
    *
    * @param wikiID
    * @param k
    * @return
    */
  override protected def nonCachedTopKScoredEntities(wikiID: Int, k: Int): Seq[(Int, Float)] = {
    logger.warn("%d not present in cache. Running raw ESA for top-k retrieval...".format(wikiID))
    ESA.wikipediaConcepts(wikiID, k)
  }

}
