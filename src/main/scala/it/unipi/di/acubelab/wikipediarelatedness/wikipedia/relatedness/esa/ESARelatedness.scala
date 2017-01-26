package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.esa

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.options.ESAOptions
import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.esa.ESA
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory



class ESARelatedness(val options: ESAOptions = new ESAOptions())  extends  Relatedness {
  val logger = LoggerFactory.getLogger(classOf[ESARelatedness])


  override def computeRelatedness(tasks: List[WikiRelateTask]) = {
    tasks.par.foreach{
      case task => task.machineRelatedness = computeRelatedness(task)
    }
  }


  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    if (srcWikiID == dstWikiID) return 1f

    val srcConcepts = ESA.wikipediaConcepts(srcWikiID, options.threshold).sortBy(_._1)
    val dstConcepts = ESA.wikipediaConcepts(dstWikiID, options.threshold).sortBy(_._1)

    Similarity.cosineSimilarity(srcConcepts, dstConcepts)
  }

  override def toString() : String = { "ESA_%s".format(options) }
}
