package com.holidaycheck.tools.elasticsearch.exporter.protocols

import com.holidaycheck.tools.elasticsearch.exporter.Entry
import com.holidaycheck.tools.elasticsearch.exporter.configurator.TCPConf
import org.elasticsearch.node.NodeBuilder
import org.elasticsearch.action.search.{SearchResponse, SearchType, SearchRequestBuilder}
import org.elasticsearch.common.unit.TimeValue
import scala.collection

/**
 * Created with IntelliJ IDEA.
 * User: cesar
 * Date: 5/26/13
 * Time: 7:48 PM
 * To change this template use File | Settings | File Templates.
 */
sealed class TcpProtocol extends TCPConf with Protocol {

  var searchResponse: SearchResponse = _
  lazy val clientDev = nodeDev.client()
  lazy val nodeDev = NodeBuilder.nodeBuilder().client(true).clusterName(clusterName).settings(settingDev).node()
  lazy val searchRequest: SearchRequestBuilder = clientDev.prepareSearch(indexIn).setSize(10000).setSearchType(SearchType.SCAN).setScroll(TimeValue.timeValueMillis(100000))

  def read: Option[List[Entry]] = {
    if (searchResponse.getHits.hits.length > 0) {
      searchResponse = clientDev.prepareSearchScroll(searchResponse.getScrollId).setScroll(TimeValue.timeValueMillis(100000)).execute.actionGet
      val buffer = searchResponse.getHits.hits().toList.par.map(entry => {
        Entry(entry.getId, entry.`type`(), entry.source())
      }).toList
      Option(buffer)
    }
    else
    None
  }

  def write(buffer: List[Entry]) = null

  def getMapping: Option[Map[String, Array[Byte]]] = {
    import scala.collection.JavaConversions._

    val cs = clientDev.admin().cluster().prepareState().setFilterIndices(indexIn).execute().actionGet().getState();
    val imd = cs.getMetaData().index(indexIn).mappings()

    val buffer = types match {
      case head :: tail => types.map(t => {
        (imd.get(t).`type`(), imd.get(t).source().string().getBytes())
      }).toMap

      case _ => imd.toMap.map(t => {
        (t._1, t._2.source().string().getBytes())
      }).toMap
    }
    Option(buffer)
  }

  def setMapping(mapping: Map[String, Array[Byte]]) {}

  def setConfiguration(c: Map[String, Any])= {
    this.config = c
    settingDev.put("node.name", "elasticsearch")
    settingDev.put("discovery.zen.ping.multicast.enabled", false)
    settingDev.put("discovery.zen.ping.unicast.hosts", hostIn)
    val nodeDev2 = NodeBuilder.nodeBuilder().client(true).clusterName(clusterName).settings(settingDev).node()
    searchResponse = searchRequest.execute.actionGet
  }

}
