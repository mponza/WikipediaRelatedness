package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.similarity.matrix

import Jama.Matrix
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph.WikiJungGraph


class CommuteTime extends MatrixSimilarity {

  override def similarity(srcWikiID: Int, dstWikiID: Int, wikiJungGraph: WikiJungGraph): Double = {
    val laplacian = new LaplacianGraph(wikiJungGraph)
    val jamaMatrix = new Matrix(laplacian.matrix)

    val h = jamaMatrix.inverse()  // see Commute Time in https://www.autonlab.org/_media/exported/uai07_sarkar_moore_final.pdf

    val i = laplacian.node2index.get(srcWikiID)
    val j = laplacian.node2index.get(dstWikiID)


    // cosine
    //val l_ij = h.get(i, j)
    //val l_ii = h.get(i, i)
    //val l_jj = h.get(j, j)

    // commute
    h.get(i, j) + h.get(j, i)
  }
}
