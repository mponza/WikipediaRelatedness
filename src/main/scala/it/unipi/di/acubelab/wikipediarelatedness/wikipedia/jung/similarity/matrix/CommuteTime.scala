package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.similarity.matrix

import Jama.{Matrix, SingularValueDecomposition}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph.WikiJungGraph
import org.slf4j.LoggerFactory


class CommuteTime extends MatrixSimilarity {

  protected val logger = LoggerFactory.getLogger(getClass)


  /**
    * Computes svd pseduoinverse.
    *
    * Scala code adaptation from: http://the-lost-beauty.blogspot.it/2009/04/moore-penrose-pseudoinverse-in-jama.html
    *
    * @param x
    * @return
    */
  protected def pinv(x: Matrix): Matrix = {

    val rows = x.getRowDimension
    val cols = x.getColumnDimension

    if (rows < cols) {
      var result = pinv(x.transpose())
      if (result != null)
        result = result.transpose()
      return result
    }

    val svdX = new SingularValueDecomposition(x)
    if (svdX.rank() < 1) return null

    val singularValues = svdX.getSingularValues
    val tol = Math.max(rows, cols) * singularValues(0) * 2E-16

    val singularValueReciprocals = Array.ofDim[Double](singularValues.size)
    singularValues.filter(_ >= tol).zipWithIndex.foreach {
      case (v, i) => singularValueReciprocals(i) = 1.0  / v
    }

    val u = svdX.getU.getArray
    val v = svdX.getV.getArray

    val min = Math.min(cols, u(0).length)
    val inverse = Array.ofDim[Double](cols, rows)

    for {
      i <- 0 until cols
      j <- 0 until u.length
      k <- 0 until min
    } {
      inverse(i)(j) += v(i)(k) * singularValueReciprocals(k) * u(j)(k)
    }

    new Matrix(inverse)

  }



  override def similarity(srcWikiID: Int, dstWikiID: Int, wikiJungGraph: WikiJungGraph): Double = {

    val laplacian = new LaplacianGraph(wikiJungGraph)

    // one of the sources nodes have been removed
    if ( !laplacian.node2index.containsKey(srcWikiID) ||  !laplacian.node2index.containsKey(dstWikiID) ) return 0.0



    val jamaMatrix = new Matrix(laplacian.matrix)

    val lplus = pinv(jamaMatrix)

    val e = Array.ofDim[Double](wikiJungGraph.graph.getVertexCount)
    e( laplacian.node2index.get(srcWikiID) ) = 1.0
    e( laplacian.node2index.get(dstWikiID) ) = - 1.0


    val el = new Matrix(e, 1)


    // fix me, ritorna sempre matrici
     val v = Math.sqrt( el.times(lplus).times(el.transpose()).get(0, 0) ) // * laplacian.volume )


    println(v)
    println("---")

    1 / v

    /*
    Matrix c = b.times(a);

    val h = jamaMatrix.inverse()  // see Commute Time in https://www.autonlab.org/_media/exported/uai07_sarkar_moore_final.pdf

    val i = laplacian.node2index.get(srcWikiID)
    val j = laplacian.node2index.get(dstWikiID)


    // cosine
    val l_ij = h.get(i, j)
    val l_ii = h.get(i, i)
    val l_jj = h.get(j, j)
    val x = l_ij / Math.sqrt(l_ii * l_jj)
    logger.debug("Commute between %d and %d is %1.5f".format(x))
    return x

    // commute
    val c = h.get(i, j) + h.get(j, i)
    logger.debug("Commute time between %d and %d %1.5f".format(srcWikiID, dstWikiID, c))

    c*/
  }
}
