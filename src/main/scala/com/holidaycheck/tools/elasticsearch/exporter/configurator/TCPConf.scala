package com.holidaycheck.tools.elasticsearch.exporter.configurator

import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.node.NodeBuilder
import org.elasticsearch.action.search.{SearchType, SearchRequestBuilder}
import org.elasticsearch.common.unit.TimeValue

/**
 * Created with IntelliJ IDEA.
 * User: cesar
 * Date: 5/25/13
 * Time: 6:24 PM
 * To change this template use File | Settings | File Templates.
 */

trait TCPConf extends Configurator {
  def portTcpIn: String = get(Conf.portTCPIn).asInstanceOf[String]
  def portTcpOut: String = get(Conf.portTCPOut).asInstanceOf[String]
  //Input Configuration
  def hostIn = this.inputHost + ":" + this.portTcpIn
  def clusterNameIn = get(Conf.clusterNameIn).asInstanceOf[String]
  lazy val settingDevIn = ImmutableSettings.settingsBuilder();
  lazy val nodeDevIn = NodeBuilder.nodeBuilder().client(true).clusterName(clusterNameIn).settings(settingDevIn).node()
  lazy val clientDevIn = nodeDevIn.client()
  lazy val searchRequest: SearchRequestBuilder = clientDevIn.prepareSearch(indexIn).setSize(10000).setSearchType(SearchType.SCAN).setScroll(TimeValue.timeValueMillis(100000))

  //Output Configuration
  def hostOut = this.outHost  + ":" + this.portTcpOut
  def clusterNameOut = get(Conf.clusterNameOut).asInstanceOf[String]
  lazy val settingDevOut = ImmutableSettings.settingsBuilder();
  lazy val nodeDevOut = NodeBuilder.nodeBuilder().client(true).clusterName(clusterNameOut).settings(settingDevOut).node()
  lazy val clientDevOut = nodeDevOut.client()


}