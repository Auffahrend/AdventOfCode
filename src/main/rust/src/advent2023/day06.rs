use regex::Regex;

use crate::utils::{test_and_run, TestVals};

pub(crate) fn solve() {
    test_and_run(&solution, &TEST_1, &TEST_2, FILE)
}

fn solution(input: &String) -> i64 {
    // part 1
    // let races = RaceRecord::parse_input(input, 0);
    // part 2
    let races = RaceRecord::parse_input(input, 1);

    races.iter().map(|r| r.count_wins())
        .fold(1i64, |a, count| a * count)
}

struct RaceRecord {
    time: i64,
    max_distance: i64,
}

impl RaceRecord {

    fn count_wins(&self) -> i64 {
        let mut wins = 0i64;
        for speed in 1..self.time {
            let distance = speed * (self.time - speed);
            if distance > self.max_distance { wins += 1 }
        }
        wins
    }

    fn parse_input(input: &str, mode: u8) -> Vec<RaceRecord> {
        let lines: Vec<&str> = input.lines().collect();
        let number_re = Regex::new(r"(\d+)").unwrap();
        let mut result: Vec<RaceRecord> = Vec::new();
        let l1 = if mode == 0 {
            lines[0].to_string()
        } else {
            lines[0].replace(" ", "")
        };
        let l2 = if mode == 0 {
            lines[1].to_string()
        } else {
            lines[1].replace(" ", "")
        };

        let mut times = number_re.captures_iter(l1.as_str());
        let mut distances = number_re.captures_iter(l2.as_str());

        times.map(|c| c[1].parse::<i64>().unwrap())
            .zip(distances.map(|c| c[1].parse::<i64>().unwrap()))
            .for_each(|(t, d)| result.push(RaceRecord { time: t, max_distance: d }));

        result
    }
}

const TEST_1: TestVals<&str, i64> = TestVals(&"Time:      7  15   30
Distance:  9  40  200
", 288i64);
const TEST_2: TestVals<&str, i64> = TestVals(&"Time:      7  15   30
Distance:  9  40  200
", 71503i64);
const FILE: &str = "../resources/advent2023/day06.txt";