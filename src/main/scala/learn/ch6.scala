package learn

import scala.language.implicitConversions

object ch6 {
  def main(args: Array[String]): Unit = {
    val r = new Rational(2, 3)
    println(2 * r)
  }
}
