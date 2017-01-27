package it.unipi.di.acubelab.wikipediarelatedness.dataset

import it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.wikipediarelatedness.dataset.wire.{WiReNN, WiReNS, WiReSS}


/**
  * Factory to create a dataset from its name.
  */
object DatasetFactory {


  /**
    * Returns the specified dataset from its name.
    *
    * @param datasetName
    * @return
    */
  def make(datasetName: String) : WikiRelateDataset = datasetName.toLowerCase() match {
    case "wikisim" => new WikiSimDataset

    case "wire-ss" => new WiReSS
    case "wire-ns" => new WiReNS
    case "wire-nn" => new WiReNN
  }


  /**
    * Method used to iterate over fresh copy of all dataset available.
    *
    * @return
    */
  def datasets() = Seq("wikisim", "wire-ss", "wire-ns", "wire-nn").map(make(_))

}