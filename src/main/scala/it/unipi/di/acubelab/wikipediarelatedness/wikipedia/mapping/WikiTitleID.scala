package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping

import java.io.File

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import it.unipi.di.acubelab.wikipediarelatedness.utils.{Config, OldConfiguration}
import org.slf4j.LoggerFactory

import scala.io.Source


class WikiTitleID {}


object WikiTitleID {
  val logger = LoggerFactory.getLogger(classOf[WikiTitleID])

  protected val serializedPath = "/tmp/title-id.bin" // FIX THIS

  protected lazy val wikiTitle2ID = loadMapping()
  protected lazy val wikiID2Title = reverseTitle2ID()


  protected def loadMapping(path: String = Config.getString("wikipedia.SOMETHING").wikipedia("title-id"))
        : Object2IntOpenHashMap[String] = {

    if(new File(serializedPath).exists()) return BinIO.loadObject(serializedPath).asInstanceOf[Object2IntOpenHashMap[String]]


    logger.info("Loading Wikipedia title-ID mapping...")
    val wikiTitle2ID = new Object2IntOpenHashMap[String]

    for(line <-  Source.fromFile(path).getLines()) {
      val fields = line.split("\t")

      wikiTitle2ID.put(fields(0), fields(1).toInt)
    }
    logger.info("Wikipedia title-ID mapping loaded.")


    BinIO.storeObject(wikiTitle2ID, serializedPath)
    wikiTitle2ID
  }


  def reverseTitle2ID(): Int2ObjectOpenHashMap[String] = {
    logger.info("Reversing WikiID2Title mapping...")

    val wikiID2Title = new Int2ObjectOpenHashMap[String]

    wikiTitle2ID.keySet().toArray.foreach {
      case wikiTitle =>
        val title = wikiTitle.toString
        wikiID2Title.put(wikiTitle2ID.getInt(title), title)
    }

    wikiID2Title
  }


  def map(wikiTitle: String) = wikiTitle2ID.getInt(wikiTitle)

  def map(wikiID: Int) = wikiID2Title.getOrDefault(wikiID, wikiID.toString)

}
