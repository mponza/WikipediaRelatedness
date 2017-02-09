package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.lucene.esa

import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.set.MilneWittenRelatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessOptions}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.ESATopK
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory


/**
  * Explicit Semantic Analysis implementation via CoreNLP and BM25.
  *
  * @param options
  */
class ESARelatedness(val options: RelatednessOptions)  extends  Relatedness {
  val logger = LoggerFactory.getLogger(getClass)
  val esa = new ESATopK

  /*val mwopts = new RelatednessOptions(graph = "in")
  val graph = WikiBVGraphFactory.make("in")
  val mw = new MilneWittenRelatedness(mwopts)
*/
  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    if (srcWikiID == dstWikiID) return 1f


    val srcConcepts = esa.topKScoredEntities(srcWikiID, options.threshold).sortBy(_._1)
    val dstConcepts = esa.topKScoredEntities(dstWikiID, options.threshold).sortBy(_._1)


    Similarity.cosineSimilarity(srcConcepts, dstConcepts)

    /*

    works ok
    val srcConcepts = esa.topKScoredEntities(srcWikiID, options.threshold)//.sortBy(_._1)//ESA.wikipediaConcepts(srcWikiID, options.threshold).sortBy(_._1)
    val dstConcepts = esa.topKScoredEntities(dstWikiID, options.threshold)//.sortBy(_._1)//ESA.wikipediaConcepts(dstWikiID, options.threshold).sortBy(_._1)


    Similarity.cosineSimilarity(
      changeweights(srcWikiID, srcConcepts).sortBy(_._1),
      changeweights(dstWikiID, srcConcepts).sortBy(_._1)
    )*/

    Similarity.cosineSimilarity(srcConcepts, dstConcepts)
  }

/*
  protected def changeweights(wikiID: Int, concepts: Seq[(Int, Float)]) = {
    concepts.filter {
      case pair => graph.wiki2node.containsKey(pair._1)
    }.map {
      case (wID, score) =>

        ( wID, mw.computeRelatedness(wikiID, wID) )
    }
  }*/

  override def toString() : String = { "ESA_threshold:%d".format(options.threshold) }
}
