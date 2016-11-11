package it.unipi.di.acubelab.wikipediarelatedness.dataset.nyt

class NYTTask(val src: NYTEntity, val dst: NYTEntity, val cooccurrence: Int,
               var distance: Int = Int.MinValue) {

  override def toString() = "%s,%s,%d,%d".format(src, dst, cooccurrence)
}



object NYTTask {

  /**
    * Creates a NYTTask from a csv row.
    * @param csvrow
    * @return
    */
  def csv2NYTTask(csvrow: Seq[String]): NYTTask = {

    val srcEntity = new NYTEntity(csvrow(0).toInt, csvrow(1).toString, csvrow(2).toInt)
    val dstEntity = new NYTEntity(csvrow(3).toInt, csvrow(4).toString, csvrow(5).toInt)

    val cooccurrence = csvrow(6).toInt
    val distance = if(csvrow.length < 8) Int.MinValue else csvrow(7).toInt

    new NYTTask(srcEntity, dstEntity, cooccurrence, distance)
  }
}