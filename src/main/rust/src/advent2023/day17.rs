use std::cmp::min;
use std::collections::{HashMap, LinkedList};
use regex::Regex;
use crate::{coord2, coord2_valid_on, parse_as_chars, parse_as_digits, value_at_coord2};
use crate::multi_dimensional::Coord2;

use crate::utils::{test_and_run, TestVals};

pub(crate) fn solve() {
    test_and_run(&solution, &TEST_1, &TEST_2, FILE)
}

fn solution(input: &String) -> i64 {
    let weights: Vec<Vec<i64>> = parse_as_digits!(input);
    find_best_path(weights)
}

#[derive(Debug, Clone, PartialEq, Eq, Hash)]
struct PathEnd {
    position: Coord2,
    last_3_dirs: Vec<Coord2>,
}

fn find_best_path(weights: Vec<Vec<i64>>) -> i64 {
    let finish = coord2!(weights[0].len() - 1, weights.len() - 1);
    let mut paths: LinkedList<PathEnd> = vec![PathEnd { position: coord2!(0, 0), last_3_dirs: vec![] }]
        .into_iter().collect();
    let mut current_best_loss = i64::MAX;
    let mut memo: HashMap<PathEnd, i64> = HashMap::new();
    memo.insert(PathEnd { position: coord2!(0, 0), last_3_dirs: vec![] }, 0);

    // DFS, pick from queue head
    while let Some(path) = paths.pop_front() {
        let total_loss = *memo.get(&path).unwrap();

        if total_loss < current_best_loss {
            if path.position == finish {
                current_best_loss = total_loss
            } else {
                vec![
                    coord2!(0, -1),
                    coord2!(-1, 0),
                    coord2!(0, 1),
                    coord2!(1, 0),
                ].into_iter()
                    .filter(|&dir| coord2_valid_on!(path.position + dir, &weights))
                    .filter(|&dir|
                        // can't go 3+ tiles in the same direction
                        path.last_3_dirs.len() < 3 || path.last_3_dirs.iter().any(|&d| d != dir)
                    )
                    .filter(|&dir|
                        // can't reverse
                        path.last_3_dirs.get(0).map(|&prev_dir| dir != -prev_dir).unwrap_or(true)
                    )
                    .for_each(|dir| {
                        let new_position = path.position + dir;
                        let new_total_loss = total_loss + value_at_coord2!(new_position, &weights);
                        let mut new_last_3_dirs = vec![dir.clone()];
                        new_last_3_dirs.extend_from_slice(&path.last_3_dirs[..min(2, path.last_3_dirs.len())]);
                        let new_path_end = PathEnd { position: new_position, last_3_dirs: new_last_3_dirs };

                        let previous_best_for_the_path_end = memo.entry(new_path_end.clone()).or_insert(i64::MAX);
                        if new_total_loss < *previous_best_for_the_path_end {
                            *previous_best_for_the_path_end = new_total_loss;
                            paths.push_front(new_path_end);
                        }
                    });
            }
        }
    }
    current_best_loss
}

const TEST_1: TestVals<&str, i64> = TestVals(&"\
2413432311323
3215453535623
3255245654254
3446585845452
4546657867536
1438598798454
4457876987766
3637877979653
4654967986887
4564679986453
1224686865563
2546548887735
4322674655533
", 102i64);
const TEST_2: TestVals<&str, i64> = TestVals(TEST_1.0, 0i64);
const FILE: &str = "../resources/advent2023/day17.txt";