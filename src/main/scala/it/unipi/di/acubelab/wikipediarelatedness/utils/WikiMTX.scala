package it.unipi.di.acubelab.wikipediarelatedness.utils

import java.io.{File, PrintWriter}
import java.nio.file.Paths
import java.util.Locale

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory


/**
  * Class used to serializes Wikipeida into MTX file format.
  *
  * @param dir
  */
class WikiMTX(val dir: String) {
  protected val logger = LoggerFactory.getLogger(classOf[WikiMTX])

  new File(dir).mkdirs
  protected val wikiGraph = WikiBVGraphFactory.make("out")


  /**
    * Serialzies Wikipedia into dir (resp. graph.mtx and dic.txt files).
    */
  def wikipedia2MTXFormat() = {
    serializeGraph()
    serializeDictionary()
  }


  /**
    * Serializes the Wikipedia graph into mtx file format.
    *
    */
  protected def serializeGraph() = {

    val header = "%%MatrixMarket matrix coordinate real general\n%\n"
    val nodesArcs = "%d %d %d\n".format(wikiGraph.graph.numNodes(), wikiGraph.graph.numNodes(), wikiGraph.graph.numArcs())

    val path = Paths.get(dir, "graph.mtx").toString
    val writer = new PrintWriter(new File(path))
    writer.write(header)
    writer.write(nodesArcs)


    logger.info("Serializing Graph...")

    for(i <- 0 until wikiGraph.graph.numNodes()) {

      val outDegree = wikiGraph.successorArray( wikiGraph.getWikiID(i) ).length
      for(wID <- wikiGraph.successorArray(  wikiGraph.getWikiID(i) )) {
        writer.write("%d %d %1.5f\n".formatLocal(Locale.US, i + 1, wikiGraph.getNodeID(wID) + 1, 1 / outDegree.toFloat))
      }
    }

    writer.close()
    logger.info("Graph serialized.")
  }


  /**
    * Serializes mapping between nodes and Wikipedia ID.
    *
    */
  protected def serializeDictionary() = {
    logger.info("Serializing dictionary...")

    val path = Paths.get(dir, "dic.txt").toString
    val writer = new PrintWriter( new File(path) )

    for(wikiID <- wikiGraph.wiki2node.keySet().toIntArray) {
      writer.write("%d %d n\n".format( wikiGraph.wiki2node.get(wikiID) + 1, wikiID))
    }

    writer.close()

    logger.info("Dictionary serialized.")
  }

}



object WikiMTX {

  def serialize(dir: String) = {
    val mtx = new WikiMTX(dir)
    mtx.wikipedia2MTXFormat()
  }
}
