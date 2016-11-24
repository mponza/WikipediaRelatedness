package wikipediarelatedness.wikipedia.mapping

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.mapping.{WikiTitleID}
import org.scalatest.{FlatSpec, Matchers}

class WikiMapping extends FlatSpec with Matchers {

  "Wikipedia Title-ID Mapping" should "map entity title to their ID." in {
    WikiTitleID.map("Silvio_Berlusconi") should equal(26909)
    WikiTitleID.map("United_States") should equal(3434750)
  }


  "Wikipedia Type Mapping" should ""

}
