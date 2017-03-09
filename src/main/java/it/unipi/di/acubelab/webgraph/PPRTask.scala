package it.unipi.di.acubelab.webgraph

import it.unimi.dsi.fastutil.doubles.DoubleArrayList
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer


/**
  * Class which contains the preference vector for PPRParallelGaussSeidel method and whose pprs field is filled by
  * steUntil method by saving the stationary distribution of PageRank.
  *
  */
class PPRTask(preferenceArray: Array[Double]) {
  protected val logger = LoggerFactory.getLogger(getClass)

  val preference =  new DoubleArrayList(preferenceArray)
  val pprs = ListBuffer.empty[Seq[Tuple2[Int, Float]]] // PageRank vectors for each iteration


  /**
    * Adds a ranks array to last position of pprs.
    *
    * @param ranks
    */
  def add(ranks: Array[Double]) = {
    // Non zero ranks, mapped into (index, rank) pair
    pprs += ranks.zipWithIndex.filter(_._1 != 0.0).map( x => (x._2, x._1.toFloat) )
  }

}