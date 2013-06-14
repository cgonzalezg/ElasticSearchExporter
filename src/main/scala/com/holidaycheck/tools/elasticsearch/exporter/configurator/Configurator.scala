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
  var config: Map[String, Any] =_
  def outHost: String = get(Conf.outHost).asInstanceOf[String]
  def inputHost: String = get(Conf.inHost).asInstanceOf[String]
  def indexIn: String = get(Conf.indexInput).asInstanceOf[String]
  def indexOut: String = get(Conf.indexOutput).asInstanceOf[String]
  def types: List[String] = get(Conf.types).asInstanceOf[List[String]]



  def get(value: String) = this.config.get(value.toString).get
}
