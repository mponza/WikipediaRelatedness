package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.graph.fast

import java.io.File

import it.unimi.dsi.fastutil.ints.{Int2ObjectOpenHashMap, IntArrayList}
import it.unimi.dsi.fastutil.io.BinIO
import it.unipi.di.acubelab.wikipediarelatedness.utils.WikiLinksReader
import org.slf4j.LoggerFactory


class FastWikiGraphProcessor {

  val logger = LoggerFactory.getLogger(classOf[FastWikiGraphProcessor])


  /**
    * Given a tsv graph, it generates and serialize FastUtil hashtables. Useful for speeding-up further loadings.
    * @param tsvFilename
    * @param outGraphFilename
    * @param inGraphFilename
    * @param symGraphFilename
    */
  def process(tsvFilename: String, outGraphFilename: String, inGraphFilename: String, symGraphFilename: String): Unit = {
    logger.info("Generating out/in graphs...")

    val outGraph = new Int2ObjectOpenHashMap[IntArrayList]
    val inGraph = new Int2ObjectOpenHashMap[IntArrayList]
    val symGraph = new Int2ObjectOpenHashMap[IntArrayList]

    val graphReader = new WikiLinksReader
    graphReader.foreach {
      case (src, dst)  =>

        outGraph.putIfAbsent(src, new IntArrayList())
        inGraph.putIfAbsent(dst, new IntArrayList())

        outGraph.get(src).add(dst)
        inGraph.get(dst).add(src)
    }


    logger.info("Generating sym graph...")
    addGraph2Graph(outGraph, symGraph)
    addGraph2Graph(inGraph, symGraph)


    logger.info("Sorting graphs' edges...")
    sortGraph(outGraph)
    sortGraph(inGraph)
    sortGraph(symGraph)

    logger.info("Serializing graphs...")
    serializeGraph(outGraph, outGraphFilename)
    serializeGraph(inGraph, inGraphFilename)
    serializeGraph(symGraph, symGraphFilename)

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
    * Sort edges of graph.
    * @param graph
    */
  def sortGraph(graph: Int2ObjectOpenHashMap[IntArrayList]): Unit = {
    val iterIDs = graph.keySet().iterator()

    while(iterIDs.hasNext) {
      val src = iterIDs.nextInt()
      java.util.Collections.sort(graph.get(src))
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

}