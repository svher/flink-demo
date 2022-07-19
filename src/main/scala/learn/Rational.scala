package learn

import scala.annotation.tailrec
import scala.language.implicitConversions

object Rational {
  implicit def intToRational(x: Int): Rational = new Rational(x)
}

class Rational(n: Int, d: Int) {
  require(d != 0)
  private val g = gcd(n.abs, d.abs)

  val numer: Int = n / g
  val denom: Int = d / g

  def this(n: Int) = this(n, 1)

  def * (i :Int): Rational =
    new Rational(numer * i, denom)

  def * (that: Rational): Rational =
    new Rational(numer * that.numer, denom * that.denom)

  @tailrec
  private def gcd(a: Int, b: Int): Int =
    if (b == 0) a else gcd(b, a % b)

  override def toString: String = numer + "/" + denom
}