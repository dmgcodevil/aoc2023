package main

import (
	"bufio"
	"fmt"
	"os"
)

type Coordinates = [2]int
type Direction int

const (
	Left Direction = iota
	Right
	Up
	Down
)

var Directions = []Direction{Left, Right, Up, Down}

var moves = map[Direction]Coordinates{
	Left:  {0, -1},
	Right: {0, 1},
	Up:    {-1, 0},
	Down:  {1, 0},
}

func main() {
	grid := loadInput()
	m := len(grid)
	n := len(grid[0])
	rowsToAdd := make([]int, 0, m)
	colsToAdd := make([]int, 0, n)

	for j := 0; j < n; j++ {
		empty := true
		for i := 0; i < m; i++ {
			if grid[i][j] != '.' {
				empty = false
				break
			}
		}
		if empty {
			colsToAdd = append(colsToAdd, j)
		}
	}

	for i := 0; i < m; i++ {
		empty := true
		for j := 0; j < n; j++ {
			if grid[i][j] != '.' {
				empty = false
				break
			}
		}
		if empty {
			rowsToAdd = append(rowsToAdd, i)
		}
	}
	colsAdded := 0
	rowsAdded := 0

	for _, j := range colsToAdd {
		grid = insertColumn(grid, j+colsAdded)
		colsAdded = colsAdded + 1
	}

	for _, i := range rowsToAdd {
		grid = insertRow(grid, i+rowsAdded)
		rowsAdded = rowsAdded + 1

	}

	m = len(grid)
	n = len(grid[0])
	galaxies := make([]Coordinates, 0)

	for i := 0; i < m; i++ {
		for j := 0; j < n; j++ {
			if grid[i][j] == '#' {
				galaxies = append(galaxies, Coordinates{i, j})
			}
		}
	}

	//fmt.Printf("galaxies: %v\n", galaxies)

	res := 0
	for i := 0; i < len(galaxies)-1; i++ {
		res = res + minDistances(grid, galaxies[i], galaxies[i+1:])
	}

	fmt.Printf("part1=%d", res)

}

func minDistances(grid [][]rune, start Coordinates, targets []Coordinates) int {
	m, n := len(grid), len(grid[0])
	visited := make([][]bool, m)
	for i := range visited {
		visited[i] = make([]bool, n)
	}

	targetReached := make(map[Coordinates]bool)
	for _, t := range targets {
		targetReached[t] = false
	}

	queue := []Coordinates{start}
	visited[start[0]][start[1]] = true
	distance := 0
	sumOfDistances := 0
	targetsReached := 0

	for len(queue) > 0 {
		size := len(queue)
		for s := 0; s < size; s++ {
			curr := queue[0]
			queue = queue[1:]
			if _, ok := targetReached[curr]; ok && !targetReached[curr] {
				sumOfDistances += distance
				targetReached[curr] = true
				targetsReached++
				if targetsReached == len(targets) {
					return sumOfDistances
				}
			}

			for _, d := range Directions {
				x, y := curr[0]+moves[d][0], curr[1]+moves[d][1]
				if x >= 0 && x < m && y >= 0 && y < n && !visited[x][y] {
					visited[x][y] = true
					queue = append(queue, Coordinates{x, y})
				}
			}
		}
		distance++
	}
	return sumOfDistances
}

func printGrid(grid [][]rune) {
	for _, row := range grid {
		println(string(row))
	}
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

func insertRow(grid [][]rune, rowIndex int) [][]rune {
	m := len(grid)
	n := len(grid[0])

	newRow := make([]rune, n)
	for i := range newRow {
		newRow[i] = '.'
	}

	newGrid := make([][]rune, m+1)

	copy(newGrid, grid[:rowIndex])
	newGrid[rowIndex] = newRow
	copy(newGrid[rowIndex+1:], grid[rowIndex:])

	return newGrid
}

func insertColumn(grid [][]rune, columnIndex int) [][]rune {
	newGrid := make([][]rune, len(grid))

	for i, row := range grid {
		newRow := make([]rune, len(row)+1)
		copy(newRow, row[:columnIndex])
		newRow[columnIndex] = '.'
		copy(newRow[columnIndex+1:], row[columnIndex:])

		newGrid[i] = newRow
	}

	return newGrid
}
