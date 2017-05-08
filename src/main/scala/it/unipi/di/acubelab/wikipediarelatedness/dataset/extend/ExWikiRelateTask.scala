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

  var srcOutDegree = 0
  var srcInDegree = 0

  var dstOutDegree = 0
  var dstInDegree = 0

  var srcPageRank = 0f
  var dstPageRank = 0f

  // Position in the PageRank's rankings
  var srcPRpos = -1
  var dstPRpos = -1

  val rel = wikiRelateTask.humanRelatedness

  var src2DstDistance = 0f
  var dst2SrcDistance = 0f

  var outJaccard = 0f
  var inJaccard = 0f


  override def toString = {
    val src = "%d,\"%s\",%d,%d,%1.10f,%d".formatLocal(Locale.US, srcWikiID, srcWikiTitle, srcOutDegree, srcInDegree, srcPageRank, srcPRpos)
    val dst = "%d,\"%s\",%d,%d,%1.10f,%d".formatLocal(Locale.US, dstWikiID, dstWikiTile, dstOutDegree, dstInDegree, dstPageRank, dstPRpos)

    //"%s,%s,%1.2f,%d,%1.5f,%1.5f".formatLocal(Locale.US, src, dst, rel, distance, outJaccard, inJaccard)
    // srcWikiID,dstWikiID,srcDegIn,srcDegOut,dstDegIn,dstDegOut,src2dstDist,dst2srcDist,rel
    "%d,%d,%d,%d,%d,%d,%1.2f,%1.2f,%1.5f,%1.2f".formatLocal(Locale.US,
      srcWikiID,dstWikiID,
      srcInDegree,srcOutDegree,
      dstInDegree,dstOutDegree,
      src2DstDistance,dst2SrcDistance,inJaccard,rel)
    //"%s,%s,%1.2f,%d,%1.5f,%1.5f".formatLocal(Locale.US, src, dst, rel, distance, outJaccard, inJaccard)
  }


  def header() = {
    //val src = "srcWikiID,srcWikiTitle,srcOutDegree,srcInDegree,srcPageRank,srcPRpos"

    //val dst = ",dstWikiID,dstWikiTile,dstOutDegree,dstInDegree,dstPageRank,dstPRpos,"
    //val pair = "rel,distance,outJaccard,inJaccard"

    //src + dst + pair

    "srcWikiID,dstWikiID,srcDegIn,srcDegOut,dstDegIn,dstDegOut,src2dstDist,dst2srcDist,jaccIn,rel"
  }
}

