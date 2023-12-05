use std::clone::Clone;
use std::collections::{HashMap, HashSet};

use regex::Regex;

use crate::multi_dimensional::{Coord2, neighbours8};
use crate::utils::{test_and_run, TestVals};

pub(crate) fn solve() {
    test_and_run(&solution, &TEST_1, &TEST_2, FILE)
}

fn solution(input: &String) -> i64 {
    let digits = Regex::new(r"(\d+)").unwrap();

    let mut gear_numbers: HashMap<Coord2, Vec<i64>> = HashMap::new();
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
                    // let is_part_number = adjacent_coords.iter()
                    //     .map(|&c| &grid[c.y as usize][c.x as usize])
                    //     .any(|&ch| !ch.is_digit(10) && ch != '.');
                    // if is_part_number {
                    //     println!("Found {} as part number", part_number);
                    //     part_numbers.push(part_number);
                    // } else {
                    //     println!("Skipped {} as part number", part_number);
                    // }

                    // part 2
                    adjacent_coords.iter()
                        .for_each(|&c| {
                            let ch = &grid[c.y as usize][c.x as usize];
                            if *ch == '*' {
                                println!("Found {} as potential gear number (gear at {:?})", part_number, c);
                                if !gear_numbers.contains_key(&c) {
                                    gear_numbers.insert(c, Vec::new());
                                };
                                gear_numbers.get_mut(&c).unwrap().push(part_number);
                            } else {
                                // println!("Skipped {} as gear number", part_number);
                            }
                        });
                })
        });

    gear_numbers.iter()
        .filter(|&(_, parts)| parts.len() == 2)
        .map(|(&c, &ref parts)| {
            println!("Gear at {:?} is next to part numbers {:?}", c, parts);
            parts[0] * parts[1]
        })
        .sum()
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

const TEST_2: TestVals<&str, i64> = TestVals(TEST_1.0, 467835i64);

const FILE: &str = "../resources/advent2023/day03.txt";
