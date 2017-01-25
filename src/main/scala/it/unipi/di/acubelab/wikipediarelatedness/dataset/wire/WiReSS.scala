package it.unipi.di.acubelab.wikipediarelatedness.dataset.wire

import it.unipi.di.acubelab.wikipediarelatedness.utils.Config


/**
  * Salient-salient pairs of the WiRe dataset.
  */
class WiReSS() extends WiReDataset {
  override def wirePath() = Config.getString("dataset.wire.salient_salient")
  override def toString() = super.toString() + "-SS"
}

