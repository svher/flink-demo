package util

import org.apache.flink.streaming.api.functions.source.{RichParallelSourceFunction, SourceFunction}

import java.time.Duration
import java.util.Calendar
import scala.util.Random

class SensorSource extends RichParallelSourceFunction[SensorReading] {
  var running: Boolean = true

  override def run(ctx: SourceFunction.SourceContext[SensorReading]): Unit = {
    val rand = new Random()
    val taskIdx = this.getRuntimeContext.getIndexOfThisSubtask

    var curFTemp = (1 to 10).map {
      i => ("sensor_" + (taskIdx * 10 + i), 65 + (rand.nextGaussian() * 20))
    }

    while (running) {
      curFTemp = curFTemp.map(t => (t._1, t._2 + (rand.nextGaussian() * 0.5)))
      var curTime = Calendar.getInstance.getTimeInMillis
      if (rand.nextFloat() < 0.001) {
        curTime -= Duration.ofSeconds(100).toMillis
      }
      curFTemp.foreach(t => if (rand.nextFloat() < 0.5) ctx.collect(SensorReading(t._1, curTime, t._2)))
      Thread.sleep(100)
    }
  }

  override def cancel(): Unit = {
    running = false
  }
}
