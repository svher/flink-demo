package learn
import scala.collection.{LinearSeq, immutable}

object ch19 {
  def main(args: Array[String]): Unit = {
    val res = flattenRight(List(List(1, 2), List(3, 4)))

    println(res)
    println(res.take(3))

    val words = List("the", "quick", "brown", "fox")
    println(words map (_.length))
    println(words map (_.toList.reverse.mkString))

    println(List(1, 2, 3, -4, 5).span (_ > 0))

    val res2 = List(List(1, 2), List(3, 4)).foldRight(List[Int]())(_ ::: _)
  }

  def sum(xs: List[Int]): Int = (0 /: xs) (_ + _)

  def flattenRight[T](xss: List[List[T]]): List[T] = (xss :\ List[T]()) (_ ::: _)
}
