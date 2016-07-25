package it.unipi.di.acubelab.graphrel.wikipedia.processing.llp

import java.io.File
import java.nio.file.Paths

import it.unimi.dsi.fastutil.ints.{Int2ObjectOpenHashMap, IntArrayList}
import it.unimi.dsi.fastutil.io.BinIO
import it.unipi.di.acubelab.graphrel.utils.Configuration
import org.slf4j.LoggerFactory

import scala.io.Source
import scala.util.parsing.json.JSON

class LLPLabels(nLabels: Int  = 10, gammaThreshold: Int = 32) {
  val logger = LoggerFactory.getLogger(classOf[LLPLabels])
  val gammas = loadGammas()
  val labels = loadLabels()


  def loadGammas() : List[Double] = {
    val gammasPath = Paths.get(labelsDirectory(), "gammas.json").toString
    val gammasStr = Source.fromFile(gammasPath).getLines.mkString
    val gammas = JSON.parseFull(gammasStr)

    gammas match {
      case Some(gammaDoubles: List[Double]) =>
        logger.info("Loaded gamma file with gammas: %s".format(gammaDoubles.toString))
        gammaDoubles

      case _ => throw new IllegalArgumentException("Can not parsing gammas.json file.")
    }
  }

  /**
    *
    * @return Map(wikiID) => [label at each iteration]?
    */
  def loadLabels() : Int2ObjectOpenHashMap[IntArrayList] = {
    logger.info("LLPLabels loading...")
    val labels =  new Int2ObjectOpenHashMap[IntArrayList]

    // Foreach files read the whole mapping at the corresponding LLP iteration.
    for (llpFile <- llpLabelsFiles()) {
      val it = BinIO.asIntIterator(llpFile)
      var nodeIndex = 0 // index of the Wikipedia node in the mapped BVgraph (see WikiBVGraph.wiki2node/node2wiki)

      while(it.hasNext) {
        val label = it.nextInt()
        labels.putIfAbsent(nodeIndex, new IntArrayList)
        labels.get(nodeIndex).add(label)
        nodeIndex += 1
      }
    }

    labels
  }



  def labelsDirectory() : String = {
    Paths.get(Configuration.wikipedia("llp"), "llp-Labels:_%s-Threshold:%s".format(nLabels, gammaThreshold)).toString
  }

  def llpLabelsFiles() : Array[File] = {
    new File(labelsDirectory).listFiles.filter(_.getName.startsWith("llp_labels-"))
  }
}
