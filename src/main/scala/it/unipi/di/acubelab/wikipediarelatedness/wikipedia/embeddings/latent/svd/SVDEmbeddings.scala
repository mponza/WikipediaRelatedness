package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.latent.svd

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.latent.LatentWikiEmbeddings
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j


class SVDEmbeddings extends LatentWikiEmbeddings {
  protected override val matrix = Nd4j.zeros(4730474, 200)
  protected lazy val wikiGraph = WikiBVGraphFactory.makeWikiBVGraph("out")


  override def apply(wikiID: Int) : INDArray = super.apply(wikiGraph.getNodeID(wikiID))

}