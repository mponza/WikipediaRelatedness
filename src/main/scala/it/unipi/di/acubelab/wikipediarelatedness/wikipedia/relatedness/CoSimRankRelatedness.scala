package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

class CoSimRankRelatedness(options: CoSimRankOptions) extends Relatedness {
  val logger = LoggerFactory.getLogger(classOf[ServerCoSimRankRelatedness])
  val graphs = wikiGraphs()
  val cosimrank = CoSimRank.make(options)
  val weighter = ServerCoSimRankRelatedness.makeWeighter(options)

  

}
