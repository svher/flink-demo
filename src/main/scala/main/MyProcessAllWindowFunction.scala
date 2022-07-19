package main

import org.apache.flink.streaming.api.scala.function.ProcessAllWindowFunction
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector
import util.SensorReading

class MyProcessAllWindowFunction extends ProcessAllWindowFunction[SensorReading, String, TimeWindow] {

  override def process(context: Context, elements: Iterable[SensorReading], out: Collector[String]): Unit = {
    var count = 0L
    for (in <- elements) {
      count = count + 1
    }
    out.collect(s"Window ${context.window} count: $count")
  }
}
