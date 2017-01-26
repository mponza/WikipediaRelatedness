package it.unipi.di.acubelab.wikipediarelatedness.serialization

import java.io.{File, PrintWriter}
import java.nio.file.Paths
import java.util.Locale

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import it.unimi.dsi.fastutil.io.BinIO
import it.unipi.di.acubelab.wikipediarelatedness.utils.OldConfiguration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory

/**
  * Class which allows to cast WikiGraph into a Matrix Market file format and the dictionary in a txt.
  */
class WikiMTX(val dir: String) {
   val logger = LoggerFactory.getLogger(classOf[WikiMTX])

   new File(dir).mkdirs
   val wikiGraph = WikiBVGraphFactory.outWikiBVGraph


  def serializeGraph() = {

    val header = "%%MatrixMarket matrix coordinate real general\n%\n"
    val nodesArcs = "%d %d %d\n".format(wikiGraph.graph.numNodes(), wikiGraph.graph.numNodes(), wikiGraph.graph.numArcs())

    logger.info("Serializing Graph...")

    val path = Paths.get(dir, "graph.mtx").toString
    val writer = new PrintWriter(new File(path))
    writer.write(header)
    writer.write(nodesArcs)

    for(i <- 0 until wikiGraph.graph.numNodes()) {

      val outDegree = wikiGraph.nodeOutDegree(i)
      for(node <- wikiGraph.nodeSuccessorArray(i)) {
        writer.write("%d %d %1.5f\n".formatLocal(Locale.US, i + 1, node + 1, 1 / outDegree.toFloat))
      }
    }

    writer.close()
    logger.info("Graph serialized.")
  }


  def serializeDictionary() = {
    val wiki2node =  BinIO.loadObject(OldConfiguration.wikipedia("wiki2node")).asInstanceOf[Int2IntOpenHashMap]

    logger.info("Serializing dictionary...")

    val path = Paths.get(dir, "dic.txt").toString
    val writer = new PrintWriter(new File(path))

    for(wikiID <- wiki2node.keySet().toIntArray()) {
      writer.write("%d %d n\n".format(wiki2node.get(wikiID) + 1, wikiID))
    }

    writer.close()

    logger.info("Dictionary serialized.")
  }

}
