use std::fmt::Debug;
use rayon::prelude::*;
use cached::proc_macro::cached;

use crate::utils::{test_and_run, TestVals};

pub(crate) fn solve() {
    test_and_run(&solution, &TEST_1, &TEST_2, FILE)
}

fn solution(input: &String) -> i64 {
    let records: Vec<Record> = input.lines()
        .map(Record::parse)
        .collect();

    records.iter().enumerate()
        .map(|(i, &ref r)| {
            let res = possible_solutions(r.clone());
            println!("Line #{} yielded solutions {:?}", i, res);
            res
        })
        .sum()
}


const OK: u8 = 1;
const BROKEN: u8 = 2;
const UNKNOWN: u8 = 0;

#[derive(Debug, Clone, Hash, Eq, PartialEq)]
struct Record {
    map: Vec<u8>,
    broken_groups: Vec<u8>,
}

#[cached]
fn possible_solutions(record: Record) -> i64 {
    if let Some(i) = record.first_unknown_i() {
        let options = record.mutations_at(i);
        options.par_iter()
            .filter(|&r| r.is_possible())
            .map(|r| possible_solutions(r.simplify()))
            .sum()
    } else { 1 }
}


impl Record {
    fn new(map: Vec<u8>, broken_groups: Vec<u8>) -> Self {
        Record { map, broken_groups }
    }

    fn simplify(&self) -> Self {
        if self.map.contains(&UNKNOWN) {
            let mut simplified_map = self.map.clone();
            let mut simplified_groups = self.broken_groups.clone();
            // removing from left
            let first_unknown_i = simplified_map.iter().position(|&c| c == UNKNOWN).unwrap();
            if let Some(last_ok_i) = simplified_map[0..first_unknown_i].iter().rposition(|&c| c == OK) {
                let groups_to_remove = Record::count_broken_groups(&simplified_map[0..last_ok_i]);
                if !simplified_groups.starts_with(groups_to_remove.as_slice()) {
                    println!("While trying to simplify {:?}, first groups to remove were {:?}, but actual groups don't start with it!",
                             self, groups_to_remove);
                    panic!();
                } else {
                    for _ in 0..groups_to_remove.len() { simplified_groups.remove(0); }
                    for _ in 0..last_ok_i+1 { simplified_map.remove(0); }
                }
            }

            // removing from right
            let last_unknown_i = simplified_map.iter().rposition(|&c| c == UNKNOWN).unwrap();
            if let Some(first_ok_i) = simplified_map[last_unknown_i..].iter().position(|&c| c == OK) {
                let groups_to_remove = Record::count_broken_groups(&simplified_map[last_unknown_i+first_ok_i..]);
                if !simplified_groups.ends_with(groups_to_remove.as_slice()) {
                    println!("While trying to simplify {:?}, last groups to remove were {:?}, but actual groups don't end with it!",
                             self, groups_to_remove);
                    panic!();
                } else {
                    for _ in 0..groups_to_remove.len() { simplified_groups.remove(simplified_groups.len()-1); }
                    for _ in (last_unknown_i+first_ok_i)..simplified_map.len() { simplified_map.remove(simplified_map.len() - 1); }
                }
            }
            Record { map: simplified_map, broken_groups: simplified_groups }
        } else {
            //  if no unknowns, trivial simplification
            Record { map: vec![OK], broken_groups: vec![] }
        }
    }


    fn is_possible(&self) -> bool {
        // total broken optimizing
        let total_broken = self.map.iter().filter(|&e| e == &BROKEN).count() as u8;
        if total_broken > self.broken_groups.iter().sum() { return false; }


        if let Some(i) = self.first_unknown_i() {
            // non-matching start of broken groups optimizing
            if let Some(last_ok_i) = self.map[0..i].iter().rposition(|&c| c == OK) {
                let starting_groups = Record::count_broken_groups(&self.map[0..last_ok_i]);
                if !starting_groups.is_empty() {
                    self.broken_groups.starts_with(starting_groups.as_slice())
                } else { true }
            } else { true }
        } else {
            // no unknowns
            let counted_broken_groups: Vec<u8> = Record::count_broken_groups(&self.map);
            counted_broken_groups == self.broken_groups
        }
    }

    fn first_unknown_i(&self) -> Option<usize> {
        self.map.iter().position(|&c| c == UNKNOWN)
    }

    fn count_broken_groups(map: &[u8]) -> Vec<u8> {
        map
            .split(|&e| e == OK)
            .inspect(|&group| if group.contains(&UNKNOWN) {
                println!("Trying to count broken groups with unknowns in {:?}!", map);
                panic!()
            })
            .map(|group| group.len() as u8)
            .filter(|&it| it != 0)
            .collect()
    }

    fn mutations_at(&self, index: usize) -> Vec<Record> {
        let mut first = self.map.clone();
        first[index] = BROKEN;
        let mut second = self.map.clone();
        second[index] = OK;
        vec![
            Record::new(first, self.broken_groups.clone()),
            Record::new(second, self.broken_groups.clone())
        ]
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
}

const TEST_1: TestVals<&str, i64> = TestVals(&"\
???.### 1,1,3
.??..??...?##. 1,1,3
?#?#?#?#?#?#?#? 1,3,1,6
????.#...#... 4,1,1
????.######..#####. 1,6,5
?###???????? 3,2,1
", 21i64);
const TEST_2: TestVals<&str, i64> = TestVals(&TEST_1.0, 525152i64);
const FILE: &str = "../resources/advent2023/day12.txt";