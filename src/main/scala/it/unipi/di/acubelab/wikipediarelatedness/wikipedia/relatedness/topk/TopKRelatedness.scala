package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.topk

import java.io.{File, FileOutputStream, PrintWriter}
import java.util.Locale

import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping.WikiTitleID
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessFactory, RelatednessOptions}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.TopKFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory


/**
  * TopK Relatedness without graph computation.
  *
  *   name:             topk
  *   topk:             esa, corpus, dw10
  *   threshold:        5, 10, 20, 30
  *
  *   weighter:         Relatedness and its parameters:
  *                         - milnewitten --weighterGraph in
  *                         - w2v --weighterModel {w2v.corpus, deepwalk.dw10}
  *
  * @param options
  */
class TopKRelatedness(val options: RelatednessOptions)  extends  Relatedness {
  protected val logger = LoggerFactory.getLogger(getClass)

  protected val topk = TopKFactory.make(options.topk)
  protected val weighter = RelatednessFactory.make( options.getWeighterRelatednessOptions )

  protected val graph = WikiBVGraphFactory.make("out")


  protected val corpus = TopKFactory.make("corpus400")


  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    if (srcWikiID == dstWikiID) return 1f

    val srcConcepts = topk.topKScoredEntities(srcWikiID, options.threshold)
    val dstConcepts = topk.topKScoredEntities(dstWikiID, options.threshold)

    save2File(srcWikiID, dstWikiID)

    Similarity.cosineSimilarity(
      changeweights(srcWikiID, srcConcepts ++ dstConcepts).sortBy(_._1),
      changeweights(dstWikiID, dstConcepts ++ srcConcepts).sortBy(_._1)
    )

  }


  protected def changeweights(wikiID: Int, concepts: Seq[(Int, Float)]) = {
    concepts.filter {
      case pair => graph.wiki2node.containsKey(pair._1)
    }.map {
      case (wID, score) =>

        ( wID, weighter.computeRelatedness(wikiID, wID) )
    }
  }



  protected def save2File(srcWikiID: Int, dstWikiID: Int) = {
    val esa = topk
    val srcESA = esa.topKScoredEntities(srcWikiID, options.threshold)
    val dstESA = esa.topKScoredEntities(dstWikiID, options.threshold)

    val srcC = corpus.topKScoredEntities(srcWikiID, options.threshold)
    val dstC = corpus.topKScoredEntities(dstWikiID, options.threshold)

    //val dw10 =TopKFactory.make("dw10")
    //val srcDW = esa.topKScoredEntities(srcWikiID, options.threshold)
    //val dstDW = esa.topKScoredEntities(dstWikiID, options.threshold)


    val write = new PrintWriter( new FileOutputStream(new File("/tmp/analysis.csv"), true) )
    write.write( WikiTitleID.map(srcWikiID) + ", " + WikiTitleID.map(dstWikiID) + "\n")

    var src = srcESA.map( c => "\"(%s, %1.3f)\"".format(WikiTitleID.map(c._1), c._2) ) mkString ","
    write.write("%s-ESA,".format(WikiTitleID.map(srcWikiID)) + src + "\n")

    src = srcC.map( c => "\"(%s, %1.3f)\"".formatLocal(Locale.US,WikiTitleID.map(c._1), c._2) ) mkString ","
    write.write("%s-w2v-corpus,".format(WikiTitleID.map(srcWikiID)) + src + "\n")



    var dst = dstESA.map( c => "\"(%s, %1.3f)\"".format(WikiTitleID.map(c._1), c._2) ) mkString ","
    write.write("%s-ESA,".format(WikiTitleID.map(dstWikiID)) + dst + "\n")


    dst = dstC.map( c => "\"(%s, %1.3f)\"".formatLocal(Locale.US,WikiTitleID.map(c._1), c._2) ) mkString ","
    write.write("%s-w2v-corpus,".format(WikiTitleID.map(dstWikiID)) + dst + "\n")


    write.flush()
    write.close()
  }


  override def toString() : String = { "TopK_threshold:%d,weighter:%s".format(options.threshold, weighter.toString()) }
}
