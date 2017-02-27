package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topkcontext
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.esa.ESA
import org.slf4j.{Logger, LoggerFactory}

class ESATopKContext extends TopKContextCached {
  override protected def logger: Logger = LoggerFactory.getLogger(getClass)
  override protected def cacheTopKContextPath = Config.getString("wikipedia.cache.topk.esa.context")


  override protected def nonCachedTopKScoredEntities(srcWikiID: Int, dstWikiID: Int, k: Int): Seq[(Int, Float)] = {
    ESA.wikipediaConcepts(srcWikiID, dstWikiID, k)
  }

}
