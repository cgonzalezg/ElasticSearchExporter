package com.holidaycheck.tools.elasticsearch.exporter.configurator

/**
 * Created with IntelliJ IDEA.
 * User: cesar
 * Date: 5/25/13
 * Time: 2:42 PM
 * To change this template use File | Settings | File Templates.
 */
import scala.collection._

trait Configurator {
  val config: Map[String, Any]
  val outHost: String = get(Conf.outHost).asInstanceOf[String]
  val inputHost: String = get(Conf.inHost).asInstanceOf[String]
  val indexIn: String = get(Conf.indexInput).asInstanceOf[String]
  val indexOut: String = get(Conf.indexOutput).asInstanceOf[String]
  val types: List[String] = get(Conf.types).asInstanceOf[List[String]]
  val port: String = get(Conf.portHttp).asInstanceOf[String]

  def get(value: String) = this.config.get(value).get
}
