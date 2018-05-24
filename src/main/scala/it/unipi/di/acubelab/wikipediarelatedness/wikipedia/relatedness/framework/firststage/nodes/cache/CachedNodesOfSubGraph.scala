package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.firststage.nodes.cache

import java.io.File

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.logging.ProgressLogger
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.firststage.nodes.{NodesOfSubGraph, RelatedWikiNeighbourNodesOfSubGraph}
import org.slf4j.LoggerFactory


class CachedNodesOfSubGraph(cacheFilename: String, nodesOfSubGraph: NodesOfSubGraph) extends NodesOfSubGraph{
  private val logger = LoggerFactory.getLogger(classOf[CachedNodesOfSubGraph])
  private val wikiID2rankedNodes = loadCache(cacheFilename)

  private def loadCache(cacheFilename: String) = {
    logger.info("Loading cache of nodes...")
    val cache = BinIO.loadObject(cacheFilename).asInstanceOf[ Int2ObjectOpenHashMap[Seq[Int]] ]
    logger.info("Done.")

    cache
  }


  /**
    * Returns the top nodes of wikiID that will populate the Wikipedia SubGraph.
    *
    * @return A list of wikiIDs.
    */
  override def topNodes(wikiID: Int): Seq[Int] = {
    if(wikiID2rankedNodes.containsKey(wikiID)) return wikiID2rankedNodes.get(wikiID)
    logger.warn(s"$wikiID not in cache.")
    nodesOfSubGraph.topNodes(wikiID)
  }
}


object CachedNodesOfSubGraph {
  private val logger = LoggerFactory.getLogger(classOf[CachedNodesOfSubGraph])

  /**
    * Generates cache for a given RelatedWikiNeighbourNodesOfSubGraph method into filename.
    * @param wikiRelNodesSubGraph
    * @param filename
    */
  def generateWikiRelatednessCache(wikiRelNodesSubGraph: RelatedWikiNeighbourNodesOfSubGraph, filename: String) = {
    val wikiID2nodes = new Int2ObjectOpenHashMap[Seq[Int]]  // cache

    val pl = new ProgressLogger(logger)
    pl.start("Starting caching generation...")

    val pairTopNodes = wikiRelNodesSubGraph.getWikiGraph.allDistinctWikiIDs.par.map(wikiID => (wikiID, wikiRelNodesSubGraph.topNodes(wikiID)) )

    logger.info(s"Mapped ${pairTopNodes.size} nodes")

    for((wikiID, nodes) <- pairTopNodes.toList) {
      wikiID2nodes.put(wikiID, nodes)
    }

    // Sequential
//    for(wikiID <- wikiRelNodesSubGraph.getWikiGraph.allDistinctWikiIDs) {
//      val nodes = wikiRelNodesSubGraph.topNodes(wikiID)
//      wikiID2nodes.put(wikiID, nodes)
//
//      pl.lightUpdate()
//    }
//    pl.done()


    logger.info("Serializing computed cache...")
    new File(filename).getParentFile.mkdirs()
    BinIO.storeObject(wikiID2nodes, filename)
    logger.info("Done.")

  }

}
