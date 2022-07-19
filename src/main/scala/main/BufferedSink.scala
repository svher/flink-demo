package main

import org.apache.flink.api.common.state.{ListState, ListStateDescriptor}
import org.apache.flink.api.common.typeinfo.{TypeHint, TypeInformation}
import org.apache.flink.runtime.state.{FunctionInitializationContext, FunctionSnapshotContext}
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.api.scala.{createTypeInformation, scalaNothingTypeInfo}

import scala.collection.JavaConverters.{iterableAsScalaIterableConverter, seqAsJavaListConverter}
import scala.collection.LinearSeq
import scala.collection.mutable.ListBuffer

class BufferedSink(threshold: Int = 0) extends SinkFunction[(String, Int)] with CheckpointedFunction {
  // indicate this field will not be serialized
  @transient
  private var checkpointedState: ListState[(String, Int)] = _

  private val bufferedElements = ListBuffer[(String, Int)]()

  override def invoke(value: (String, Int), context: SinkFunction.Context): Unit = {
    bufferedElements += value
    if (bufferedElements.size >= threshold) {
      for (element <- bufferedElements) {

      }
      bufferedElements.clear()
    }
  }

  override def snapshotState(context: FunctionSnapshotContext): Unit = {
    checkpointedState.clear()
    for (element <- bufferedElements) {
      checkpointedState.add(element)
    }
  }

  override def initializeState(context: FunctionInitializationContext): Unit = {
    val descriptor = new ListStateDescriptor[(String, Int)](
      "buffered-elements",
      createTypeInformation[(String, Int)]
    )

    checkpointedState = context.getOperatorStateStore.getListState(descriptor)

    if (context.isRestored) {
      for (element <- checkpointedState.get().asScala) {
        bufferedElements += element
      }
    }
  }
}
