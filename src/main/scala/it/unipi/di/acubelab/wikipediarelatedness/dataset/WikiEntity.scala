package it.unipi.di.acubelab.wikipediarelatedness.dataset

class WikiEntity(val wikiID: Int, val wikiTitle: String) {

  override def toString() : String = {
    wikiTitle
  }
}
