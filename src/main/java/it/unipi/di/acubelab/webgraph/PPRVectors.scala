package it.unipi.di.acubelab.webgraph

import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer


/**
  * Class filled by PPRParallelGaussSeidel's stepUntil method.
  *
  */
class PPRVectors {
  protected val logger = LoggerFactory.getLogger(getClass)
  val pprs = ListBuffer.empty[Seq[Tuple2[Int, Float]]] // PageRank vectors for each iteration

  /**
    * Adds a ranks array to last position of pprs.
    *
    * @param ranks
    */
  def add(ranks: Array[Double]) = {
    // Non zero ranks, mapped into index, rank
    pprs += ranks.zipWithIndex.filter(_._1 != 0.0).map( x => (x._2, x._1.toFloat) )
    logger.info("Added %d non-zero ranks".format(pprs.last.size))
  }

}
