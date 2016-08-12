package it.unipi.di.acubelab.graphrel.wikipedia.relatedness
import it.unimi.dsi.fastutil.ints.{Int2DoubleOpenHashMap, Int2IntOpenHashMap}
import it.unipi.di.acubelab.graphrel.dataset.WikiRelTask
import it.unipi.di.acubelab.graphrel.utils.CoSimRank
import it.unipi.di.acubelab.graphrel.wikipedia.WikiGraph
import it.unipi.di.acubelab.graphrel.wikipedia.processing.webgraph.WikiSubBVGraph
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

/**
  * {
  *   "relatedness": CoSimRank/PPRCos   // (algorithm in CoSimRank parameters)
  *   "graph": outGraph                 // used for subgraph computation
  *
  *   "algorithm"                       // Default: same of "relatedness"
  *   "iters": 5
  *   "decay": 0.8
  *
  *   "weighting":                  // if not specified, EdgeWeighter will be used.
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
class CoSimRankRelatedness(options: Map[String, Any]) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[CoSimRankRelatedness])

  val graphName = if (options.contains("graph")) options("graph").toString else "outGraph"
  val graph = WikiGraph.wikiBVGraph(graphName)

  val cosimrank = CoSimRank.make(options)

  // Default weighter. It gives the same weight to all edges.
  class EdgeWeighter extends Relatedness {
    override def computeRelatedness(wikiRelTask: WikiRelTask): Double = {
      1.0
    }
  }

  override def computeRelatedness(wikiRelTask: WikiRelTask): Double = {
    val srcWikiID = wikiRelTask.src.wikiID
    val dstWikiID = wikiRelTask.dst.wikiID

    val subGraph = new WikiSubBVGraph(graph, srcWikiID, dstWikiID)
    val weightedEdges = computeWeightedEdges(subGraph, srcWikiID, dstWikiID)

    cosimrank.computeSimilarity(weightedEdges, srcWikiID, dstWikiID)
  }


  def computeWeightedEdges(graph: WikiSubBVGraph, srcWikiID: Int, dstWikiID: Int)
    : List[(Int, Int, Double)] = {

    val weighter = if(options.contains("weighting")) RelatednessFactory.make(Some(options("weighting")))
                    else new EdgeWeighter

    val weightedEdges = new ListBuffer[(Int, Int, Double)]    // [(src, dst, weight)]
    val normFactors = new Int2DoubleOpenHashMap

    // Generates edges with the corresonding pairs.
    val edges = graph.wikiEdges()
    edges.keySet().toIntArray().foreach {
      src => val dsts = edges.get(src).toIntArray()
        dsts.foreach {

          dst =>
            val weight = weighter.computeRelatredness(srcWikiID, dstWikiID)
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
    if(!options.contains("weighting")) {
      return "%s-Weight_Prob".format(cosimrank.toString)
    }


    options("weighting") match {
      case weighting: Map[String, Any] @unchecked =>
        
    }

    "%s-Weight_".format(
      options("relatedness").toString,
      options("")
    )
  }
}
