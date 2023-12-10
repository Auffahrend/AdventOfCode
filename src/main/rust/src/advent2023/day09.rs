use regex::Regex;

use crate::utils::{test_and_run, TestVals};

pub(crate) fn solve() {
    test_and_run(&solution, &TEST_1, &TEST_2, FILE)
}

fn solution(input: &String) -> i64 {
    let lines = input.lines()
        .map(|line| OasisLine::parse(line))
        .collect::<Vec<OasisLine>>();
    // part 1
    let next_values: i64 = lines.iter()
        .map(OasisLine::next_value)
        .sum();

    // part 2
    let prev_values = lines.iter()
        .map(OasisLine::prev_value)
        .sum();
    prev_values
}

struct OasisLine {
    nums: Vec<i64>,
}

impl OasisLine {
    fn next_value(&self) -> i64 {
        if self.nums.iter().all(|&i| i == 0) {
            0
        } else {
            let diffs = self.nums.windows(2)
                .map(|w| w[1] - w[0])
                .collect::<Vec<i64>>();
            let next_diff = OasisLine{ nums: diffs.clone() }.next_value();
            self.nums.last().unwrap() + next_diff
        }
    }

    fn prev_value(&self) -> i64 {
        if self.nums.iter().all(|&i| i == 0) {
            0
        } else {
            let diffs = self.nums.windows(2)
                .map(|w| w[1] - w[0])
                .collect::<Vec<i64>>();
            let prev_diff = OasisLine{ nums: diffs.clone() }.prev_value();
            self.nums.first().unwrap() - prev_diff
        }
    }

    fn parse(line: &str) -> OasisLine {
        let num_re = Regex::new(r"(-?\d+)").unwrap();
        let nums = num_re.captures_iter(line)
            .map(|c| c[1].parse::<i64>().unwrap())
            .collect::<Vec<i64>>();
        OasisLine { nums }
    }
}

const TEST_1: TestVals<&str, i64> = TestVals(&"0 3 6 9 12 15
1 3 6 10 15 21
10 13 16 21 30 45
", 114i64);
const TEST_2: TestVals<&str, i64> = TestVals(TEST_1.0, 2i64);
const FILE: &str = "../resources/advent2023/day09.txt";