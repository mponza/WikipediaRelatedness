package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping
import java.util.zip.GZIPInputStream

import it.unipi.di.acubelab.wikipediarelatedness.utils.OldConfiguration

import scala.io.Source
import java.io.{File, FileInputStream}

import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.fastutil.objects.{Object2ObjectOpenHashMap, ObjectArrayList}
import org.slf4j.LoggerFactory

import scala.collection.immutable.HashSet


class WikiTypeMapping {}


object WikiTypeMapping {
  val logger = LoggerFactory.getLogger(classOf[WikiTypeMapping])
  protected val serializedPath = "/tmp/titleTypes.bin"

  protected lazy val wikiTitle2Types = loadMapping()


  protected def loadMapping(path: String = OldConfiguration.wikipedia("instance-types"))
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
      val xmlOntology = getXMLFieldOntology(fields(2))
      val xmlType = getXMLFieldName(fields(2))

      if(xmlOntology.startsWith("dbpedia")) {
        wikiTitle2Types.putIfAbsent(xmlTitle, new ObjectArrayList[String])
        wikiTitle2Types.get(xmlTitle).add(xmlType)
      }

    }

    BinIO.storeObject(wikiTitle2Types, serializedPath)
    logger.info("Wikipedia types loaded and cached.")

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

  protected def getXMLFieldOntology(xmlString: String) = xmlString.split("/")(2)


  def types(wikiTitle: String) = wikiTitle2Types.getOrDefault(wikiTitle, new ObjectArrayList[String])


  /**
    *
    * @param wikiTitle
    * @return Wikipedia Type as {Person, Organization, Location, Object}
    */
  def typePerOrgLoc(wikiTitle: String) : String= {
    val pol = HashSet("Person", "Organisation", "Location")
    val wikiTypes = types(wikiTitle)

    for(i <- 0 until wikiTypes.size()) {
      if (pol.contains(wikiTypes.get(i))) return wikiTypes.get(i)
    }

    "Object"
  }
}