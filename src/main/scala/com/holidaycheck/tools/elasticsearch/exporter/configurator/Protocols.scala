package com.holidaycheck.tools.elasticsearch.exporter.configurator

/**
 * Created with IntelliJ IDEA.
 * User: cesar
 * Date: 5/26/13
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
trait Protocol
trait TcpProtocol extends Protocol
trait HttpProtocol extends Protocol {

}
