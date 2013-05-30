package com.holidaycheck.tools.elasticsearch.exporter.protocols

import com.holidaycheck.tools.elasticsearch.exporter._
import com.holidaycheck.tools.elasticsearch.exporter
import com.holidaycheck.tools.elasticsearch.exporter.Entry
import scala.Some

/**
 * Created with IntelliJ IDEA.
 * User: cesar
 * Date: 5/26/13
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
trait Protocol {
  var i: Long = 0
  var count: Long = 0
  val time = System.nanoTime()
  var stepTime = time
  var totalEntries: Long

  def read: Option[List[Entry]]

  protected def writer(buffer: Entry): Option[String]

  def getMapping: Option[Map[String, Array[Byte]]]

  def setMapping(mapping: Map[String, Array[Byte]])

  def setConfiguration(conf: Map[String, Any])

  def synchronize(input: Protocol) = {
    input.getMapping match {
      case Some(x) => {
        this.totalEntries = input.totalEntries
        this.setMapping(x)
      }
      case None =>
    }

  }

   def write(buffer: List[Entry]): List[Option[String]] = {
    import exporter._
    val aux = EntriesPerSecond(count, i, stepTime)
    count = i
    stepTime = System.nanoTime()
    buffer.map(entry => {
      i = i + 1
      print(i + " of " + totalEntries + "(" + percentage(i, totalEntries) + "%) Entries/seg-> " + "%7.2f".format(aux) + "\r")
      writer(entry)
    }).toList
  }
}

