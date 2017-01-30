package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.lm

import edu.berkeley.nlp.lm.io.LmReaders
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessOptions}


class LMRelatedness(options: RelatednessOptions) extends Relatedness {
  val nGramModel = LmReaders.readLmBinary[String](Config.getString("wikipedia.language_model"))


  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = {

    import scala.collection.JavaConversions._
    val ents = List("ent_%d".format(srcWikiID), "ent_%d".format(dstWikiID))

    (nGramModel.scoreSentence(ents) + nGramModel.scoreSentence(ents.reverse)) * 0.5f
  }

  override def toString(): String = {
    "LMRelatedness"
  }
}