package com.holidaycheck.tools.elasticsearch.exporter.configurator

/**
 * Created with IntelliJ IDEA.
 * User: cesar
 * Date: 5/25/13
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
trait HttpConf extends Configurator {
  val inputURL = httpCreator(this.inputHost, this.port, this.indexIn)
  val outputURL = httpCreator(this.outHost, this.port, this.indexIn)

}
