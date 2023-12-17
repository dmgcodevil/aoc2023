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

var enterFromLeft = []rune{'S', '-', 'F', 'L'}
var enterFromRight = []rune{'S', '-', 'J', '7'}
var enterFromUp = []rune{'S', '|', 'L', 'J'}
var enterFromDown = []rune{'S', '|', '7', 'F'}

//var leftSided = []rune{'S', '|', 'L', 'F'}
//var rightSided = []rune{'S', '|', 'J', '7'}

var verticalAlign = make(map[string]bool)
var horizontalAlign = make(map[string]bool)

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

	for _, left := range []rune{'S', '|', 'J', '7'} {
		for _, right := range []rune{'S', '|', 'L', 'F'} {
			verticalAlign[string(left)+string(right)] = true
		}
	}
	for _, left := range []rune{'S', '-', 'J', 'L'} {
		for _, right := range []rune{'S', '-', '7', 'F'} {
			horizontalAlign[string(right)+string(left)] = true
		}
	}

}

type Coordinates = [2]int // coordinate + junction direction/ horizontal
type LoopPath = []Coordinates
type Cache = [][]LoopPath

func getConnType(a Coordinates, b Coordinates) rune {
	if a[0] == b[0] && a[1] != b[1] {
		return '-'
	} else if a[0] != b[0] && a[1] == b[1] {
		return '|'
	} else {
		fmt.Printf("%v = %v\n", a, b)
		return '?'
		//panic("not connected")
	}
	//return '*'
}

// go
func findLongestLoop2(i int, j int, grid [][]rune,
	path LoopPath, visited [][]bool,
	targetX int, targetY int, cache Cache) LoopPath {
	m := len(grid)
	n := len(grid[0])
	//key := strconv.Itoa(i) + "," + strconv.Itoa(j)

	//if value, exists := cache[key]; exists {
	//	return value
	//}

	//if len(cache[i][j]) != 0 {
	//	return cache[i][j]
	//}

	if visited[i][j] && i == targetX && j == targetY {
		//fmt.Printf("reached 'S'=%d\n, path=%s", loopLen, path)
		copiedSlice := make(LoopPath, len(path))
		copy(copiedSlice, path)
		return copiedSlice
	}
	if visited[i][j] {
		return make(LoopPath, 0)
	}
	visited[i][j] = true
	res := make(LoopPath, 0)
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
				//nextKey := strconv.Itoa(x) + "," + strconv.Itoa(y)
				tmp := findLongestLoop2(x, y, grid, append(path, Coordinates{x, y}), visited,
					targetX, targetY, cache)
				if len(tmp) > len(res) {
					res = tmp
				}

			}

		}
	}

	visited[i][j] = false
	cache[i][j] = res
	return res
}

func main() {
	//part1Ans := part1()
	//fmt.Printf("part1=%d", part1Ans)
	//println(part1())
	println(part2())
}

func loadInput() [][]rune {
	file, err := os.Open("input")
	if err != nil {
		panic(err)
	}
	defer func(file *os.File) {
		err := file.Close()
		if err != nil {
			panic(err)
		}
	}(file)
	scanner := bufio.NewScanner(file)
	var lines []string
	for scanner.Scan() {
		lines = append(lines, scanner.Text())
	}
	var grid [][]rune
	for _, line := range lines {
		grid = append(grid, []rune(line))
	}
	return grid
}

func findLongestLoop(grid [][]rune) LoopPath {
	m := len(grid)
	n := len(grid[0])
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
	cache := make([][]LoopPath, m)
	for i := range visited {
		cache[i] = make([]LoopPath, n)
	}
	return findLongestLoop2(startX, startY, grid, LoopPath{Coordinates{startX, startY}}, visited, startX, startY, cache)
}

func part1() int {
	grid := loadInput()
	return len(findLongestLoop(grid)) / 2
}

func flood(i int, j int, grid [][]rune, visited [][]bool) {
	m := len(grid)
	n := len(grid[0])
	//if i < 0 || i >= m || j < 0 || j >= n || grid[i][j] != '.' || grid[i][j] != '#' {
	//	return
	//}

	grid[i][j] = 'O'
	if visited[i][j] {
		return
	}
	visited[i][j] = true

	for _, d := range Directions {
		x := moves[d][0] + i
		y := moves[d][1] + j
		if x >= 0 && x < m && y >= 0 && y < n && (grid[x][y] == '.' || grid[x][y] == '#') {
			flood(x, y, grid, visited)
		}
	}
	visited[i][j] = false

}

