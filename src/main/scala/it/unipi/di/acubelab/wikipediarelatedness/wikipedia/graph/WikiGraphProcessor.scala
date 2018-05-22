package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.graph

import java.io.File
import java.util.Collections

import it.unimi.dsi.fastutil.ints.{Int2ObjectOpenHashMap, IntArrayList, IntOpenHashSet}
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.logging.ProgressLogger
import org.slf4j.LoggerFactory


class WikiGraphProcessor {

  val logger = LoggerFactory.getLogger(classOf[WikiGraphProcessor])


  /**
    * Given a tsv graph, it generates and serialize FastUtil hashtables. Useful for speeding-up further loadings.
    * @param tsvFilename
    * @param outGraphFilename
    * @param inGraphFilename
    * @param symGraphFilename
    */
  def process(tsvFilename: String, outGraphFilename: String, inGraphFilename: String, symGraphFilename: String): Unit = {
    logger.info("Generating out/in graphs...")
    val pl = new ProgressLogger(logger)
    pl.start()

    val outGraph = new Int2ObjectOpenHashMap[IntArrayList]
    val inGraph = new Int2ObjectOpenHashMap[IntArrayList]
    val symGraph = new Int2ObjectOpenHashMap[IntArrayList]

    val graphReader = new WikiGraphTSVReader(tsvFilename)
    graphReader.foreach {
      case (src, dst)  =>

        outGraph.putIfAbsent(src, new IntArrayList())
        inGraph.putIfAbsent(dst, new IntArrayList())

        outGraph.get(src).add(dst)
        inGraph.get(dst).add(src)

        pl.lightUpdate()
    }
    pl.done()

    logger.info("Generating sym graph (it can take a while)...")
    addGraph2Graph(outGraph, symGraph)
    addGraph2Graph(inGraph, symGraph)

    logger.info("Removing duplicated edges...")
    uniqueGraphEdges(outGraph)
    uniqueGraphEdges(inGraph)
    uniqueGraphEdges(symGraph)

    logger.info("Sorting graphs' edges...")
    sortGraph(outGraph)
    sortGraph(inGraph)
    sortGraph(symGraph)

    logger.info("Serializing graphs...")
    serializeGraph(outGraph, outGraphFilename)
    serializeGraph(inGraph, inGraphFilename)
    serializeGraph(symGraph, symGraphFilename)

    logger.info("Out-graph statistics")
    statistics(outGraph)
    logger.info("In-graph statistics")
    statistics(inGraph)
    logger.info("Sym-graph statistics")
    statistics(symGraph)
  }


  /**
    * Add all edges of fstGraph into scdGraph.
    *
    * @param fstGraph
    * @param scdGraph
    */
  def addGraph2Graph(fstGraph: Int2ObjectOpenHashMap[IntArrayList], scdGraph: Int2ObjectOpenHashMap[IntArrayList]) = {
    val iterIDs = fstGraph.keySet().iterator()

    while(iterIDs.hasNext) {
      val src = iterIDs.nextInt()
      scdGraph.putIfAbsent(src, new IntArrayList())
      scdGraph.get(src).addAll( fstGraph.get(src) )
    }

  }


  /**
    * Removes duplicated edges from the graph.
    * @param graph
    */
  def uniqueGraphEdges(graph:  Int2ObjectOpenHashMap[IntArrayList]) = {
    val iterIDs = graph.keySet().iterator()

    while(iterIDs.hasNext) {
      val src = iterIDs.nextInt()
      val edges = graph.get(src)
      val distinctEdges = new IntOpenHashSet(edges)
      graph.put(src, new IntArrayList(distinctEdges))
    }
  }


  /**
    * Sort edges of graph.
    * @param graph
    */
  def sortGraph(graph: Int2ObjectOpenHashMap[IntArrayList]): Unit = {
    val iterIDs = graph.keySet().iterator()

    while(iterIDs.hasNext) {
      val src = iterIDs.nextInt()
      val edges = graph.get(src)
      Collections.sort(edges)
    }

  }


  /**
    * Store graph in FastUtil format.
    * @param graph
    * @param filename
    */
  def serializeGraph(graph: Int2ObjectOpenHashMap[IntArrayList], filename: String): Unit = {
    new File( new File(filename).getParent ).mkdirs()
    BinIO.storeObject(graph, filename)
  }


  /**
    * Prints statistics of graph.
    * @param graph
    */
  def statistics(graph: Int2ObjectOpenHashMap[IntArrayList]): Unit = {
    val wikiGraph = new WikiGraph(graph)
    logger.info(s"${wikiGraph.getNumNodes} indexed nodes")
    logger.info(s"${wikiGraph.allEdges()} indexed edges")
  }

}