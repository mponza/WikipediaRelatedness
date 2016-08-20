package it.unipi.di.acubelab.wikipediarelatedness.dataset

trait RelatednessDataset extends Traversable[WikiRelTask] {
  override def toString() : String
}
