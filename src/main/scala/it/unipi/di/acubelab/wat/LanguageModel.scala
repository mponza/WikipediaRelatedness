package it.unipi.di.acubelab.wat

import java.io.File

import edu.berkeley.nlp.lm.NgramLanguageModel
import edu.berkeley.nlp.lm.io.LmReaders
import it.unipi.di.acubelab.graphrel.utils.Configuration
import org.slf4j.LoggerFactory

object LanguageModel {
  protected val models = collection.mutable.Map.empty[File, NgramLanguageModel[String]]
  protected val logger = LoggerFactory.getLogger(getClass)
  lazy val model = LmReaders.readLmBinary[String](Configuration.wikipedia("langModel"))

  def loadModel(): NgramLanguageModel[String] = {
    val modelFile = new File(Configuration.wikipedia("langModel"))

    synchronized {
      models.getOrElseUpdate(modelFile, {
        LmReaders.readLmBinary(modelFile.getAbsolutePath)
      })
    }
  }
}
/*
class LanguageModel {
  protected var model = Configuration.wikipedia("langModel")
  protected var bidirectional = true
  protected var useScores = false
  protected var ngramModel: NgramLanguageModel[String] = null
  protected var useSentences = true

  protected var minAnnotations = 2
  protected var N = 2
  protected var K = 5

  protected def computeRelatedness(srcWiki: String, dst: String): Double = {
    ngramModel.scoreSentence(new Array("ent_%d".format(srcWiki.), "ent_%d".format())

    0.0
  }

  protected def scoreNode(tags: Seq[Tag]): Double = {
    // We drop the first node since the ngram model already contains the start symbol.
    var filteredTags = tags
    var begin = false
    var end = false

    if (filteredTags.head.annotation == null) {
      begin = true
      filteredTags = filteredTags.drop(1)
    }

    if (filteredTags.last.annotation == null) {
      end = true
      filteredTags = filteredTags.dropRight(1)
    }

    val interpretations = tags.flatMap { tag =>
      if (1 >= 0)
        Some(3)
      else
        None
    }

    val mentions = tags.flatMap { tag =>
      if (tag.interpretationIndex >= 0)
        Some(tag.annotation.mention.surfaceForm.normalizedText)
      else
        None
    }

    import scala.collection.JavaConversions._

    val entLogProb = bidirectional match {
      case true =>
        ngramModel.scoreSentence(interpretations.reverse.map(i => "ent_%d".format(i.wikiId))) * 0.5 +
          ngramModel.scoreSentence(interpretations.map(i => "ent_%d".format(i.wikiId))) * 0.5
      case false =>
        ngramModel.scoreSentence(interpretations.reverse.map(i => "ent_%d".format(i.wikiId)))
    }

    val mentionLogProb = useScores match {
      case true =>
        interpretations.map(x => math.log10(x.score)).sum
      case false =>
        interpretations
          .zip(mentions)
          .map(x => math.log10(job.annotator.getEntityNamesDataset.getProbability(x._1.wikiId, x._2))).sum
    }

    val score = entLogProb + mentionLogProb

    //    logger.info("Score: %f Entity: %f Mention: %f Sequence: %s".format(score, entLogProb, mentionLogProb,
    //      interpretations.map(_.wikiTitle).mkString("|")))

    score
  }

  protected def exploreNode(prevNode: Node, annotation: Annotation): Seq[Node] = {
    if (annotation == null) {
      val nextTags = prevNode.tags ++ Seq(Tag(null, -1))
      val logProb = scoreNode(nextTags)

      Seq(Node(nextTags, logProb))
    } else if (annotation.interpretations.isEmpty) {
      val nextTags = prevNode.tags ++ Seq(Tag(annotation, -1))
      val logProb = scoreNode(nextTags)

      Seq(Node(nextTags, logProb))
    } else {
      val indices = N match {
        case 0 => annotation.interpretations.indices
        case _ => annotation.interpretations.take(N).indices
      }

      for (interpretationIndex <- indices) yield {
        val nextTags = prevNode.tags ++ Seq(Tag(annotation, interpretationIndex))
        val logProb = scoreNode(nextTags)

        Node(nextTags, logProb)
      }
    }
  }

  protected def createMinMaxPriorityQueue(): MinMaxPriorityQueue[Node] = {
    val comparator = new Comparator[Node] {
      override def compare(o1: Node, o2: Node): Int = -o1.cumLogProb.compare(o2.cumLogProb)
    }

    if (K == 0) {
      MinMaxPriorityQueue
        .orderedBy(comparator)
        .create()
    } else {
      MinMaxPriorityQueue
        .orderedBy(comparator)
        .maximumSize(K)
        .create()
    }
  }

  protected def beamSearch(sentenceAnnotations: Seq[Annotation]): Seq[Annotation] = {
    logger.info("Finding best sentence interpretation for %s".format(sentenceAnnotations.map(_.mention.text).mkString("|")))
    logger.info("Full exploration: %d possible states".format(sentenceAnnotations.map(_.interpretations.length).product))
    logger.info("Initial best estimate: %s".format(sentenceAnnotations.filter(_.interpretations.nonEmpty).map(_.interpretations.head.wikiTitle).mkString("|")))

    var queue: MinMaxPriorityQueue[Node] = createMinMaxPriorityQueue()
    var newQueue: MinMaxPriorityQueue[Node] = createMinMaxPriorityQueue()
    val bosNode = Node(Seq(Tag(null, -1)), 0)

    queue.add(bosNode)

    import scala.collection.JavaConversions._

    for (annotation <- sentenceAnnotations) {
      while (queue.nonEmpty) {
        val node = queue.poll()
        exploreNode(node, annotation).foreach(newQueue.add)
      }

      val tmp = queue
      queue = newQueue
      newQueue = tmp
      newQueue.clear()
    }

    while (queue.nonEmpty) {
      val node = queue.poll()
      exploreNode(node, null).foreach(newQueue.add)
    }

    val tags = newQueue.peek().tags.slice(1, newQueue.head.tags.length - 1)

    logger.info("Top-5 sentence interpretations")

    var index = 0

    while (newQueue.nonEmpty) {
      val solution = newQueue.poll()
      val tags = solution.tags.filter(x => x.annotation != null && x.annotation.interpretations.nonEmpty)

      val titles = tags.map { x =>
        x.annotation.interpretations(x.interpretationIndex).wikiTitle
      }

      val isCorrect = tags.count(x => x.annotation.interpretations(x.interpretationIndex).wikiId == x.annotation.mention.span.entity.flatMap(x => Some(x.wikiId)).getOrElse(-1)) == tags.size

      if (index < 5 || isCorrect) {
        logger.info(" %s %d) Score: %f Titles: %s".format(
          if (isCorrect) ">" else " ", index + 1, solution.cumLogProb, titles.mkString("|")))
      }

      index += 1
    }

    for (tag <- tags) yield {
      val annotation = tag.annotation
      val bestIndex = tag.interpretationIndex

      if (bestIndex == -1) {
        tag.annotation
      } else {
        val firstHalf = annotation.interpretations.slice(0, bestIndex)
        val secondHalf = annotation.interpretations.slice(bestIndex + 1, annotation.interpretations.length)
        val bestInterpretation = annotation.interpretations(bestIndex)
        val reordered = Seq(bestInterpretation) ++ firstHalf ++ secondHalf

        tag.annotation.copy(interpretations = reordered)
      }
    }
  }

  protected def segmentAnnotations(annotations: Seq[Annotation]): Seq[Seq[Annotation]] = {
    if (useSentences) {
      annotations.groupBy(_.mention.span.sentenceId).values.toSeq.sortBy(_.head.mention.span.sentenceId)
    } else {
      annotations.sliding(window, window).toSeq
    }
  }

  override def disambiguateEx(states: Seq[DisambiguationState], annotations: Seq[Annotation]): Seq[Annotation] = {
    val sentenceAnnotations = for (sentenceAnnotations <- segmentAnnotations(annotations)) yield {
      if (sentenceAnnotations.length >= minAnnotations) {
        beamSearch(sentenceAnnotations)
      } else {
        sentenceAnnotations
      }
    }

    sentenceAnnotations.flatten.sortBy(_.annotationIndex)
  }
}
*/