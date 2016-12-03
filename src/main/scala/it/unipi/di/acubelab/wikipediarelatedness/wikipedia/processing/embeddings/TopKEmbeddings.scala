package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.embeddings

import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.fastutil.objects.{Object2ObjectOpenHashMap, ObjectArrayList}
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import org.slf4j.LoggerFactory


class TopKEmbeddings(path: String) {

  val logger = LoggerFactory.getLogger(classOf[TopKEmbeddings])


  logger.info("Loading %s TopKEmbedding cache...".format(path))
  val entity2entity = BinIO.loadObject(path)
                        .asInstanceOf[Object2ObjectOpenHashMap[Int, ObjectArrayList[Int]]]

  def getTopKWikiIDs(entityWikiID: Int) : List[Int] = {
    entity2entity.getOrDefault(entityWikiID, new ObjectArrayList[Int]()).elements().toList
  }

}



object TopKEmbeddings {

  lazy val corpusSG = new TopKEmbeddings(Configuration.topKEmbeddings("sg"))
  lazy val deepWalkSG = new TopKEmbeddings(Configuration.topKEmbeddings("dwsg"))

}