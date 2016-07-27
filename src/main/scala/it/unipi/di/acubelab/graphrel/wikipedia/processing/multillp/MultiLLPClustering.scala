package it.unipi.di.acubelab.graphrel.wikipedia.processing.multillp

import java.nio.file.Paths

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unipi.di.acubelab.graphrel.utils.Configuration
import it.unipi.di.acubelab.graphrel.wikipedia.processing.llp.LLPTask
import it.unipi.di.acubelab.graphrel.wikipedia.processing.llp.LLPClustering
import org.slf4j.LoggerFactory

import scala.collection.mutable

class MultiLLPClustering(nLLP: Int, llpTask: LLPTask = new LLPTask) {
  val logger = LoggerFactory.getLogger(classOf[MultiLLPClustering])
  val multiClusters = loadMultiClusters()

  def loadMultiClusters() : List[LLPClustering] = {
    val multiClusters = mutable.MutableList.empty[LLPClustering]

    for(i <- 0 until nLLP) {
      val clusterPath = "%s-%d".format(multiClusterDir, i)
      multiClusters += new LLPClustering(llpTask, clusterPath)
    }

    multiClusters.toList
  }

  /**
    *
    * @param clusterIndex
    * @param wikiID
    * @return Array of labels of wikiID from the clusterIndex-th LLP cluster.
    */
  def labels(clusterIndex: Int, wikiID: Int) : IntArrayList = {
    multiClusters(clusterIndex).labels.get(wikiID)
  }

  def multiClusterDir() : String = {
    Paths.get(Configuration.wikipedia("multiLLP"), "-nLLP_%d".format(nLLP)).toString
  }

}
