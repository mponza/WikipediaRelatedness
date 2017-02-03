package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.esa.ESA

class ESAEntityTopK extends TopK {

  override def topKScoredEntities(wikiID: Int, k: Int): Seq[(Int, Float)] = {
      ESA.wikipediaConcepts("ent_%d".format(wikiID), k)
  }

}
