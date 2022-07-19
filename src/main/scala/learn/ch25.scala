package learn

import scala.collection.mutable.ArrayBuffer

object ch25 {
  def main(args: Array[String]): Unit = {
    val buf = new ArrayBuffer[String]
    val bldr = buf mapResult (_.toList)
    bldr ++= Seq("hello", "world")
    println(bldr.result())
  }
}
