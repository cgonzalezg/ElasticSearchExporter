package com.holidaycheck.tools.elasticsearch.exporter

import com.holidaycheck.tools.elasticsearch.exporter.writer._
import org.elasticsearch.common.unit.TimeValue

import scalaj.http.{HttpOptions, Http}

import scala.util.parsing.json.{JSONArray, JSONFormat, JSONObject}
import com.holidaycheck.tools.elasticsearch.exporter.configurator._
import com.holidaycheck.tools.elasticsearch.exporter.reader.{Reader, TCPReader}
import scala.util.control.Breaks
import com.holidaycheck.tools.elasticsearch.exporter.agents.Agent

/**
 * Created with IntelliJ IDEA.
 * User: cgonzalez
 * Date: 5/7/13
 * Time: 7:57 AM
 * To change this template use File | Settings | File Templates.
 */

case class Entry(id: String, `type`: String, data: Array[Byte])


class Pipe {
  type TCP <: TcpProtocol
  type HTTP <: HttpProtocol

  def selector[A <: Agent](conf: Map[String, Any])(implicit manifestA: Manifest[A]): A = {
    val x = manifestA.erasure.asInstanceOf[A]
    val config = manifestA.erasure.asInstanceOf[A] match {
      case (m: TCP) => TCPConf(conf)
      case (m: HTTP) => HttpConf(conf)

    }
    x.setConfig(config).asInstanceOf[A]

  }

  def mapping [A <: Reader , B <: Writer ](conf: Map[String, Any])(implicit manifestA: Manifest[A], manifestB: Manifest[B]) = {

  }


    def pipe[A <: Reader , B <: Writer ](conf: Map[String, Any])(implicit manifestA: Manifest[A], manifestB: Manifest[B]) = {
    val input = selector[Reader](conf)
    val output = selector[Writer](conf)
      input.mapping match {
        case Some(x) => output.mapping(x)
        case None =>
      }
    val loop = new Breaks
    loop.breakable {
      while (true) {
        val buffer = input.read
        buffer match {
          case Some(x) => output.write(x)
          case None => loop.break()
        }

      }
    }

  }
}

/**
 *
 */
object Executor extends Pipe with App {

  //TODO Leer Conf.json
  lazy val conf = Map[String, Any](
    //Hosts
    "inHost" -> "",
    "outHost" -> "",
    //Protocols
    "Input_Protocol" -> "tcp",
    "Output_Protocol" -> "http",
    //Indexes
    "indexInput" -> "facetedsearch",
    "indexOutput" -> "facetedsearch",
    //Ports
    "portHttp"-> "9200",
    "portTCP" -> "9300",
    //types
    "types"->List[String](),
    //
    "clusterName" ->"exporter"



  )
  //
  val input = conf.get("Input_Protocol").get.toString
  val output = conf.get("Output_Protocol").get.toString
  val configurations = (input, output) match {
    case ("tcp", "http") => {

      pipe[TCPReader , HttpWriter](conf)
    }
    //case ("http", "http") => print("no implemented")
    case _ => print("no implemented")

  }


  //  var lostRequest = List[String]()
  //  var x = totalEntries
  //  var stepTime = System.nanoTime()
  //  var i = 1
  //  var count = 0
  //
  //  //Create Indexes if its are not created
  //  create(outputURL)
  //  //Set the mappings if its not created
  //  println(setMapping(outputURL, importMapping(inputURL)))
  //
  //  while (x > 0) {
  //    val aux = EntriesPerSecond(count, i, stepTime)
  //    count=i
  //    stepTime=System.nanoTime()
  //    searchResponse = clientDev.prepareSearchScroll(searchResponse.getScrollId).setScroll(TimeValue.timeValueMillis(100000)).execute.actionGet
  //    x = searchResponse.getHits.hits.length
  //    lostRequest = searchResponse.getHits().hits().toList.par.map(entry => {
  //      print(i + " of " + totalEntries + "(" + percentage(i, totalEntries) + "%) Entries/seg-> " + "%7.2f".format(aux) + "\r")
  //
  //      i = i + 1
  //      post(outputURL, entry, 0)
  //    }).toList.flatten ::: lostRequest
  //  }
  //
  //
  //  val totalTime = (System.nanoTime() - time) / (1000 * 1000 * 1000)
  //  println("Total Time ->" + totalTime)
  //  println("Average Entries/seg->" + "%7.2f".format(totalEntries.toDouble / totalTime.toDouble))
  //  println("Lost Resquests ->" + lostRequest.size, "\t", lostRequest)
  println("Uuuhhhhhhhh fuck node.js")


}
