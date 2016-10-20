package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.classification.tuning

import it.unipi.di.acubelab.wikipediarelatedness.dataset.WikiRelateTask
import it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.RelatednessFactory
import org.slf4j.LoggerFactory
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.classification.classifiers.ThresholdClassifier


/**
  *
  * @param options
  *                {
  *                   "relatedness": RelatednessName
  *                   "threshold" thresholdValue
  *
  *                }
  * @param wikiSimDataset
  */
class ThresholdCrossValidation(options: Map[String, String], wikiSimDataset: WikiSimDataset) {
  val logger = LoggerFactory.getLogger(classOf[ThresholdCrossValidation])

  val threshold = options("threshold").toDouble
  val relatedness = RelatednessFactory.make(options)

  val classifier = new ThresholdClassifier(relatedness, threshold)


  def runCrossValidation() = {

  }

  def tun(train: List[WikiRelateTask], eval: List[WikiRelateTask]) = {

  }


  override def toString() : String = {
    "ThresholdCrossValidation_%s_%1.5f".format(relatedness.toString(), threshold)
  }
}


object ThresholdCrossValidation {

  def getThresholds(options: Map[String, String]) : List[Double] = options("relatedness") match {

    case "MilneWitten" => List(0.0)
    case "Jaccard" => List(0.0)

    case "w2v" =>
                  val step = options.getOrElse("model", "corpus") match {
                      case "corpus" => 0.0
                      case "dw" => 0.0
                      case "sg" => 0.0
                      case  "deepCorpus" => 0.0
                      case "dwsg" => 0.0
                    }

                  (-1.0 until 1.0 by step).toList

    case "LocalClustering" => List(0.0)
    case "MultiLLP" =>  (0.0 until 1.0 by 0.005).toList
    case "LDA" | "SVD" | "CoSimRank" | "MilneWitten" => (0.0 until 1.0 by 0.05).toList
    case "ESA" => List(0.0)

    case _ => throw new IllegalArgumentException("The specified relatedness does not exist %s."
      .format(options("relatedness")))
  }
}
