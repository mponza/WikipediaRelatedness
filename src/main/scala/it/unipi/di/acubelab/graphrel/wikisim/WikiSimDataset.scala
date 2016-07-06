package it.unipi.di.acubelab.graphrel.wikisim

import java.net.URL

import scala.collection.mutable
import scala.io.Source


class WikiSimPair (val line: String) {
  val tuple =  line.split(",")

  val src = new {val wiki_id = tuple(1).toInt; val wiki_title = tuple(2)}
  val dst = new {val wiki_id = tuple(4).toInt; val wiki_title = tuple(5).replace(" ", "_")}

  val rel = tuple(6)
}


class WikiSimDataset {
  val url = getClass.getResource("/wikiSim411.csv")
  val simPairs = loadWikiSimPairs(url)

  /**
    *
    * @param url
    * @return List of WikiSimPair  with the corresponding human relatedness
    */
  def loadWikiSimPairs(url: URL) : List[WikiSimPair]= {
    val pairs = new mutable.MutableList[WikiSimPair]

    for(line <- Source.fromFile(url.toString).getLines) {
      pairs += new WikiSimPair(line)

    }

    pairs.toList
  }
}
