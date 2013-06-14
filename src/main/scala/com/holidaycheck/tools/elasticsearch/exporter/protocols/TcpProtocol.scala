package com.holidaycheck.tools.elasticsearch.exporter.protocols

import com.holidaycheck.tools.elasticsearch.exporter.Entry
import com.holidaycheck.tools.elasticsearch.exporter.configurator.TCPConf
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.common.unit.TimeValue
import scala.collection
import org.elasticsearch.action.bulk.BulkRequestBuilder
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest

/**
 * Created with IntelliJ IDEA.
 * User: cesar
 * Date: 5/26/13
 * Time: 7:48 PM
 * To change this template use File | Settings | File Templates.
 */
sealed class TcpProtocol extends TCPConf with Protocol {
  var searchResponse: SearchResponse = _

  def read: Option[List[Entry]] = {
    searchResponse = clientDevIn.prepareSearchScroll(searchResponse.getScrollId).setScroll(TimeValue.timeValueMillis(100000)).execute.actionGet
    if (searchResponse.getHits.hits.length > 0) {
      val buffer = searchResponse.getHits.hits().toList.par.map(entry => {
        Entry(entry.getId, entry.`type`(), entry.source())
      }).toList
      Option(buffer)
    }
    else
      None
  }

  import collection.mutable.Buffer

  val sBuffer: Buffer[Entry] = Buffer[Entry]()

  def writer(entry: Entry) = {
    //add entry to new subuffer
    sBuffer.append(entry)
    if (sBuffer.size < getBuffer) {
      val bulkRequest: BulkRequestBuilder = clientDevOut.prepareBulk()
      val writeRequest = clientDevOut.prepareIndex().setIndex(indexOut)
      sBuffer.map(entry => {
        bulkRequest.add(writeRequest.setType(entry.`type`).setId(entry.id).setSource(entry.data))
      })
      bulkRequest.execute().actionGet()
      sBuffer.clear()
    }
    None
  }

  def getMapping: Option[Map[String, Array[Byte]]] = {
    import scala.collection.JavaConversions._

    val cs = clientDevIn.admin().cluster().prepareState().setFilterIndices(indexIn).execute().actionGet().getState();
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

  def setMapping(mapping: Map[String, Array[Byte]]) {
    mapping.map(t => {
      clientDevOut.admin().indices().create(new CreateIndexRequest(indexOut).mapping(t._1, new String(t._2)))
    })
  }

  def setConfiguration(c: Map[String, Any]) = {
    this.config = c
    settingDevIn.put("node.name", "elasticsearch")
    settingDevIn.put("discovery.zen.ping.multicast.enabled", false)
    settingDevIn.put("discovery.zen.ping.unicast.hosts", hostIn)
    searchResponse = searchRequest.execute.actionGet
    totalEntries = searchResponse.getHits.getTotalHits
  }

  override var totalEntries: Long = _
}
