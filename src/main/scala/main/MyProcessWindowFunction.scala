package main

import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector
import util.SensorReading

class MyProcessWindowFunction extends ProcessWindowFunction[SensorReading, String, String, TimeWindow] {
  override def process(key: String, context: Context, elements: Iterable[SensorReading], out: Collector[String]): Unit = {
    var count = 0L
    for (in <- elements) {
      count = count + 1
    }
    out.collect(s"Window ${context.window} count: $count")
  }
}
