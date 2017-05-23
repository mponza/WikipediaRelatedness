package it.unipi.di.acubelab.wikipediarelatedness.dataset.extend.kore

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiEntity
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer
import scala.io.Source

class WikiKOREDataset {

  val logger = LoggerFactory.getLogger(getClass)
  val tasks = loadDataset()


  def loadDataset() : Seq[WikiRankTask] = {

    val path = Config.getString("dataset.wikikore")

    val wikiRankTasks = ListBuffer.empty[WikiRankTask]
    var seedEntity: WikiEntity = null
    var rankedEntities = ListBuffer.empty[WikiEntity]

    for( line <- Source.fromFile(path).getLines()) {

      if (!line.contains("\t")) {
        if (seedEntity != null) wikiRankTasks += new WikiRankTask(seedEntity, rankedEntities)

        seedEntity = line2WikiEntitiy(line)
        // reset ranked list
        rankedEntities = ListBuffer.empty[WikiEntity]

      } else  {
        rankedEntities += line2WikiEntitiy(line)
      }
    }
    wikiRankTasks += new WikiRankTask(seedEntity, rankedEntities)

    logger.info("Loaded %d WikiRankTasks".format( wikiRankTasks.length ))
    wikiRankTasks
  }


  def line2WikiEntitiy(line: String) : WikiEntity = {
    val splitted = line.split(" ")

    val wikiID = splitted(1).toInt
    val wikiTitle = splitted(0).replace("\t", "")

    new WikiEntity(wikiID, wikiTitle)
  }

}
