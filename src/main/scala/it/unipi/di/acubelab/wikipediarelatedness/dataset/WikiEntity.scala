package it.unipi.di.acubelab.wikipediarelatedness.dataset

/**
  * Wikipedia Entity.
  *
  * @param wikiID
  * @param wikiTitle
  */
class WikiEntity(val wikiID: Int, val wikiTitle: String) {

  override def toString() : String = wikiTitle
}
