package day19

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.Using

object Main {

  sealed trait Rule {
    def name: String

    def test(a: Int): Action
  }

  case class Gt(name: String, value: Int, onTrue: Action, onFalse: Action = NoOp) extends Rule {
    override def test(a: Int): Action = {
      if (a > value) onTrue
      else onFalse
    }
  }

  case class Lt(name: String, value: Int, onTrue: Action, onFalse: Action = NoOp) extends Rule {
    override def test(a: Int): Action = {
      if (a < value) onTrue
      else onFalse
    }
  }

  // --------------------------------
  sealed trait Action

  case class Switch(name: String) extends Action

  case object Accept extends Action

  case object Reject extends Action

  case object NoOp extends Action

  // --------------------------------

  case class Flow(name: String, rules: List[Rule])

  def parseAction(s: String): Action = {
    s match {
      case "A" => Accept
      case "R" => Reject
      case _ => Switch(s)
    }
  }

  val rulesPattern =
    "((?<part>[xmsa]{1})(?<cond>[<>]{1})(?<operand>\\d+):(?<true>\\w+)((?=,[xmsa]{1}[<>]{1})|(,(?<false>\\w+))?))".r

  def parse(s: String): Flow = {
    val rules = ListBuffer.empty[Rule]
    val rulesSrt = s.substring(s.indexOf("{") + 1, s.length - 1)
    for (x <- rulesPattern.findAllMatchIn(rulesSrt)) {
      val part = x.group("part")
      val cond = x.group("cond")
      val operand = x.group("operand")
      val trueRule = parseAction(x.group("true"))
      val falseRule = Option(x.group("false")).map(parseAction).getOrElse(NoOp)

      cond match {
        case ">" => rules.append(Gt(part, operand.toInt, trueRule, falseRule))
        case "<" => rules.append(Lt(part, operand.toInt, trueRule, falseRule))
      }
      //      println(
      //        s"""
      //           |matched: ${x.matched}
      //           |part: $part
      //           |cond: $cond
      //           |operand: $operand
      //           |trueRule: $trueRule
      //           |falseRule: $falseRule
      //           |""".stripMargin
      //      )
    }
    Flow(s.substring(0, s.indexOf("{")), rules.toList)
  }


  def run(flow: Flow, flows: Map[String, Flow], ratings: Map[String, Int]): Action = {
    for (rule <- flow.rules) {
      rule.test(ratings(rule.name)) match {
        case Switch(name) => return run(flows(name), flows, ratings)
        case res@(Accept | Reject) => return res
        case NoOp => ()
      }
    }
    throw new RuntimeException("?")
  }

  def main(args: Array[String]): Unit = {
    val flows = mutable.Map.empty[String, Flow]
    Using.resource(Source.fromFile(args(0))) { source =>
      val lines = source.getLines().toList
      val inputs = lines.splitAt(lines.indexOf(""))
      for (elem <- inputs._1) {
        val flow = parse(elem)
        flows += flow.name -> flow
      }

      val ratings = inputs._2.filter(_.nonEmpty).map { e =>
        val ratingsStr = e.substring(1, e.length - 1)
        ratingsStr.split(",").map { pair =>
          val arr = pair.split("=")
          arr(0) -> arr(1).toInt
        }.toMap
      }
      var answer = 0
      for (rating <- ratings) {
        val res = run(flows("in"), flows.toMap, rating)
        res match {
          case Accept => answer = answer + rating.values.sum
          case _ => ()
        }
      }
      println(s"answer=$answer")
    }

  }
}
