package learn

import scala.collection.generic.CanBuildFrom
import scala.collection.{IndexedSeqLike, mutable}
import scala.collection.mutable.ArrayBuffer

object RNATest {
  def main(args: Array[String]): Unit = {
    val rna = RNA(A, U, G, G, T)
    println(rna map { case A => T case c => c })
  }
}

abstract class Base

case object A extends Base
case object T extends Base
case object G extends Base
case object U extends Base

object Base {
  val fromInt: Int => Base = Array(A, T, G, U)
  val toInt: Base => Int = Map(A -> 0, T -> 1, G -> 2, U -> 3)
}

object RNA {
  private val S = 2
  private val N = 32 / S
  private val M = (1 << S) - 1
  def fromSeq(buf: Seq[Base]): RNA = {
    val groups = new Array[Int]((buf.length + N - 1) / N)
    for (i <- buf.indices) {
      groups(i / N) |= Base.toInt(buf(i)) << (i % N * S)
    }
    new RNA(groups, buf.length)
  }

  def apply(bases: Base*): RNA = fromSeq(bases)

  def newBuilder: mutable.Builder[Base, RNA] = new ArrayBuffer[Base] mapResult fromSeq

  implicit def canBuildFrom: CanBuildFrom[RNA, Base, RNA] = {
    new CanBuildFrom[RNA, Base, RNA] {
      override def apply(from: RNA): mutable.Builder[Base, RNA] = newBuilder
      override def apply(): mutable.Builder[Base, RNA] = newBuilder
    }
  }
}

final class RNA private (
                          val groups: Array[Int],
                          val length: Int
                        ) extends IndexedSeq[Base] with IndexedSeqLike[Base, RNA]  {
  import RNA._

  override protected[this] def newBuilder: mutable.Builder[Base, RNA] =
    new ArrayBuffer[Base] mapResult fromSeq

  override def apply(idx: Int): Base = {
    if (idx < 0 || length <= idx) {
      throw new IndexOutOfBoundsException
    }
    Base.fromInt(groups(idx / N) >> (idx % N * S) & M)
  }
}
