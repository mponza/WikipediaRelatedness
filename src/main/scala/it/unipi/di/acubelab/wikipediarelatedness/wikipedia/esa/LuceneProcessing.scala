package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.esa

import java.io.{File, FileInputStream}
import java.nio.file.Paths
import java.util.zip.GZIPInputStream

import it.unipi.di.acubelab.lucene.VecTextField
import it.unipi.di.acubelab.wikipediarelatedness.utils.Config
import org.apache.lucene.document._
import org.apache.lucene.index.{IndexWriter, IndexWriterConfig}
import org.apache.lucene.store.FSDirectory
import org.slf4j.LoggerFactory

import scala.io.Source
import scala.util.parsing.json.JSON


class LuceneProcessing {
  protected val indexedDocs = 1000000

  val logger = getLogger()

  def getLogger() = LoggerFactory.getLogger(classOf[LuceneProcessing])


  /**
    * Builds Lucene Index.
    *
    */
  def process() = {
    logger.info("Indexing Wikipedia documents...")
    val directory = FSDirectory.open(Paths.get(Config.getString("wikipedia.lucene")))

    val analyzer = LuceneIndex.analyzer
    val config = new IndexWriterConfig(analyzer)
    val writer = new IndexWriter(directory, config)

    for (wikiDocs <- wikipediaDocuments().grouped(indexedDocs)) {
      wikiDocs.par.foreach(writer.addDocument(_))
    }

    logger.info("Wikipedia indexed. Now segment merging...")
    writer.forceMerge(1)
    writer.close()

    logger.info("Wikipedia indexing complete.")
    directory.close()
  }


  /**
    * Iterator over Wikipedia documents.
    *
    * @return (WikiTitle, WikiID, Body)
    */
  def wikipediaDocuments(): Iterator[Document] = {
    logger.info("Reading Wikipedia documents...")

    val fileStream = Source.fromInputStream(
      new GZIPInputStream(
        new FileInputStream(
          new File(Config.getString("wikipedia.corpus"))
        )
      )
    )

    for ((line, index) <- fileStream.getLines().zipWithIndex)
      yield {
        val wikiDoc = new Document()

        val (title, id, body) = line2WikiTitleIDBody(line)


        wikiDoc.add(new StringField("title", title, Field.Store.YES))
        wikiDoc.add(new StringField("id", id.toString, Field.Store.YES))  // int field?

        // val ft = new FieldType()
        // ft.setStored(true)
        // ft.setStoreTermVectors(true)
        //  ft.setTokenized(true)

        wikiDoc.add(new VecTextField("body", body, Field.Store.YES))


        if ((index + 1) % 10000 == 0) {
          logger.info("Indexed %d Wikipedia documents.".format(index + 1))
        }

        wikiDoc
      }
  }


  /**
    * Convert a json line into a Tuple of (WikiTitle, WikiID, Splitted Sentences).
    *
    * @param line
    * @return
    */
  def line2WikiTitleIDBody(line: String): Tuple3[String, Int, String] = {
    val jsonLine = JSON.parseFull(line)

    jsonLine match {
      case Some(jsonObject: Map[String, Any] @unchecked) =>

        val title = jsonObject("wikiTitle").asInstanceOf[String]
        val id = jsonObject("wikiId").asInstanceOf[Double].toInt
        val body = jsonObject("sentences").asInstanceOf[List[String]]

        return (title, id, body.mkString(" "))

      case _ => ;
    }

    throw new IllegalArgumentException("Error while parsing Wikipedia JSON row: %s".format(line))
  }
}
