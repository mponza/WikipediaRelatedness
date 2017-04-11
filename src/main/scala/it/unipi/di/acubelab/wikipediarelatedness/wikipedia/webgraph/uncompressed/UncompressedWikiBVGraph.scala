package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.uncompressed

import java.io.File
import java.util.concurrent.TimeUnit

import it.unimi.dsi.fastutil.ints.{Int2IntOpenHashMap, Int2ObjectOpenHashMap}
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.logging.ProgressLogger
import it.unimi.dsi.webgraph.ImmutableGraph
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraph
import org.slf4j.LoggerFactory


class UncompressedWikiBVGraph(graph: ImmutableGraph, wiki2node: Int2IntOpenHashMap) extends WikiBVGraph(graph, wiki2node) {

  override val logger = LoggerFactory.getLogger(getClass)
  val wikiID2neighs = loadUncompressedWikiBVGraph()


  protected def loadUncompressedWikiBVGraph() : Int2ObjectOpenHashMap[Array[Int]] = {
    val filename = Config.getString("wikipedia.cache.fast.uncompressed")
    if (new File(filename).exists()) return BinIO.loadObject(filename).asInstanceOf[Int2ObjectOpenHashMap[Array[Int]]]

    val pl = new ProgressLogger(logger, 1, TimeUnit.MINUTES)
    pl.start("Uncompressing graph...")

    val adjacentList = new Int2ObjectOpenHashMap[Array[Int]]()

    for (wikiID <- super.getVertices) {
      val adj = Array.ofDim[Int](super.outdegree(wikiID))

      super.successorArray(wikiID).zipWithIndex.foreach {
        case (w: Int, index: Int) =>
          adj(index) = w
      }

      adjacentList.put(wikiID, adj)

      pl.update()
    }
    pl.done()


    new File(filename).getParentFile.mkdirs()

    logger.info("Storing Uncompressed graph info %s..." format filename)
    BinIO.storeObject(adjacentList, filename)
    logger.info("Stored completed.")

    adjacentList
  }

  override def successorArray(wikiID: Int) = wikiID2neighs.get(wikiID)

  override def outdegree(wikiID: Int) = wikiID2neighs.get(wikiID).size
}
