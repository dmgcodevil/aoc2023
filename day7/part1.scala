package day7

import scala.io.Source
import scala.util.Using

object part1 {

  import Type._

  val orderMap = Map(
    'A' -> 12,
    'K' -> 11,
    'Q' -> 10,
    'J' -> 9,
    'T' -> 8,
    '9' -> 7,
    '8' -> 6,
    '7' -> 5,
    '6' -> 4,
    '5' -> 3,
    '4' -> 2,
    '3' -> 1,
    '2' -> 0,
  )

  def main(args: Array[String]): Unit = {


    val lines: List[String] = Using.resource(Source.fromFile("input")) { source =>
      source.getLines().toList
    }

    val hands = lines.map { line =>
      val p = line.split(" ")
      Hand(p(0), p(1).toInt)
    }

    val sorted = hands.sorted
    val ans = sorted.zipWithIndex.foldLeft(0L)((acc, p) => acc + p._1.bid * (p._2 + 1))
    println(ans)

  }

  enum Type:
    case High, One, Two, Three, Full, Four, Five

  case class Hand(s: String, t: Type, bid: Int) extends Comparable[Hand] {
    override def compareTo(o: Hand): Int = {
      val cmp = t.ordinal.compareTo(o.t.ordinal)
      if (cmp != 0) cmp

      else s.zip(o.s).dropWhile((a, b) => orderMap(a) == orderMap(b))
        .headOption.map((a, b) => orderMap(a).compareTo(orderMap(b))).getOrElse(0)
    }
  }

  object Hand {
    def apply(str: String, bid: Int = 0): Hand = {
      val cardCount = str.toCharArray.foldLeft(Map.empty[Char, Int])((m, c) => m + (c -> (m.getOrElse(c, 0) + 1)))
      val count = cardCount.values.foldLeft(Map.empty[Int, Int])((m, c) => m + (c -> (m.getOrElse(c, 0) + 1)))
      val t = if (count.get(5).contains(1)) {
        Five
      } else if (count.get(4).contains(1)) {
        Four
      } else if (count.get(3).contains(1) && count.get(2).contains(1)) {
        Full
      } else if (count.get(3).contains(1)) {
        Three
      } else if (count.get(2).contains(2)) {
        Two
      } else if (count.get(2).contains(1)) {
        One
      }
      else High
      Hand(str, t, bid)
    }
  }


}
