package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast

import it.unimi.dsi.fastutil.ints.{Int2ObjectOpenHashMap, IntOpenHashSet}
import it.unimi.dsi.fastutil.io.BinIO
import it.unipi.di.acubelab.wikipediarelatedness.utils
import it.unipi.di.acubelab.wikipediarelatedness.utils.{Config, Similarity}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast.relatedness.{FastDeepWalkRelatedness, FastMWDWRelatedness, FastMilneWittenRelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast.wikiout.WikiOut
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph.WikiJungSparseGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph.SubNodeCreatorFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping.WikiTitleID
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessFactory, RelatednessOptions}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.{MilneWittenTopK, TopKFactory}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

/**
  * Faster implementation of the Algorithmic Scheme for computing Relatedness and evaluating its performance
  * with compressed/uncompressed data.
  */
class FastAlgorithmicScheme(milnewittenCompressed: Boolean = true, deepwalkCompressed: Boolean = false) extends Relatedness {
  protected val logger = LoggerFactory.getLogger(getClass)
  protected val outSize = 30

  val wikiOut = new WikiOut

  val mwdw = {
    val options = new RelatednessOptions(name="mix", lambda=0.5,
      firstname="milnewitten", firstgraph="in",
      secondname="w2v", secondmodel="deepwalk.dw10"
    )
    RelatednessFactory.make(options)
    //val mw = new FastMilneWittenRelatedness(milnewittenCompressed)
    //val dw = new FastDeepWalkRelatedness(deepwalkCompressed)

    //new FastMWDWRelatedness(mw, dw)
  }

  val subNodeCreator = SubNodeCreatorFactory.make("mw.out", 30)

  //override def toString = "FastASR_mw:%s,dw:%s" format (mw, dw)

