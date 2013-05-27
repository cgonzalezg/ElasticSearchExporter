package com.holidaycheck.tools.elasticsearch.exporter.reader

import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.node.NodeBuilder
import org.elasticsearch.action.search.{SearchRequestBuilder, SearchResponse, SearchType}
import org.elasticsearch.common.unit.TimeValue
import scalaj.http.Http
import com.holidaycheck.tools.elasticsearch.exporter.configurator.{TcpProtocol, HttpConf, TCPConf, Configurator}
import org.elasticsearch.search.SearchHit
import com.holidaycheck.tools.elasticsearch.exporter.Entry

/**
 * Created with IntelliJ IDEA.
 * User: cgonzalez
 * Date: 5/7/13
 * Time: 7:58 AM
 * To change this template use File | Settings | File Templates.
 */
//    import scala.collection.JavaConversions._
//
class TCPReader(c: TCPConf) extends Reader(c) with TcpProtocol{
  val nodeDev = NodeBuilder.nodeBuilder().client(true).clusterName(c.clusterName).settings(c.settingDev).node()
  val clientDev = nodeDev.client()
  val searchRequest: SearchRequestBuilder = clientDev.prepareSearch(c.indexIn).setSize(10000).setSearchType(SearchType.SCAN).setScroll(TimeValue.timeValueMillis(100000))
  var searchResponse: SearchResponse = searchRequest.execute.actionGet
  def setConfig(config: T): U = new TCPReader(config.asInstanceOf[TCPConf])

  def mapping = {
    import scala.collection.JavaConversions._

    val cs = clientDev.admin().cluster().prepareState().setFilterIndices(c.indexIn).execute().actionGet().getState();
    val imd = cs.getMetaData().index(c.indexIn).mappings()

    val buffer = c.types match {
      case head :: tail => c.types.map(t =>{
        (imd.get(t).`type`(), imd.get(t).source().string().getBytes())
      }).toMap

      case _ => imd.toMap.map(t => {
        (t._1, t._2.source().string().getBytes())
      }).toMap
    }
    Option(buffer)
  }



  def read = {
    if (searchResponse.getHits.hits.length > 0) {
      searchResponse = clientDev.prepareSearchScroll(searchResponse.getScrollId).setScroll(TimeValue.timeValueMillis(100000)).execute.actionGet
      val buffer = searchResponse.getHits.hits().toList.par.map(entry => {
        Entry(entry.getId, entry.`type`(),entry.source())
      }).toList
      Option(buffer)
    }
    else
    Option.empty
  }



}



