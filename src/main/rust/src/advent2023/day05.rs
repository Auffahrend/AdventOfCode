use std::ops::Range;
use lazy_static::lazy_static;
use regex::Regex;

use crate::utils::{test_and_run, TestVals};

pub(crate) fn solve() {
    test_and_run(&solution, &TEST_1, &TEST_2, FILE)
}

fn solution(input: &String) -> i64 {
    let parts: Vec<&str> = input.split("\n\n").collect();
    let seeds: Vec<i64> = NUMBER_RE.captures_iter(parts[0])
        .map(|c| {
            let (_, [n]) = c.extract();
            n.parse::<i64>().unwrap()
        })
        .collect();
    let seedRanges: Vec<Range<i64>> = SEED_RANGE_RE.captures_iter(parts[0])
        .map(|c| {
            let (_, [ss, sl]) = c.extract();
            let range_start = ss.parse::<i64>().unwrap();
            let range_length = sl.parse::<i64>().unwrap();
            range_start..(range_start + range_length)
        })
        .collect();

    let mappings: Vec<Mapping> = parts.iter().skip(1)
        .map(|p| Mapping::parse(p))
        .collect();

    // part 1
    // seeds.iter()
    //     .map(|&seed| {
    //         let dest = mappings.iter()
    //             .fold(seed, |source, mapping| mapping.map(source));
    //         println!("Seed {} maps to location {}", seed, dest);
    //         dest
    //     })
    //     .min().unwrap()

    // part 2
    seedRanges.iter()
        .map(|sr| sr.map(|seed| {
            let dest = mappings.iter()
                .fold(seed, |source, mapping| mapping.map(source));
            if mappings.len() < 10 {
                println!("Seed {} maps to location {}", seed, dest);
            }
            dest
        }
        )
            .min().unwrap()
        )
        .min().unwrap()
}

struct MappingRange {
    source: Range<i64>,
    dest: Range<i64>,
}

struct Mapping {
    name: String,
    ranges: Vec<MappingRange>,
}

lazy_static! {
    static ref NUMBER_RE : Regex = Regex::new(r"(\d+)").unwrap();
    static ref SEED_RANGE_RE : Regex = Regex::new(r"(\d+) (\d+)").unwrap();
    static ref RANGE_RE : Regex = Regex::new(r"(\d+) (\d+) (\d+)").unwrap();
}
impl Mapping {
    fn map(&self, source: i64) -> i64 {
        let range = self.ranges.iter()
            .find(|&r| r.source.contains(&source));
        if let Some(mappingRange) = range {
            let offset = source - mappingRange.source.start;
            mappingRange.dest.start + offset
        } else {
            source
        }
    }

    // fn map_range(&self, sourceRange: Range<i64>) -> Vec<Range<i64>> {
    //     let mut curRange = sourceRange.clone();
    //     let mut result: Vec<Range<i64>> = Vec::new();
    //
    //     let mut i = 0usize;
    //     while !curRange.is_empty() && i < self.ranges.len() {
    //         let rangeStart =
    //     }
    //
    //     if !curRange.is_empty() {
    //         result.push();
    //     }
    //     return result;
    //     let self.ranges.iter()
    //         .find(|&r| r.source.contains(&source));
    //     if let Some(mappingRange) = range {
    //         let offset = source - mappingRange.source.start;
    //         mappingRange.dest.start + offset
    //     } else {
    //         source
    //     }
    // }

    fn parse(input: &str) -> Mapping {
        let lines: Vec<&str> = input.lines().collect();
        let ranges: Vec<MappingRange> = lines.iter().skip(1)
            .map(|l| Mapping::parse_range(l))
            .collect();
        Mapping { name: lines[0].to_string(), ranges }
    }

    fn parse_range(line: &str) -> MappingRange {
        let capt = RANGE_RE.captures(line).unwrap();
        let (_, [d, s, l]) = capt.extract();
        let source = s.parse::<i64>().unwrap();
        let dest = d.parse::<i64>().unwrap();
        let len = l.parse::<i64>().unwrap();

        MappingRange { source: source..source + len, dest: dest..dest + len }
    }
}

const TEST_1: TestVals<&str, i64> = TestVals(&"\
seeds: 79 14 55 13

seed-to-soil map:
50 98 2
52 50 48

soil-to-fertilizer map:
0 15 37
37 52 2
39 0 15

fertilizer-to-water map:
49 53 8
0 11 42
42 0 7
57 7 4

water-to-light map:
88 18 7
18 25 70

light-to-temperature map:
45 77 23
81 45 19
68 64 13

temperature-to-humidity map:
0 69 1
1 0 69

humidity-to-location map:
60 56 37
56 93 4
", 35i64);
const TEST_2: TestVals<&str, i64> = TestVals(TEST_1.0, 46i64);
const FILE: &str = "../resources/advent2023/day05.txt";