package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.framework.subgraph

import it.unimi.dsi.fastutil.ints.IntOpenHashSet

// nodeWikiIDs includes srcWikiID and dstWikiID
class WikiSubGraph(val srcWikiID: Int, val dstWikiID: Int, val nodeWikiIDs: IntOpenHashSet, val k: Int)