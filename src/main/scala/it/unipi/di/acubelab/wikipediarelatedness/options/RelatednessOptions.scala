package it.unipi.di.acubelab.wikipediarelatedness.options


class RelatednessOptions(val json: Option[Any]) {
  val relatedness = getString("relatedness")

  def getString(key: String, default: String = null) : String = {
    json match {
      case Some(options: Map[String, Any] @unchecked) =>

        try {

          if (!options.contains(key)) {
            if (default != null) return default
            throw new IllegalArgumentException("Key not present, default value is null.")

          } else {
            options(key).toString
          }

        } catch {
          case e : Exception => throw new IllegalArgumentException("Error while getting %s value: %s".format(key, e))
        }

      case _ => throw new IllegalArgumentException("Error in matching json string.")
    }
  }

  def getInt(key: String, default: Int = null) : Int = {
    json match {
      case Some(options: Map[String, Any] @unchecked) =>

        try {

          if (!options.contains(key)) {
            if (default != null) return default
            throw new IllegalArgumentException("Key not present, default value is null.")

          } else {
            options(key).asInstanceOf[Double].toInt
          }

        } catch {
          case e : Exception => throw new IllegalArgumentException("Error while getting %s value: %s".format(key, e))
        }

      case _ => throw new IllegalArgumentException("Error in matching json string.")
    }
  }

  def getFloat(key: String, default: Float = null) : Float = {
    json match {
      case Some(options: Map[String, Any] @unchecked) =>

        try {

          if (!options.contains(key)) {
            if (default != null) return default
            throw new IllegalArgumentException("Key not present, default value is null.")

          } else {
            options(key).asInstanceOf[Double].toFloat
          }

        } catch {
          case e : Exception => throw new IllegalArgumentException("Error while getting %s value: %s".format(key, e))
        }

      case _ => throw new IllegalArgumentException("Error in matching json string.")
    }
  }

  def getOptionAny(key: String) : Option[Any] = {
    json match {
      case Some(options: Map[String, Any] @unchecked) =>

        try {

          options(key).asInstanceOf[Option[Any]]

        } catch {
          case e : Exception => throw new IllegalArgumentException("Error while getting %s value: %s".format(key, e))
        }

      case _ => throw new IllegalArgumentException("Error in matching json string.")
    }
  }
}