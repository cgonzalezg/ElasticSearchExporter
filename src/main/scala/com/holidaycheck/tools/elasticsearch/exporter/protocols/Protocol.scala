package com.holidaycheck.tools.elasticsearch.exporter.protocols

import com.holidaycheck.tools.elasticsearch.exporter.Entry

/**
 * Created with IntelliJ IDEA.
 * User: cesar
 * Date: 5/26/13
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
trait Protocol {
  var totalEntries: Long
   val time = System.nanoTime()
  def read: Option[List[Entry]]

  def write(buffer: List[Entry]): List[Option[String]]

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
}

