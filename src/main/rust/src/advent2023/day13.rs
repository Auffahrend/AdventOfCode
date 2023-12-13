use std::cmp::min;

use crate::utils::{test_and_run, TestVals};

pub(crate) fn solve() {
    test_and_run(&solution, &TEST_1, &TEST_2, FILE)
}

fn solution(input: &String) -> i64 {
    let patterns: Vec<Pattern> = input.split("\n\n")
        .map(|part| Pattern::parse(part))
        .collect();

    patterns.iter()
        .map(|p| {
            let mut res = 0;
            if let Some(x) = p.find_reflection_x() { res += x + 1 };
            if let Some(y) = p.find_reflection_y() { res += 100 * (y + 1) };
            if res == 0 {
                println!("Could not find reflection in {:?}", p);
                panic!()
            }
            println!("Found reflection {}", res);
            res
        })
        .sum()
}

#[derive(Debug)]
struct Pattern {
    grid: Vec<Vec<char>>,
}

impl Pattern {
    fn find_reflection_x(&self) -> Option<i64> {
        // skip first and last column
        for x in 0..(self.grid[0].len() - 1) {
            let n_columns_to_check = min(x + 1, self.grid[0].len() - 1 - x);
            let differences_in_reflections: i32 = (0..n_columns_to_check).into_iter()
                .map(|dx| (x - dx, x + 1 + dx))
                .map(|(x1, x2)| self.differences_in_columns(x1, x2))
                .sum();
            if differences_in_reflections == 1 { return Some(x as i64) }
        }
        return None;
    }

    fn find_reflection_y(&self) -> Option<i64> {
        self.transpose().find_reflection_x()
    }

    fn differences_in_columns(&self, x1: usize, x2: usize) -> i32 {
        let mut diffs = 0;
        for y in 0..self.grid.len() {
            if self.grid[y][x1] != self.grid[y][x2] { diffs += 1 }
        }
        return diffs
    }

    fn transpose(&self) -> Pattern {
        let mut transposed: Vec<Vec<char>> = Vec::new();
        for y in 0..self.grid.len() {
            for x in 0..self.grid[y].len() {
                if y == 0 { transposed.push(Vec::new()) }
                transposed[x].push(self.grid[y][x].clone());
            }
        }
        Pattern { grid: transposed }
    }
    fn parse(lines: &str) -> Pattern {
        let grid: Vec<Vec<char>> = lines.lines()
            .map(|l| l.chars().map(|c| c.clone()).collect::<Vec<char>>())
            .collect();
        Pattern { grid }
    }
}

const TEST_1: TestVals<&str, i64> = TestVals(&"\
#.##..##.
..#.##.#.
##......#
##......#
..#.##.#.
..##..##.
#.#.##.#.

#...##..#
#....#..#
..##..###
#####.##.
#####.##.
..##..###
#....#..#
", 405i64);
const TEST_2: TestVals<&str, i64> = TestVals(TEST_1.0, 400i64);
const FILE: &str = "../resources/advent2023/day13.txt";