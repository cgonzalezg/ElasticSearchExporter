package com.holidaycheck.tools.elasticsearch.exporter.agents

import com.holidaycheck.tools.elasticsearch.exporter.configurator.Configurator

/**
 * Created with IntelliJ IDEA.
 * User: cesar
 * Date: 5/25/13
 * Time: 8:00 PM
 * To change this template use File | Settings | File Templates.
 */
trait Agent {
  type T = Configurator
  type U = Agent
  def setConfig(c: T):U

}
