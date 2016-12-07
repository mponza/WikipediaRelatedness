package utils

import java.util.Locale

import it.unimi.dsi.fastutil.doubles.DoubleArrayList
import it.unipi.di.acubelab.wikipediarelatedness.utils.Similarity
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.ops.transforms.Transforms
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Random

class Cosine extends FlatSpec with Matchers {

  "Cosine Similarity" should "be compute correctly upon integers." in {
    val a = List(2, 1, 0, 2, 0, 1, 1, 1).map(_.toFloat)
    val b = List(2, 1, 1, 1, 1, 0, 1, 1).map(_.toFloat)

    val zipListA = a.zipWithIndex.map(x => (x._2, x._1)).filter(_._2 != 0.0)
    val zipListB = b.zipWithIndex.map(x => (x._2, x._1)).filter(_._2 != 0.0)

    "%1.2f".formatLocal(Locale.ENGLISH, Similarity.cosineSimilarity(zipListA, zipListB)) should equal("0.82")
  }


  "Cosine similarity" should  "be compute correctly upon doubles." in {
    val a = Array.fill(1000)(Random.nextDouble())
    val b = Array.fill(1000)(Random.nextDouble())

    val fastA = new DoubleArrayList(a)
    val fastB = new DoubleArrayList(b)
    val simCos = "%.2f".format(Similarity.cosineSimilarity(fastA, fastB))

    val indA = Nd4j.create(a)
    val indB = Nd4j.create(b)

    val nd4jSim = "%.2f".format(Transforms.cosineSim(indA, indB))

    simCos should equal(nd4jSim)
  }


  "Cosine similarity" should  "be compute correctly upon indexed doubles." in {
    val a = Array.fill(1000)(Random.nextDouble()).map(x => if (x <= 0.3) 0.0 else x)
    val b = Array.fill(1000)(Random.nextDouble()).map(x => if (x <= 0.3) 0.0 else x)

    val zipListA = a.zipWithIndex.map(x => (x._2, x._1.toFloat)).filter(_._2 != 0.0).toList
    val zipListB = b.zipWithIndex.map(x => (x._2, x._1.toFloat)).filter(_._2 != 0.0).toList
    val simCos = "%.2f".format(Similarity.cosineSimilarity(zipListA, zipListB))

    val indA = Nd4j.create(a)
    val indB = Nd4j.create(b)

    val nd4jSim = "%.2f".format(Transforms.cosineSim(indA, indB))

    simCos should equal(nd4jSim)
  }

}