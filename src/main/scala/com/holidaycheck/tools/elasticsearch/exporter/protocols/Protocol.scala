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
  def read: Option[List[Entry]]
  def write(buffer: List[Entry])
  def getMapping: Option[Map[String,Array[Byte]]]
  def setMapping(mapping: Map[String,Array[Byte]])
}

