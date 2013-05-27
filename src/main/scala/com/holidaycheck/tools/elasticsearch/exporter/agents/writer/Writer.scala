package com.holidaycheck.tools.elasticsearch.exporter.writer

import com.holidaycheck.tools.elasticsearch.exporter.configurator.Configurator
import com.holidaycheck.tools.elasticsearch.exporter.Entry
import com.holidaycheck.tools.elasticsearch.exporter.agents.Agent

/**
 * Created with IntelliJ IDEA.
 * User: cgonzalez
 * Date: 5/7/13
 * Time: 7:59 AM
 * To change this template use File | Settings | File Templates.
 */

abstract case class Writer(val c: Configurator) extends Agent{
  def write(buffer: List[Entry])
  def mapping(buffer: Map[String,Array[Byte]])

}
