package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.oldllp

import java.io.{File, PrintWriter}
import java.nio.file.Paths

import it.unimi.dsi.fastutil.doubles.DoubleArrayList
import it.unimi.dsi.law.graph.LayeredLabelPropagation
import it.unimi.dsi.webgraph.BVGraph
import it.unipi.di.acubelab.wikipediarelatedness.utils.OldConfiguration
import org.slf4j.LoggerFactory

import scala.util.Random

/**
  * @param graph
  */
class LLPProcessor(graph: BVGraph, llpTask : LLPTask = new LLPTask) {

  val logger = LoggerFactory.getLogger(classOf[LLPProcessor])
  val llp = new LayeredLabelPropagation(graph, System.currentTimeMillis)
  val gammas = generateGammas(llpTask.nGammas, llpTask.gammaThreshold)

  logger.info("LLPProcessor with %s labels, %s gamma threshold and %s instantiated."
    .format(llpTask.nGammas, llpTask.gammaThreshold, llpTask.maxUpdates))

  /**
    * Generates gamma values in a similar way as described in the LLP paper but giving a threshold.
 *
    * @param n  Number of gammas (label)
    * @param threshold  maximum value of gamma
    * @return
    */
  def generateGammas(n: Int, threshold: Int): DoubleArrayList = {
    val gammas = new DoubleArrayList()
    val random = new Random

    0 until n foreach {
      i =>
        val randomThreshold = (threshold min i + 2)
        val r = random.nextInt(randomThreshold)
        val gamma = if(r == 0) 0 else math.pow(2.0, -(r - 1))

        gammas.add(gamma)
    }

    gammas
  }

  def dirPath() : String = {
    Paths.get(OldConfiguration.wikipedia("llp"), llpTask.toString).toString
  }

  /**
    *
    * @param path   Where save node labels.
    */
  def process(path: String = null) = {
    logger.info("LLP processing with gammas: %s".format(gammas.toString))

    val llpPath = if (path == null) dirPath() else path
    new File(llpPath).mkdirs

    saveGammas(llpPath)

    llp.labelBasename(Paths.get(llpPath, "llp_labels").toString)
    llp.computePermutation(gammas.toDoubleArray, null, llpTask.maxUpdates)
  }

  def saveGammas(path: String) = {
    val file = new PrintWriter(Paths.get(path, "gammas.json").toString)
    file.write(gammas.toString)
    file.close
  }
}
