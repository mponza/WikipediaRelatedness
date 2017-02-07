package it.unipi.di.acubelab.wikipediarelatedness.dataset.wire.subwire

import it.unipi.di.acubelab.wikipediarelatedness.utils.Config


/**
  * Salient-salient pairs of the WiRe dataset.
  *
  */
class SubWiReSS() extends SubWiReDataset {
  override def wirePath() = Config.getString("dataset.wire.salient_salient")
  override def toString() = super.toString() + "-SS"
}

