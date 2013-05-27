package com.holidaycheck.tools.elasticsearch.exporter.writer

import com.holidaycheck.tools.elasticsearch.exporter.configurator.HttpConf
import com.holidaycheck.tools.elasticsearch.exporter.Entry

/**
 * Created with IntelliJ IDEA.
 * User: cgonzalez
 * Date: 5/7/13
 * Time: 7:59 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class TCPWriter(c:HttpConf) extends Writer(c) {

  def write(buffer: List[Entry]) {}

  def mapping(buffer: Map[String, Array[Byte]]) {}
}
