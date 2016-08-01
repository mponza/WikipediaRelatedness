package it.unipi.di.acubelab.graphrel.wikipedia.relatedness
import it.unipi.di.acubelab.graphrel.dataset.WikiRelTask
import it.unipi.di.acubelab.graphrel.wikipedia.processing.llp.{LLPClustering, LLPTask}
import org.slf4j.LoggerFactory

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
}