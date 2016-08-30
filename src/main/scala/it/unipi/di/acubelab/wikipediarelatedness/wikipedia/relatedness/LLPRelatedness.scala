package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness
import it.unimi.dsi.fastutil.ints.{Int2DoubleOpenHashMap, IntArrayList}
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelTask
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.llp.{LLPClustering, LLPTask}
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

/**
  *
  * @param options Warning: before using this, you have to process the graph via LLP with the relative parameters.
  *                {
  *                   "nLabels"
                      "gammaThreshold"
                      "maxUpdates"
  *                }
  */
class LLPRelatedness(options: Map[String, Any], dirPath: String = null) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[LLPRelatedness])

  val llpTask = LLPTask.makeFromOption(options)
  val simName = options.getOrElse("similarity", "hamming").toString

  val llpClustering = new LLPClustering(llpTask, dirPath)

  override def computeRelatedness(wikiRelTask: WikiRelTask): Double = {
    val srcWikiID = wikiRelTask.src.wikiID
    val dstWikiID = wikiRelTask.dst.wikiID

    val srcVec = llpClustering.labels.get(srcWikiID)
    val dstVec = llpClustering.labels.get(dstWikiID)

    val sim = Similarity.computeSimilarity(simName, srcVec, dstVec)

    sim / srcVec.size.toDouble
  }

  override def toString(): String = {
    "LLP-%s".format(llpTask.toString)
  }


  // Warning: Sperimental Functions

  def probabilityVector(wikiID: Int) : ObjectArrayList[Tuple2[Int, Double]] = {
    val probsMap = new Int2DoubleOpenHashMap

    val vec = llpClustering.labels.get(wikiID)
    for (i <- 0 until vec.size) {
      val label = vec.getInt(i)

      val prob = probsMap.getOrDefault(label, 0.0)
      probsMap.put(label, (prob + 1.0) / vec.size())
    }

    val probs = new ObjectArrayList[Tuple2[Int, Double]]

    val labels = probsMap.keySet().toIntArray.sorted
    for(index <- 0 until labels.length) {
      val label = labels(index)
      probs.add((label, probsMap.get(label)))
    }

    probs
  }

  def computeCosineRelatedness(wikiRelTask: WikiRelTask): Double = {
    val (srcWikiID, dstWikiID) = wikiRelTask.wikiIDs()

    val srcVec = probabilityVector(srcWikiID)
    val dstVec = probabilityVector(dstWikiID)

    Similarity.cosineSimilarity(srcVec, dstVec)
  }
}