package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping

import java.io.File

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import org.slf4j.LoggerFactory

import scala.io.Source


/**
  * Manager for WikiID -> WikiTitle mapping.
  *
  */
object WikiTitleID {
  protected val logger = LoggerFactory.getLogger("WikiTitleIDMapping")
  protected val serializedPath = Config.getString("wikipedia.cache.mapping.title_id")

  protected lazy val wikiTitle2ID = loadMapping()
  protected lazy val wikiID2Title = reverseTitle2ID()


  /**
    * Loads Wikipedia mapping between ID -> Title and viceversa.
    *
    * @return
    */
  protected def loadMapping()
        : Object2IntOpenHashMap[String] = {
    if(new File(serializedPath).exists()) return BinIO.loadObject(serializedPath).asInstanceOf[Object2IntOpenHashMap[String]]

    logger.info("Loading Wikipedia title-ID mapping...")

    val path = Config.getString("wikipedia.mapping.title_id")
    val wikiTitle2ID = new Object2IntOpenHashMap[String]

    for(line <-  Source.fromFile(path).getLines()) {
      val fields = line.split("\t")

      wikiTitle2ID.put(fields(0), fields(1).toInt)
    }
    logger.info("Wikipedia title-ID mapping loaded.")


    BinIO.storeObject(wikiTitle2ID, serializedPath)
    wikiTitle2ID
  }


  /**
    * Generate WikiTitle -> WikiID mapping.
    *
    * @return
    */
  protected def reverseTitle2ID(): Int2ObjectOpenHashMap[String] = {
    logger.info("Reversing WikiID2Title mapping...")

    val wikiID2Title = new Int2ObjectOpenHashMap[String]

    wikiTitle2ID.keySet().toArray.foreach {
      case wikiTitle =>
        val title = wikiTitle.toString
        wikiID2Title.put(wikiTitle2ID.getInt(title), title)
    }

    wikiID2Title
  }


  /**
    * Maps a WikiTitle to its WikiID.
    *
    * @param wikiTitle
    * @return
    */
  def map(wikiTitle: String) = wikiTitle2ID.getInt(wikiTitle)


  /**
    * Maps a WikiID to its WikiTitle.
    *
    * @param wikiID
    * @return
    */
  def map(wikiID: Int) = wikiID2Title.getOrDefault(wikiID, wikiID.toString)

}
