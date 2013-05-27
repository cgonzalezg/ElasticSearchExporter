package com.holidaycheck.tools.elasticsearch.exporter.reader

import com.holidaycheck.tools.elasticsearch.exporter.configurator.Configurator
import com.holidaycheck.tools.elasticsearch.exporter.Entry
import com.holidaycheck.tools.elasticsearch.exporter.agents.Agent


abstract class Reader(c: Configurator) extends Agent{
  def read:Option[List[Entry]]
  def mapping: Option[Map[String,Array[Byte]]]


}
