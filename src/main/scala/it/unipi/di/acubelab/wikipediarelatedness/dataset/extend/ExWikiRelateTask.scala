package it.unipi.di.acubelab.wikipediarelatedness.dataset.extend

import java.util.Locale

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask


/**
  * Class containing the extended version of WikiRelateTask
  *
  * @param wikiRelateTask
  */
class ExWikiRelateTask(wikiRelateTask: WikiRelateTask) {
  val srcWikiID = wikiRelateTask.src.wikiID
  val srcWikiTitle = wikiRelateTask.src.wikiTitle

  val dstWikiID = wikiRelateTask.dst.wikiID
  val dstWikiTile = wikiRelateTask.dst.wikiTitle

  val rel = wikiRelateTask.humanRelatedness

  var distance = 0

  var srcOutDegree = 0
  var srcInDegree = 0

  var dstOutDegree = 0
  var dstInDegree = 0

  var srcPageRank = 0f
  var dstPageRank = 0f

  // Position in the PageRank's rankings
  var srcPRpos = -1
  var dstPRpos = -1


  override def toString = {
    val src = "%d,\"%s\",%d,%d,%1.10f,%d".formatLocal(Locale.US, srcWikiID, srcWikiTitle, srcOutDegree, srcInDegree, srcPageRank, srcPRpos)
    val dst = "%d,\"%s\",%d,%d,%1.10f,%d".formatLocal(Locale.US, dstWikiID, dstWikiTile, dstOutDegree, dstInDegree, dstPageRank, dstPRpos)

    "%s,%s,%1.2f,%d".formatLocal(Locale.US, src, dst, rel, distance)
  }


  def header() = "srcWikiID,srcWikiTitle,srcOutDegree,srcInDegree,srcPageRank,dstWikiID,dstWikiTile,dstOutDegree,dstInDegree,dstPageRank,rel,distance"
}

