package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.embeddings

import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.fastutil.objects.{Object2ObjectArrayMap, Object2ObjectOpenHashMap, ObjectArrayList}
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

class TopKEmbeddings(path: String) {

  val logger = LoggerFactory.getLogger(classOf[TopKEmbeddings])
  val entity2entity = BinIO.loadObject(path)
                        .asInstanceOf[Object2ObjectOpenHashMap[Int, ObjectArrayList[Int]]]


  def getTopKWikiIDs(entityWikiID: Int) : List[Int] = {
    entity2entity.getOrDefault(entityWikiID, new ObjectArrayList[Int]()).elements().toList
  }

}
