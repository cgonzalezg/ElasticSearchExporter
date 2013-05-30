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
    "inHost" -> "localhost",
    "outHost" -> "localhost",
    //Protocols
    "Input_Protocol" -> "tcp",
    "Output_Protocol" -> "tcp",
    //Indexes
    "indexInput" -> "tweter",
    "indexOutput" -> "tweter2",
    //Ports
    "portHttp" -> "9200",
    "portTCP" -> "9300",
    //types
    "types" -> List[String]("tweet"),
    //Cluster Names
    "clusterNameIn" -> "elasticsearch",
    "clusterNameOut" ->"elasticsearch"

  )

  val input = conf.get("Input_Protocol").get.toString
  val output = conf.get("Output_Protocol").get.toString
  (input, output) match {
    case ("tcp", "http") => {
      pipe[TcpProtocol , HttpProtocol ](conf)
    }
    case ("tcp", "tcp") => pipe[TcpProtocol, TcpProtocol](conf)
    case _ => print("no implemented")

  }

  println("Finish")


}
