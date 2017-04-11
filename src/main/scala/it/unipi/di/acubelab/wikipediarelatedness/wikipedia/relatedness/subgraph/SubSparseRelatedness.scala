package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.subgraph

import java.io.FileWriter
import java.util.Locale

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph.WikiJungSparseGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.WikiBVDistance
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory


class SubSparseRelatedness(options: RelatednessOptions) extends SubGraphRelatedness(options) {

  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = {
    val nodes = subNodeCreator.subNodes(srcWikiID, dstWikiID)
    val subGraph = new WikiJungSparseGraph(srcWikiID, dstWikiID, nodes, weighter)

    println("Node length %d" format nodes.size)

    distance(srcWikiID, nodes)
    distance(dstWikiID, nodes)

    simRanker.similarity(srcWikiID, dstWikiID, subGraph).toFloat
  }



  def distance(query: Int, nodes: Seq[Int]) = {
    println("Distancing for %d..." format query)
    nodes.foreach {
      case n => if(n != query) SubSparseRelatedness.updateDistance(query, n)
    }
  }


  override def toString() = "Sparse_%s".format(subGraphString())
}



object SubSparseRelatedness {

  val inDistance = new WikiBVDistance(WikiBVGraphFactory.make("ef.in"))
  val outDistance = new WikiBVDistance(WikiBVGraphFactory.make("ef.out"))


  def updateDistance(src: Int, dst: Int) = {


    var inDist = inDistance.getDistance(src, dst)
    var outDist = outDistance.getDistance(src, dst)


    println("============= From %d to %d distance is %d" format (src, dst, inDist) )
    val inFile = new FileWriter("/tmp/in.csv", true)
    inFile.write( "%d,%d,%d\n" format (src, dst, inDist) )
    inFile.close()

    val outFile = new FileWriter("/tmp/out.csv", true)
    outFile.write( "%d,%d,%d\n" format (src, dst, outDist) )
    outFile.close()

    val avgFile = new FileWriter("/tmp/avg.csv", true)
    if(inDist == -1f || outDist == -1f) println("%d and %d is -1" format (src, dst) )
    val avgDist = if(inDist == -1f || outDist == -1f) -1f else (outDist + inDist) * 0.5f
    avgFile.write( "%d,%d,%1.2f\n" formatLocal (Locale.US, src, dst, avgDist) )
    avgFile.close()


  }




}