use std::collections::{HashMap, HashSet};
use regex::Regex;
use crate::multi_dimensional::Coord2;
use crate::parse_as_chars;

use crate::utils::{test_and_run, TestVals};

pub(crate) fn solve() {
    test_and_run(&solution, &TEST_1, &TEST_2, FILE)
}

fn solution(input: &String) -> i64 {
    let optics = parse_as_chars!(input);
    let light_levels: Vec<Vec<HashSet<Coord2>>> = optics.iter()
        .map(|row| vec![HashSet::new(); row.len()])
        .collect();
    // part 1
    // let mut grid = Grid { light_directions: light_levels, optics };
    // grid.spread_light(&Coord2 { x: 0, y: 0 }, &Coord2 { x: 1, y: 0 });
    // part 2
    let grid = Grid { light_directions: light_levels, optics };
    grid.find_most_energetic()
}

#[derive(Debug, Clone)]

struct Grid {
    light_directions: Vec<Vec<HashSet<Coord2>>>,
    optics: Vec<Vec<char>>,
}

impl Grid {
    fn find_most_energetic(&self) -> i64 {
        let mut all_starting_beams: Vec<(Coord2, Coord2)> = vec![];
        (0..self.light_directions[0].len())
            .for_each(|x| {
                all_starting_beams.push((Coord2 { x: x as isize, y: 0 }, Coord2 { x: 0, y: 1 }));
                all_starting_beams.push((Coord2 { x: x as isize, y: self.light_directions.len() as isize - 1 }, Coord2 { x: 0, y: -1 }));
            });
        (0..self.light_directions.len())
            .for_each(|y| {
                all_starting_beams.push((Coord2 { x: 0isize, y: y as isize }, Coord2 { x: 1, y: 0 }));
                all_starting_beams.push((Coord2 { x: self.light_directions[0].len() as isize - 1, y: y as isize }, Coord2 { x: -1, y: 0 }));
            });

        all_starting_beams.iter()
            .map(|(start, dir)| {
                let mut copy: &mut Grid = &mut self.clone();
                copy.spread_light(start, dir);
                copy.total_light()
            })
            .max().unwrap()
    }

    fn total_light(&self) -> i64 {
        self.light_directions.iter()
            .flat_map(|row| row.iter())
            .filter(|&l| l.len() > 0)
            .count() as i64
    }

    fn spread_light(&mut self, start: &Coord2, direction: &Coord2) {
        let mut beams = vec![(start.clone(), direction.clone())];
        while !beams.is_empty() {
            let (point, dir) = beams.remove(0);
            if !self.light_directions[point.y as usize][point.x as usize].contains(&dir) {
                self.light_directions[point.y as usize][point.x as usize].insert(dir.clone());

                let new_beams: Vec<Coord2> = match self.optics[point.y as usize][point.x as usize] {
                    '/' => vec![Coord2 { x: -dir.y, y: -dir.x }],
                    '\\' => vec![Coord2 { x: dir.y, y: dir.x }],
                    '|' => if dir.x == 0 { vec![dir] } else { vec![Coord2 { x: 0, y: 1 }, Coord2 { x: 0, y: -1 }] },
                    '-' => if dir.y == 0 { vec![dir] } else { vec![Coord2 { x: 1, y: 0 }, Coord2 { x: -1, y: 0 }] },
                    _ => vec![dir.clone()],
                };
                new_beams.iter()
                    .for_each(|&dir| {
                        let next_p = point + dir;
                        if next_p.y >= 0 && (next_p.y as usize) < self.light_directions.len()
                            && next_p.x >= 0 && (next_p.x as usize) < self.light_directions[0].len() {
                            beams.push((next_p, dir));
                        }
                    })
            }
        }
    }
}

const TEST_1: TestVals<&str, i64> = TestVals(&"\
.|...\\....
|.-.\\.....
.....|-...
........|.
..........
.........\\
..../.\\\\..
.-.-/..|..
.|....-|.\\
..//.|....
", 46i64);
const TEST_2: TestVals<&str, i64> = TestVals(TEST_1.0, 51i64);
const FILE: &str = "../resources/advent2023/day16.txt";