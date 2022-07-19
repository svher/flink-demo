package main

import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.util.Collector

case class CountWithTimestamp(count: Long, lastModified: Long)

class CountWithTimeoutFunction extends KeyedProcessFunction[String, (String, Double), (String, Long)] {

  lazy val state: ValueState[CountWithTimestamp] = getRuntimeContext.getState(new ValueStateDescriptor[CountWithTimestamp]("mystate", classOf[CountWithTimestamp]))

  override def processElement(value: (String, Double), ctx: KeyedProcessFunction[String, (String, Double), (String, Long)]#Context, out: Collector[(String, Long)]): Unit = {
    val current:CountWithTimestamp = state.value match {
      case null =>
        CountWithTimestamp(1, ctx.timestamp())
      case CountWithTimestamp(count, _) =>
        CountWithTimestamp(count + 1, ctx.timestamp())
    }

    state.update(current)

    ctx.timerService().registerEventTimeTimer(current.lastModified + 600)
  }

  override def onTimer(timestamp: Long, ctx: KeyedProcessFunction[String, (String, Double), (String, Long)]#OnTimerContext, out: Collector[(String, Long)]): Unit =
    state.value match {
      case CountWithTimestamp(count, lastModified) if timestamp == lastModified + 600 =>
        out.collect((ctx.getCurrentKey, count))
      case _ =>
    }
}
