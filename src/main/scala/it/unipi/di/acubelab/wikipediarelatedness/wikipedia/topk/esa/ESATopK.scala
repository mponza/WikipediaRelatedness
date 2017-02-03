package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.esa

import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.esa.ESA
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.TopKCached
import org.slf4j.LoggerFactory


class ESATopK extends TopKCached {
  override protected def logger = LoggerFactory.getLogger(getClass)
  override protected def cachePath = Config.getString("wikipedia.cache.esa")


  /**
    * Pure computation of the top-k most similar entities wihtout using cache.
    *
    * @param wikiID
    * @param k
    * @return
    */
  override protected def nonCachedTopKScoredEntities(wikiID: Int, k: Int): Seq[(Int, Float)] = {
    logger.warn("%d not present in cache. Running raw ESA for top-k retrieval...")
    ESA.wikipediaConcepts(wikiID, k)
  }
}
