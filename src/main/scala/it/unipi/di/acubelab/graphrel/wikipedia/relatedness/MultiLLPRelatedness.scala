package it.unipi.di.acubelab.graphrel.wikipedia.relatedness

import java.nio.file.Paths

import it.unipi.di.acubelab.graphrel.dataset.WikiRelTask
import it.unipi.di.acubelab.graphrel.utils.Configuration
import it.unipi.di.acubelab.graphrel.wikipedia.processing.llp.LLPTask
import org.slf4j.LoggerFactory

import scala.collection.mutable

class MultiLLPRelatedness(options: Map[String, Any]) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[LLPRelatedness])

  val nLLP = options.getOrElse("nLLP", 10.0).asInstanceOf[Double].toInt
  val llpTask = LLPTask.makeFromOption(options)

  val multiLLPRels = loadMultiLLPRelatedness()

  def loadMultiLLPRelatedness() : List[LLPRelatedness] = {
    val multiLLPRels = mutable.MutableList.empty[LLPRelatedness]

    for(i <- 0 until nLLP) {
      val llpPath = Paths.get(dirPath(), "llp-%d".format(i)).toString
      multiLLPRels += new LLPRelatedness(options, llpPath)
    }

    multiLLPRels.toList
  }

  def computeRelatedness(wikiRelTask: WikiRelTask) : Double = {
    multiLLPRels.map(llpRels => llpRels.computeRelatedness(wikiRelTask)).sum / nLLP.toDouble
  }

  override def toString() : String = {
    "MultiLLPRelatedness-nLLP_%d-%s".format(nLLP, llpTask)
  }


  def dirPath() : String = {
    Paths.get(Configuration.wikipedia("multiLLP"), "multiLLP-nLLP_%d-%s".format(nLLP, llpTask.toString)).toString
  }
}
