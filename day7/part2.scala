package day7

import day7.part2.Type.*

import scala.collection.mutable
import scala.io.Source
import scala.util.Using


// this code works for all possible input I could find on internet and still fails on the actual input
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

  def replace(input: String): String = {
    val chars = input.toCharArray
    val left_count = Array.fill(chars.length)(1)
    val right_count = Array.fill(chars.length)(1)

    def calcCounts(): Unit = {
      for i <- 0 until chars.length do
        left_count(i) = 1
        right_count(i) = 1

      for i <- 1 until chars.length do
        if chars(i) == chars(i - 1) then left_count(i) = left_count(i - 1) + 1

      for i <- chars.length - 2 to 0 by -1 do
        if chars(i + 1) == chars(i) then right_count(i) = right_count(i + 1) + 1
    }

    //    for i <- 0 until chars.length do
    //      println(s"i=$i, char '${chars(i)}', left_count=${left_count(i)}, right_count=${right_count(i)}")
    calcCounts()
    val jCount = chars.count(_ == 'J')
    if jCount > 0 && jCount < 5 then
      for i <- 0 until chars.length do
        if chars(i) == 'J' then
          val aIdx = i - 1
          val bIdx = chars.indexWhere(_ != 'J', i + 1)
          val aCount = if aIdx != -1 then left_count(aIdx) else 0
          val bCount = if bIdx != -1 then right_count(bIdx) else 0
          //          if aCount == bCount then
          //            if orderMap(chars(aIdx)) > orderMap(chars(bIdx)) then
          //              chars(i) = chars(aIdx)
          //            else for j <- i until bIdx do chars(j) = chars(bIdx)
          //          else if aCount > bCount then chars(i) = chars(aIdx)
          //          else for j <- i until bIdx do chars(j) = chars(bIdx)
          if aCount > bCount then chars(i) = chars(aIdx)
          else for j <- i until bIdx do chars(j) = chars(bIdx)
        calcCounts()

    new String(chars)
  }


  object Hand {
    def apply(str: String, bid: Int = 0): Hand = {
      val replaced = replace(str)

      val cardCount = mutable.Map(replaced.toCharArray
        .foldLeft(Map.empty[Char, Int])((m, c) => m + (c -> (m.getOrElse(c, 0) + 1))).toSeq: _*)
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
