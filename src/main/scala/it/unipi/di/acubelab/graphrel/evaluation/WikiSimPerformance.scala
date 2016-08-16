package it.unipi.di.acubelab.graphrel.evaluation

import java.io.File
import java.nio.file.Paths

import com.github.tototoshi.csv.CSVWriter

trait WikiSimPerformance {
  // String which represents the performance.
  def toString() : String

  def savePerformance(path: String) = {
    new File(new File(path).getParent()).mkdirs

    val csvWriter = CSVWriter.open(new File(path))
    csvWriter.writeRow(toString())
    csvWriter.close
  }

  // Name of each column.
  def csvFields() : List[String]

  // Value of each column.
  def csvValues() : List[Double]
}