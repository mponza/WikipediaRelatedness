package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.embeddings

import java.io.File

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.fastutil.objects.{Object2ObjectOpenHashMap, ObjectArrayList}
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import org.slf4j.LoggerFactory


class TopKEmbeddings(dirPath: String) {

  val logger = LoggerFactory.getLogger(classOf[TopKEmbeddings])


  logger.info("Loading %s TopKEmbedding caches...".format(dirPath))

  // {wikiID -> [wikiID, weight]}
  val entity2entities = BinIO.loadObject(new File(dirPath, "ent2ents.bin").getAbsolutePath)
                              .asInstanceOf[Int2ObjectOpenHashMap[List[Tuple2[Int, Float]]]]

  // {(wikiID, wikiID) -> [wikiID, weight]}
  val entityPair2entities = BinIO.loadObject(new File(dirPath, "pairs2ents.bin").getAbsolutePath)
                              .asInstanceOf[Object2ObjectOpenHashMap[Tuple2[Int, Int], List[Tuple2[Int, Float]]]]


  def getTopK(entityWikiID: Int) : List[Tuple2[Int, Float]] = {
    entity2entities.getOrDefault(entityWikiID, List.empty[Tuple2[Int, Float]])
  }


  def getTopK(srcWikiID: Int, dstWikiID: Int) = {
    entityPair2entities.getOrDefault(Tuple2(srcWikiID, dstWikiID), List.empty[Tuple2[Int, Float]])
  }

}



object TopKEmbeddings {

  lazy val corpusSG = new TopKEmbeddings(Configuration.topKEmbeddings("sg"))
  lazy val deepWalkSG = new TopKEmbeddings(Configuration.topKEmbeddings("dwsg"))

}