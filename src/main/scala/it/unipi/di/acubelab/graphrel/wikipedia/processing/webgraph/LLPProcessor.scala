package it.unipi.di.acubelab.graphrel.wikipedia.processing.webgraph

import java.io.{File, PrintWriter}
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.Calendar

import it.unimi.dsi.fastutil.doubles.DoubleArrayList
import it.unimi.dsi.law.graph.LayeredLabelPropagation
import it.unimi.dsi.util.XorShift1024StarRandom
import it.unimi.dsi.webgraph.BVGraph

/**
  * TODO: Multiple runnings. More labels?
  * @param graph
  * @param nLabels  aka number of gammas/clusters
  * @param gammaThreshold Ha davvero senso?
  */
class LLPProcessor(graph: BVGraph, nLabels: Int = 10, gammaThreshold: Int = 32) {
  val llp = new LayeredLabelPropagation(graph, System.currentTimeMillis)
  val gammas = generateGammas(nLabels, gammaThreshold)

  /**
    * Generates gamma values in a similar way as described in the LLP paper.
    * @param n  Number of gammas (label)
    * @param threshold
    * @return
    */
  def generateGammas(n: Int, threshold: Int): DoubleArrayList = {
    val gammas = new DoubleArrayList()
    val random = new XorShift1024StarRandom()

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
    val format = new SimpleDateFormat("d-M-y_HH:mm:ss")
    val timeStr = format.format(Calendar.getInstance.getTime)

    "llp-N_%s-T_%s-%s".format(nLabels, gammaThreshold, timeStr).toString
  }

  /**
    *
    * @param path   Where save node labels.
    */
  def process(path: String = null) = {
    val llpPath = if (path == null) dirPath() else path
    new File(llpPath).mkdirs

    saveGammas(llpPath)

    llp.labelBasename(llpPath)
    llp.computePermutation(gammas.toDoubleArray, null)

  }

  def saveGammas(path: String) = {
    val file = new PrintWriter(Paths.get(path, "gammas.json").toString)
    file.write(gammas.toString)
    file.close
  }
}
