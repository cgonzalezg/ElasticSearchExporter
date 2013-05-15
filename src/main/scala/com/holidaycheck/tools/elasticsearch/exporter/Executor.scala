package com.holidaycheck.tools.elasticsearch.exporter

import com.holidaycheck.tools.elasticsearch.exporter.reader.{ReaderM12ndev1, ReaderLocalHost}
import com.holidaycheck.tools.elasticsearch.exporter.writer._
import org.elasticsearch.common.unit.TimeValue

import scalaj.http.{HttpOptions, Http}

import scala.util.parsing.json.{JSONArray, JSONFormat, JSONObject}

/**
 * Created with IntelliJ IDEA.
 * User: cgonzalez
 * Date: 5/7/13
 * Time: 7:57 AM
 * To change this template use File | Settings | File Templates.
 */


trait Configurator {
  val outputURL = "http://staging.cloud.hc.ag:9200/facetedsearch2/"
  val inputURL = "http://m12ndev1.hc.lan:9200/facetedsearch/"
  val index = "facetedsearch"
  val time = System.nanoTime()
}

/**
 *
 */
object Executor extends ReaderM12ndev1 with Writer with App with Configurator {

  var lostRequest = List[String]()
  var x = totalEntries
  var stepTime = System.nanoTime()
  var i = 1
  var count = 0

  //Create Indexes if its are not created
  create(outputURL)
  //Set the mappings if its not created
  println(setMapping(outputURL, importMapping(inputURL)))

  while (x > 0) {
    val aux = EntriesPerSecond(count, i, stepTime)
    count=i
    stepTime=System.nanoTime()
    searchResponse = clientDev.prepareSearchScroll(searchResponse.getScrollId).setScroll(TimeValue.timeValueMillis(100000)).execute.actionGet
    x = searchResponse.getHits.hits.length
    lostRequest = searchResponse.getHits().hits().toList.par.map(entry => {
      print(i + " of " + totalEntries + "(" + percentage(i, totalEntries) + "%) Entries/seg-> " + "%7.2f".format(aux) + "\r")

      i = i + 1
      post(outputURL, entry, 0)
    }).toList.flatten ::: lostRequest
  }


  val totalTime = (System.nanoTime() - time) / (1000 * 1000 * 1000)
  println("Total Time ->" + totalTime)
  println("Average Entries/seg->" + "%7.2f".format(totalEntries.toDouble / totalTime.toDouble))
  println("Lost Resquests ->" + lostRequest.size, "\t", lostRequest)
  println("Uuuhhhhhhhh fuck node.js")


}
