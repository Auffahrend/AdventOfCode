use crate::{coord2, value_at_coord2};
use crate::multi_dimensional::{Coord2};
use crate::utils::{test_and_run, TestVals};

pub(crate) fn solve() {
    test_and_run(&solution, &TEST_1, &TEST_2, FILE)
}

fn solution(input: &String) -> i64 {
    PipeMap::parse(input)
        .area()
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
                    start = coord2!(p.0, *y);
                    true
                } else { false }
            });
        // println!("Found starting point at {:?}", start);
        pipes[start.y as usize][start.x as usize] = PipeMap::pipe_at_start(&pipes, &start);
        // println!("Starting pipe identified as {:?}", value_at_coord2!(&start, &pipes));
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

    fn area(&self) -> i64 {
        let mut first_vertex = coord2!(-1, -1);
        let mut vertices = vec![];
        let mut perimeter = 0i64;
        let mut current = self.start.clone();
        let mut direction = match value_at_coord2!(&current, &self.pipes) {
            '7' => coord2!(0, 1),
            'F' => coord2!(0, 1),
            '|' => coord2!(0, 1),
            'L' => coord2!(1, 0),
            '-' => coord2!(1, 0),
            'J' => coord2!(-1, 0),
            _ => unreachable!()
        };
        let corners = vec!['J', '7', 'F', 'L'];
        while current != first_vertex {
            if first_vertex.x != -1 {
                perimeter += 1;
            }
            let pipe = value_at_coord2!(&current, &self.pipes);
            if corners.contains(&pipe) {
                if vertices.is_empty() { first_vertex = current }
                vertices.push(current);
                direction = neighbor_offsets(&pipe).iter().find(|&d| *d != -direction).unwrap().clone();
            } else {
               // just continue straight
            }

            current = current + direction;
        }
        vertices.push(first_vertex); perimeter += 1;
        println!("Total vertices+1 {}, total perimeter {}", vertices.len(), perimeter);

        vertices.windows(2)
            .fold(0isize, |a, pair| a + pair[0].x * pair[1].y - pair[1].x * pair[0].y) as i64
            - perimeter / 2 - vertices.len() as i64 + 1
    }

    fn dist_at(&self, coord2: Coord2) -> i64 {
        self.distances[coord2.y as usize][coord2.x as usize].clone()
    }

    fn pipe_at(&self, coord2: Coord2) -> char {
        value_at_coord2!(&coord2, &self.pipes)
    }

    fn pipe_at_start(grid: &Vec<Vec<char>>, start: &Coord2) -> char {
        let connect_top = if start.y > 0 {
            grid.get(start.y as usize - 1)
                .map(|r| vec!['|', '7', 'F'].contains(&r[start.x as usize]))
                .unwrap_or(false)
        } else { false };
        let connect_bottom = grid.get(start.y as usize + 1)
            .map(|r| vec!['|', 'J', 'L'].contains(&r[start.x as usize]))
            .unwrap_or(false);

        let connect_left = if start.x > 0 {
            grid[start.y as usize].get(start.x as usize - 1)
                .map(|c| vec!['-', 'L', 'F'].contains(c))
                .unwrap_or(false)
        } else { false };
        let connect_right = grid[start.y as usize].get(start.x as usize + 1)
            .map(|c| vec!['-', '7', 'J'].contains(c))
            .unwrap_or(false);

        match (connect_left, connect_top, connect_right, connect_bottom) {
            (true, true, false, false) => 'J',
            (true, false, true, false) => '-',
            (true, false, false, true) => '7',
            (false, true, true, false) => 'L',
            (false, true, false, true) => '|',
            (false, false, true, true) => 'F',
            _ => panic!("Unable to connect the starter pipe! {}, {}, {}, {}", connect_left, connect_top, connect_right, connect_bottom),
        }
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