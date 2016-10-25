package it.unipi.di.acubelab.wikipediarelatedness.ibm

import java.io.File

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap
import it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import org.slf4j.LoggerFactory

import scala.io.Source



class IBMESA(filename: File) {
  val logger = LoggerFactory.getLogger(classOf[IBMESA])

  protected val relatedenss = loadRelatednessComputations(filename)


  def loadRelatednessComputations(ibmFile: File): Object2FloatOpenHashMap[Tuple2[Int, Int]] = {
    val ibmESA = new Object2FloatOpenHashMap[Tuple2[Int, Int]]()

    for(line <- Source.fromFile(ibmFile).getLines()) {
      val (srcWikiTitle, dstWikiTitle, relatedenss) = parseIBMLine(line)

      val key = getKey(srcWikiTitle, dstWikiTitle)
      ibmESA.put(key, relatedenss)
    }

    ibmESA
  }


  def parseIBMLine(line: String) : Tuple3[String, String, Float] = {
    val tuple = line.split("####")
    (tuple(0), tuple(1), tuple(2).toFloat)
  }


  def getKey(srcWikiTitle: String, dstWikiTitle: String) : Tuple2[Int, Int] = {
    val srcWikiID = IBMESA.wikiTitle2wikiID(srcWikiTitle)
    val dstWikiID = IBMESA.wikiTitle2wikiID(dstWikiTitle)

    new Tuple2[Int, Int](srcWikiID, dstWikiID)
  }


  def getKey(srcWikiID: Int, dstWikiID: Int) : Tuple2[Int, Int] = new Tuple2[Int, Int](srcWikiID, dstWikiID)


  def getRelatedness(srcWikiID: Int, dstWikiID: Int) : Float = {
    val key = getKey(srcWikiID, dstWikiID)

    if (!relatedenss.containsKey(key)) throw new NoSuchElementException("%d and %d keys not found."
                                                                          .format(srcWikiID, dstWikiID))

    relatedenss.getFloat(key)
  }
}



object IBMESA {
  protected val wikiSimDataset = new WikiSimDataset(Configuration.dataset("procWikiSim"))


  def wikiTitle2wikiID(wikiTitle: String) : Int = {
    val title = wikiTitle.replaceAll(" ", "_")

    wikiSimDataset.foreach {
      case wikiRelTask =>
        if (wikiRelTask.src.wikiTitle.equalsIgnoreCase(title)) return wikiRelTask.src.wikiID
        if (wikiRelTask.dst.wikiTitle.equalsIgnoreCase(title)) return wikiRelTask.dst.wikiID

        // Different mapping due to different Wikipedia versions.
        title match {
          case "Military_awards_and_decorations" => return 250041
          case _ => ;
        }
    }

    throw new IllegalArgumentException("Error while mapping %s to its wikiID".format(title))
  }
}