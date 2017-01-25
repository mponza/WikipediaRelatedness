package it.unipi.di.acubelab.wikipediarelatedness.ibm

import java.io.File

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unipi.di.acubelab.wikipediarelatedness.utils.OldConfiguration


object IBMFactory {
  lazy val threshold2ESA = loadESAComputations(OldConfiguration.ibmDir)


  def make(threshold: Int) = threshold2ESA.get(threshold)


  def loadESAComputations(path: String) = {
    val threshold2ESA = new Object2ObjectOpenHashMap[Int, IBMESA]()

    val files = getListOfFiles(path)

    files.foreach{
      case file =>

        val nameNoExtension = file.getName.substring(0, file.getName.lastIndexOf("."))
        val threshold = nameNoExtension.split("__")(1).toInt

        val ibmESA = new IBMESA(file)
        threshold2ESA.put(threshold, ibmESA)
    }

    threshold2ESA
  }


  // https://www.safaribooksonline.com/library/view/scala-cookbook/9781449340292/ch12s09.html
  def getListOfFiles(dir: String): List[File] = {
    val d = new File(dir)

    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }
}
