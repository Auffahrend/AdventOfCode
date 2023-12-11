use crate::multi_dimensional::{Coord2};
use crate::utils::{test_and_run, TestVals};

pub(crate) fn solve() {
    test_and_run(&solution, &TEST_1, &TEST_2, FILE)
}

fn solution(input: &String) -> i64 {
    PipeMap::parse(input)
        .find_furthers_dist_in_pipe()
}

struct PipeMap {
    start: Coord2,
    pipes: Vec<Vec<char>>,
    distances: Vec<Vec<i64>>,
}

const UNREACHABLE: i64 = 1_000_000i64;

impl PipeMap {
    fn parse(input: &String) -> PipeMap {
        let mut pipes: Vec<Vec<char>> = input.lines()
            .map(|l| l.chars().collect::<Vec<char>>())
            .collect();

        let distances: Vec<Vec<i64>> = pipes.iter()
            .map(|row| row.iter().map(|_| UNREACHABLE).collect::<Vec<i64>>())
            .collect();
        let mut start = Coord2 { x: 0, y: 0 };
        pipes.iter().enumerate()
            .find(|(y, &ref row)| {
                if let Some(p) = row.iter().enumerate().find(|(x, &c)| c == 'S') {
                    start.x = p.0 as isize;
                    start.y = *y as isize;
                    true
                } else { false }
            });
        println!("Found starting point at {:?}", start);
        pipes[start.y as usize][start.x as usize] = if (pipes.len() == 5) { 'F' } else { '7' };
        PipeMap { start, pipes, distances }
    }

    fn find_furthers_dist_in_pipe(&mut self) -> i64 {
        let mut max_dist = 0i64;
        self.distances[self.start.y as usize][self.start.x as usize] = 0;
        let mut queue: Vec<Coord2> = Vec::new();
        queue.push(self.start);
        while !queue.is_empty() {
            let p = queue.remove(0);
            let distance = self.dist_at(p);
            if distance > max_dist { max_dist = distance }
            let neighbors: Vec<Coord2> = neighbor_offsets(&(self.pipe_at(p)))
                .iter().map(|&d| Coord2 { y: p.y + d.y, x: p.x + d.x })
                .filter(|&neighbor| self.dist_at(neighbor) > distance + 1)
                .collect();
            neighbors.iter()
                .for_each(|&neighbor| {
                    self.distances[neighbor.y as usize][neighbor.x as usize] = distance + 1;
                    queue.push(neighbor);
                })
        }
        max_dist
    }

    fn dist_at(&self, coord2: Coord2) -> i64 {
        self.distances[coord2.y as usize][coord2.x as usize].clone()
    }

    fn pipe_at(&self, coord2: Coord2) -> char {
        self.pipes[coord2.y as usize][coord2.x as usize].clone()
    }
}

fn neighbor_offsets(pipe: &char) -> Vec<Coord2> {
    match pipe {
        &'|' => [Coord2 { x: 0, y: -1 }, Coord2 { x: 0, y: 1 }].to_vec(),
        &'-' => [Coord2 { x: -1, y: 0 }, Coord2 { x: 1, y: 0 }].to_vec(),
        &'7' => [Coord2 { x: -1, y: 0 }, Coord2 { x: 0, y: 1 }].to_vec(),
        &'F' => [Coord2 { x: 1, y: 0 }, Coord2 { x: 0, y: 1 }].to_vec(),
        &'J' => [Coord2 { x: -1, y: 0 }, Coord2 { x: 0, y: -1 }].to_vec(),
        &'L' => [Coord2 { x: 1, y: 0 }, Coord2 { x: 0, y: -1 }].to_vec(),
        _ => {
            println!("Tried to find way out of {}!", pipe);
            panic!()
        }
    }
}

const TEST_1: TestVals<&str, i64> = TestVals(&"\
-L|F7
7S-7|
L|7||
-L-J|
L|-JF
", 4i64);
const TEST_2: TestVals<&str, i64> = TestVals(&"\
7-F7-
.FJ|7
SJLL7
|F--J
LJ.LJ
", 8i64);
const FILE: &str = "../resources/advent2023/day10.txt";