package com.holidaycheck.tools.elasticsearch

/**
 * Created with IntelliJ IDEA.
 * User: cgonzalez
 * Date: 5/15/13
 * Time: 2:43 PM
 * To change this template use File | Settings | File Templates.
 */
package object exporter {

  def seg(t: Long): Boolean = {
    (t - System.nanoTime() + (1000 * 1000 * 1000)) < 0
  }

  def percentage(n: Int, totalEntries: Long) = "%3.2f".format((n.toDouble / totalEntries.toDouble) * 100)

  def EntriesPerSecond(count: Long, i: Long, time: Long): Double= {
      (i.toDouble-count.toDouble) / ((System.nanoTime()-time)/(1000 * 1000 * 1000))
  }

}
