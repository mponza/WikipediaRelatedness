package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.lm

import edu.berkeley.nlp.lm.io.LmReaders
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessOptions}
import org.slf4j.LoggerFactory


class LMRelatedness(options: RelatednessOptions) extends Relatedness {
  protected val logger = LoggerFactory.getLogger(getClass)
  protected val nGramModel = LmReaders.readLmBinary[String](Config.getString("wikipedia.language_model"))



  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = {
    val srcEntityName = "ent_%s".format(srcWikiID)
    val dstEntityName = "ent_%s".format(dstWikiID)

    val list = new java.util.ArrayList[String](2)

    list.add(srcEntityName)
    list.add(dstEntityName)

    val logProb = nGramModel.getLogProb(list)
    math.pow(2.0, logProb).toFloat
  }

  override def toString(): String = {
    "LMRelatedness"
  }
}