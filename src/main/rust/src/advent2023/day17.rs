use std::cmp::{min, Ordering};
use std::collections::BinaryHeap;

use dashmap::DashMap;

use crate::{coord2, coord2_valid_on, parse_as_digits, value_at_coord2};
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
    last_10_dirs: Vec<Coord2>,
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
    let start = PathEnd { position: coord2!(0, 0), last_10_dirs: vec![] };
    let mut queue: BinaryHeap<PathEndEstimate> = BinaryHeap::new();
    queue.push(PathEndEstimate { end: start.clone(), estimate_loss: (finish.x + finish.y) as i64 });

    let mut current_best_loss = i64::MAX;
    let mut memo: DashMap<PathEnd, i64> = DashMap::new();
    memo.insert(start.clone(), 0);

    while let Some(path) = queue.pop() {
        let total_loss = *memo.get(&path.end).unwrap();

        if total_loss < current_best_loss {
            if path.end.position == finish &&
                // part 2 - need at least 4 tiles to stop
                path.end.last_10_dirs[..4].iter()
                    .reduce(|f, s| if *f == *s { f } else { &coord2!(0, 0) })
                    .filter(|&non_zero| *non_zero != coord2!(0, 0)).is_some()
            {
                current_best_loss = total_loss
            } else {
                vec![
                    coord2!(0, -1),
                    coord2!(-1, 0),
                    coord2!(0, 1),
                    coord2!(1, 0),
                ].into_iter()
                    .filter(|&dir| coord2_valid_on!(path.end.position + dir, &weights))
                    .filter(|&dir| can_go_this_direction(&path.end, dir))
                    .filter(|&dir|
                        // can't reverse
                        path.end.last_10_dirs.get(0)
                            .map(|&prev_dir| dir != -prev_dir)
                            .unwrap_or(true)
                    )
                    .for_each(|dir| {
                        let new_position = path.end.position + dir;
                        let new_total_loss = total_loss + value_at_coord2!(new_position, &weights);
                        let mut new_last_N_dirs = vec![dir.clone()];
                        // part 2: 3 -> 10
                        new_last_N_dirs.extend_from_slice(&path.end.last_10_dirs[..min(9, path.end.last_10_dirs.len())]);
                        let new_path_end = PathEnd { position: new_position, last_10_dirs: new_last_N_dirs };
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

fn can_go_this_direction(path: &PathEnd, dir: Coord2) -> bool {
    // part 1
    // can't go 3+ tiles in the same direction
    // .filter(|&dir|
    //             path.end.last_10_dirs.len() < 3 || path.end.last_10_dirs.iter().any(|&d| d != dir)

    // part 2
    // can't go 10+ tiles in the same direction, but also can't turn before 4 tiles
    (path.last_10_dirs.len() < 10 || path.last_10_dirs.iter().any(|&d| d != dir))
        && path.last_10_dirs.get(0)
        .map(|&prev_dir| {
            if path.last_10_dirs.len() < 4 {
                // traveled less than 4 tiles - can only continue straight
                dir == prev_dir
            } else {
                // can turn if moved 4 tiles in the same direction
                path.last_10_dirs[..4].iter().all(|&pd| pd == prev_dir) ||
                    // or continue straight
                    dir == prev_dir
            }
        }).unwrap_or(true)
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
", 94i64);
const TEST_2: TestVals<&str, i64> = TestVals(&"\
111111111111
999999999991
999999999991
999999999991
999999999991
", 71i64);
const FILE: &str = "../resources/advent2023/day17.txt";