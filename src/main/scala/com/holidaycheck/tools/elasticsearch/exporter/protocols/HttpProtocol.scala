package com.holidaycheck.tools.elasticsearch.exporter.protocols

import com.holidaycheck.tools.elasticsearch.exporter.configurator.HttpConf
import com.holidaycheck.tools.elasticsearch.exporter.Entry
import java.net.{HttpURLConnection, URL}
import scalaj.http.{HttpOptions, Http}

sealed case class HttpProtocol(override val config: Map[String, Any]) extends HttpConf with Protocol {
  def put(url: String, data: Array[Byte]) = {
    val uri = new URL(url);
    val conn = uri.openConnection().asInstanceOf[HttpURLConnection]
    conn.setRequestMethod("PUT");
    conn.setDoOutput(true);
    conn.setRequestProperty("Content-Type", "application/json")
    conn.setRequestProperty("Accept", "application/json")
    conn.getOutputStream().write(data)
    conn.getOutputStream().close()
    conn.getResponseMessage
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

  def write(buffer: List[Entry]): List[Option[String]] = {
    buffer.par.map(entry => {
      post(entry, 0)
    }).toList
  }

  def getMapping: Option[Map[String, Array[Byte]]] = null

  def setMapping(mapping: Map[String, Array[Byte]]) = {
    mapping.map(x => {
      (x._1 -> put(outputURL + "/" + x._1 + "/_mapping", x._2))
    })
  }

  def create = {
    put(outputURL, "".getBytes)
  }

  def setConfiguration(c: Map[String, Any]): Protocol = this.copy(c)

}