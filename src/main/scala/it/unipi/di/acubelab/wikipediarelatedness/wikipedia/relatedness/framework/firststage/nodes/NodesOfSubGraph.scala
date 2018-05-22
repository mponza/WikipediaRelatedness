package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.firststage.nodes

trait NodesOfSubGraph {

  /**
    * Returns the top nodes of wikiID that will populate the Wikipedia SubGraph.
    * @return A list of wikiIDs.
    */
  def topNodes(wikiID: Int): Seq[Int]

}
