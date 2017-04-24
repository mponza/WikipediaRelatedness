package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.fast.relatedness.{FastEntity2VecRelatedness, FastMWEmbeddingRelatedness, FastMilneWittenRelatedness}


class FastLambdaMWE2VAlgoScheme(milnewittenCompressed: Boolean, e2vCompressed: Boolean) extends FastLambdaAlgoScheme  {


   def getMWEmbeddingWeighter() = {
      val mw = new FastMilneWittenRelatedness(milnewittenCompressed)
      val e2v = new FastEntity2VecRelatedness(e2vCompressed)

      new FastMWEmbeddingRelatedness(mw, e2v)
   }

}
