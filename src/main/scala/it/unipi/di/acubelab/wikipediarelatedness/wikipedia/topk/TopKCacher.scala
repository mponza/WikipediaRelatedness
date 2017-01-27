package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask


/**
  * Trait for generating a topK cache.

  *
  */
trait TopKCacher {

  def e2esPath: String

  def generate(tasks: Seq[WikiRelateTask]) = {
   // entity2entities = generateTopKCache(tasks)


  }

  protected def generateTopKCache(tasks: Seq[WikiRelateTask])
}



/*
*
    /**
      * Method used to generate the cache from a dataset.
      *
      * @param tasks
      */
    def generate(tasks: Seq[WikiRelateTask]) = {
      val wikiIDs = tasks.foldLeft(List.empty[Int])((IDs, task) => IDs ++ List(task.src.wikiID, task.dst.wikiID)).distinct

      logger.info("Retrieving bodies...")
      val bodies = wikiIDs.map(wikiID => ESA.lucene.wikipediaBody(wikiID))

      val pl = new ProgressLogger(logger, 1, TimeUnit.MINUTES)
      pl.start("Retrieving concepts...")

      val concepts = ListBuffer.empty[List[Tuple2[Int, Float]]]
      bodies.foreach {
        case body =>
          concepts += ESA.wikipediaConcepts(body, size)
          pl.update()
      }

      pl.done()

      logger.info("Building wikiID concepts mapping...")
      val wikiID2Concepts = new Int2ObjectOpenHashMap[List[Tuple2[Int, Float]]]()
      wikiIDs.zipWithIndex.foreach {
        case(wikiID: Int, index: Int) =>

          val wikiConcepts = concepts(index)
          wikiID2Concepts.put(wikiID, wikiConcepts)
      }

      logger.info("Serializing wikiIDs-concepts mapping...")
      new File(cache).getParentFile.mkdirs
      BinIO.storeObject(wikiID2Concepts, cache)
    }
*
* */