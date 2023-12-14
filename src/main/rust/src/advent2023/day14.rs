use std::collections::HashMap;
use regex::{Regex, Replacer};
use log::debug;
use crate::parse_as_chars;

use crate::utils::{test_and_run, TestVals};

pub(crate) fn solve() {
    test_and_run(&solution, &TEST_1, &TEST_2, FILE)
}

fn solution(input: &String) -> i64 {
    let mut grid = parse_as_chars!(input);
    // part 1
    // tilt_vertical(&mut grid, 0);
    // part 2
    let last_grid = cycle_tilts(&mut grid);
    total_load(&last_grid) as i64
}

const FIXED_STONE: char = '#';
const ROLLING_STONE: char = 'O';

fn total_load(grid: &Vec<Vec<char>>) -> usize {
    let total_rows = grid.len();
    grid.iter().enumerate()
        .map(|(y, row)| row.iter()
            .map(|&c| if c == ROLLING_STONE { total_rows - y } else { 0 } )
            .sum::<usize>()
        )
        .sum()
}

fn cycle_tilts(grid: &mut Vec<Vec<char>>) -> Vec<Vec<char>> {
    // each element is the snapshot of the state of the grid at that step
    let mut history: Vec<Vec<Vec<char>>> = Vec::new();
    let mut history_index: HashMap<Vec<Vec<char>>, usize> = HashMap::new();
    let mut cycle = 0usize;

    while cycle < 1000 {
        if let Some(i) = history_index.get(grid) {
            let loop_len = cycle - i;
            let offset_in_loop = (CYCLES - i ) % loop_len;
            println!("Loop found after step {} with start at step {} and duration {}. Offset in loop is {}", cycle, i, loop_len, offset_in_loop);
            return history.get(i + offset_in_loop).unwrap().clone()
        } else {
            let snapshot = grid.clone();
            history_index.insert(snapshot.clone(), cycle.clone());
            history.push(snapshot.clone());
            tilt_vertical(grid, 0);
            tilt_horizontal(grid, 0);
            tilt_vertical(grid, 1);
            tilt_horizontal(grid, 1);
            cycle += 1;
        }
    }
    println!("The loop was not found!");
    panic!()
}

fn tilt_vertical(grid: &mut Vec<Vec<char>>, direction: u8){
    for x in 0..grid[0].len() {
        let column = grid.iter().map(|row| row[x]).collect::<Vec<char>>();
        let sorted_rocks = tilt_vector(direction, column);
        // putting back into result
        let mut sorted_rock_i = 0usize;
        for y in 0..grid.len() {
            if grid[y][x] != FIXED_STONE {
                grid[y][x] = sorted_rocks[sorted_rock_i];
                sorted_rock_i += 1;
            }
        }
    }
}

fn tilt_horizontal(grid: &mut Vec<Vec<char>>, direction: u8) {
    for y in 0..grid.len() {
        let row = grid[y].clone();
        let sorted_rocks = tilt_vector(direction, row);
        // putting back into result
        let mut sorted_rock_i = 0usize;
        for x in 0..grid[0].len() {
            if grid[y][x] != FIXED_STONE {
                grid[y][x] = sorted_rocks[sorted_rock_i];
                sorted_rock_i += 1;
            }
        }
    }
}

fn tilt_vector(direction: u8, vector: Vec<char>) -> Vec<char> {
    let mut sorted_column = vector.split(|&c| c == FIXED_STONE)
        // and all 'O' roll down
        .map(|slice| {
            let mut copy = slice.to_vec();
            copy.sort();
            if direction == 0 { copy.reverse(); }
            copy
        })
        .flat_map(|chars| chars.into_iter())
        .collect::<String>();
    sorted_column.chars().collect::<Vec<char>>()
}

const TEST_1: TestVals<&str, i64> = TestVals(&"\
O....#....
O.OO#....#
.....##...
OO.#O....O
.O.....O#.
O.#..O.#.#
..O..#O..O
.......O..
#....###..
#OO..#....
", 136i64);

const CYCLES: usize = 1000000000;
const TEST_2: TestVals<&str, i64> = TestVals(TEST_1.0, 64i64);
const FILE: &str = "../resources/advent2023/day14.txt";