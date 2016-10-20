package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph

import it.unimi.dsi.fastutil.ints.{Int2DoubleArrayMap, Int2IntArrayMap, Int2ObjectOpenHashMap, IntOpenHashSet}

class ClusteringCoefficient(wikiBVGraph: WikiBVGraph) {
  val localClustCoeffCache = new Int2DoubleArrayMap    // local clustering coefficients already computed on wikiBVGraph.
                                                  // {wikiID: localClusteringScore}

  /**
    * @param wikiID
    * @return Mapping between the successors of wikiID and their successor list. Useful when contains is needed.
    *         Warning: the mapping uses nodeID for keys.
    */
  def successorsOfSuccessors(wikiID: Int) : Int2ObjectOpenHashMap[IntOpenHashSet] = {
    val succMap = new Int2ObjectOpenHashMap[IntOpenHashSet]

    val succIter = wikiBVGraph.successors(wikiID)
    var nodeID = succIter.nextInt
    while(nodeID != -1) {

      val nodeIter = wikiBVGraph.successors(WikiBVGraph.node2wiki.get(nodeID))
      var succID = nodeIter.nextInt
      succMap.put(nodeID, new IntOpenHashSet)
      while(succID != -1) {
        succMap.get(nodeID).add(succID)
        succID = nodeIter.nextInt
      }

      nodeID = succIter.nextInt
    }

    succMap
  }

  def localClusteringCoefficient(wikiID: Int): Double = {

    if (localClustCoeffCache.containsKey(wikiID)) return localClustCoeffCache.get(wikiID)

    val k = wikiBVGraph.outdegree(wikiID)
    if(k > 10000) println(k)
    if(k <= 1 || k > 30000) {
      localClustCoeffCache.put(wikiID, 0.0)
      return 0.0
    }

    var triangles = 0

    val succSucc = successorsOfSuccessors(wikiID)

    val iterJ = wikiBVGraph.successors(wikiID)
    var vJ = iterJ.nextInt()
    while(vJ != -1) {
      val wikiIDJ = WikiBVGraph.getWikiID(vJ)

      val iterK = wikiBVGraph.successors(wikiID)
      var vK = iterK.nextInt
      while(vK != -1) {

        if(vK != vJ) {  // Self-loop can exist
          if (succSucc.get(vJ).contains(vK)) {
            triangles += 1
          }
        }

        vK = iterK.nextInt
      }

      vJ = iterJ.nextInt
    }

    val clustCoeff = triangles / (k * (k - 1)).toDouble
    localClustCoeffCache.put(wikiID, clustCoeff)

    clustCoeff
  }
}
