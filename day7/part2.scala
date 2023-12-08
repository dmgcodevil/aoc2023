package day7

import day7.part2.Type.*

import scala.collection.mutable
import scala.io.Source
import scala.util.Using

object part2 {

  val orderMap = Map(
    'A' -> 12,
    'K' -> 11,
    'Q' -> 10,
    'T' -> 9,
    '9' -> 8,
    '8' -> 7,
    '7' -> 6,
    '6' -> 5,
    '5' -> 4,
    '4' -> 3,
    '3' -> 2,
    '2' -> 1,
    'J' -> 0,
  )

  def main(args: Array[String]): Unit = {

    val fileName = "input"

    val lines: List[String] = Using.resource(Source.fromFile(fileName)) { source =>
      source.getLines().toList
    }


    val hands = lines.map { line =>
      val p = line.split(" ")
      Hand(p(0), p(1).toInt)
    }

    val sorted = hands.sorted
    println(sorted)
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
      if (str == "JJJJJ") return Hand(str, Five, bid)

      val cardCount = mutable.Map(str.toCharArray.filter(_ != 'J')
        .foldLeft(Map.empty[Char, Int])((m, c) => m + (c -> (m.getOrElse(c, 0) + 1))).toSeq: _*)

      val jCount = str.count(_ == 'J')

      for _ <- 0 until jCount do
        val maxKey = cardCount.maxBy(_._2)._1
        cardCount(maxKey) = cardCount(maxKey) + 1

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
