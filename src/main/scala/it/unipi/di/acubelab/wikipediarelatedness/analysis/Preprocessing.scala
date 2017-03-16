package it.unipi.di.acubelab.wikipediarelatedness.analysis

import java.io.FileWriter
import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit

import com.madhukaraphatak.sizeof.SizeEstimator
import it.unimi.dsi.fastutil.ints.{Int2ObjectOpenHashMap, IntOpenHashSet}
import it.unimi.dsi.logging.ProgressLogger
import it.unipi.di.acubelab.wat.dataset.embeddings.EmbeddingsDataset
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.TopKFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.{WikiBVGraph, WikiBVGraphFactory}
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

object Preprocessing {

  protected val logger = LoggerFactory.getLogger("Preprocessing")

  def main(args: Array[String]) = {

    //wikipediaSize
    embeddingSize()
    /*
    val outWikiBVGraph = WikiBVGraphFactory.make("out")

    val mwtopk = TopKFactory.make("mw.out")

    val uniqueTopK = new IntOpenHashSet()
    val scoredTopKs = new Int2ObjectOpenHashMap[Seq[(Int, Float)]]()


    val pl = new ProgressLogger(logger, 1, TimeUnit.MINUTES)
    pl.expectedUpdates = outWikiBVGraph.getVertices.size
    pl.start()


    for (wikiID <- outWikiBVGraph.getVertices) {

      val topk = mwtopk.topKScoredEntities(wikiID, 30).filter(_._2 > 0)

      topk.foreach( x => uniqueTopK.add(x._1) )
      scoredTopKs.put(wikiID, topk)

      pl.update()

    }

    pl.done()

    logger.info("Number of unique Top-K entities is %d".format(uniqueTopK.size()))

    val f = new FileWriter("/tmp/top30weights.csv")
    f.write("src,dst,weight\n")
    for(wikiID <- scoredTopKs.keySet().toIntArray) {

      scoredTopKs.get(wikiID).foreach {
        case (nodeID: Int, weight: Float) =>

          f.write("%d,%d,%1.5f\n".format(wikiID, nodeID, weight))
      }
    }
    f.close()*/

  }

  /**
    * Computes in-degree statistics.
    *
    */
  def inDegStats() = {
    val inWikiBVGraph = WikiBVGraphFactory.make("in")

    val degs = ListBuffer.empty[Int]

    for (wikiID <- inWikiBVGraph.getVertices) {

      degs += inWikiBVGraph.outdegree(wikiID)
    }

    logger.info("Edges %d" format degs.sum )

    val avgInDeg = degs.sum / inWikiBVGraph.getVertices.size.toFloat
    logger.info("AVG in degree: %1.5f" format avgInDeg)
  }


  def wikipediaSize() = {
    val outWikiBVGraph = WikiBVGraphFactory.make("out")
    logger.info("Compressed out-Wikipedia Graph: %d" format SizeEstimator.estimate(outWikiBVGraph.graph) )
    logger.info("Mapping out-Wikipedia Graph: %d" format SizeEstimator.estimate(outWikiBVGraph.wiki2node) )
    uncompressedGraph(outWikiBVGraph)


    val inWikiBVGraph = WikiBVGraphFactory.make("in")
    logger.info("Compressed in-Wikipedia Graph: %d" format SizeEstimator.estimate(inWikiBVGraph.graph) )
    uncompressedGraph(inWikiBVGraph)
  }


  def uncompressedGraph(wikiBVGraph: WikiBVGraph) = {
    //val adjacentList = new Int2ObjectOpenHashMap[Array[Int]]
    val adjacentList = Array.ofDim[(Int, Array[Int])](wikiBVGraph.getVertices.size)

    var sumsize = 0L
    var i = 0
    for (wikiID <- wikiBVGraph.getVertices) {
      val adj = Array.ofDim[Int](wikiBVGraph.outdegree(wikiID))

      wikiBVGraph.successorArray(wikiID).zipWithIndex.foreach {
        case (w: Int, index: Int) =>
          adj(index) = w
      }

      //adjacentList.put(wikiID, adj)
      adjacentList(i) =  ((wikiID, adj))
      i += 1
      sumsize += SizeEstimator.estimate(adj)
    }

    logger.info("Size Of uncomrpessed graph: %d" format SizeEstimator.estimate(adjacentList))
    logger.info("Size Of uncomrpessed graph: %d" format sumsize)

  }


  def embeddingSize() = {
    val dwcbow = WordVectorSerializer.loadGoogleModel(new File(Config.getString("wikipedia.neural.deepwalk.dw10")), true)

    logger.info("Embedding Lookup's size: %d" format SizeEstimator.estimate(dwcbow.lookupTable()))
    logger.info("Embedding Model's size: %d" format SizeEstimator.estimate(dwcbow))
  }


}
