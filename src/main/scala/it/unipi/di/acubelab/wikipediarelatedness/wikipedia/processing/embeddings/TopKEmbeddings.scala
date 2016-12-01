package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.embeddings

import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.fastutil.objects.{Object2ObjectArrayMap, Object2ObjectOpenHashMap, ObjectArrayList}
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

class TopKEmbeddings(path: String) {

  val logger = LoggerFactory.getLogger(classOf[TopKEmbeddings])
  val entity2entity = BinIO.loadObject(path)
                        .asInstanceOf[Object2ObjectOpenHashMap[String, ObjectArrayList[String]]]

  def getTopKWikiIDs(wikiID: Int) : List[Int] = {
    val wordWikiIDs = entity2entity.getOrDefault("ent_%d".format(wikiID),
                                              new ObjectArrayList[String]()).elements()

    val wikiIDs = ListBuffer.empty[Int]
    wordWikiIDs.foreach {
      case word => wikiIDs += word2WikiID(word)
    }

    wikiIDs.toList
  }



  def word2WikiID(word: String) = word.substring(4, word.length).toInt

}
