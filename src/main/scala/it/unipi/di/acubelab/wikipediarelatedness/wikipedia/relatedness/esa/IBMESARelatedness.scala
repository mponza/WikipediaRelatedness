package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.esa

import it.unipi.di.acubelab.wikipediarelatedness.ibm.IBMFactory
import it.unipi.di.acubelab.wikipediarelatedness.options.IBMESAOptions
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory

class IBMESARelatedness(options: IBMESAOptions) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[IBMESARelatedness])
  val ibmESA = IBMFactory.make(options.threshold)


  override def computeRelatedness(srcWikiID: Int, dstWikiID: Int): Float = {
    try {
      ibmESA.getRelatedness(srcWikiID, dstWikiID)
    } catch {
        case e: Exception => Float.NaN
    }
  }


  override def toString(): String = {
    "IBMESA_threshold:%s".format(options)
  }
}
