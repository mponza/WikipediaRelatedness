package it.unipi.di.acubelab.graphrel.evaluation

import java.io.{File, PrintWriter}
import java.nio.file.Paths

import com.github.tototoshi.csv.CSVWriter

trait WikiSimPerformance {
  // String which represents the performance.
  def toString() : String

  def savePerformance(path: String) = {
    new File(new File(path).getParent()).mkdirs

    val printer = new PrintWriter(path)
    printer.write(toString())
    printer.close()
  }

  // Name of each column.
  def csvFields() : List[String]

  // Value of each column.
  def csvValues() : List[Double]
}