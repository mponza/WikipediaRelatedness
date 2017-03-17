package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast

import it.unimi.dsi.fastutil.io.BinIO
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory

/**
  * Faster implementation of the Algorithmic Scheme for computing Relatedness and evaluating its performance
  * with compressed/uncompressed data.
  */
class FastASR(milnewittenCompressed: Boolean = true, deepwalkCompressed: Boolean = false) {
  protected val logger = LoggerFactory.getLogger(getClass)

  val wikiID2ScoredOuts = BinIO.loadObject(Config.getString("wikipedia.cache.fast.storedout"))

  val mw = new FastMilneWittenRelatedness(milnewittenCompressed)
  val dw = new FastDeepWalkRelatedness(deepwalkCompressed)
}
