package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.options.CoSimRankOptions
import it.unipi.di.acubelab.wikipediarelatedness.utils.{CoSimRank, OldConfiguration}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.{WikiGraph$, WikiBVGraphFactory}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.subgraph.SubWikiBVGraph
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

/**
  * {
  *   "relatedness": CoSimRank/PPRCos   // (algorithm in CoSimRank parameters)
  *   "graph": outGraph, inGraph
  *             /outGraph,inGraph       // used for subgraph computation
  *
  *   "algorithm"                       // Default: same of "relatedness"
  *   "iters": 5
  *   "decay": 0.8
  *
  *   "weighting":                       // if not specified, EdgeWeighter will be used.
  *     {
  *       "relatedness"
  *
  *       ...
  *
  *       This object will be directed passed to the RelatednessFactory to setup the
  *       weighter/relatedness options.
  *     }
  * }
  * */
/*class ServerCoSimRankRelatedness(options: CoSimRankOptions) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[ServerCoSimRankRelatedness])
  val graphs = wikiGraphs()
  val cosimrank = CoSimRank.make(options)
  val weighter = ServerCoSimRankRelatedness.makeWeighter(options)


  def wikiGraphs() : Map[String, WikiBVGraph] = {
    val graphNames = if (options.contains("graph"))
                         options("graph").toString.split(",")
                     else Array("outGraph")

    graphNames.map {
      graphName => graphName -> WikiGraph.wikiBVGraph(graphName)
    }.toMap
  }


  override def computeRelatedness(wikiRelTask: WikiRelateTask): Double = {
    val srcWikiID = wikiRelTask.src.wikiID
    val dstWikiID = wikiRelTask.dst.wikiID

    try {
      // Tries CoSimRank by generating the subgraph with all the
      // specified graphs.
      computeCoSimRank(graphs, srcWikiID, dstWikiID)

    } catch {
      case iae: IllegalArgumentException =>

        // Graph too big, resizing graph by using only outGraph.
        logger.warn("Graph too big. Trying Subgraph generation with outGraph.")
        val graphs = Map("outGraph" -> WikiGraph.outGraph)
        computeCoSimRank(graphs, srcWikiID, dstWikiID)

      case _ : Exception =>
        logger.error("Unexpected error in CoSimRank computation between  %s and %s. Returning 0.0."
                      .format(wikiRelTask.src.wikiTitle, wikiRelTask.dst.wikiTitle))
        0.0
    }
  }

  def computeCoSimRank(graphs: Map[String, WikiBVGraph], srcWikiID: Int, dstWikiID: Int) : Double = {
    val subGraph = new WikiSubBVGraph(graphs, srcWikiID, dstWikiID)
    if (subGraph.immSubGraph.numNodes() >= Configuration.subgraphThreshold) {
      throw new IllegalArgumentException("The generated subgraph is too big.")
    }

    val weightedEdges = computeWeightedEdges(subGraph, srcWikiID, dstWikiID)

    cosimrank.computeSimilarity(weightedEdges, srcWikiID, dstWikiID)
  }


  def computeWeightedEdges(graph: WikiSubBVGraph, srcWikiID: Int, dstWikiID: Int)
  : List[(Int, Int, Double)] = {

    val weightedEdges = new ListBuffer[(Int, Int, Double)]    // [(src, dst, weight)]
    val normFactors = new Int2DoubleOpenHashMap

    // Generates edges with the corresonding pairs.
    val edges = graph.wikiEdges()
    edges.keySet().toIntArray().foreach {
      src => val dsts = edges.get(src).toIntArray()
        dsts.foreach {

          dst =>
            val weight = weighter.computeRelatredness(src, dst)
            weightedEdges += ((src, dst, weight))
            normFactors.put(src, normFactors.getOrDefault(src, 0.0) + weight)
        }
    }

    // Normalizes over the weighted sum.
    val normaWeightedEdges = weightedEdges.toList.map {
      case (src, dst, weight) =>
        val normFactor = normFactors.get(src)
        val normWeight = if(normFactor == 0.0) 0.0 else weight / normFactor
        (src, dst, normWeight)
    }

    normaWeightedEdges
  }


  override def toString: String = {
    "%s-Weight_%s-Graph_%s".format(cosimrank.toString, weighter,
                                   graphs.keys.mkString("_"))
  }
}


// Default Weighter. It assigns the same weight to all edges.
class EdgeWeighter extends Relatedness {
  override def computeRelatedness(wikiRelTask: WikiRelateTask): Double = {
    1.0
  }
  override def toString: String = "Prob"
}

object ServerCoSimRankRelatedness {

  def makeWeighter(options: Map[String, Any]): Relatedness = {
    if (options.contains("weighting"))
      RelatednessFactory.make(Some(options("weighting")))
    else
      new EdgeWeighter
  }
}*/