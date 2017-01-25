package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import edu.berkeley.nlp.lm.io.LmReaders
import it.unipi.di.acubelab.wikipediarelatedness.options.LMOptions
import it.unipi.di.acubelab.wikipediarelatedness.utils.OldConfiguration


class LMRelatedness(options: LMOptions) extends Relatedness {

  lazy val nGramModel = LmReaders.readLmBinary[String](OldConfiguration.wikipedia("langModel"))

  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = {

    import scala.collection.JavaConversions._
    val ents = List("ent_%d".format(srcWikiID), "ent_%d".format(dstWikiID))

    (nGramModel.scoreSentence(ents) + nGramModel.scoreSentence(ents.reverse)) * 0.5f
  }

  override def toString(): String = {
    "LMRelatedness"
  }
}