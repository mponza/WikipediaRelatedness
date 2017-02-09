package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph.topk

import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph.SubNodeCreator
import org.slf4j.Logger
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.TopK


abstract class TopKSubNodeCreator(size: Int) extends SubNodeCreator {
  protected def logger: Logger
  protected def topK: TopK

  /**
    * Returns Top-K most "similar" nodes to wikiID, sorted from the most to the lowest relevant.
    *
    * @param wikiID
    * @return
    */
  def topKNodes(wikiID: Int): Seq[Int] = topK.topKEntities(wikiID, size)


  /**
    * Computes tw top-k vectors of the most relevant concepts of srcWikiID and dstWikiID, respectively.
    * Then it builds a Seq of length size by alternatively keeping one element from both the top-k vectors.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  override def subNodes(srcWikiID: Int, dstWikiID: Int): Seq[Int] = {
    val srcTopK = topKNodes(srcWikiID)
    val dstTopK = topKNodes(dstWikiID)

    val nodes = new IntOpenHashSet()

    nodes.add(srcWikiID)
    nodes.add(dstWikiID)

    var (i, j) = (0, 0)
    var n = nodes.size()

    while(i < srcTopK.length && j < dstTopK.length && nodes.size() < size) {

      while (i < srcTopK.length && n == nodes.size() && nodes.size() < size) {
        nodes.add( srcTopK(i) )
        i += 1
      }
      if (n != nodes.size()) n += 1

      while (j < dstTopK.length && n == nodes.size() && nodes.size() < size) {
        nodes.add( dstTopK(j) )
        j += 1
      }
      if (n != nodes.size()) n += 1

    }

    while (i < srcTopK.length && nodes.size() < size) { nodes.add( srcTopK(i) ) ; i += 1 }
    while (j < dstTopK.length && nodes.size() < size) { nodes.add( dstTopK(j) ) ; j += 1 }

    nodes.toIntArray
  }
}
