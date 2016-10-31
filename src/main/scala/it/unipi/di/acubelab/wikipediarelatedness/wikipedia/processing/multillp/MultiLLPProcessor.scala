package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.multillp

import java.nio.file.Paths

import it.unimi.dsi.webgraph.BVGraph
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.oldllp.{LLPProcessor, LLPTask}
import org.slf4j.LoggerFactory

/**
  * Generates more clustering with the same LLP parameters specified in LLPTask
  * and by using different gammas (randomly generated from each LLPProcessor)
 *
  * @param graph
  * @param nLLP
  * @param llpTask
  */
class MultiLLPProcessor(graph: BVGraph, nLLP: Int = 10, llpTask: LLPTask = new LLPTask){
  val logger = LoggerFactory.getLogger(classOf[MultiLLPProcessor])

  def process(path: String = null) = {
    val multiLLPPath = if (path == null) dirPath() else path

    for(i <- 0 until nLLP) {
      val llpProc = new LLPProcessor(graph, llpTask)
      val llpPath = Paths.get(multiLLPPath, "%s-%d".format(llpTask.toString, i)).toString

      llpProc.process(llpPath)
    }
  }

  def dirPath() : String = {
    Paths.get(Configuration.wikipedia("multiLLP"), "multi-nLLP_%d-%s".format(nLLP, llpTask.toString)).toString
  }
}
