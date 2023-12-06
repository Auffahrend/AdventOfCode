use std::collections::HashSet;
use regex::Regex;
use lazy_static::lazy_static;

use crate::utils::{test_and_run, TestVals};

pub(crate) fn solve() {
    test_and_run(&solution, &TEST_1, &TEST_2, FILE)
}

fn solution(input: &String) -> i64 {
    let cards: Vec<Card> = input.lines()
        .map(|l| Card::from_line(l))
        .collect();
    // part 1
    // cards.iter().map(|c| c.score())
    //     .sum()
    let mut card_counts = Vec::from_iter(cards.iter().map(|_| 1));
    for i in 0..cards.len() {
        let card = &cards[i];
        let count = card_counts[i];
        let matches = card.matches();
        for j in 1..matches+1 {
            card_counts[i + j as usize] += count
        }
    };
    card_counts.iter().sum()
}

#[derive(Debug)]
struct Card {
    id: usize,
    winning: HashSet<i64>,
    own: HashSet<i64>,
}

lazy_static! {
    static ref CARD_FULL_RE: Regex = Regex::new(r"Card (\d+): (\d+ ?)+ | (\d+ ?)+").unwrap();
    static ref CARD_NUMBER_RE: Regex = Regex::new(r"(\d+)").unwrap();
}

impl Card {
    fn score(&self) -> i64 {
        let matches = self.matches();
        if matches > 0 {
            2i64.pow(matches - 1)
        } else { 0 }
    }

    fn matches(&self) -> u32 {
        let commons = self.winning.intersection(&self.own);
        commons.count() as u32
    }

    fn from_line(line: &str) -> Card {
        let parts: Vec<&str> = line.split(&[':', '|'][..]).collect();
        // println!("Parts to parse: {:?}", parts);
        let id_capture = CARD_NUMBER_RE.captures(parts[0]).unwrap();
        let card_id = id_capture[1].trim();

        let win_nums = CARD_NUMBER_RE.captures_iter(parts[1])
            .map(|c| {
                let (_, [num]) = c.extract();
                num.parse::<i64>().unwrap()
            })
            .collect::<HashSet<i64>>();
        let own_nums = CARD_NUMBER_RE.captures_iter(parts[2])
            .map(|c| {
                let (_, [num]) = c.extract();
                num.parse::<i64>().unwrap()
            })
            .collect::<HashSet<i64>>();
        Card { id: card_id.parse().unwrap(), winning: win_nums, own: own_nums }
    }
}

const TEST_1: TestVals<&str, i64> = TestVals(&"\
Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
", 13i64);
const TEST_2: TestVals<&str, i64> = TestVals(TEST_1.0, 30i64);
const FILE: &str = "../resources/advent2023/day04.txt";