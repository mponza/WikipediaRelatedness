package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.topk.esa

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
/*
class ESATopKCacher {

  /**
    * Cache between a wikiID to its vector of concepts.
    *
    * @param size
    */
  class ESATopKCacher(val size: Int = 10000) extends TopKCache {
    val logger = LoggerFactory.getLogger(getClass)

    override val e2esPath = Config.getString("wikipedia.cache.esa")


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


    /**
      * Loads cache in memory.
      *
      * @return
      */
    protected def load() = {
      logger.info("Loading ESA cache from %s...".format(cache))

      try {

        BinIO.loadObject(cache).asInstanceOf[Int2ObjectOpenHashMap[List[Tuple2[Int, Float]]]]

      } catch {

        case e: Exception =>
          logger.warn("Cache not found, no cache will be used.")
          new Int2ObjectOpenHashMap[List[Tuple2[Int, Float]]]()  // empty cache
      }
    }


    /***
      *
      * @param wikiID
      * @param threshold
      * @return List of [wikiID, weight] of size threshold, sorted by decreasing weight.
      */
    def get(wikiID: Int, threshold: Int = size) : List[Tuple2[Int, Float]] = {
      if (!wikiID2Concepts.containsKey(wikiID)) return null
      wikiID2Concepts.get(wikiID).slice(0, threshold)
    }
  }


}*/