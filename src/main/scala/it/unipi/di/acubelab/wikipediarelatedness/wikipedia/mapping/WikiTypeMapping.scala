package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping
import java.util.zip.GZIPInputStream

import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration

import scala.io.Source
import java.io.{File, FileInputStream}

import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.fastutil.objects.{Object2ObjectOpenHashMap, ObjectArrayList}
import org.slf4j.LoggerFactory


class WikiTypeMapping {}


object WikiTypeMapping {
  val logger = LoggerFactory.getLogger(classOf[WikiTypeMapping])
  protected val serializedPath = "/tmp/titleTypes.bin"

  protected lazy val wikiTitle2Types = loadMapping()


  protected def loadMapping(path: String = Configuration.wikipedia("instance-types"))
      : Object2ObjectOpenHashMap[String, ObjectArrayList[String]] = {

    if(new File(serializedPath).exists()) return BinIO.loadObject(serializedPath).asInstanceOf[Object2ObjectOpenHashMap[String, ObjectArrayList[String]]]

    logger.info("Loading Wikipedia types...")
    val reader = Source.fromInputStream(
      new GZIPInputStream(
        new FileInputStream(
          new File(path)
        )
      )
    )

    val wikiTitle2Types = new Object2ObjectOpenHashMap[String, ObjectArrayList[String]]()

    for(line <- reader.getLines().filter(!_.startsWith("#"))) {
        val fields = line.split(" ")

        val xmlTitle = getXMLFieldName(fields(0))
        val xmlType = getXMLFieldName(fields(2))

        wikiTitle2Types.putIfAbsent(xmlTitle, new ObjectArrayList[String])
        wikiTitle2Types.get(xmlTitle).add(xmlType)

    }
    logger.info("Wikipedia types loaded.")

    BinIO.storeObject(wikiTitle2Types, serializedPath)
    wikiTitle2Types
  }


  /**
    * Example: input: <path/to/something/with/multiple/slashes/name> -> output: name
    *
    * @param xmlString
    * @return
    */
  protected def getXMLFieldName(xmlString: String) = {
    xmlString.substring(xmlString.lastIndexOf("/") + 1, xmlString.indexOf(">"))
  }


  def types(wikiTitle: String) = wikiTitle2Types.getOrDefault(wikiTitle, new ObjectArrayList[String])
}