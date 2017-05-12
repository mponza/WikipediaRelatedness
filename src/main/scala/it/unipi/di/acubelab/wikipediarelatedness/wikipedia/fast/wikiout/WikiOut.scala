package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast.wikiout


import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import org.slf4j.LoggerFactory


/**
  * Class with the preprocessed information from Wikipedia out.
  */
class WikiOut {
  protected val logger = LoggerFactory.getLogger(getClass)

  // Top-30 for each Wikipedia ID, sorted by their M&W score
  protected val wikiID2TopKOut = BinIO.loadObject( Config.getString( "wikipedia.cache.fast.wikiout.topk" ) )
                                      .asInstanceOf[ Int2ObjectOpenHashMap[ Array[Int] ] ]

  // (M&W + DW) / 2 score for every Wikipedia ID and its top30 out nodes
  protected val wikiIDs2MWDW = BinIO.loadObject( Config.getString( "wikipedia.cache.fast.wikiout.mwdw" ) )
                                    .asInstanceOf[ Long2FloatOpenHashMap ]

  /**
    * TopK of WikiID respect with its out neighbors according with its Milne&Witten score
    * @param wikiID
    * @return
    */
  def topK(wikiID: Int) = wikiID2TopKOut.get(wikiID)


  /**
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return -1 relatedness between srcWikiID and dstWikiID not yet computed.
    */
  def relatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    val wikiIDs = wikiIDs2Long(srcWikiID, dstWikiID)
    wikiIDs2MWDW.getOrDefault(wikiIDs, -1f)
  }


  protected def wikiIDs2Long(srcWikiID: Int, dstWikiID: Int) = {
    val src = Math.min(srcWikiID, dstWikiID)
    val dst = Math.max(srcWikiID, dstWikiID)

    val srcShifted = src.asInstanceOf[Long] << 32
    srcShifted | dst
  }


  def getWikiIDTopKOut = wikiID2TopKOut

}


