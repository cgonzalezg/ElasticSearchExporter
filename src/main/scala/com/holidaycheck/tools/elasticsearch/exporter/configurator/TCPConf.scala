package com.holidaycheck.tools.elasticsearch.exporter.configurator

import org.elasticsearch.common.settings.ImmutableSettings

/**
 * Created with IntelliJ IDEA.
 * User: cesar
 * Date: 5/25/13
 * Time: 6:24 PM
 * To change this template use File | Settings | File Templates.
 */

trait TCPConf extends Configurator {
  def hostIn = this.inputHost + ":" + this.portTcp
  def hostOut = this.outHost  + ":" + this.portTcp
  def clusterName = get(Conf.clusterName).asInstanceOf[String]
  lazy val settingDev = ImmutableSettings.settingsBuilder();


}