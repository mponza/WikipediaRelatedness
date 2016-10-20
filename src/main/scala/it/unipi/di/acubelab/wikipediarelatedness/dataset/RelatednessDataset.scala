package it.unipi.di.acubelab.wikipediarelatedness.dataset

trait RelatednessDataset extends Traversable[WikiRelateTask] {
  override def toString() : String
}
