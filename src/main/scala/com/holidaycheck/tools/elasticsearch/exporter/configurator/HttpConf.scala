package com.holidaycheck.tools.elasticsearch.exporter.configurator

/**
 * Created with IntelliJ IDEA.
 * User: cesar
 * Date: 5/25/13
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
case class HttpConf(f: Map[String, Any]) extends Configurator(f) {
  val inputURL = httpCreateor(this.inputHost,this.port,this.indexIn,this.types)
  val outputURL = httpCreateor(this.outHost, this.port,this.indexIn, this.types)

}
