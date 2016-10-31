package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.llp

import java.nio.file.Paths

import it.unimi.dsi.fastutil.io.TextIO
import it.unimi.dsi.law.graph.LayeredLabelPropagation
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.{WikiGraph, WikiGraphFactory}
import org.slf4j.LoggerFactory

class LLPProcessor(val wikiGraph: WikiGraph = WikiGraphFactory.noLoopSymGraph,
                    val task: LLPTask = new LLPTask()) {
  val logger = LoggerFactory.getLogger(classOf[LLPProcessor])


  def clusterize(dir: String = Configuration.wikipedia("llp")) = {
    val llpPath = Paths.get(dir, task)
    createLLPPaths()

    saveGammas(llpDir)
    // create directories


    val llp = new LayeredLabelPropagation(wikiGraph.graph, System.currentTimeMillis())

    llp.labelBasename()
    llp.computePermutation(task.gammas.map(_.toDouble).toArray, null, task.maxUpdates)
  }


  def createLLPPaths(dir: String) : String = {
    val llpPath
  }


  def saveGammas(path: String) = {
    val gammaPath = Paths.get(path, "gammas.txt").toString

    logger.info("Saving gammas {%s} into %s".format(gammaPath, task.gammas.mkString(" ")))
    TextIO.storeFloats(task.gammas.toArray, gammaPath)
  }
}
