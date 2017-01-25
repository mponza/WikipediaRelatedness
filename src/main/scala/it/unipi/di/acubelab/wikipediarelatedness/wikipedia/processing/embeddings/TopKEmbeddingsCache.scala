package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.embeddings

import java.io.File

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unipi.di.acubelab.wikipediarelatedness.utils.OldConfiguration
import org.slf4j.LoggerFactory

/**
  * TopK cache.
  * <p>
  *  <ul>
  *
  *   <li> entity2entities. Lists of entities which are most simlar to a specific set of entities.
  *   <li> entityPair2entities. List of entities which are most similar to a specific set of context vectors (average vector
  *   between two Wikipedia IDs.)
  *
  *   <ul/>
  * </p>
 *
  * @param dirPath
  */
class TopKEmbeddingsCache(dirPath: String) {

  val logger = LoggerFactory.getLogger(classOf[TopKEmbeddingsCache])


  logger.info("Loading %s TopKEmbedding caches...".format(dirPath))

  // {wikiID -> [wikiID, weight]}
  val entity2entities = loadE2ECache(new File(dirPath, "ent2ents.bin"))

  // {(wikiID, wikiID) -> [wikiID, weight]}
  val entityPair2entities = loadEP2ECache(new File(dirPath, "pairs2ents.bin"))

  //
  // Loaders
  //
  protected def loadE2ECache(file: File) = {
    if (file.exists()) BinIO.loadObject(file).asInstanceOf[Int2ObjectOpenHashMap[List[Tuple2[Int, Float]]]]
    else new Int2ObjectOpenHashMap[List[Tuple2[Int, Float]]]()
  }
  protected def loadEP2ECache(file: File) = {
    if (file.exists()) BinIO.loadObject(file).asInstanceOf[Object2ObjectOpenHashMap[Tuple2[Int, Int], List[Tuple2[Int, Float]]]]
    else new Object2ObjectOpenHashMap[Tuple2[Int, Int], List[Tuple2[Int, Float]]]()
  }

  //
  // Top-K Retrieval Methods
  //
  def getTopK(entityWikiID: Int) : List[Tuple2[Int, Float]] = {
    entity2entities.getOrDefault(entityWikiID, List.empty[Tuple2[Int, Float]])
  }
  def getTopK(srcWikiID: Int, dstWikiID: Int) = {
    entityPair2entities.getOrDefault(Tuple2(srcWikiID, dstWikiID), List.empty[Tuple2[Int, Float]])
  }

}


object TopKEmbeddingsCache {

  lazy val corpusSG = new TopKEmbeddingsCache(OldConfiguration.topKEmbeddings("sg"))
  lazy val deepWalkSG = new TopKEmbeddingsCache(OldConfiguration.topKEmbeddings("dwsg"))
}