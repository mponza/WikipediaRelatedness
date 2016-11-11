package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping

import java.io.FileInputStream
import java.util.zip.GZIPInputStream

import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration

import scala.io.Source
import java.io.{File, FileInputStream}

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

import scala.collection.mutable.ListBuffer

object WikiTypeMapping {

  lazy val wikiTitle2Types = 3

  def title2types() = {

  }


  def loadMapping(path: String = Configuration.wikipedia("instance-types")) = {
    val reader = Source.fromInputStream(
      new GZIPInputStream(
        new FileInputStream(
          new File(path)
        )
      )
    )

    val wikiTitle2Types = new Object2ObjectOpenHashMap[String, ListBuffer[String]]()

    for(line <- reader.getLines().drop(1)) {
      val fields = line.split(" ")

      val xmlTitle = getXMLFieldName(fields(0))
      val xmlType = getXMLFieldName(fields(3))

      wikiTitle2Types.putIfAbsent(xmlTitle, ListBuffer.empty[String])
      wikiTitle2Types.get(xmlTitle) += xmlType
    }
  }


  /**
    * Example: input: <path/to/something/with/multiple/slashes/name> -> output: name
    * @param xmlString
    * @return
    */
  protected def getXMLFieldName(xmlString: String) = {
    xmlString.substring(xmlString.lastIndexOf("/") + 1, xmlString.indexOf(">"))
  }
}