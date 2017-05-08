package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.webgraph.{BVGraph, EFGraph, ImmutableGraph}
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.uncompressed.UncompressedWikiBVGraph
import org.slf4j.LoggerFactory


/**
  * Factory object to have the wikiBVGraph from its name.
  *
  */
object WikiBVGraphFactory {
  val logger = LoggerFactory.getLogger("WikiBVGraphFactory")

  lazy val immOutGraph = loadImmutableGraph(Config.getString("wikipedia.webgraph.out"))
  lazy val immInGraph = loadImmutableGraph(Config.getString("wikipedia.webgraph.in"))
  lazy val immSymGraph = loadImmutableGraph(Config.getString("wikipedia.webgraph.sym"))
  lazy val immSymNoLoopGraph = loadImmutableGraph(Config.getString("wikipedia.webgraph.sym_no_loop"))

  lazy val efImmInGraph = loadEFImmutableGraph(Config.getString("wikipedia.webgraph.ef.in"))
  lazy val efImmOutGraph = loadEFImmutableGraph(Config.getString("wikipedia.webgraph.ef.out"))

  // WikiID -> NodeID mapping
  lazy val wiki2node = BinIO.loadObject(Config.getString("wikipedia.webgraph.mapping"))
                        .asInstanceOf[Int2IntOpenHashMap]


  /**
    * Loads an immutable graph from path.
    *
    * @param path
    */
  protected def loadImmutableGraph(path: String): ImmutableGraph = {
      logger.info("Loading BVGraph from %s".format(path))
      val graph = BVGraph.load(path)
      logger.info("BVGraph loaded. |Nodes| = %d and |Edges| = %d".format(graph.numNodes, graph.numArcs))

      graph
  }


  protected def loadEFImmutableGraph(path: String): ImmutableGraph = EFGraph.load(path)


  /**
    * Returns a lightweight copy WikiBVGraph from its name.
    * If threadSafe is true it returns a lightweight copy of the graph which can be parallel processed.
    *
    * @param graphName
    * @return
    */
  def make(graphName: String, threadSafe: Boolean = false) : WikiBVGraph = {


    if (graphName == "un.in") return new UncompressedWikiBVGraph(immInGraph, wiki2node)
    if (graphName == "un.out") return new UncompressedWikiBVGraph(immOutGraph, wiki2node)
    if (graphName == "un.sym") return new UncompressedWikiBVGraph(immSymGraph, wiki2node)

      val immGraph = graphName match {
        case "out" => immOutGraph
        case "in" => immInGraph
        case "sym" => immSymGraph
        case "sym_no_loop" =>  immSymNoLoopGraph
        case "ef.in" => efImmInGraph
        case "ef.out" => efImmOutGraph
      }
      //val threadedImmGraph = if (threadSafe) immGraph.copy() else immGraph

      new WikiBVGraph(immGraph, wiki2node)
  }

}