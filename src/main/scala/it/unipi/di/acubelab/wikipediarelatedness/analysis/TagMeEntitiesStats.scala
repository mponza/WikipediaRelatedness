package it.unipi.di.acubelab.wikipediarelatedness.analysis

import java.io.FileWriter

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping.WikiTitleID
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.algorithms.WikiBVDistance
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory

import scala.io.Source

object TagMeEntitiesStats {

  def main(args: Array[String]) {

    val wikiDistancer = new WikiBVDistance(WikiBVGraphFactory.make("un.sym"))
    val distances = new IntArrayList()

    val tuples = Source.fromFile("/tmp/ace.tsv").getLines()
    for (line <- tuples) {
      val wikiTitles = line.split("\t")

      for(x <- wikiTitles;  y <- wikiTitles) {

        try {
          val xID = WikiTitleID.map(x)
          val yID = WikiTitleID.map(y)
          val d = wikiDistancer.getDistance(xID, yID)

          println("%s %s is %d".format(x, y, d) )

          distances.add(d)

        } catch {
          case e: Exception => println("Solme error occurs %s".format(e.toString))
            println(x)
            println(y)
        }
      }
    }


    val dstStr = distances.toIntArray() mkString(",")
    val f = new FileWriter("/tmp/aceDist.csv")
    f.write(dstStr)
    f.close()

  }

}
