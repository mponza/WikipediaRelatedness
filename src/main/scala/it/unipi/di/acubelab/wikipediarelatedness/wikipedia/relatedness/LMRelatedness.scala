package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import edu.berkeley.nlp.lm.io.LmReaders
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.options.LMOptions
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration


class LMRelatedness(options: LMOptions) extends Relatedness {

  lazy val nGramModel = LmReaders.readLmBinary[String](Configuration.wikipedia("langModel"))

  override def computeRelatedness(wikiRelTask: WikiRelateTask): Double = {
    val srcWikiID = wikiRelTask.src.wikiID
    val dstWikiID = wikiRelTask.dst.wikiID

    import scala.collection.JavaConversions._
    val ents = List("ent_%d".format(srcWikiID), "ent_%d".format(dstWikiID))

    (nGramModel.scoreSentence(ents) + nGramModel.scoreSentence(ents.reverse)) * 0.5
  }

  override def toString() : String = {
    "LMRelatedness"
  }
}
