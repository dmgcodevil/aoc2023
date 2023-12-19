use std::cmp::max;
use std::collections::{HashMap, HashSet};
use std::fs::File;
use std::io::{self, BufRead};
use std::path::Path;

use lazy_static::lazy_static;

use crate::Direction::*;

#[derive(Eq)]
#[derive(PartialEq)]
#[derive(Hash)]
#[derive(Copy)]
#[derive(Clone)]
#[derive(Debug)]
enum Direction {
    Up,
    Down,
    Left,
    Right,
}

#[derive(Copy)]
#[derive(Clone)]
struct Move {
    d: Direction,
    x: i32,
    y: i32,
}

lazy_static! {
static ref MOVES: HashMap<Direction, Move> = HashMap::from([
    (Up, Move { d:Up, x: -1, y: 0 }),
    (Down, Move {d:Down, x: 1, y: 0 }),
    (Left, Move {d:Left, x: 0, y: -1 }),
    (Right, Move {d:Right, x: 0, y: 1 }),
]);

static ref REFLECTIONS_MAP: HashMap<Direction, HashMap<char, Vec<Move>>> = HashMap::from(
    [
        (Up,
         HashMap::from([
             ('.', vec![MOVES[&Up]]),
             ('|', vec![MOVES[&Up]]),
             ('\\', vec![MOVES[&Left]]),
             ('/', vec![MOVES[&Right]]),
             ('-', vec![MOVES[&Left], MOVES[&Right]]),
         ])),
        (Down,
         HashMap::from([
             ('.', vec![MOVES[&Down]]),
             ('|', vec![MOVES[&Down]]),
             ('\\', vec![MOVES[&Right]]),
             ('/', vec![MOVES[&Left]]),
             ('-', vec![MOVES[&Left], MOVES[&Right]]),
         ])),
        (Left,
         HashMap::from([
             ('.', vec![MOVES[&Left]]),
             ('|', vec![MOVES[&Up], MOVES[&Down]]),
             ('\\', vec![MOVES[&Up]]),
             ('/', vec![MOVES[&Down]]),
             ('-', vec![MOVES[&Left]]),
         ])),
        (Right,
         HashMap::from([
             ('.', vec![MOVES[&Right]]),
             ('|', vec![MOVES[&Up], MOVES[&Down]]),
             ('\\', vec![MOVES[&Down]]),
             ('/', vec![MOVES[&Up]]),
             ('-', vec![MOVES[&Right]]),
         ])),
    ]);
}

fn load() -> io::Result<Vec<String>> {
    let path = Path::new("input");
    let file = File::open(&path)?;
    let reader = io::BufReader::new(file);
    let mut lines: Vec<String> = Vec::new();
    for line in reader.lines() {
        lines.push(line?);
    }
    Ok(lines)
}

fn visit(grid: &Vec<String>, curr_dir: Direction,
         i: i32, j: i32, seen: &mut HashSet<String>,
         visited: &mut Vec<Vec<bool>>) {
    let m = grid.len() as i32;
    let n = grid[0].len() as i32;

    if i < 0 || i >= m || j < 0 || j >= n {
        return;
    }

    let key = format!("{:?}-{}-{}", curr_dir, i, j);
    if seen.contains(&key) {
        return;
    }
    seen.insert(key);
    visited[i as usize][j as usize] = true;

    let next_cell = grid[i as usize].chars().nth(j as usize).unwrap();
    for next_move in &REFLECTIONS_MAP[&curr_dir][&next_cell] {
        let next_x = i + next_move.x;
        let next_y = j + next_move.y;
        visit(grid, next_move.d, next_x, next_y, seen, visited);
    }
}

fn count_energized(grid: &Vec<String>, i: i32, direction: Direction, j: i32) -> i32 {
    let m = grid.len();
    let n = grid[0].len();
    let mut seen = HashSet::new();
    let mut visited = vec![vec![false; n]; m];
    visit(&grid, direction, i, j, &mut seen, &mut visited);
    let mut count = 0;
    for row in &visited {
        for &val in row {
            if val {
                count = count + 1;
            }
        }
    }
    return count;
}

fn part1(grid: &Vec<String>) {
    let answer = count_energized(grid, 0, Right, 0);
    println!("part1 = {}", answer);
}

fn part2(grid: &Vec<String>) {
    let m = grid.len();
    let n = grid[0].len();
    let mut answer: i32 = 0;
    // top
    for j in 0..n {
        answer = max(answer, count_energized(grid, 0, Down, j as i32));
    }
    // down
    for j in 0..n {
        answer = max(answer, count_energized(grid, m as i32 - 1, Up, j as i32));
    }
    // left
    for i in 0..m {
        answer = max(answer, count_energized(grid, i as i32, Right, 0));
    }
    // Right
    for i in 0..m {
        answer = max(answer, count_energized(grid, i as i32, Left, n as i32 - 2));
    }

    println!("part2 = {}", answer);
}

fn main() -> io::Result<()> {
    let grid: Vec<String> = load()?;
    for line in grid.iter() {
        println!("{}", line);
    }
    println!("============");
    part1(&grid);
    part2(&grid);
    Ok(())
}
