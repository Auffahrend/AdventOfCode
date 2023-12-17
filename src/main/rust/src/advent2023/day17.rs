use std::cmp::{min, Ordering};
use std::collections::{BinaryHeap, BTreeMap, HashMap, LinkedList};
use dashmap::DashMap;
use rayon::prelude::IntoParallelIterator;
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

#[derive(Debug, Clone, PartialEq, Eq, Hash)]
struct PathEndEstimate {
    end: PathEnd,
    estimate_loss: i64,
}

// We want to prioritize smaller loss, so we implement comparison in reverse.
impl Ord for PathEndEstimate {
    fn cmp(&self, other: &Self) -> Ordering {
        other.estimate_loss.cmp(&self.estimate_loss)
    }
}

impl PartialOrd for PathEndEstimate {
    fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
        Some(self.cmp(other))
    }
}

fn find_best_path(weights: Vec<Vec<i64>>) -> i64 {
    let finish = coord2!(weights[0].len() - 1, weights.len() - 1);
    let start = PathEnd { position: coord2!(0, 0), last_3_dirs: vec![] };
    let mut queue: BinaryHeap<PathEndEstimate> = BinaryHeap::new();
    queue.push(PathEndEstimate { end: start.clone(), estimate_loss: (finish.x + finish.y) as i64 });

    let mut current_best_loss = i64::MAX;
    let mut memo: DashMap<PathEnd, i64> = DashMap::new();
    memo.insert(start.clone(), 0);

    while let Some(path) = queue.pop() {
        let total_loss = *memo.get(&path.end).unwrap();

        if total_loss < current_best_loss {
            if path.end.position == finish {
                current_best_loss = total_loss
            } else {
                vec![
                    coord2!(0, -1),
                    coord2!(-1, 0),
                    coord2!(0, 1),
                    coord2!(1, 0),
                ].into_iter()
                    .filter(|&dir| coord2_valid_on!(path.end.position + dir, &weights))
                    .filter(|&dir|
                        // can't go 3+ tiles in the same direction
                        path.end.last_3_dirs.len() < 3 || path.end.last_3_dirs.iter().any(|&d| d != dir)
                    )
                    .filter(|&dir|
                        // can't reverse
                        path.end.last_3_dirs.get(0).map(|&prev_dir| dir != -prev_dir).unwrap_or(true)
                    )
                    .for_each(|dir| {
                        let new_position = path.end.position + dir;
                        let new_total_loss = total_loss + value_at_coord2!(new_position, &weights);
                        let mut new_last_3_dirs = vec![dir.clone()];
                        new_last_3_dirs.extend_from_slice(&path.end.last_3_dirs[..min(2, path.end.last_3_dirs.len())]);
                        let new_path_end = PathEnd { position: new_position, last_3_dirs: new_last_3_dirs };

                        let mut previous_best_for_the_path_end = memo.entry(new_path_end.clone()).or_insert(i64::MAX);
                        if new_total_loss < *previous_best_for_the_path_end {
                            *previous_best_for_the_path_end = new_total_loss;
                            let reminder = finish - new_position;
                            queue.push(PathEndEstimate { end: new_path_end, estimate_loss: new_total_loss + (reminder.x as i64 + reminder.y as i64) });
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