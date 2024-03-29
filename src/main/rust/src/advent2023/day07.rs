use std::cmp::Ordering;
use std::collections::HashMap;
use lazy_static::lazy_static;

use crate::utils::{test_and_run, TestVals};

pub(crate) fn solve() {
    test_and_run(&solution, &TEST_1, &TEST_2, FILE)
}

fn solution(input: &String) -> i64 {
    let mut hands: Vec<Hand> = input.lines()
        .map(|l| l.trim())
        .map(|l| Hand::parse(l))
        // .inspect(|h| println!("Parsed hand {:?}", h))
        .collect();

    hands.sort_by(Hand::cmp);

    let mut winnings = 0i64;
    for rank in 0..hands.len() {
        let win = hands[rank].bid * (1i64 + rank as i64);
        println!("Hand {:?} winning is {}", hands[rank], win);
        winnings += win
    }

    winnings
}

const JOKER: u8 = 11;
lazy_static!(
    static ref MODE: u8 = 2;
);

#[derive(Debug, PartialOrd, PartialEq, Eq)]
enum HandType {
    Five,
    Four,
    FullHouse,
    Three,
    TwoPair,
    OnePair,
    High,
}

impl HandType {
    fn strength(&self) -> u8 {
        match self {
            HandType::Five => 7,
            HandType::Four => 6,
            HandType::FullHouse => 5,
            HandType::Three => 4,
            HandType::TwoPair => 3,
            HandType::OnePair => 2,
            HandType::High => 1
        }
    }
}

impl Ord for HandType {
    fn cmp(&self, other: &Self) -> Ordering {
        self.strength().cmp(&other.strength())
    }
}

#[derive(Debug, PartialOrd, PartialEq, Eq)]
struct Hand {
    cards: [u8; 5],
    bid: i64,
    hand_type: Option<HandType>,
}

impl Hand {
    fn parse(line: &str) -> Hand {
        let mut cards: [u8; 5] = [0, 0, 0, 0, 0];
        (0..5)
            .for_each(|i| cards[i] = Hand::parse_card(line.chars().nth(i).unwrap()));
        let bid = line.split(" ").nth(1).unwrap().parse::<i64>().unwrap();
        let mut hand = Hand { cards, bid, hand_type: None };
        hand.hand_type();
        hand
    }

    fn parse_card(symbol: char) -> u8 {
        match symbol {
            '2' => 2,
            '3' => 3,
            '4' => 4,
            '5' => 5,
            '6' => 6,
            '7' => 7,
            '8' => 8,
            '9' => 9,
            'T' => 10,
            'J' => JOKER.clone(),
            'Q' => 12,
            'K' => 13,
            'A' => 14,
            _ => {
                println!("Unknown card {}!!", symbol);
                panic!()
            }
        }
    }

    fn hand_type(&mut self) {
        if self.hand_type == None {
            let mut groups: HashMap<u8, u8> = HashMap::new();

            let mut jockers = 0;
            for card in self.cards {
                if JOKER == card && *MODE == 2 {
                    jockers += 1;
                } else {
                    let count = groups.entry(card).or_insert(0);
                    *count += 1;
                }
            }

            let mut counts = groups.clone().values().map(|&e| e).collect::<Vec<u8>>();
            counts.sort();
            counts.reverse();
            if counts.is_empty() && jockers == 5 {
                counts.push(0);
            }
            counts[0] += jockers;
            self.hand_type = match counts.as_slice() {
                [5] => Some(HandType::Five),
                [4, 1] => Some(HandType::Four),
                [3, 2] => Some(HandType::FullHouse),
                [3, 1, 1] => Some(HandType::Three),
                [2, 2, 1] => Some(HandType::TwoPair),
                [2, 1, 1, 1] => Some(HandType::OnePair),
                [1, 1, 1, 1, 1] => Some(HandType::High),
                _ => {
                    println!("Unhandled case {:?}", self.hand_type);
                    panic!()
                }
            }
        }
    }

    fn card_cmp(card: &u8, other: &u8) -> Ordering {
        if *MODE == 2 {
            match (*card, *other) {
                (JOKER, JOKER) => Ordering::Equal,
                (JOKER, _) => Ordering::Less,
                (_, JOKER) => Ordering::Greater,
                (_, _) => card.cmp(other),
            }
        } else {
            card.cmp(other)
        }
    }
}

impl Ord for Hand {
    fn cmp(&self, other: &Self) -> Ordering {
        let res = self.hand_type.cmp(&other.hand_type)
            .then(Hand::card_cmp(&self.cards[0], &other.cards[0]))
            .then(Hand::card_cmp(&self.cards[1], &other.cards[1]))
            .then(Hand::card_cmp(&self.cards[2], &other.cards[2]))
            .then(Hand::card_cmp(&self.cards[3], &other.cards[3]))
            .then(Hand::card_cmp(&self.cards[4], &other.cards[4]));
        // println!("Comparing {:?} and {:?}. Result is {:?}", self, &other, res);
        res
    }

}


const TEST_1: TestVals<&str, i64> = TestVals(&"\
32T3K 765
T55J5 684
KK677 28
KTJJT 220
QQQJA 483
", 6440i64);
const TEST_2: TestVals<&str, i64> = TestVals(TEST_1.0, 5905i64);
const FILE: &str = "../resources/advent2023/day07.txt";