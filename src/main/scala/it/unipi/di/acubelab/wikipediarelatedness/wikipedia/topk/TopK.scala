package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk


/**
  * Trait for top-k most similar concepts.
  *
  */
trait TopK {

  def topKEntities(wikiID: Int, k: Int) = topKScoredEntities(wikiID, k).map(_._1)


  /**
    * Top-k most similar wikiIDs to wikiID and their corresponding score.
    * WARNING: entities are sorted by score, NOT by wikiID.
    *
    * @param k
    * @return
    */
  def topKScoredEntities(wikiID: Int, k: Int) : Seq[(Int, Float)]
}
