package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph
import java.io.File
import java.lang.Double
import java.util.concurrent.TimeUnit

import edu.uci.ics.jung.graph.{DirectedGraph, DirectedSparseGraph}
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.logging.ProgressLogger
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.weight.UniformWeights
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.apache.commons.collections15.Transformer
import org.slf4j.LoggerFactory



class WikiJungBVGraph(graphName: String = "out") extends WikiJungGraph {
  protected val logger = LoggerFactory.getLogger(getClass)

  override val graph: DirectedGraph[Int, Long] = wikiBVDirectedSparseGraph(graphName)
  override val weights: Transformer[Long, Double] = new UniformWeights(graph)



  protected def wikiBVDirectedSparseGraph(graphName: String) : DirectedSparseGraph[Int, Long] = {
    val path = Config.getString("wikipedia.jung.%s".format(graphName))
    if ( new File(path).exists() ) BinIO.loadObject(path).asInstanceOf[ DirectedSparseGraph[Int, Long] ]


    val wikiBVGraph = WikiBVGraphFactory.make(graphName)
    val jungGraph = new DirectedSparseGraph[Int, Long]

    wikiBVGraph.wiki2node.keySet().toIntArray.foreach(jungGraph.addVertex)

    val pl = new ProgressLogger(logger, 1, TimeUnit.MILLISECONDS)
    pl.start("Starting building Jung Graph over the whole Wikipedia %s".format(graphName))

    wikiBVGraph.wiki2node.keySet().toIntArray.foreach {
      case src =>
        val srcShifted = src.asInstanceOf[Long] << 32

        wikiBVGraph.successorArray(src).foreach {
          case dst =>
            val edge = srcShifted | dst
            jungGraph.addEdge(edge, src, dst)
        }

        pl.update()
    }

    pl.done()

    logger.info("Serializing Jung Wikipedia Graph...")
    new File(path).getParentFile.mkdirs()
    BinIO.storeObject(jungGraph, path)
    logger.info("Jung Wikipedia Graph serialized.")

    jungGraph
  }
}
