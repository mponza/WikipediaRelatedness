package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.lucene

import java.io.{File, FileInputStream}
import java.nio.file.Paths
import java.util.zip.GZIPInputStream

import it.unipi.di.acubelab.wikipediarelatedness.utils.Configuration
import org.apache.lucene.document._
import org.apache.lucene.index.{IndexWriter, IndexWriterConfig}
import org.apache.lucene.store.FSDirectory
import org.slf4j.LoggerFactory

import scala.io.Source
import scala.util.parsing.json.JSON


class LuceneProcessing {
  val logger = LoggerFactory.getLogger(classOf[LuceneProcessing])

  def process() = {
    logger.info("Indexing Wikipedia documents...")
    val directory = FSDirectory.open(Paths.get(Configuration.wikipedia("lucene")))

    val analyzer = new WikipediaBodyAnalyzer()
    val config = new IndexWriterConfig(analyzer)
    val writer = new IndexWriter(directory, config)
    wikipediaDocuments().toStream.par.foreach(wikiDoc => writer.addDocument(wikiDoc))

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
    val fileStream = Source.fromInputStream(
      new GZIPInputStream(
        new FileInputStream(
          new File(Configuration.wikipedia("linkCorpus"))
        )
      )
    )

    for (line <- fileStream.getLines())
      yield {
        val wikiDoc = new Document()

        val (title, id, body) = line2WikiTitleIDBody(line)

        wikiDoc.add(new StringField("title", title, Field.Store.YES))
        wikiDoc.add(new StringField("id", id.toString, Field.Store.YES))  // simple int field?
        wikiDoc.add(new TextField("body", body, Field.Store.YES))

        wikiDoc
      }
  }

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
