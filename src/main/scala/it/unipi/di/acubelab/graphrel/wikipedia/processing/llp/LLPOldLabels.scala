package it.unipi.di.acubelab.graphrel.wikipedia.processing.llp

import java.io.File

import it.unimi.dsi.fastutil.ints.{Int2ObjectOpenHashMap, IntArrayList, IntOpenHashSet}
import it.unimi.dsi.fastutil.io.BinIO
import it.unimi.dsi.law.graph.LayeredLabelPropagation
import it.unipi.di.acubelab.graphrel.utils.Configuration

class LLPOldLabels(nLabels: Int  = 10, gammaThreshold: Int = 32,
                   maxUpdates: Int = LayeredLabelPropagation.MAX_UPDATES) extends LLPClustering {

  override def loadLabels() : Int2ObjectOpenHashMap[IntArrayList] = {
    logger.info("LLOldPLabels loading...")
    val labels =  new Int2ObjectOpenHashMap[IntArrayList]

    val diff = new IntOpenHashSet

    val files = new File(Configuration.wikipedia("old")).listFiles.filter(_.getName.startsWith("gamma_100_32-")).sortBy {
      _.getName.split("-")(1).toInt
    }


    // Foreach files read the whole mapping at the corresponding LLP iteration.
    for (llpFile <- files) {
      logger.info("Reading labels from %s".format(llpFile.getName))
      val it = BinIO.asIntIterator(llpFile)
      var wikiID = 0 // index of the Wikipedia node in the mapped BVgraph (see WikiBVGraph.wiki2node/node2wiki)

      while(it.hasNext) {
        val label = it.nextInt()

        labels.putIfAbsent(wikiID, new IntArrayList)
        labels.get(wikiID).add(label)
        wikiID += 1

        diff.add(label)
      }
      logger.info(diff.size().toString)
    }

    labels
  }
}
