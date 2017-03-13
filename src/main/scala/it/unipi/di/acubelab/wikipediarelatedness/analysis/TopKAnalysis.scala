package it.unipi.di.acubelab.wikipediarelatedness.analysis

import java.io.FileWriter

import it.unipi.di.acubelab.wikipediarelatedness.dataset.DatasetFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph.SubNodeCreatorFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.subgraph.topk.TopKSubNodeCreator
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.TopK
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer


class TopKAnalysis {
  protected val logger = LoggerFactory.getLogger(getClass)

  protected val outBVgraph = WikiBVGraphFactory.make("out")
  protected val inBVgraph = WikiBVGraphFactory.make("in")


  def percentileTopK2Graph(topk: TopKSubNodeCreator, outpath: String) = {
    // Entity pairs
    val entityParis = DatasetFactory.datasets().flatten.map(x => (x.src.wikiID, x.dst.wikiID))

    val analysis = new ListBuffer[String] // srcWikiID,dstWikiID,label

    entityParis.foreach {
      case pair =>
        val src = pair._1
        val dst = pair._2

        val nodes = topk.subNodes(src, dst)


        nodes.foreach {
          case node =>
            val srcLabel = whatNeighbor(src, node)
            val srcRow = "%d,%d,%s".format(src, node, srcLabel)
            analysis += srcRow

            val dstLabel = whatNeighbor(dst, node)
            val dstRow = "%d,%d,%s".format(dst, node, dstLabel)
            analysis += dstRow
        }
    }

    storeAnalysis(analysis.toList, outpath)
  }


  /**
    * Single entity analysis
    *
    * @param topk
    * @param outpath
    */
  def analysisTopK2Graph(topk: TopK, outpath: String) = {
    // Unique entities
    val entities = DatasetFactory.datasets().flatten.foldLeft(List.empty[Int])((IDs, task) => IDs ++ List(task.src.wikiID, task.dst.wikiID)).distinct

    val analysis = new ListBuffer[String] // srcWikiID,dstWikiID,label

    entities.foreach {
      case src =>
        val topKentities = topk.topKEntities(src, 15)

        topKentities.foreach {
          case dst =>

            val label = whatNeighbor(src, dst)
            val row = "%d,%d,%s".format(src, dst, label)

            analysis += row
        }
    }

    storeAnalysis(analysis.toList, outpath)
  }


  def whatNeighbor(srcWikiID: Int, dstWikiID: Int) : String = {
    val outNeighs = outBVgraph.successorArray(srcWikiID)
    val inNeighs = inBVgraph.successorArray(srcWikiID)

    if (outNeighs.contains(dstWikiID) && inNeighs.contains(dstWikiID)) { "out+in" }
    else if (outNeighs.contains(dstWikiID)) { "out" }
    else if (inNeighs.contains(dstWikiID)) { "in" } else { "none" }
  }


  def storeAnalysis(analysis: List[String], outfile: String) = {
    val file = new FileWriter(outfile)
    file.write(analysis mkString "\n")
    file.close()
  }

}


object TopKAnalysis {

  def mainTest(args: Array[String]) = {
    val topKAnalysis = new TopKAnalysis


    val subnode = SubNodeCreatorFactory.make(args(0), 30)
    topKAnalysis.percentileTopK2Graph(subnode, args(1))
    //val topk = TopKFactory.make(args(0))
    //topKAnalysis.analysisTopK2Graph(topk, args(1))
  }

}
