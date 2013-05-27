package com.holidaycheck.tools.elasticsearch.exporter.writer

import java.net.{HttpURLConnection, URL}
import org.elasticsearch.search.SearchHit
import scalaj.http.{HttpOptions, Http}
import com.holidaycheck.tools.elasticsearch.exporter.configurator.{Configurator, HttpConf}
import com.holidaycheck.tools.elasticsearch.exporter.Entry

/**
 * Created with IntelliJ IDEA.
 * User: cesar
 * Date: 5/26/13
 * Time: 2:57 AM
 * To change this template use File | Settings | File Templates.
 */
class HttpWriter(c: HttpConf) extends Writer(c){
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

  def mapping(mappings: Map[String, Array[Byte]]) = {
    mappings.map(x => {
      (x._1 -> put(c.outputURL + "/" + x._1 + "/_mapping", x._2))
    })
  }

  def create(outURL: String) = {
    put(outURL, "".getBytes)
  }

  def post(entry: Entry, resend: Int): Option[String] = {
    try {
      Http.postData(c.outputURL + "/" + entry.`type` + "/" + entry.id, entry.data).option(HttpOptions.connTimeout(1000)).option(HttpOptions.readTimeout(5000)).asString
      None
    } catch {
      case e: java.net.SocketTimeoutException => {
        if (resend < 3) {
          post( entry, resend + 1)

        }
        else {
          println(entry.id)
          Option(entry.id)
        }
      }
    }
  }

  def write(buffer: List[Entry]) {
    buffer.par.map(entry=>{
      post(entry,0)
    })
  }

  def setConfig(c: HttpWriter#T): HttpWriter#U = null

  def setConfigurator(c: Configurator) = new HttpWriter(c.asInstanceOf[HttpConf])
}
