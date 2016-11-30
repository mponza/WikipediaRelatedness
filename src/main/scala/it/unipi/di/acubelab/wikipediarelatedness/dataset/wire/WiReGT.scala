package it.unipi.di.acubelab.wikipediarelatedness.dataset.wire

import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import org.slf4j.LoggerFactory

class WiReGT(path: String) extends WiReDataset(path) {
  override val logger = LoggerFactory.getLogger(classOf[WiReGT])
  val nytPairs = loadNYTPairs(path)

  override def toString() : String = {
    val salience = path.substring(path.lastIndexOf("/" ) + 1, path.lastIndexOf("."))  // filename with no extension
    "%sGT-%s".format(super.toString(), salience)
  }
}


object WiReGT {

  def makeDatasets() = {
    List(
      new WiReGT(Configuration.wiReGT("ss")),
      new WiReGT(Configuration.wiReGT("ns")),
      new WiReGT(Configuration.wiReGT("nn"))
    )
  }
}