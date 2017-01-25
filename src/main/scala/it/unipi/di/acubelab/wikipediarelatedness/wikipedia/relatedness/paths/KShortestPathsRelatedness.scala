package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.paths

import it.unipi.di.acubelab.wikipediarelatedness.options.KShortestPathsOptions
import it.unipi.di.acubelab.wikipediarelatedness.utils.ArithmeticFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.algorithms.JungKShortestPaths
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.jung.graph.JungCliqueWikiGraph
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping.WikiTitleID
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.subgraph.SubWikiGraphFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness
import org.slf4j.LoggerFactory

class KShortestPathsRelatedness(options: KShortestPathsOptions = new KShortestPathsOptions()) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[KShortestPathsRelatedness])

  // function to map path weights upon a number
  val pathFun = ArithmeticFactory.make(options.pathFun)
  // function to map the number of each path to a unique one
  val kFun = ArithmeticFactory.make(options.kFun)
  // function to combine two k-shortest path from src to dst
  val combFun = ArithmeticFactory.make(options.combFun)


  def computeRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    if (srcWikiID == dstWikiID) return 1f

    val wgSubGraph = SubWikiGraphFactory.make(options.subGraph, srcWikiID, dstWikiID, "outGraph", options.threshold)
    val subGraph = new JungCliqueWikiGraph(wgSubGraph)
    val kSP = new JungKShortestPaths(subGraph, options.weighting, options.k)

    // Shortest Path from src -> dst and viceversa
    val srcPaths = kSP.topKShortestPaths(srcWikiID, dstWikiID)
    val dstPaths = kSP.topKShortestPaths(dstWikiID, srcWikiID)

    printPaths(srcWikiID, dstWikiID, srcPaths)
    printPaths(dstWikiID, srcWikiID, dstPaths)

    val rel = combFun(List(weightedPaths2Score(srcPaths), weightedPaths2Score(dstPaths)))

    logger.debug("Relatedenss between %d and %d is %1.2f".format(srcWikiID, dstWikiID, rel))

    if (rel.isNaN) throw new IllegalArgumentException("Spaccato tutto!")
    rel
  }


  def weightedPaths2Score(paths: List[List[Tuple2[Int, Float]]]) = {
    val pathsWeights = paths.map(_.map(_._2))
    // one score for each path
    val pathScores = pathsWeights.map(pathFun)
    // one score from the k paths
   kFun(pathScores)
  }


  override def toString(): String = {
    "KShortestPaths_%s".format(options)
  }



  def printPaths(src: Int, dst: Int, paths: List[List[Tuple2[Int, Float]]]) = {
    logger.info("Top paths from %s to %s".format(WikiTitleID.map(src), WikiTitleID.map(dst)))


    val strPath = paths.map {
      case path =>

        path.map {
          case pair => "(%s, %1.3f)".format(WikiTitleID.map(pair._1), pair._2)
        } mkString "->"

    } mkString "\n"

    logger.info("%s".format(strPath))
  }
}