  /**
    * Computes the relatedness between two Wikipedia entities uniquely identified by their ID.
    *
    * @param srcWikiID
    * @param dstWikiID
    * @return
    */
  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {

    if (srcWikiID == dstWikiID) return 1f

    val srcVec = wikiOut.topK(srcWikiID)
    val dstVec = wikiOut.topK(dstWikiID)


    // Create Set of unique Wikipedia nodes
    val nodes = new IntOpenHashSet()

    nodes.add(srcWikiID)
    nodes.add(dstWikiID)

    var (i, j) = (0, 0)
    var n = nodes.size()

    while(i < srcVec.length && j < dstVec.length && nodes.size() < outSize) {

      while (i < srcVec.length && n == nodes.size() && nodes.size() < outSize) {
        nodes.add( srcVec(i) )
        i += 1
      }
      if (n != nodes.size()) n += 1

      while (j < dstVec.length && n == nodes.size() && nodes.size() < outSize) {
        nodes.add( dstVec(j) )
        j += 1
      }
      if (n != nodes.size()) n += 1

    }

    while (i < srcVec.length && nodes.size() < outSize) { nodes.add( srcVec(i) ) ; i += 1 }
    while (j < dstVec.length && nodes.size() < outSize) { nodes.add( dstVec(j) ) ; j += 1 }


    // Computes cosine similiarty where the score are given by their MWDWRelatedness

    //val graph = new WikiJungSparseGraph(srcWikiID, dstWikiID, nodes.toIntArray(), mwdw)

/*
    val sv = normalizedVector(nodes, srcWikiID, graph)
    val dv = normalizedVector(nodes, dstWikiID, graph)


    var inner = 0f
    var srcMagnitude = 0f
    var dstMagnitude = 0f
    for(i <- 0 until nodes.size()) {
      inner += sv(i) * dv(i)
      srcMagnitude +=  math.pow(sv(i), 2f).toFloat
      dstMagnitude +=  math.pow(dv(i), 2f).toFloat
    }

    if (inner == 0f) return 0f

     inner / (math.sqrt(srcMagnitude) * math.sqrt(dstMagnitude)).toFloat

    */


    val subGraph = new WikiJungSparseGraph(srcWikiID, dstWikiID, nodes.toIntArray(), mwdw)


    var inner = 0f
    var srcMagnitude = 0f
    var dstMagnitude = 0f

    val srcWeightVec = Array.ofDim[Float](nodes.size())
    val dstWeightVec = Array.ofDim[Float](nodes.size())

    val iterNode = nodes.iterator()
    i = 0
    var s = 0
    var d = 0
    val wikiIDs = new ListBuffer[Int]()
    var sNorm = 0f
    var dNorm = 0f
    while(iterNode.hasNext) {

      val nodeID = iterNode.nextInt()

      if (nodeID != srcWikiID && nodeID != dstWikiID) {

        //logger.warn("nodes")
        srcWeightVec(i) = getGraphWeight(subGraph, srcWikiID, nodeID)
        dstWeightVec(i) = getGraphWeight(subGraph, dstWikiID, nodeID)

        var sw = mwdw.computeRelatedness(srcWikiID, nodeID)
        var dw = mwdw.computeRelatedness(dstWikiID, nodeID)

        val z =  sw + dw
        val normsw = sw / z
        val normdw = dw / z


        //srcWeightVec(i) = sw
        //dstWeightVec(i) = dw

        sNorm += srcWeightVec(i)
        dNorm += dstWeightVec(i)
/*
        println("--- Algo ---")
        println("%d -> %d %1.3f" format (nodeID, srcWikiID, sw) )
        println("%d -> %d %1.3f" format (nodeID, dstWikiID, dw) )
        println("Norm factor %1.2f" format (z))
        println("Normalized %d -> %d %1.3f" format (nodeID, srcWikiID, normsw) )
        println("Normalized %d -> %d %1.3f" format  (nodeID, dstWikiID, normdw) )

        println("--- Weight ---")
        println("%d -> %d %1.3f" format (nodeID, srcWikiID, subGraph.weights.rawWs.get(getEdge(nodeID, srcWikiID))) )
        println("%d -> %d %1.3f" format (nodeID, dstWikiID, subGraph.weights.rawWs.get(getEdge(nodeID, dstWikiID))) )
        println("Norm factor %1.2f" format subGraph.weights.normSums.get(nodeID))
        println("Normalized %d -> %d %1.3f" format (nodeID, srcWikiID,  subGraph.weights.transform(getEdge(nodeID, srcWikiID)).toFloat) )
        println("Normalized %d -> %d %1.3f" format  (nodeID, dstWikiID, subGraph.weights.transform(getEdge(nodeID, dstWikiID)).toFloat) )


        println("========")
*/

        if(normsw != subGraph.weights.transform(getEdge(nodeID, srcWikiID)).toFloat ) {
          //println("AAAAAAAA %1.2f vs %1.2f" format (normsw, subGraph.weights.transform(getEdge(nodeID, srcWikiID)).toFloat ) )
        }

        if(normdw != subGraph.weights.transform(getEdge(nodeID, dstWikiID)).toFloat ) {
          //println("AAAAAAAA %1.2f vs %1.2f" format (normdw, subGraph.weights.transform(getEdge(nodeID, dstWikiID)).toFloat ) )
        }


      }

      if (nodeID == srcWikiID) {
        //logger.warn("src")
        srcWeightVec(i) = 0.1f
        dstWeightVec(i) =  getGraphWeight(subGraph, dstWikiID, nodeID)

        //sNorm += srcWeightVec(i)
        dNorm += dstWeightVec(i)
        /*
        dstWeightVec(i) = mwdw.computeRelatedness(dstWikiID, nodeID)
        srcWeightVec(i) = 0.1f*/
        s = i
      }

      if(nodeID == dstWikiID) {
        //logger.warn("dst")
        srcWeightVec(i) = getGraphWeight(subGraph, srcWikiID, nodeID)
        dstWeightVec(i) = 0.1f

        sNorm += srcWeightVec(i)
        //dNorm += dstWeightVec(i)
        /*
        srcWeightVec(i) = mwdw.computeRelatedness(srcWikiID, nodeID)
        dstWeightVec(i) = 0.1f*/
        d = i
      }

      wikiIDs += nodeID

      i += 1
    }

    //val pairs = srcWeightVec zip wikiIDs sortBy (_._2) mkString " "
    //if(srcWikiID == 17547) println( pairs)

    // in normalizzat e settare a 0.1

    val srcS = (srcWeightVec.sum - 0.1f) / .9f
    val dstS = (dstWeightVec.sum - 0.1f) / .9f


    val srcnormvec = srcWeightVec.map(_ / srcS)
    srcnormvec(s) = 0.1f
    val dstnormvec = dstWeightVec.map(_ / dstS)
    dstnormvec(d) = 0.1f

    Similarity.cosineSimilarity(srcnormvec.zipWithIndex.map(x => (x._2, x._1)), dstnormvec.zipWithIndex.map(x => (x._2, x._1)) )
    //Similarity.cosineSimilarity(srcWeightVec.zipWithIndex.map(x => (x._2, x._1)), dstWeightVec.zipWithIndex.map(x => (x._2, x._1)) )

/*
    // re-write it from scratch, try also with the graph weights...

    val norms = srcWeightVec zip dstWeightVec map ( x => x._1 + x._2 ) map (x => if (x == 0f) 1f else x )

    val srcNorm = srcWeightVec.zipWithIndex.map(x => x._1 / norms(x._2))
    val dstNorm = dstWeightVec.zipWithIndex.map(x => x._1 / norms(x._2))


    val sd = mwdw.computeRelatedness(srcWikiID, dstWikiID)

    val srcNorm1 = srcNorm.sum
    val dstNorm1 = dstNorm.sum

    srcNorm(d) = sd / dstNorm1
    dstNorm(d) = 0.1f

    srcNorm(s) = 0.1f
    dstNorm(s) = sd / srcNorm1*/






    //val srcNorm = srcWeightVec.zipWithIndex.map( x => (x._2, x._1 / norms(x._2) ) ).toSeq.toArray
    //val dstNorm = dstWeightVec.zipWithIndex.map( x => (x._2, x._1 / norms(x._2) ) ).toSeq.toArray


    //Similarity.cosineSimilarity(srcNorm.zipWithIndex.map(x => ((x._2, x._1))).toSeq,
    //                            dstNorm.zipWithIndex.map(x => ((x._2, x._1))).toSeq)




     //inner / ( math.sqrt(srcMagnitude) * math.sqrt(dstMagnitude) ).toFloat

  }


  protected def normalizedVector(nodes: IntOpenHashSet, src: Int, graph: WikiJungSparseGraph) : Array[Float] = {
    val array = Array.ofDim[Float](nodes.size())
    var sum = 0f

    val iter = nodes.iterator()
    var i = 0
    while(iter.hasNext) {
      val n = iter.nextInt()

      val srcWeight = if (n != src) mwdw.computeRelatedness(src, n) else 1f


      array(i) = srcWeight
      sum += srcWeight
      i += 1
    }

    val a = array.map(_ / sum)
a

    /*
    // Compare with graph
    i = 0
    while(iter.hasNext) {
      val n = iter.nextInt()

      val graphWeight = graph.weights.transform( getEdge(src, ) )
    }*/
  }


  def getEdge(src: Int, dst: Int) = {
    // src -> dst
    val srcShifted = src.asInstanceOf[Long] << 32
    srcShifted | dst
  }


  def getGraphWeight(graph: WikiJungSparseGraph, src: Int, dst: Int) : Float = {
    //logger.warn("weight...")
    graph.weights.transform(getEdge(src, dst)).toFloat
  }


  /*
  def check(vec: Array[I]) = {



  }*/



  override def toString = "AlgorithmicScheme_%s" format mwdw.toString
}
