package main

import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.api.common.state.{StateTtlConfig, ValueState, ValueStateDescriptor}
import org.apache.flink.api.common.time.Time
import org.apache.flink.api.scala.{createTypeInformation, scalaNothingTypeInfo}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.util.Collector

import scala.collection.mutable.ArrayBuffer

class CountWindowAverage extends RichFlatMapFunction[(Long, Long), (Long, Long)] {
  private var sum: ValueState[(Long, Long)] = _
  override def flatMap(input: (Long, Long), out: Collector[(Long, Long)]): Unit = {
    val tmpCurrentSum = sum.value

    val currentSum = if (tmpCurrentSum != null) {
      tmpCurrentSum
    } else {
      (0L, 0L)
    }

    val newSum = (currentSum._1 + 1, currentSum._2 + input._2)

    sum.update(newSum)

    if (newSum._1 >= 2) {
      out.collect((input._1, newSum._2 / newSum._1))
      sum.clear()
    }
  }

  override def open(parameters: Configuration): Unit = {
    val ttlConfig = StateTtlConfig
      .newBuilder(Time.seconds(1))
      .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
      .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
      .build
    val stateDescriptor = new ValueStateDescriptor[(Long, Long)]("average", createTypeInformation[(Long, Long)])
    stateDescriptor.enableTimeToLive(ttlConfig)

    sum = getRuntimeContext.getState(
      stateDescriptor
    )
  }
}

object playground {
  def show(x: Option[String]): String = x match {
    case Some(s) => s
    case None => "?"
  }

  def main(args: Array[String]): Unit = {
    val longest = longestWord("The quick brown fox".split(" "))
    println(longest._1)

    val wc = WC("ab", 1)

    val capitals = Map("France" -> "Paris", "Japan" -> "Tokyo")

    println(show(capitals.get("A")))

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // placeholder syntax
    env.fromElements((1L, 1L), (1L, 5L), (1L, 7L), (1L, 4L), (1L, 3L), (1L, 9L)).keyBy(_._1).flatMap(new CountWindowAverage).print()
    env.execute()

  }

  def curriedSum(x: Int)(y: Int): Int = x + y

  def longestWord(words: Array[String]): (String, Int) = {
    var word = words(0)
    var idx = 0
    for (i <- 1 until words.length) {
      if (words(i).length > word.length) {
        word = words(i)
        idx = i
      }
    }
    (word, idx)
  }
}

abstract class IntQueue {
  def get(): Int
  def put(x: Int): Unit
}

class BasicIntQueue extends IntQueue {
  private val buf = new ArrayBuffer[Int]
  def get(): Int = buf.remove(0)
  def put(x: Int): Unit = { buf += x }
}

trait Doubling extends IntQueue {
  abstract override def put(x: Int): Unit = { super.put(2 * x) }
}

class MyQueue extends BasicIntQueue with Doubling

case class WC(word: String, count: Int)