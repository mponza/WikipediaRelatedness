package it.unipi.di.acubelab.wikipediarelatedness.dataset.nyt

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiEntity

class NYTEntity(wikiID: Int, wikiTitle: String, val frequency: Int)

  extends WikiEntity(wikiID, wikiTitle) {

  override def toString() = "%d,\"%s\",%d".format(wikiID, wikiTitle, frequency)
}
