package topk

import org.scalatest.{FlatSpec, Matchers}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.esa.ESA

/**
  * ESA Cache UnitTests.
  */
class ESASorting extends FlatSpec with Matchers {

  val obamaWikiID = 534366
  val obamaWikiTitle = "Barack_Obama"
  val obamaWikiBody = ESA.lucene.wikipediaBody(obamaWikiID)

  "ESA concepts" should "be sorted by their weight." in {
    ESA.wikipediaConcepts(obamaWikiID, 10) should equal(ESA.wikipediaConcepts(obamaWikiID, 10).sortBy(_._2).reverse)
  }


  "ESA Cache" should "be the same of ESA Lucene Index." in {
    ESA.cache.get(obamaWikiID, 10) should equal (ESA.wikipediaConcepts(obamaWikiBody, 10))
  }

}