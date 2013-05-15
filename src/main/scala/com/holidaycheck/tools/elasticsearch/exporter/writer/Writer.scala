package com.holidaycheck.tools.elasticsearch.exporter.writer

import java.net.{URL, HttpURLConnection}
import java.io.OutputStreamWriter
import org.elasticsearch.search.SearchHit
import scalaj.http.{HttpOptions, Http}

/**
 * Created with IntelliJ IDEA.
 * User: cgonzalez
 * Date: 5/7/13
 * Time: 7:59 AM
 * To change this template use File | Settings | File Templates.
 */
trait Writer {
  val esServer: String = "http://localhost:9292/facetedsearch/"

  def put(url: String, data: String) = {
    val uri = new URL(url);
    val conn = uri.openConnection().asInstanceOf[HttpURLConnection]
    conn.setRequestMethod("PUT");
    conn.setDoOutput(true);
    conn.setRequestProperty("Content-Type", "application/json")
    conn.setRequestProperty("Accept", "application/json")
    val osw = new OutputStreamWriter(conn.getOutputStream());
    osw.write(data);
    osw.flush();
    osw.close();
    conn.getResponseMessage
  }

  def setMapping(outURL: String, mappings: Map[String, String]) = {
    mappings.map(x => {
      (x._1 -> put(outURL + "/" + x._1 + "/_mapping", x._2))
    })
  }

  def create(outURL: String) = {
    put(outURL, "")
  }

  def post(outputURL: String, entry: SearchHit, resend: Int): Option[String] = {
    try {
      Http.postData(outputURL + "/" + entry.`type`() + "/" + entry.id(), entry.source).option(HttpOptions.connTimeout(1000)).option(HttpOptions.readTimeout(5000)).asString
      None
    } catch {
      case e: java.net.SocketTimeoutException => {
        if (resend < 3) {
          post(outputURL, entry, resend + 1)

        }
        else {
          println(entry.id)
          Option(entry.id)
        }
      }
    }
  }

}
