package it.unipi.di.acubelab.wikipediarelatedness.options


abstract class RelatednessOptions(val json: Option[Any] = None) {
  //val relatedness = getString("relatedness")

  def getString(key: String, default: String = null) : String = {
    if (!json.isDefined) return default

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

  def getInt(key: String, default: Int) : Int = {
    if (!json.isDefined) return default

    json match {
      case Some(options: Map[String, Any] @unchecked) =>

        try {

          if (!options.contains(key)) {
            return default

          } else {
            options(key) match {
              case v: Double => v.toInt
              case v: Int => v
            }
            //options(key).asInstanceOf[Double].toInt
          }

        } catch {
          case e : Exception => throw new IllegalArgumentException("Error while getting %s value: %s".format(key, e))
        }

      case _ => throw new IllegalArgumentException("Error in matching json string.")
    }
  }

  def getInt(key: String) : Int = {
    json match {
      case Some(options: Map[String, Any] @unchecked) =>

        try {

          options(key).asInstanceOf[Double].toInt

        } catch {
          case e : Exception =>
            throw new IllegalArgumentException("Error while getting %s value (no default): %s".format(key, e))
        }

      case _ => throw new IllegalArgumentException("Error in matching json string.")
    }
  }

  def getFloat(key: String, default: Float = Float.NaN) : Float = {
    if (!json.isDefined) return default

    json match {
      case Some(options: Map[String, Any] @unchecked) =>

        try {

          if (!options.contains(key)) {
            if (default != Float.NaN) return default
            throw new IllegalArgumentException("Key not present, default value is NaN.")

          } else {
            options(key).asInstanceOf[Double].toFloat
          }

        } catch {
          case e : Exception => throw new IllegalArgumentException("Error while getting %s value: %s".format(key, e))
        }

      case _ => throw new IllegalArgumentException("Error in matching json string.")
    }
  }

  def getOptionAny(key: String) : Option[Map[String, Any]] = {
    json match {
      case Some(options: Map[String, Any] @unchecked) =>

        try {

          val o = options.get(key)//.asInstanceOf[Option[Any]]
          println(o)

          o.asInstanceOf[Option[Map[String, Any]]]

        } catch {
          case e : Exception => throw new IllegalArgumentException("Error while getting %s value: %s".format(key, e))
        }

      case _ => throw new IllegalArgumentException("Error in matching json string.")
    }
  }
}