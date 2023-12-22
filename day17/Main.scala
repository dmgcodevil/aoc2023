import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.Using

object Main {

  case class Pos(x: Int, y: Int)

  sealed trait Direction {
    def move(x: Int, y: Int): Pos

    def move(p: Pos): Pos = move(p.x, p.y)
  }

  case object Down extends Direction {
    override def move(x: Int, y: Int): Pos = Pos(x + 1, y)
  }

  case object Up extends Direction {
    override def move(x: Int, y: Int): Pos = Pos(x - 1, y)
  }

  case object Left extends Direction {
    override def move(x: Int, y: Int): Pos = Pos(x, y - 1)
  }

  case object Right extends Direction {
    override def move(x: Int, y: Int): Pos = Pos(x, y + 1)
  }


  case class Crucible(pos: Pos, blocks: Int,
                      heatLoss: Int,
                      direction: Direction) extends Comparable[Crucible] {
    override def compareTo(o: Crucible): Int = {
      if (heatLoss != o.heatLoss) {
        Integer.compare(heatLoss, o.heatLoss)
      } else if (direction == o.direction && blocks != o.blocks) {
        Integer.compare(blocks, o.blocks)
      } else if (pos.y != o.pos.y) {
        Integer.compare(pos.y, o.pos.y)
      } else {
        Integer.compare(pos.x, o.pos.x)
      }
    }
  }

  def nextDirections(direction: Direction): Seq[Direction] = {
    direction match {
      case n@(Down | Up) => Seq(n, Left, Right)
      case n@(Left | Right) => Seq(n, Up, Down)
    }
  }

  def nextDirections2(direction: Direction): Seq[Direction] = {
    direction match {
      case n@(Down | Up) => Seq(Left, Right)
      case n@(Left | Right) => Seq(Up, Down)
    }
  }

  def part1(grid: Vector[Vector[Int]]): Int = {
    val m = grid.length
    val n = grid(0).length
    val queue = new java.util.PriorityQueue[Crucible]()
    val visited = new mutable.HashSet[String]()
    queue.add(Crucible(Pos(0, 1), 1, grid(0)(1), Right))
    queue.add(Crucible(Pos(1, 0), 1, grid(1)(0), Down))
    while (!queue.isEmpty) {
      val node = queue.poll()
      val key = s"${node.pos}-${node.blocks}-${node.direction}"
      if (visited.add(key)) {
        if (node.pos.x == m - 1 && node.pos.y == n - 1) {
          return node.heatLoss
        }
        val mustTurn = node.blocks >= 3
        val directions = nextDirections(node.direction).filter(x => !mustTurn || x != node.direction)

        for (d <- directions) {
          val nextPos = d.move(node.pos)
          if (nextPos.x >= 0 && nextPos.x < m && nextPos.y >= 0 && nextPos.y < n) {
            val heatLoss = node.heatLoss + grid(nextPos.x)(nextPos.y)
            val blocks = if (d == node.direction) node.blocks + 1 else 1
            val next = Crucible(nextPos, blocks, heatLoss, d)
            queue.add(next)
          }
        }
      }
    }

    -1
  }

  def part2(grid: Vector[Vector[Int]]): Int = {
    val m = grid.length
    val n = grid(0).length
    val queue = new java.util.PriorityQueue[Crucible]()
    val visited = new mutable.HashSet[String]()
    queue.add(Crucible(Pos(0, 1), 1, grid(0)(1), Right))
    queue.add(Crucible(Pos(1, 0), 1, grid(1)(0), Down))
    while (!queue.isEmpty) {
      val node = queue.poll()
      val key = s"${node.pos}-${node.blocks}-${node.direction}"
      if (visited.add(key)) {
        if (node.pos.x == m - 1 && node.pos.y == n - 1 && node.blocks >= 4) {
          return node.heatLoss
        }
        val mustTurn = node.blocks >= 10
        val directions = ListBuffer.empty[Direction]
        if (mustTurn || node.blocks >= 4) {
          directions.appendAll(nextDirections2(node.direction))
        }
        if (!mustTurn) {
          directions.addOne(node.direction)
        }

        for (d <- directions) {
          val nextPos = d.move(node.pos)
          if (nextPos.x >= 0 && nextPos.x < m && nextPos.y >= 0 && nextPos.y < n) {
            val heatLoss = node.heatLoss + grid(nextPos.x)(nextPos.y)
            val blocks = if (d == node.direction) node.blocks + 1 else 1

            val next = Crucible(nextPos, blocks, heatLoss, d)
            queue.add(next)
          }
        }
      }
    }

    -1
  }

  def main(args: Array[String]): Unit = {
    val grid: Vector[Vector[Int]] = Using.resource(Source.fromFile("input")) { source =>
      source.getLines().toVector.map(s => s.toCharArray.map(_ - '0').toVector)
    }
    println("part1: " + part1(grid))
    println("part2: " + part2(grid))
  }
}
