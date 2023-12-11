package main

import (
	"bufio"
	"fmt"
	"os"
)

type Direction int

const (
	Left Direction = iota
	Right
	Up
	Down
)

var Directions = []Direction{Left, Right, Up, Down}

var moves = map[Direction][2]int{
	Left:  {0, -1},
	Right: {0, 1},
	Up:    {-1, 0},
	Down:  {1, 0},
}

var enterFromLeft = []rune{'-', 'F', 'L'}
var enterFromRight = []rune{'-', 'J', '7'}
var enterFromUp = []rune{'|', 'L', 'J'}
var enterFromDown = []rune{'|', '7', 'F'}

// read as curr -> next
var junctions = map[Direction]map[string]bool{
	Left:  {},
	Right: {},
	Up:    {},
	Down:  {},
}

func init() {
	// LEFT / RIGHT
	for _, l := range enterFromLeft {
		for _, r := range enterFromRight {
			junctions[Left][string(r)+string(l)] = true
			junctions[Right][string(l)+string(r)] = true
		}
	}

	// UP / DOWN
	for _, d := range enterFromDown {
		for _, u := range enterFromUp {
			junctions[Down][string(d)+string(u)] = true
			junctions[Up][string(u)+string(d)] = true
		}
	}

	//fmt.Printf("junctions[Left]=%v\n", junctions[Left])
	//fmt.Printf("junctions[Right]=%v\n", junctions[Right])
	//fmt.Printf("junctions[Up]=%v\n", junctions[Up])
	//fmt.Printf("junctions[Down]=%v\n", junctions[Down])

}

func findLongestLoopLen(i int, j int, grid [][]rune, loopLen int, visited [][]bool,
	targetX int, targetY int, cache [][]int) int {
	m := len(grid)
	n := len(grid[0])
	if cache[i][j] != 0 {
		return cache[i][j]
	}
	if visited[i][j] && i == targetX && j == targetY {
		//fmt.Printf("reached 'S'=%d\n, path=%s", loopLen, path)
		return loopLen
	}
	if visited[i][j] {
		return 0
	}
	visited[i][j] = true
	res := 0
	for _, d := range Directions {
		x := moves[d][0] + i
		y := moves[d][1] + j
		if x >= 0 && x < m && y >= 0 && y < n && grid[x][y] != '.' {
			connected := false
			junction := string(grid[i][j]) + string(grid[x][y])
			switch d {
			case Left:
				_, connected = junctions[d][junction]
				break
			case Right:
				_, connected = junctions[d][junction]
				break
			case Up:
				_, connected = junctions[d][junction]
				break
			case Down:
				_, connected = junctions[d][junction]
				break
			}
			if connected {
				tmp := findLongestLoopLen(x, y, grid, loopLen+1, visited,
					targetX, targetY, cache)
				res = max(res, tmp)
			}

		}
	}

	visited[i][j] = false
	cache[i][j] = res
	return res
}

func main() {
	part1Ans := part1()
	fmt.Printf("part1=%d", part1Ans)
}

func part1() int {
	file, err := os.Open("input")
	if err != nil {
		panic(err)
	}
	defer file.Close()
	scanner := bufio.NewScanner(file)
	var lines []string
	for scanner.Scan() {
		lines = append(lines, scanner.Text())
	}
	var grid [][]rune
	for _, line := range lines {
		grid = append(grid, []rune(line))
	}

	m := len(grid)
	n := len(grid[0])
	ans := 0
	startX := 0
	startY := 0
	for i := 0; i < m; i++ {
		for j := 0; j < n; j++ {
			if grid[i][j] == 'S' {
				startX = i
				startY = j
				goto start
			}
		}
	}
start:
	visited := make([][]bool, m)
	for i := range visited {
		visited[i] = make([]bool, n)
	}
	cache := make([][]int, m)
	for i := range visited {
		cache[i] = make([]int, n)
	}

	for _, c := range []rune{'|', '-', 'J', 'L', 'F', '7'} {
		grid[startX][startY] = c
		length := findLongestLoopLen(startX, startY, grid, 0, visited, startX, startY, cache)
		//fmt.Printf("%c -> %d\n", c, tmp)
		ans = max(ans, length)
	}

	return ans / 2
}
