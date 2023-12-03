use regex::Regex;

use crate::advent2023::utils::{test_and_run, TestVals};

pub(crate) fn solve() {
    test_and_run(&solution, &TEST_1, &TEST_2, FILE)
}

fn solution(input: &String) -> i64 {

}

const TEST_1: TestVals<&str, i64> = TestVals(&"", 0i64);
const TEST_2: TestVals<&str, i64> = TestVals(&"", 0i64);
const FILE: &str = "../resources/advent2023/dayXX.txt";