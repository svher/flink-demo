package learn

class ch24 {

}

sealed abstract class Tree extends Iterable[Int] {
  override def foreach[U](f: Int => U): Unit = this match {
    case Node(elem) => f(elem)
    case Branch(left, right) => left foreach f; right foreach f
  }

  override def iterator: Iterator[Int] = this match {
    case Node(elem) => Iterator.single(elem)
    case Branch(left, right) => left.iterator ++ right.iterator
  }
}


case class Branch(left: Tree, right: Tree) extends Tree
case class Node(elem: Int) extends Tree

