package learn

import scala.collection.generic.CanBuildFrom
import scala.collection.{immutable, mutable}

object PrefixMapTest {

  def main(args: Array[String]): Unit = {
    val mp = PrefixMap("hello" -> 5, "hi" -> 2)
    val mp2 = mp map { case (k, v) => (k + "!", "x" * v)}
    for ((k, v) <- mp) {
      println(k, v)
    }
    println(mp)
    println(mp2)
  }
}

object PrefixMap {

  def empty[T] = new PrefixMap[T]

  def apply[T](kvs: (String, T)*): PrefixMap[T] = {
    val m: PrefixMap[T] = empty
    for (kv <- kvs) m += kv
    m
  }

  def newBuilder[T]: mutable.Builder[(String, T), PrefixMap[T]] =
    new mutable.MapBuilder[String, T, PrefixMap[T]](empty)

  implicit def canBuildFrom[T]: CanBuildFrom[PrefixMap[_], (String, T), PrefixMap[T]] =
    new CanBuildFrom[PrefixMap[_], (String, T), PrefixMap[T]] {

      override def apply(from: PrefixMap[_]): mutable.Builder[(String, T), PrefixMap[T]] = newBuilder[T]

      override def apply(): mutable.Builder[(String, T), PrefixMap[T]] = newBuilder[T]
    }
}

class PrefixMap[T] extends mutable.Map[String, T] with mutable.MapLike[String, T, PrefixMap[T]] {

  var suffixes : immutable.Map[Char, PrefixMap[T]] = Map.empty

  var value: Option[T] = None

  override def empty = new PrefixMap[T]

  override def +=(kv: (String, T)): PrefixMap.this.type = { update(kv._1, kv._2); this }

  override def -=(key: String): PrefixMap.this.type = { remove(key); this }

  def withPrefix(s: String): PrefixMap[T] =
    if (s.isEmpty) this
    else {
      val leading = s(0)
      suffixes get leading match {
        case None =>
          suffixes = suffixes + (leading -> empty)
        case _ =>
      }
      suffixes(leading) withPrefix (s substring 1)
    }

  override def update(key: String, value: T): Unit =
    withPrefix(key).value = Some(value)

  override def remove(key: String): Option[T] =
    if (key.isEmpty) { val prev = value; value = None; prev }
    else (suffixes get key(0)) flatMap (_.remove(key substring 1))

  override def get(key: String): Option[T] =
    if (key.isEmpty) value
    else (suffixes get key(0)) flatMap (_.get(key substring 1))

  override def iterator: Iterator[(String, T)] =
    (for (v <- value.iterator) yield ("", v)) ++
      (for ((chr, m) <- suffixes.iterator; (s, v) <- m.iterator) yield (chr +: s, v))
}