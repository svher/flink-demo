package learn

object ch20 {
  def main(args: Array[String]): Unit = {
    println(new RationalClass(1, 2))

    val s = Seq(1, 2, 3)
    println(4 +: s)
  }
}

trait RationalTrait {
  val numerArg: Int
  val denomArg: Int
  require(denomArg != 0)
  private val g = 1
  val numer: Int = numerArg / g
  val denom: Int = denomArg / g
  override def toString: String = numer + "/" + denom
}

class RationalClass(n: Int, d: Int) extends {
  val numerArg = n
  val denomArg = d
} with RationalTrait {

}