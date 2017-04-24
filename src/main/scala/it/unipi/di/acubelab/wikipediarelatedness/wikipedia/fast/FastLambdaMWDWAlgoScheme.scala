package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast.relatedness.{FastDeepWalkRelatedness, FastMWEmbeddingRelatedness, FastMilneWittenRelatedness}


class FastLambdaMWDWAlgoScheme(milnewittenCompressed: Boolean, deepwalkCompressed: Boolean)
  extends FastLambdaAlgoScheme {


  def getMWEmbeddingWeighter() = {
    val mw = new FastMilneWittenRelatedness(milnewittenCompressed)
    val dw = new FastDeepWalkRelatedness(deepwalkCompressed)

    new FastMWEmbeddingRelatedness(mw, dw)
  }
}