func part2() int {
	grid := loadInput()
	m := len(grid)
	n := len(grid[0])
	path := findLongestLoop(grid)
	strPath := ""
	for _, c := range path {
		strPath = strPath + string(grid[c[0]][c[1]])
	}
	println(strPath)

	tmpGrid := make([][]rune, m)
	for i := range tmpGrid {
		tmpGrid[i] = make([]rune, n)
	}
	for i := 0; i < m; i++ {
		for j := 0; j < n; j++ {
			tmpGrid[i][j] = '.'
		}
	}

	for _, c := range path {
		tmpGrid[c[0]][c[1]] = grid[c[0]][c[1]]
	}
	grid = tmpGrid
	for _, row := range grid {
		fmt.Printf("%v\n", string(row))
	}

	println("expand v")
	grid = expandVertical(grid)
	grid = expandHorizontal(grid)
	m = len(grid)
	n = len(grid[0])
	visited := make([][]bool, len(grid))
	for i := range visited {
		visited[i] = make([]bool, len(grid[0]))
	}
	// top
	for j := 0; j < n; j++ {
		flood(0, j, grid, visited)
	}
	// down
	for j := 0; j < n; j++ {
		flood(m-1, j, grid, visited)
	}
	// left
	for i := 0; i < m; i++ {
		flood(i, 0, grid, visited)
	}
	// right
	for i := 0; i < m; i++ {
		flood(i, n-1, grid, visited)
	}

	for _, row := range grid {
		fmt.Printf("%v\n", string(row))
	}

	iCount := 0
	for i := 0; i < m; i++ {
		for j := 0; j < n; j++ {
			if grid[i][j] == '.' {
				iCount = iCount + 1
			}
		}
	}

	println(iCount)

	return 0
}

func expandVertical(grid [][]rune) [][]rune {
	m := len(grid)
	n := len(grid[0])

	for i := 0; i < m; i++ {
		for j := 0; j < n-1; j++ {
			v := string(grid[i][j]) + string(grid[i][j+1])
			ok, _ := verticalAlign[v]
			if ok {
				grid = insertColumn(grid, j+1)
				for k := 0; k < m; k++ {
					jun := string(grid[k][j]) + string(grid[k][j+2])
					//println(jun)
					ok, _ = verticalAlign[jun]
					if ok {
						grid[k][j+1] = '#'
					} else if ok, _ = junctions[] {
						grid[k][j+1] = '#'
					} else {
						grid[k][j+1] = '-'
					}

				}
				return expandVertical(grid)
			}

		}
	}
	return grid
}

func expandHorizontal(grid [][]rune) [][]rune {
	m := len(grid)
	n := len(grid[0])

	for i := 0; i < m-1; i++ {
		for j := 0; j < n; j++ {
			h := string(grid[i][j]) + string(grid[i+1][j])
			ok, _ := horizontalAlign[h]
			if ok {
				grid = insertRow(grid, i+1)
				for k := 0; k < n; k++ {
					jun := string(grid[i][k]) + string(grid[i+2][k])
					ok, _ = horizontalAlign[jun]
					if ok {
						grid[i+1][k] = '#'
					} else if jun == ".." {
						grid[i+1][k] = '#'
					} else {
						grid[i+1][k] = '|'
					}
				}
				return expandHorizontal(grid)
			}
		}

	}
	return grid
}

func insertRow(grid [][]rune, rowIndex int) [][]rune {
	m := len(grid)
	n := len(grid[0])

	// Create a new slice for the new row
	newRow := make([]rune, n)
	for i := range newRow {
		newRow[i] = '?'
	}

	// Create a new grid with an extra row
	newGrid := make([][]rune, m+1)

	// Copy the rows up to the insertion point
	copy(newGrid, grid[:rowIndex])

	// Insert the new row
	newGrid[rowIndex] = newRow

	// Copy the remaining rows
	copy(newGrid[rowIndex+1:], grid[rowIndex:])

	return newGrid
}

func insertColumn(grid [][]rune, columnIndex int) [][]rune {
	newGrid := make([][]rune, len(grid))

	for i, row := range grid {
		// Create a new row with an extra space for the new column
		newRow := make([]rune, len(row)+1)

		// Copy elements up to the insertion point
		copy(newRow, row[:columnIndex])
		// Insert the '.' rune
		newRow[columnIndex] = '?'

		// Copy the remaining elements
		copy(newRow[columnIndex+1:], row[columnIndex:])

		newGrid[i] = newRow
	}

	return newGrid
}

/*
func part2() int {
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
	loopPath := ""
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
	cache := make(map[string]string)

	for _, c := range []rune{'|', '-', 'J', 'L', 'F', '7'} {
		grid[startX][startY] = c
		path := findLongestLoop(startX, startY, grid, strconv.Itoa(startX)+","+strconv.Itoa(startY),
			visited, startX, startY, cache)
		//fmt.Printf("%c -> %d\n", c, tmp)
		println(path)
		if len(path) > len(loopPath) {
			loopPath = path
		}
		//ans = max(ans, len(strings.Split(path, ";")))
	}

	println(loopPath)

	for _, cell := range strings.Split(loopPath, ";") {
		tmp := strings.Split(cell, ",")
		i, _ := strconv.ParseInt(tmp[0], 10, 64)
		j, _ := strconv.ParseInt(tmp[1], 10, 64)
		grid[i][j] = '*'
	}

	for _, row := range grid {
		fmt.Printf("%v\n", string(row))
	}

	//enclosedCount := 0
	//for i := 0; i < m; i++ {
	//	for j := 0; j < n; j++ {
	//		if grid[i][j] == '*' {
	//			// find another right '*'
	//			k := j + 1
	//			for ; k < n; k++ {
	//				if grid[i][k] == '*' {
	//					break
	//				}
	//			}
	//			if k < n {
	//				enclosedCount = enclosedCount + (k - j - 1)
	//				j = k // +1 in for
	//			}
	//		}
	//	}
	//}
	//
	//println(enclosedCount)

	return -1
}
*/
