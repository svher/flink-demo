package learn

object QueueTest {
  def doesCompile(q: Queue[Any]): Queue[Any] = {
    q
  }
  def main(args: Array[String]): Unit = {
    val q: Queue[Int] = Queue(1, 2)
    println(doesCompile(q).enqueue(4).enqueue(3))
  }
}

trait Queue[+T] {
  def head: T
  def tail: Queue[T]
  def enqueue[U >: T](x: U): Queue[U]
}

object Queue {
  def apply[T](xs: T*): Queue[T] = new QueueImpl[T](xs.toList, Nil)

  // private[this] is necessary to avoid error
  private class QueueImpl[+T](
                  private[this] var leading: List[T],
                  private[this] var trailing: List[T]
                ) extends Queue[T] {
    private def mirror(): Unit =
      if (leading.isEmpty) {
        while (trailing.nonEmpty) {
          leading = trailing.head :: leading
          trailing = trailing.tail
        }
      }

    def head: T = {
      mirror()
      leading.head
    }
    // n times tail operation will amortize O(n) mirror operation
    def tail: QueueImpl[T] = {
      mirror()
      new QueueImpl(leading.tail, trailing)
    }
    // T as the lower bound of U
    def enqueue[U >: T](x: U): QueueImpl[U] = {
      new QueueImpl(leading, x :: trailing)
    }

    override def toString: String = (leading ::: trailing.reverse).toString
  }
}

