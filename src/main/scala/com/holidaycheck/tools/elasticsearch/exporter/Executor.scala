package com.holidaycheck.tools.elasticsearch.exporter


import scala.util.control.Breaks
import com.holidaycheck.tools.elasticsearch.exporter.protocols.{HttpProtocol, TcpProtocol, Protocol}
import scala.io.Source

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
    val time = System.nanoTime()
    val input = manifestA.erasure.newInstance.asInstanceOf[A]
    input.setConfiguration(conf)
    val output = manifestB.erasure.newInstance.asInstanceOf[B]
    output.setConfiguration(conf)
    output.synchronize(input)

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
    val totalTime = (System.nanoTime() - time) / (1000 * 1000 * 1000)
    println("Total Time ->" + totalTime)
    println("Average Entries/seg->" + "%7.2f".format(input.totalEntries.toDouble / totalTime.toDouble))

  }
}

/**
 *
 */
object Executor extends Pipe with App {

  //TODO Leer Conf.json
  lazy val conf = Map[String, Any](
    //Hosts
    "inHost" -> "dev-dach-m12n.hc.lan",
    "outHost" -> "m12n-sf.hc.lan",
    //Protocols
    "Input_Protocol" -> "tcp",
    "Output_Protocol" -> "http",
    //Indexes
    "indexInput" -> "search_de_de",
    "indexOutput" -> "search_de_de",
    //Ports
    "portHttpOut" -> "9200",
    "portHttpIn" -> "9200",
    "portTCPIn" -> "9500",
    "portTCPOut" -> "9500",
    //types
    "types" -> List[String]("destination"),
    //Cluster Names
    "clusterNameIn" -> "tomcat-product-solo",
    "clusterNameOut" ->"testing-es-client",
    //buffer size
    "bufferSize" -> 50000
  )
//  val jsonQuery: Array[Byte] = Option(Source.fromFile("").getLines().toList) match {
//    case Some(head::tail)=>head.getBytes
//    case None => match_all.getBytes()
//  }

  val input = conf.get("Input_Protocol").get.toString
  val output = conf.get("Output_Protocol").get.toString
  (input, output) match {
    case ("tcp", "http") => {
      pipe[TcpProtocol , HttpProtocol ](conf)
    }
    case ("tcp", "tcp") => pipe[TcpProtocol, TcpProtocol](conf)
    case ("http", "http") => print("no implemented")
    case ("http", "tcp") => print("no implemented")
    case _ => print("no implemented")

  }

  println("Finish")


}
