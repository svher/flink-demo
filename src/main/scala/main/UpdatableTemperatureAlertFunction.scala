package main

import org.apache.flink.api.common.state.{MapState, MapStateDescriptor, ValueState, ValueStateDescriptor}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction
import org.apache.flink.util.Collector
import util.SensorReading

case class ThresholdUpdate(id: String, threshold: Double)

object UpdatableTemperatureAlertFunction {

  lazy val thresholdStateDescriptor = new MapStateDescriptor[String, Double]("thresholds", classOf[String], classOf[Double])
}

class UpdatableTemperatureAlertFunction extends KeyedBroadcastProcessFunction[String, SensorReading, ThresholdUpdate, (String, Double, Double)] {

  import UpdatableTemperatureAlertFunction._

  private var lastTempState: MapState[String, Double] = _

  override def open(parameters: Configuration): Unit = {
    val lastTempDescriptor = new MapStateDescriptor("lastTemp", classOf[String], classOf[Double])
    lastTempState = getRuntimeContext.getMapState(lastTempDescriptor)
  }

  override def processElement(value: SensorReading, ctx: KeyedBroadcastProcessFunction[String, SensorReading, ThresholdUpdate, (String, Double, Double)]#ReadOnlyContext, out: Collector[(String, Double, Double)]): Unit = {
    val thresholds = ctx.getBroadcastState(thresholdStateDescriptor)
    if (thresholds.contains(value.id)) {
      val sensorThreshold = thresholds.get(value.id)
      val lastTemp = lastTempState.get(value.id)
      val tempDiff = (value.temperature - lastTemp).abs
      if (tempDiff > sensorThreshold) {
        out.collect((value.id, value.temperature, tempDiff))
      }
    }
    lastTempState.put(value.id, value.temperature)
  }

  override def processBroadcastElement(value: ThresholdUpdate, ctx: KeyedBroadcastProcessFunction[String, SensorReading, ThresholdUpdate, (String, Double, Double)]#Context, out: Collector[(String, Double, Double)]): Unit = {
    val thresholds = ctx.getBroadcastState(thresholdStateDescriptor)
    if (value.threshold != 0.0d) {
      thresholds.put(value.id, value.threshold)
    } else {
      thresholds.remove(value.id)
    }
  }
}
