package com.holidaycheck.tools.elasticsearch.exporter

/**
 * Created with IntelliJ IDEA.
 * User: cesar
 * Date: 5/25/13
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
package object configurator {
  val http = "http://"
  def httpCreator(host: String, port: String, index: String) = {
    http+host+":"+port+"/"+index+"/"
  }
  object Conf extends Enumeration {
    type Conf = Value
    val outHost = "outHost"
    val inHost = "inHost"
    val indexInput="indexInput"
    val indexOutput="indexOutput"
    val types = "types"
    val portHttp = "portHttp"
    val portTCP = "portTCP"
    val clusterNameIn = "clusterNameIn"
    val clusterNameOut = "clusterNameOut"

  }
}
