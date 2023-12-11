use regex::Regex;
use crate::multi_dimensional::Coord2;

use crate::utils::{test_and_run, TestVals};

pub(crate) fn solve() {
    test_and_run(&solution, &TEST_1, &TEST_2, FILE)
}

fn solution(input: &String) -> i64 {
    let pre_expand = find_galaxies(input);
    // println!("Parsed galaxies {:?}", pre_expand);

    let post_expand = expand(&pre_expand);
    // println!("Expanded galaxies {:?}", post_expand);

    post_expand.iter()
        .flat_map(|g1| post_expand.iter().filter(|&g2| g1.id < g2.id)
            .map(move |g2| (g1, g2))
        )
        .map(|(g1, g2)| g1.distance_to(g2))
        .sum()
}

#[derive(Debug)]
struct Galaxy {
    id: i64,
    coord: Coord2,
}

impl Galaxy {
    fn distance_to(&self, other: &Galaxy) -> i64 {
        (
            (other.coord.y - self.coord.y).abs() +
                (other.coord.x - self.coord.x).abs()
        ) as i64
    }
}

fn find_galaxies(input: &String) -> Vec<Galaxy> {
    let mut id = 1i64;
    let mut coords: Vec<Galaxy> = Vec::new();
    input.lines().enumerate()
        .for_each(|(y, line)| line.chars().enumerate()
            .for_each(|(x, c)| {
                if c == '#' {
                    coords.push(Galaxy { id: id.clone(), coord: Coord2 { x: x as isize, y: y as isize } });
                    id += 1;
                }
            })
        );
    coords
}

fn expand(galaxies: &Vec<Galaxy>) -> Vec<Galaxy> {

    let max_y: isize = galaxies.iter().max_by(|&f, &s| f.coord.y.cmp(&s.coord.y)).unwrap().coord.y;
    let max_x: isize = galaxies.iter().max_by(|&f, &s| f.coord.x.cmp(&s.coord.x)).unwrap().coord.x;

    let mut empty_ys: Vec<isize> = Vec::new();
    for y in 0..max_y {
        let is_empty = !galaxies.iter().any(|g| g.coord.y == y);
        if is_empty { empty_ys.push(y.clone()) }
    }
    let mut empty_xs: Vec<isize> = Vec::new();
    for x in 0..max_x {
        let is_empty = !galaxies.iter().any(|g| g.coord.x == x);
        if is_empty { empty_xs.push(x.clone()) }
    }

    let expansion_factor = 1000_000-1;
    let expanded: Vec<Galaxy> = galaxies.iter()
        .map(|g| {
            let shift_x = empty_xs.iter().filter(|&x| g.coord.x > *x).count() as isize;
            let shift_y = empty_ys.iter().filter(|&y| g.coord.y > *y).count() as isize;
            Galaxy { id: g.id.clone(), coord: Coord2 { x: g.coord.x + shift_x * expansion_factor, y: g.coord.y + shift_y * expansion_factor } }
        })
        .collect();

    expanded
}

const TEST_1: TestVals<&str, i64> = TestVals(&"\
...#......
.......#..
#.........
..........
......#...
.#........
.........#
..........
.......#..
#...#.....
", 374i64);
const TEST_2: TestVals<&str, i64> = TestVals(TEST_1.0, 8410i64);
const FILE: &str = "../resources/advent2023/day11.txt";