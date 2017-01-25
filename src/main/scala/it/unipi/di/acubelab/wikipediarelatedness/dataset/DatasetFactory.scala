package it.unipi.di.acubelab.wikipediarelatedness.dataset

import it.unipi.di.acubelab.wikipediarelatedness.dataset.wikisim.WikiSimDataset
import it.unipi.di.acubelab.wikipediarelatedness.dataset.wire.{WiReNN, WiReNS, WiReSS}

object DatasetFactory {

  def make(datasetName: String) : WikiRelateDataset = datasetName.toLowerCase() match {
    case "wikisim" => new WikiSimDataset

    case "wire-ss" => new WiReSS
    case "wire-ns" => new WiReNS
    case "wire-nn" => new WiReNN
  }
}