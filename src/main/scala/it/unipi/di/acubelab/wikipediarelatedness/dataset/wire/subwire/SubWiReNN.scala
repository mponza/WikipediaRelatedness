package it.unipi.di.acubelab.wikipediarelatedness.dataset.wire.subwire

import it.unipi.di.acubelab.wikipediarelatedness.utils.Config


/**
  * Non-salient-non-salient pairs of the WiRe dataset.
  *
  */
class SubWiReNN() extends SubWiReDataset {

  override def wirePath() = Config.getString("dataset.wire.nonsalient_nonsalient")
  override def toString() = super.toString() + "-NN"
}