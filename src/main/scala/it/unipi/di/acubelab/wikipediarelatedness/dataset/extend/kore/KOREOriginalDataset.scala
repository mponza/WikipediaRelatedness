package it.unipi.di.acubelab.wikipediarelatedness.dataset.extend.kore

import java.io.FileWriter

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiEntity
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping.WikiTitleID
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer
import scala.io.Source


class WikiRankTask(val seedWikiEntity: WikiEntity, val rankedEntities: Seq[WikiEntity]) {}


object KOREOriginalDataset {

  protected val logger = LoggerFactory.getLogger("KORE")
  protected val wikiGraph = WikiBVGraphFactory.make("in")

  protected var removed = 0

  //val tasks = loadDataset()


  def main(args: Array[String]) : Unit = {

    val path = Config.getString("dataset.kore")


    // Unmapped WikiRankTasks
    val wikiRankTasks = ListBuffer.empty[WikiRankTask]
    var seedEntity: WikiEntity = null
    var rankedEntities = ListBuffer.empty[WikiEntity]

    for( (line, index) <- Source.fromFile(path).getLines().zipWithIndex) {

      if (index % 21 == 0) {
        if (seedEntity != null)
          wikiRankTasks += new WikiRankTask(seedEntity, rankedEntities)

        // new seed entity
        seedEntity = new WikiEntity( -1, line.replace(" ", "_") )
        // reset ranked list
        rankedEntities = ListBuffer.empty[WikiEntity]

      } else  {
        rankedEntities += new WikiEntity(-1, line.substring(1, line.length) .replace("\t", "").replace(" ", "_"))
      }
    }

    wikiRankTasks += new WikiRankTask(seedEntity, rankedEntities)

    val mapped = wikiRankTasks.map( map2Wikipedia ).filter(_ != null)
    writeDataset(mapped)

    logger.info("Removed %d".format(removed))

    //mapped

  }

  def map2Wikipedia(task: WikiRankTask) : WikiRankTask = {
    println(task.seedWikiEntity.wikiTitle)
    val wikiSeedEntity = new WikiEntity(WikiTitleID.map(task.seedWikiEntity.wikiTitle), task.seedWikiEntity.wikiTitle )
    if (wikiSeedEntity.wikiID < 0 ) {
      logger.info("WikiID fail for %s".format(wikiSeedEntity.wikiTitle))
      return null
    }
    if (!wikiGraph.contains(wikiSeedEntity.wikiID)) {
      logger.info("Graph not contain %s".format(wikiSeedEntity.wikiTitle))
      return null
    }



    // Prints

    val f = new FileWriter("/tmp/out", true)
    var wikiRankedEntities = task.rankedEntities.map( e => new WikiEntity( WikiTitleID.map(e.wikiTitle), e.wikiTitle) )
    for(e <- wikiRankedEntities) {

      if(!wikiGraph.contains(e.wikiID)) {
        logger.info("=>%s".format(e.wikiTitle))
        f.write("\"%s\" -> \"\",\n".format(e.wikiTitle))
      }
    }
    f.close()



    wikiRankedEntities = task.rankedEntities.map( e => new WikiEntity( WikiTitleID.map(e.wikiTitle), e.wikiTitle) )
      .filter(_.wikiID > 0).filter(w => wikiGraph.contains(w.wikiID))

    if(wikiRankedEntities.length != task.rankedEntities.length) {
      logger.warn("Seed %s, ranked list from %d to %d".format(
        wikiSeedEntity.wikiTitle, task.rankedEntities.length, wikiRankedEntities.length
      ))

      removed += task.rankedEntities.length - wikiRankedEntities.length

      println( task.rankedEntities.map( e => new WikiEntity( WikiTitleID.map(e.wikiTitle), e.wikiTitle) ).filter( _.wikiID <= 0).map(_.wikiTitle) mkString " + "   )
      logger.warn("==================")
    }

    new WikiRankTask(wikiSeedEntity, wikiRankedEntities)

  }


  def writeDataset(mappedTasks: Seq[WikiRankTask]) = {

    val f = new FileWriter("/tmp/koreWAT.csv")
    var n = 0
    for(task<- mappedTasks) {

      // Writes seed as "WikiTitle WikiID"
      f.write("%s %d\n".format(task.seedWikiEntity.wikiTitle, task.seedWikiEntity.wikiID))
      n += 1

      // Writes ranked entities
      for(rankedEntity <- task.rankedEntities) {
        f.write( "\t%s %d\n".format(rankedEntity.wikiTitle, rankedEntity.wikiID) )
        n += 1
      }
    }

    println(n)
    f.close()

  }

}
