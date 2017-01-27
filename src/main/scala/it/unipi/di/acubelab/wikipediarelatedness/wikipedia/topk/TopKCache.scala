package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.io.BinIO


/**
  * Class with a pre-computed set of top-k (wikiID, weight) for each entity.
  *
  */
trait TopKCache {

  def e2esPath: String

  val entity2entities = loadTopKE2Es()


  /**
    * Loads topK entity2entities cache.
    *
    * @return
    */
  protected def loadTopKE2Es() = {
    BinIO.loadObject(e2esPath).asInstanceOf[Int2ObjectOpenHashMap[List[Tuple2[Int, Float]]]]
  }


  /**
    * Returns topK entities with their weights.
    *
    * @param srcWikiID
    * @param k
    */
  def topKWegihtedEntities(srcWikiID: Int, k: Int): Seq[Tuple2[Int, Float]] = entity2entities.get(srcWikiID).slice(0, k)



  /**
    * Returns topK entities without their weights.
    *
    * @param srcWikiID
    * @param k
    */
  def topKentities(srcWikiID: Int, k: Int) = Seq[Tuple2[Int, Float]] = topKWegihtedEntities(srcWikiID, k).map(_._1)

}
