package it.unipi.di.acubelab.wikipediarelatedness.dataset.extend.kore

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiEntity
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping.WikiTitleID
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer
import scala.io.Source


class WikiRankTask(val seedWikiEntity: WikiEntity, val rankedEntities: Seq[WikiEntity]) {
}


object KOREDataset {

  protected val logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Seq[WikiRankTask] = {

    val path = Config.getString("dataset.kore")


    // Unmapped WikiRankTasks
    val wikiRankTasks = List.empty[WikiRankTask]
    var seedEntity: WikiEntity = null
    var rankedEntities = ListBuffer.empty[WikiEntity]

    for(line <- Source.fromFile(path).getLines()) {
      if (!line.contains("\t")) {

        wikiRankTasks :+ new WikiRankTask(seedEntity, rankedEntities)

        // new seed entity
        seedEntity = new WikiEntity( -1, line.replace(" ", "_") )
        // reset ranked list
        rankedEntities = ListBuffer.empty[WikiEntity]
      }

      if (line.contains("\t")) {
        rankedEntities :+ new WikiEntity(-1, line.replace("\t", "").replace(" ", "_"))
      }
    }


    val mappedWikiRankTasks = wikiRankTasks.map( map2Wikipedia )

    mappedWikiRankTasks
  }


  def map2Wikipedia(task: WikiRankTask) : WikiRankTask = {
    val wikiSeedEntity = new WikiEntity(WikiTitleID.map(task.seedWikiEntity.wikiTitle), task.seedWikiEntity.wikiTitle )
    if (wikiSeedEntity.wikiID < 0) {
      logger.info("Null for %s".format(wikiSeedEntity.wikiTitle))
      return null
    }
    val wikiRankedEntities = task.rankedEntities.map( e => new WikiEntity( WikiTitleID.map(e.wikiTitle), e.wikiTitle) ).filter(_.wikiID > 0)

    if(wikiRankedEntities.length != task.rankedEntities.length) logger.warn("Seed %s, ranked list from %d to %d".format(
      wikiSeedEntity.wikiTitle, task.rankedEntities.length, wikiRankedEntities.length
    ))

    new WikiRankTask(wikiSeedEntity, wikiRankedEntities)

  }
}
