package day20

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.Using

object Main {

  val conjuctionsTerm = mutable.HashSet("jf", "bh", "mz", "sh") // an attempt to solve the second part

  private val eventQueue = mutable.Queue.empty[Message]

  private val modules = mutable.HashMap.empty[String, Module]

  sealed trait Pulse

  case object Hi extends Pulse {
    override def toString: String = "high"
  }

  case object Low extends Pulse {
    override def toString: String = "low"
  }

  case class Message(sender: String, pulse: Pulse, target: String)

  var hiCount = 0
  var lowCount = 0
  var buttonCount = 0

  abstract class Module {
    val id: String
    val inputs: ListBuffer[String] = ListBuffer.empty
    val outputs: ListBuffer[String] = ListBuffer.empty
    var on: Boolean = false

    def process(sender: String, pulse: Pulse): Unit

    def send(pulse: Pulse): Unit = {
      for (out <- outputs) {
        eventQueue.append(Message(id, pulse, out))
      }
    }

    def connectInput(input: String): Unit = {
      inputs.append(input)
    }

    def connectOutput(input: String): Unit = {
      outputs.append(input)
    }
  }

  class FlipFlop(val id: String) extends Module {
    override def process(sender: String, pulse: Pulse): Unit = {
      pulse match {
        case Hi => ()
        case Low =>
          on = !on
          val p = if (on) Hi else Low
          send(p)
      }
    }
  }

  class Conjunction(val id: String) extends Module {
    val hiInputs: mutable.HashSet[String] = mutable.HashSet.empty

    override def process(sender: String, pulse: Pulse): Unit = {
      pulse match {
        case Hi =>
          hiInputs.add(sender)
          if (hiInputs.size == inputs.size) {
            send(Low)
          } else {
            send(Hi)
          }
        case Low =>
          hiInputs.remove(sender)
          send(Hi)
      }
    }
  }

  val rxGates = mutable.HashSet.empty[String]

  def register(module: Module): Unit = {
    if (modules.contains(module.id)) throw new RuntimeException(s"module '${module.id}' already registered")
    modules.put(module.id, module)
  }

  def step(): Unit = {
    var size = eventQueue.size
    while (size > 0) {
      val msg = eventQueue.removeHead()
      //println(s"${msg.sender} -${msg.pulse}-> ${msg.target}")
      modules.get(msg.target) match {
        case Some(module) => module.process(msg.sender, msg.pulse)
        case None => () //println(s"unknown module: '${msg.target}'")
      }


      // for the second part
      if ("mf".equals(msg.target) && modules(msg.sender).isInstanceOf[Conjunction]) {
        rxGates.add(msg.sender)
      }

      msg.pulse match {
        case Hi =>
          if (conjuctionsTerm.contains(msg.target)) {
            println(s"${msg.target}: $buttonCount")
          }
          hiCount = hiCount + 1
        case Low =>
          lowCount = lowCount + 1
      }
      size = size - 1
    }
  }


  val startMessages = ListBuffer.empty[Message]


  def init(cables: Seq[String]): Unit = {
    // create and register
    for (cable <- cables) {
      val parts = cable.split(" -> ")
      val moduleString = parts(0)
      if ("broadcaster".equals(moduleString)) {
        for (target <- parts(1).split(",").map(_.trim)) {
          startMessages.append(Message("broadcaster", Low, target))
        }
      } else {
        val module = moduleString.charAt(0) match {
          case '%' =>
            new FlipFlop(moduleString.substring(1))
          case '&' =>
            new Conjunction(moduleString.substring(1))
        }
        register(module)
        for (target <- parts(1).split(",").map(_.trim)) {
          module.connectOutput(target)
        }
      }
    }

    for (module <- modules.values) {
      for (out <- module.outputs) {
        modules.get(out) match {
          case Some(target) => target.connectInput(module.id)
          case None => ()
        }
      }
    }
  }

  def loadAndRun(): Unit = {
    val cables: List[String] = Using.resource(Source.fromFile("./day20/input")) { source =>
      source.getLines().toList
    }

    init(cables)
    for (_ <- 1 to 20000) {
      lowCount = lowCount + 1
      buttonCount = buttonCount + 1
      eventQueue.appendAll(startMessages)
      run()
      //println("=" * 10)
    }
  }

  def run(): Unit = {
    while (eventQueue.nonEmpty) {
      step()
    }
  }

  def main(args: Array[String]): Unit = {
    loadAndRun()
    println(rxGates)
    println(lowCount)
    println(hiCount)
  }

}
