package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping
import java.util.zip.GZIPInputStream

import it.unipi.di.acubelab.wikipediarelatedness.utils.Config

import scala.io.Source
import java.io.{File, FileInputStream}

import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.fastutil.objects.{Object2ObjectOpenHashMap, ObjectArrayList}
import org.slf4j.LoggerFactory

import scala.collection.immutable.HashSet


class WikiTypeMapping {}


/**
  * Mapping between WikiTitle and their instance types.
  *
  */
object WikiTypeMapping {
  protected val logger = LoggerFactory.getLogger(getClass)
  protected val serializedPath = Config.getString("wikipedia.cache.mapping.title_type")

  protected lazy val wikiTitle2Types = loadMapping()


  /**
    * Loads Wikipedia instance type mapping.
    *
    * @return
    */
  protected def loadMapping()
      : Object2ObjectOpenHashMap[String, ObjectArrayList[String]] = {

    val path = Config.getString("wikipedia.mapping.instance_types")
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


  /**
    * Get second field of an xml string.
    *
    * @param xmlString
    * @return
    */
  protected def getXMLFieldOntology(xmlString: String) = xmlString.split("/")(2)


  /**
    * Instance type of wikiTitle.
    *
    * @param wikiTitle
    * @return
    */
  def types(wikiTitle: String) = wikiTitle2Types.getOrDefault(wikiTitle, new ObjectArrayList[String])


  /**
    * Wikipedia Type as {Person, Organization, Location, Object}
    *
    * @param wikiTitle
    * @return
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