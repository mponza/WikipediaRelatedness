package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.subgraph


class WeightedWikiSubGraph(val srcVec: Seq[Float], val srcNorm: Float, val srcWikiIDIndex: Int,
                           val dstVec: Seq[Float], val dstNorm: Float, val dstWikiIDIndex: Int
                           )