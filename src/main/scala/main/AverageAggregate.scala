package main

import org.apache.flink.api.common.functions.AggregateFunction
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector
import util.SensorReading

class AverageAggregate extends AggregateFunction[SensorReading, (Double, Long), Double]{
  override def createAccumulator(): (Double, Long) = (0, 0L)

  override def add(value: SensorReading, accumulator: (Double, Long)): (Double, Long) =
    (accumulator._1 + value.temperature, accumulator._2 + 1)

  override def getResult(accumulator: (Double, Long)): Double =
    accumulator._1 / accumulator._2

  override def merge(a: (Double, Long), b: (Double, Long)): (Double, Long) =
    (a._1 + b._1, a._2 + b._2)
}

class AverageProcessWindowFunction extends ProcessWindowFunction[Double, (String, Double), String, TimeWindow] {

  override def process(key: String, context: Context, elements: Iterable[Double], out: Collector[(String, Double)]): Unit = {
    val average = elements.iterator.next()
    out.collect((key, average))
  }
}