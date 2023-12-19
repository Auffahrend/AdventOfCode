use std::collections::HashSet;
use regex::Regex;
use crate::multi_dimensional::Coord2;
use crate::{coord2, coord2_valid_on, neighbours4, parse_as_chars, set_value_at_coord2, value_at_coord2};

use crate::utils::{test_and_run, TestVals};

pub(crate) fn solve() {
    test_and_run(&solution, &TEST_1, &TEST_2, FILE)
}

fn solution(input: &String) -> i64 {
    let instructions = DigLine::parse(input);
    let vertices = dig(&instructions);
    area_between(vertices, &instructions)
}

fn area_between(vertices: Vec<Coord2>, lines: &Vec<DigLine>) -> i64 {
    //shoelace formula
    let doubled = vertices.windows(2)
        .fold(0i64, |a, s| a + (s[0].x * s[1].y - s[0].y * s[1].x) as i64);
    let edges: i64 = lines.iter().map(|l| l.len as i64).sum::<i64>() / 2;
    doubled / 2 + edges + 1 // 1 to account for the outermost corners
}

struct DigLine {
    dir: Coord2,
    len: usize,
    color: u32,
}

const UNKNOWN: u32 = 0;
const FLAT: u32 = 0x1_00_00_00;
const LAVA: u32 = 0x2_00_00_00;

fn dig(lines: &Vec<DigLine>) -> Vec<Coord2> {
    let mut current = coord2!(0, 0);
    let mut vertices: Vec<Coord2> = vec![current];
    for line in lines {
        current = current + coord2!(line.dir.x * line.len as isize, line.dir.y * line.len as isize);
        println!("Current vertex is {}", current);
        vertices.push(current.clone());
    }
    if let Some(last) = vertices.last() {
        if *last != coord2!(0, 0) {
            panic!("The last vertex {:?} doesn't match the first one!", last)
        }
    }
    vertices
}

struct Lagoon {
    tile_colors: Vec<Vec<u32>>,
}

impl Lagoon {
    // fn dig_internals(&mut self) {
    //     while let Some(start) = self.find_any(UNKNOWN) {
    //         let area = self.find_connected(start, UNKNOWN);
    //         let filler = if self.touches_edge(&area) { FLAT } else { LAVA };
    //         self.fill_with(area, filler);
    //     }
    // }
    //
    // fn count_volume(&self) -> i64 {
    //     self.tile_colors.iter()
    //         .flat_map(|row| row.iter())
    //         .filter(|&c| *c != FLAT)
    //         .count() as i64
    // }
    //
    // fn find_any(&self, color: u32) -> Option<Coord2> {
    //     for y in 0..self.tile_colors.len() {
    //         for x in 0..self.tile_colors[y].len() {
    //             let coord = coord2!(x, y);
    //             if value_at_coord2!(coord, self.tile_colors) == color {
    //                 return Some(coord);
    //             }
    //         }
    //     }
    //     None
    // }
    // fn find_connected(&self, start: Coord2, color: u32) -> Vec<Coord2> {
    //     let mut result: HashSet<Coord2> = HashSet::new();
    //     let mut queue: HashSet<Coord2> = vec![start].into_iter().collect();
    //     while let Some(&point) = queue.iter().find(|_| true) {
    //         queue.remove(&point);
    //         result.insert(point);
    //         neighbours4!(point).iter()
    //             .for_each(|p| {
    //                 if coord2_valid_on!(p, self.tile_colors) &&
    //                     value_at_coord2!(p, self.tile_colors) == color &&
    //                     !result.contains(p) && !queue.contains(p) {
    //                     queue.insert(p.clone());
    //                 }
    //             });
    //     }
    //     result.into_iter().collect()
    // }
    // fn touches_edge(&self, area: &Vec<Coord2>) -> bool {
    //     area.iter()
    //         .any(|p| p.x == 0 || p.y == 0 ||
    //             p.x as usize == self.tile_colors[0].len() - 1 ||
    //             p.y as usize == self.tile_colors.len() - 1)
    // }
    // fn fill_with(&mut self, area: Vec<Coord2>, color: u32) {
    //     area.iter()
    //         .for_each(|p| set_value_at_coord2!(p, self.tile_colors, color))
    // }
}


impl DigLine {
    fn parse(input: &str) -> Vec<DigLine> {
        let re = Regex::new(r"([LRUD]) (\d)+ \(#([0-9a-f]+)\)").unwrap();
        input.lines()
            .map(|l| {
                let (_, [d, len, c]) = re.captures(l).unwrap().extract();
                DigLine {
                    dir: match d {
                        "U" => coord2!(0, -1),
                        "D" => coord2!(0, 1),
                        "L" => coord2!(-1, 0),
                        "R" => coord2!(1, 0),
                        _ => unreachable!(),
                    },
                    len: len.parse().unwrap(),
                    color: u32::from_str_radix(c, 16).unwrap(),
                }
            })
            .collect()
    }
}

const TEST_1: TestVals<&str, i64> = TestVals(&"\
R 6 (#70c710)
D 5 (#0dc571)
L 2 (#5713f0)
D 2 (#d2c081)
R 2 (#59c680)
D 2 (#411b91)
L 5 (#8ceee2)
U 2 (#caa173)
L 1 (#1b58a2)
U 2 (#caa171)
R 2 (#7807d2)
U 3 (#a77fa3)
L 2 (#015232)
U 2 (#7a21e3)
", 62i64);
const TEST_2: TestVals<&str, i64> = TestVals(TEST_1.0, 0i64);
const FILE: &str = "../resources/advent2023/day18.txt";