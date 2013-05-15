package com.holidaycheck.tools.elasticsearch.exporter.reader

import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.node.NodeBuilder
import org.elasticsearch.action.search.{SearchRequestBuilder, SearchResponse, SearchType}
import org.elasticsearch.common.unit.TimeValue
import scalaj.http.Http

/**
 * Created with IntelliJ IDEA.
 * User: cgonzalez
 * Date: 5/7/13
 * Time: 7:58 AM
 * To change this template use File | Settings | File Templates.
 */
//trait Reader {
//  def mapping = {
//    import scala.collection.JavaConversions._
//
//    val cs = clientDev.admin().cluster().prepareState().setFilterIndices("facetedsearch").execute().actionGet().getState();
//    val imd = cs.getMetaData().index("facetedsearch").mappings()
//    imd.toMap.map(x => x._1).toList
//  }
//
//  def importMapping(inputUrl: String):List[String] = {
//    mapping.map(t => {Http.get(inputUrl + t + "/_mapping").asString}).toList
//  }
//}
trait ReaderM12ndev1 {
  val settingDev = ImmutableSettings.settingsBuilder();
  settingDev.put("node.name", "facetedsearch-importer")
  settingDev.put("discovery.zen.ping.multicast.enabled", false)
  settingDev.put("discovery.zen.ping.unicast.hosts", "m12ndev1.hc.lan")
  val nodeDev = NodeBuilder.nodeBuilder.client(true).clusterName("m12ndev1").settings(settingDev).node
  val clientDev = nodeDev.client()
//  val searchRequest: SearchRequestBuilder = clientDev.prepareSearch("facetedsearch").setTypes("destination").setSize(10000).setSearchType(SearchType.SCAN).setScroll(TimeValue.timeValueMillis(100000))
  val searchRequest: SearchRequestBuilder = clientDev.prepareSearch("facetedsearch").setSize(10000).setSearchType(SearchType.SCAN).setScroll(TimeValue.timeValueMillis(100000))
  var searchResponse: SearchResponse = searchRequest.execute.actionGet
  val totalEntries = searchResponse.getHits.getTotalHits

  def mapping = {
    import scala.collection.JavaConversions._

    val cs = clientDev.admin().cluster().prepareState().setFilterIndices("facetedsearch").execute().actionGet().getState();
    val imd = cs.getMetaData().index("facetedsearch").mappings()
    imd.toMap.map(x => x._1).toList
  }

  def importMapping(inputUrl: String): Map[String, String] = {
    mapping.map(t => {
      (t, Http.get(inputUrl+"/" + t + "/_mapping").asString)
    }).toMap
  }
}

trait ReaderLocalHost {
  val settingDev = ImmutableSettings.settingsBuilder();
  settingDev.put("node.name", "testing-es-client")
  settingDev.put("discovery.zen.ping.multicast.enabled", false)
  settingDev.put("http.enabled", "true")
  settingDev.put("discovery.zen.ping.unicast.hosts", "127.0.0.1:9393")
  val nodeDev = NodeBuilder.nodeBuilder.client(true).clusterName("localhost-testing").settings(settingDev).node
  val clientDev = nodeDev.client()

  val searchRequest: SearchRequestBuilder = clientDev.prepareSearch("facetedsearch").setSize(10000).setSearchType(SearchType.SCAN).setScroll(TimeValue.timeValueMillis(100000))
  var searchResponse: SearchResponse = searchRequest.execute.actionGet

  def mapping = {
    import scala.collection.JavaConversions._

    val cs = clientDev.admin().cluster().prepareState().setFilterIndices("facetedsearch").execute().actionGet().getState();
    val imd = cs.getMetaData().index("facetedsearch").mappings()
    imd.toMap.map(x => x._1).toList
  }

  def importMapping(inputUrl: String): Map[String, String] = {
    mapping.map(t => {
      (t, Http.get(inputUrl + t + "/_mapping").asString)
    }).toMap
  }
}