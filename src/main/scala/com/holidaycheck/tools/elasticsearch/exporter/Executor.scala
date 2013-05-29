package com.holidaycheck.tools.elasticsearch.exporter


import scala.util.control.Breaks
import com.holidaycheck.tools.elasticsearch.exporter.protocols.{HttpProtocol, TcpProtocol, Protocol}

/**
 * Created with IntelliJ IDEA.
 * User: cgonzalez
 * Date: 5/7/13
 * Time: 7:57 AM
 * To change this template use File | Settings | File Templates.
 */

sealed case class Entry(id: String, `type`: String, data: Array[Byte])



trait Pipe {

  def pipe[A <: Protocol, B <: Protocol](conf: Map[String, Any])(implicit manifestA: Manifest[A], manifestB: Manifest[B]) = {
    val input = manifestA.erasure.newInstance.asInstanceOf[A]
    input.setConfiguration(conf)
    val output = manifestB.erasure.newInstance.asInstanceOf[B]
    output.setConfiguration(conf)
    input.getMapping match {
      case Some(x) => output.setMapping(x)
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
    "inHost" -> "localhost",
    "outHost" -> "localhost",
    //Protocols
    "Input_Protocol" -> "tcp",
    "Output_Protocol" -> "http",
    //Indexes
    "indexInput" -> "tweter",
    "indexOutput" -> "tweeter2",
    //Ports
    "portHttp" -> "9200",
    "portTCP" -> "9300",
    //types
    "types" -> List[String]("tweet"),
    //
    "clusterName" -> "elasticsearch"


  )

  val input = conf.get("Input_Protocol").get.toString
  val output = conf.get("Output_Protocol").get.toString
  (input, output) match {
    case ("tcp", "http") => {
      pipe[TcpProtocol , HttpProtocol ](conf)
    }
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
