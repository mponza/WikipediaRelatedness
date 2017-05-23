package it.unipi.di.acubelab.wikipediarelatedness.ranking

import it.unipi.di.acubelab.wikipediarelatedness.dataset.extend.kore.{KOREOriginalDataset, WikiKOREDataset, WikiRankTask}
import it.unipi.di.acubelab.wikipediarelatedness.evaluation.Correlation
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.{Relatedness, RelatednessFactory, RelatednessOptions}
import org.apache.commons.math.stat.correlation.SpearmansCorrelation

object RankingKORE {


  def main(args: Array[String]) {

    val options = RelatednessOptions.make(args)
    val rel = RelatednessFactory.make(options)

    val dataset = new WikiKOREDataset

    val it = dataset.tasks.slice(0, 5)
    val itSps = computeAVGSpearman("It Companies", it, rel)


    val hol = dataset.tasks.slice(5, 10)
    val holSps = computeAVGSpearman("Hollywood", hol, rel)

    val tv = dataset.tasks.slice(16, 21)
    val tvSps = computeAVGSpearman("TV Series", tv, rel)

    val vgs = dataset.tasks.slice(10, 15)
    val vgsSps = computeAVGSpearman("Videogames", vgs, rel)

    val chuck = dataset.tasks.slice(15, 16)
    val chuckSps = computeAVGSpearman("Chuck Norris", chuck, rel)



    val s = List(itSps, holSps, vgsSps, chuckSps, tvSps)
    println("Topic-AVG %1.2f".format(  s.sum / s.length ))


    println("-------------")
    println("All %1.2f".format(computeAVGSpearman("All", dataset.tasks, rel)) )
    println("All NO VideoGames %1.2f".format(computeAVGSpearman("All", it ++ hol ++ tv ++ chuck, rel)) )

  }



  def computeAVGSpearman(name: String, tasks: Seq[WikiRankTask], rel: Relatedness) = {

    println("From: %s to %s".format(tasks.head.seedWikiEntity.wikiTitle, tasks.reverse.head.seedWikiEntity.wikiTitle) )

    val sps = tasks.map {
      case task =>

        val seedWikiID = task.seedWikiEntity.wikiID

        val humanRanking = (1 to task.rankedEntities.length).map(_.toDouble).toArray.reverse
        val machineScores = task.rankedEntities.map(r => rel.computeRelatedness(seedWikiID, r.wikiID)).map(_.toDouble).toArray

        println(humanRanking mkString " ")
        println(machineScores mkString " ")

        val s = new SpearmansCorrelation().correlation(humanRanking, machineScores)
        //println(s)

        s
    }

    println("%s AVGSpearman is : %1.5f".format(name, sps.sum / sps.length.toFloat))

    sps.sum / sps.length.toFloat
  }
}
