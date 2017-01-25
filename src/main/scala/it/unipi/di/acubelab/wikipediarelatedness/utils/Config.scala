package it.unipi.di.acubelab.wikipediarelatedness.utils

import com.typesafe.config.ConfigFactory

/**
  * Wrapper to the global configuration parameters.
  * Settings are loaded from src/main/resources/reference.conf file.
  */
object Config {
  protected val reference = ConfigFactory.load(ConfigFactory.defaultReference())

  def getInt(path: String) = reference.getInt("wikipediarelatedness." + path)
  def getString(path: String) = reference.getString("wikipediarelatedness." + path)
}
