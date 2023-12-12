use std::io::Read;
use rayon::iter::IntoParallelRefIterator;
use regex::Regex;

use crate::utils::{test_and_run, TestVals};

pub(crate) fn solve() {
    test_and_run(&solution, &TEST_1, &TEST_2, FILE)
}

fn solution(input: &String) -> i64 {
    let records: Vec<Record> = input.lines()
        .map(Record::parse)
        // .inspect(|r| println!("Parsed {:?}", r))
        .collect();

    records.par_iter()
        .map(Record::possible_solutions)
        .inspect(|r| println!("Solutions {:?}", r))
        .sum()
}

const OK: u8 = 1;
const BROKEN: u8 = 2;
const UNKNOWN: u8 = 0;

#[derive(Debug)]
struct Record {
    map: Vec<u8>,
    broken_groups: Vec<u8>,
    should_be_broken_total: u8,
    first_unknown: Option<usize>,

}

impl Record {
    fn new(map: Vec<u8>, broken_groups: Vec<u8>) -> Self {
        let should_be_broken_total: u8 = broken_groups.iter().sum();
        let mut first_unknown: Option<usize> = None;
        for i in 0..map.len() {
            if map[i] == UNKNOWN {
                first_unknown = Some(i);
                break;
            }
        };

        Record { map, broken_groups, should_be_broken_total, first_unknown }
    }
    fn is_possible(&self) -> bool {
        // total broken optimizing
        let total_broken = self.map.iter().filter(|&e| e == &BROKEN).count() as u8;
        if total_broken > self.should_be_broken_total { return false; }


        if let Some(i) = self.first_unknown {
            // non-matching start of broken groups optimizing
            let sub_map: Vec<u8> = self.map.iter().take(i).map(|e| e.clone()).collect();
            let starting_groups = Record::count_broken_groups(&sub_map);
            if !starting_groups.is_empty() {
                self.groups_can_start_with(&starting_groups)
            } else { true }
        } else {
            let counted_broken_groups: Vec<u8> = Record::count_broken_groups(&self.map);
            counted_broken_groups == self.broken_groups
        }
    }

    fn count_broken_groups(map: &Vec<u8>) -> Vec<u8> {
        map.split(|&e| e == OK)
            .map(|group| group.len() as u8)
            .filter(|&it| it != 0)
            .collect()
    }

    fn possible_solutions(&self) -> i64 {
        if !self.is_possible() { return 0; } else {
            if let Some(i) = self.first_unknown {
                let (first, second) = self.mutations_at(i);
                first.possible_solutions() + second.possible_solutions()
            } else { 1 }
        }
    }

    fn mutations_at(&self, index: usize) -> (Record, Record) {
        let mut first = self.map.clone();
        first[index] = BROKEN;
        let mut second = self.map.clone();
        second[index] = OK;
        (
            Record::new(first, self.broken_groups.clone()),
            Record::new(second, self.broken_groups.clone())
        )
    }

    fn parse(line: &str) -> Record {
        let mut parts = line.split(' ');
        let mut map: Vec<u8> = parts.nth(0).unwrap()
            .chars().map(|c| match c {
            '.' => OK,
            '#' => BROKEN,
            '?' => UNKNOWN,
            _ => {
                println!("Unknown char {}", c);
                panic!()
            }
        })
            .collect();

        let mut broken_groups: Vec<u8> = parts.nth(0).unwrap().split(',')
            .map(|s| s.parse::<u8>().unwrap())
            .collect();

        // part 2
        let copies = 5;
        map.push(UNKNOWN);
        map = map.repeat(copies);
        map.remove(map.len() - 1);
        broken_groups = broken_groups.repeat(copies);

        Record::new(map, broken_groups)
    }
    fn groups_can_start_with(&self, groups: &Vec<u8>) -> bool {
        for i in 0..(groups.len() - 1) {
            if self.broken_groups[i] != groups[i] { return false }
        }
        // last group can be less or equal, because it might grow in the future (while resolving next unknowns)
        self.broken_groups[groups.len() - 1] >= groups[groups.len() - 1]
    }
}

const TEST_1: TestVals<&str, i64> = TestVals(&"\
???.### 1,1,3
.??..??...?##. 1,1,3
?#?#?#?#?#?#?#? 1,3,1,6
????.#...#... 4,1,1
????.######..#####. 1,6,5
?###???????? 3,2,1
", 525152i64);
const TEST_2: TestVals<&str, i64> = TestVals(&"", 0i64);
const FILE: &str = "../resources/advent2023/day12.txt";