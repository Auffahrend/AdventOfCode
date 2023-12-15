use regex::Regex;
use crate::parse_as_chars;

use crate::utils::{test_and_run, TestVals};

pub(crate) fn solve() {
    test_and_run(&solution, &TEST_1, &TEST_2, FILE)
}

fn solution(input: &String) -> i64 {
    let mut boxes: Vec<Box> = vec![Box { lenses: Vec::new() }; 256];
    let re = Regex::new(r"([a-z]+)(-?)=?(\d?)").unwrap();
    input.split(',')
        .for_each(|step| {
            let (_, [label, rem, add]) = re.captures(step).unwrap().extract();
            let box_i = hash(label) as usize;
            if rem == "-" {
                boxes[box_i].remove(label);
            }
            if add != "" {
                boxes[box_i].add(label, add.parse::<u8>().unwrap());
            }
        });
    boxes.iter().enumerate()
        .flat_map(|(box_i, b)| b.lenses.iter().enumerate()
            .map(move |(lens_i, lens)| (1 + box_i as i64) * (1 + lens_i as i64) * (lens.power as i64)))
        .sum()
}

#[derive(Debug, Clone)]
struct Box {
    lenses: Vec<Lens>,
}

impl Box {
    fn remove(&mut self, label: &str) {
        if let Some(i) = self.lenses.iter().position(|l| l.label == label) {
            self.lenses.remove(i);
        }
    }

    fn add(&mut self, label: &str, power: u8) {
        if let Some(i) = self.lenses.iter().position(|l| l.label == label) {
            self.lenses[i].power = power;
        } else {
            self.lenses.push( Lens { label: label.to_string(), power })
        }
    }

}

#[derive(Debug, Clone)]
struct Lens {
    label: String,
    power: u8,
}

fn hash(line: &str) -> i64 {
    line.chars()
        .map(|c| if c.is_ascii() { c as u8 } else { panic!() })
        .fold(0i64, |acc, code| (acc + code as i64) * 17 % 256)
}

const TEST_1: TestVals<&str, i64> = TestVals(&"rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7", 1320i64);
const TEST_2: TestVals<&str, i64> = TestVals(TEST_1.0, 145i64);
const FILE: &str = "../resources/advent2023/day15.txt";