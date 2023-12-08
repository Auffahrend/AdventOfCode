use std::collections::HashMap;
use std::string::ToString;
use num_integer::lcm;
use lazy_static::lazy_static;
use regex::Regex;

use crate::utils::{test_and_run, TestVals};

pub(crate) fn solve() {
    test_and_run(&solution, &TEST_1, &TEST_2, FILE)
}

fn solution(input: &String) -> i64 {
    let navig = Navigation::parse(input);

    // Part 1
    // navig.find_end()

    // part 2
    let steps_per_start: Vec<i64> = navig.nodes.keys()
        .filter(|&n| n.chars().nth(2) == Some('A'))
        .map(|start| navig.steps_till_end(start))
        .collect();

    println!("Found following steps for each start: {:?}", steps_per_start);

    steps_per_start.iter().fold(1i64, |acc, &x| lcm(acc, x))
}


struct Navigation {
    instructions: Vec<char>,
    nodes: HashMap<String, (String, String)>,
}

const START: &str = "AAA";
const END: &str = "ZZZ";
lazy_static! {
    static ref NODE_RE : Regex = Regex::new(r"([A-Z0-9]+) = \(([A-Z0-9]+), ([A-Z0-9]+)\)").unwrap();
}

impl Navigation {
    fn parse(input: &String) -> Navigation {
        let lines: Vec<&str> = input.lines().collect();
        let mut nodes: HashMap<String, (String, String)> = HashMap::new();
        lines.iter().skip(2)
            .for_each(|&line| {
                let (_, [n1, n2, n3]) = NODE_RE.captures(line).unwrap().extract();
                nodes.insert(n1.to_string(), (n2.to_string(), n3.to_string()));
            });
        Navigation {instructions : lines[0].chars().collect(), nodes}
    }

    fn find_end(&self) -> i64 {
        let mut steps = 0i64;
        let mut current: &str = START;

        while current != END {
            let turn = self.instructions[steps as usize % self.instructions.len()];
            steps += 1;
            current = if turn == 'L' {
                &(self.nodes.get(current).unwrap().0)
            } else {
                &(self.nodes.get(current).unwrap().1)
            };
            println!("Moved to {}", current)
        }

        return steps
    }

    fn steps_till_end(&self, start: &str) -> i64 {
        let mut steps = 0i64;
        let mut current: &str = start;

        while current.chars().nth(2) != Some('Z') {
            let turn = self.instructions[steps as usize % self.instructions.len()];
            steps += 1;
            current = if turn == 'L' {
                &(self.nodes.get(current).unwrap().0)
            } else {
                &(self.nodes.get(current).unwrap().1)
            };
            println!("Moved to {}", current)
        }

        return steps
    }
}

const TEST_1: TestVals<&str, i64> = TestVals(&"RL

AAA = (BBB, CCC)
BBB = (DDD, EEE)
CCC = (ZZZ, GGG)
DDD = (DDD, DDD)
EEE = (EEE, EEE)
GGG = (GGG, GGG)
ZZZ = (ZZZ, ZZZ)
", 2i64);
const TEST_2: TestVals<&str, i64> = TestVals(&"LR

11A = (11B, XXX)
11B = (XXX, 11Z)
11Z = (11B, XXX)
22A = (22B, XXX)
22B = (22C, 22C)
22C = (22Z, 22Z)
22Z = (22B, 22B)
XXX = (XXX, XXX)
", 6i64);
const FILE: &str = "../resources/advent2023/day08.txt";