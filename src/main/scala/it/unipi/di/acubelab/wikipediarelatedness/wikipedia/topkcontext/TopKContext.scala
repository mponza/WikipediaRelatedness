package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topkcontext

trait TopKContext {

  def topKEntities(srcWikiID: Int, dstWikiID: Int, k: Int) = topKScoredEntities(srcWikiID, dstWikiID, k).map(_._1)


  /**
    * Top-k most similar wikiIDs to srcWikiID and dstWikiID with their score.
    * WARNING: entities are sorted by score, NOT by wikiID.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @param k
    * @return
    */
  def topKScoredEntities(srcWikiID: Int, dstWikiID: Int, k: Int) : Seq[(Int, Float)]
}
