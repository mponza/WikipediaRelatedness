package it.unipi.di.acubelab.wikipediarelatedness.evaluation.dataset

import it.unipi.di.acubelab.wikipediarelatedness.evaluation.task.WikiRelTask

class WiReDataset(filename: String) extends WikiRelCSVDataset {

  override protected def loadDataset(): Seq[WikiRelTask] = {
    loadCSVDataset(filename)
  }

  def name() = "WiRe"
}
