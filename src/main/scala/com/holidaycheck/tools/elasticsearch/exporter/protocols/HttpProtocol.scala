package com.holidaycheck.tools.elasticsearch.exporter.protocols

import com.holidaycheck.tools.elasticsearch.exporter.configurator.HttpConf
import com.holidaycheck.tools.elasticsearch.exporter.Entry
import java.net.{HttpURLConnection, URL}
import scalaj.http.{HttpOptions, Http}
import java.io.OutputStreamWriter
import com.holidaycheck.tools.elasticsearch.exporter

sealed class HttpProtocol extends HttpConf with Protocol {

  def get(url: String, data: Array[Byte]) = {
    request("GET",url, data)
  }

  def post(url: String, data: Array[Byte]) = {
    request("POST",url,data)
  }

  def put (url: String, data: Array[Byte]) = {
    request("PUT", url, data)
  }

  def request(`type`: String, url: String, data: Array[Byte]) = {
    val uri = new URL(url);
    val conn = uri.openConnection().asInstanceOf[HttpURLConnection]
    conn.setRequestMethod(`type`);
    conn.setDoOutput(true);
    conn.setRequestProperty("Content-Type", "application/json")
    conn.setRequestProperty("Accept", "application/json")
    conn.getOutputStream().write(data)
    conn.getOutputStream().close()
    val response = conn.getResponseMessage
    conn.disconnect()
    response
  }

  def post(entry: Entry, resend: Int): Option[String] = {
    try {
      Http.postData(outputURL + "/" + entry.`type` + "/" + entry.id, entry.data).option(HttpOptions.connTimeout(1000)).option(HttpOptions.readTimeout(5000)).asString
      None
    } catch {
      case e: java.net.SocketTimeoutException => {
        if (resend < 3) {
          post(entry, resend + 1)

        }
        else {
          println(entry.id)
          Option(entry.id)
        }
      }
    }
  }

  def read: Option[List[Entry]] = null
//
//     types match {
//       case head :: tail => {
//         types.map{
//           t=> get(inputURL+"/"+indexIn +"/"+t+"/"+"_search?search_type=scan&scroll=1m&size="+getBuffer)
//         }
//       }
//       case None => get(inputURL+"/"+indexIn+"/"+"_search?search_type=scan&scroll=1m&size="+getBuffer,)
//     }
//  }

  def writer(buffer: Entry): Option[String] = {
    post(buffer, 0)
  }

  def getMapping: Option[Map[String, Array[Byte]]] = null

  def setMapping(mapping: Map[String, Array[Byte]]) = {
    create
    mapping.map(x => {
      (x._1 -> put(outputURL + "/" + x._1 + "/_mapping", x._2))
    })
  }

  def create = {
    put(outputURL, "".getBytes)
  }

  def setConfiguration(c: Map[String, Any]) {
    this.config = c
  }

  override var totalEntries: Long = _

}