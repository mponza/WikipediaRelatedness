package it.unipi.di.acubelab.wikipediarelatedness.dataset.wire

import it.unipi.di.acubelab.wikipediarelatedness.utils.Config


/**
  * Non-salient-salient pairs of the WiRe dataset.
  */
class WiReNS() extends WiReDataset {
  override def wirePath() = Config.getString("dataset.wire.nonsalient_salient")
  override def toString() = super.toString() + "-NS"
}
