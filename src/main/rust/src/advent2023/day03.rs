use std::collections::HashSet;

use regex::{Match, Regex};

use crate::multi_dimensional::{Coord2, neighbours8};
use crate::utils::{test_and_run, TestVals};

pub(crate) fn solve() {
    test_and_run(&solution, &TEST_1, &TEST_2, FILE)
}

fn solution(input: &String) -> i64 {
    let digits = Regex::new(r"(\d+)").unwrap();

    let mut part_numbers: Vec<i64> = Vec::new();
    let lines: Vec<&str> = input.lines().collect();
    let grid: Vec<Vec<char>> = lines.iter().map(|&r| r.chars().collect()).collect();
    lines.iter().enumerate()
        .for_each(|(r, &line)| {
            digits.captures_iter(line)
                .for_each(|captures| {
                    let group = captures.get(1).unwrap();
                    let part_number = group.as_str().parse::<i64>().unwrap();
                    let group_coords: HashSet<Coord2> = group.range().map(|c| Coord2 { x: c as isize, y: r as isize }).collect();
                    let all_coords: HashSet<Coord2> = group_coords.iter()
                        .flat_map(|c| neighbours8(c, &grid))
                        .map(|c| c)
                        .collect();
                    let adjacent_coords: HashSet<_> = all_coords.difference(&group_coords).cloned().collect();
                    // println!("for group {:?} adjacent indices are {:?}", &group, adjacent_coords);
                    let is_part_number = adjacent_coords.iter()
                        .map(|&c| &grid[c.y as usize][c.x as usize])
                        .any(|&ch| !ch.is_digit(10) && ch != '.');
                    if is_part_number {
                        println!("Found {} as part number", part_number);
                        part_numbers.push(part_number);
                    } else {
                        println!("Skipped {} as part number", part_number);
                    }
                })
        });
    part_numbers.iter().sum()
}

const TEST_1: TestVals<&str, i64> = TestVals(&"\
467..114..
...*......
..35..633.
......#...
617*......
.....+.58.
..592.....
......755.
...$.*....
.664.598..
", 4361i64);

const TEST_2: TestVals<&str, i64> = TestVals(&"", 0i64);

const FILE: &str = "../resources/advent2023/day03.txt";
