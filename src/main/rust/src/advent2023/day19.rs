use std::cmp::{max, min};
use std::collections::HashMap;
use std::ops::Range;
use lazy_static::lazy_static;
use regex::Regex;

use crate::utils::{test_and_run, TestVals};

pub(crate) fn solve() {
    test_and_run(&solution, &TEST_1, &TEST_2, FILE)
}

fn solution(input: &String) -> i64 {
    let parts: Vec<&str> = input.split("\n\n").collect();
    let workflows: HashMap<String, Vec<(WfPredicate, String)>> = parts[0].lines()
        .map(|line| {
            let wf = Workflow::parse(line);
            (wf.name, wf.steps)
        })
        .collect();

    // part 1
    // let parts: Vec<Part> = parts[1].lines().map(|line| Part::parse(line)).collect();
    // accepted_parts(&workflows, &parts).iter()
    //     .map(|p| p.x + p.m + p.a + p.s)
    //     .sum()

    // part 2
    let all_possible = PartInterval { xmas: HashMap::from(
        [('x', 1..4001), ('m', 1..4001), ('a', 1..4001), ('s', 1..4001)]
    ) };
    accepted_intervals(workflows, all_possible).iter()
        .map(|i| i.size())
        .sum()
}

fn accepted_intervals(workflows: HashMap<String, Vec<(WfPredicate, String)>>, init: PartInterval) -> Vec<PartInterval> {
    let mut result: Vec<PartInterval> = vec![];
    let mut queue: Vec<(PartInterval, String, usize)> = vec![(init, "in".to_string(),0)];
    while !queue.is_empty() {
        let (interval, wf_name, step_i) = queue.remove(0);
        if "A" == wf_name {
            result.push(interval.clone());
            println!("{:?} accepted, size {}", interval, interval.size());
        } else if "R" == wf_name {
            // println!("Part {:?} rejected", part);
        } else if !interval.is_empty() {
            let wf = workflows.get(&wf_name).unwrap();
            let sub_inters = wf[step_i].0.split(&interval);
            if sub_inters.len() > 2 { panic!("More than 2 intervals after a condition split") }
            queue.push((sub_inters[0].clone(), wf[step_i].1.clone(), 0));

            if let Some(other) = sub_inters.get(1) {
                queue.push((other.clone(), wf_name, step_i + 1));
            }
        }
    }
    result
}

fn accepted_parts(workflows: &HashMap<String, Vec<(WfPredicate, String)>>, parts: &Vec<Part>) -> Vec<Part> {
    let mut result: Vec<Part> = vec![];
    for part in parts {
        let mut current_wf_name = "in".to_string();
        loop {
            if "A" == current_wf_name {
                result.push(part.clone());
                println!("Part {:?} accepted", part);
                break;
            } else if "R" == current_wf_name {
                println!("Part {:?} rejected", part);
                break;
            } else {
                let wf = workflows.get(&current_wf_name).unwrap();
                for step in wf {
                    if step.0.test(part) {
                        current_wf_name = step.1.clone();
                        break;
                    }
                }
            }
        }
    }
    result
}

lazy_static!(
    static ref WF_RE: Regex = Regex::new(r"(.+)\{(.+)}").unwrap();
    static ref WF_PREDICATE_RE: Regex = Regex::new(r"([xmas])([<>])(\d+):(\w+)").unwrap();
    static ref PART_RE: Regex = Regex::new(r"\{x=(\d+),m=(\d+),a=(\d+),s=(\d+)}").unwrap();
);

struct Workflow {
    name: String,
    steps: Vec<(WfPredicate, String)>,
}

impl Workflow {
    fn parse(line: &str) -> Workflow {
        let (_, [name, all_steps]) = WF_RE.captures(line).unwrap().extract();
        let steps: Vec<&str> = all_steps.split(",").collect();
        let mut conditions: Vec<(WfPredicate, String)> = steps[..steps.len() - 1].iter()
            .map(|&step| {
                let (_, [attr, comp, value, wf]) = WF_PREDICATE_RE.captures(step).unwrap().extract();
                (WfPredicate::new(attr, comp, value.parse::<i64>().unwrap()), wf.to_string())
            })
            .collect();
        conditions.push((WfPredicate::new("_", "_", 0i64), steps.last().unwrap().to_string()));
        Workflow { name: name.to_string(), steps: conditions }
    }
}

struct WfPredicate {
    attribute: char,
    comparison: char,
    value: i64,
}

impl WfPredicate {
    fn split(&self, interval: &PartInterval) -> Vec<PartInterval> {
        match &self.comparison {
            '_' => vec![interval.clone()],
            '<' => interval.split_at(self.attribute, self.value, false),
            '>' => interval.split_at(self.attribute, self.value + 1, true),
            _ => unreachable!()
        }
    }


    fn test(&self, part: &Part) -> bool {
        match (&self.attribute, &self.comparison) {
            ('_', '_') => true,
            ('x', '<') => part.x < *&self.value,
            ('x', '>') => part.x > *&self.value,
            ('m', '<') => part.m < *&self.value,
            ('m', '>') => part.m > *&self.value,
            ('a', '<') => part.a < *&self.value,
            ('a', '>') => part.a > *&self.value,
            ('s', '<') => part.s < *&self.value,
            ('s', '>') => part.s > *&self.value,
            _ => unreachable!()
        }
    }
    fn new(attribute: &str, comparison: &str, value: i64) -> WfPredicate {
        WfPredicate {
            attribute: attribute.chars().nth(0).unwrap(),
            comparison: comparison.chars().nth(0).unwrap(),
            value,
        }
    }
}

#[derive(Copy, Clone, Debug)]
struct Part {
    x: i64,
    m: i64,
    a: i64,
    s: i64,
}

impl Part {
    fn parse(line: &str) -> Part {
        let (_, [x, m, a, s]) = PART_RE.captures(line).unwrap().extract();
        Part::new(x.parse::<i64>().unwrap(), m.parse::<i64>().unwrap(), a.parse::<i64>().unwrap(), s.parse::<i64>().unwrap())
    }

    fn new(x: i64, m: i64, a: i64, s: i64) -> Part {
        Part { x, m, a, s }
    }
}

#[derive(Clone, Debug)]
struct PartInterval {
    xmas: HashMap<char, Range<i64>>,
}

impl PartInterval {
    fn is_empty(&self) -> bool {
        self.xmas.values().any(|r| r.is_empty())
    }

    fn size(&self) -> i64 {
        if self.is_empty() { 0i64 }
        else {
            self.xmas.values()
                .map(|r| r.end - r.start)
                .product()
        }
    }

    fn split_at(&self, attribute: char, value: i64, reverse: bool) -> Vec<PartInterval> {
        let mut i1 = self.clone();
        i1.xmas.entry(attribute)
            .and_modify(|i| i.end = min(value, i.end));
        let mut i2 = self.clone();
        i2.xmas.entry(attribute)
            .and_modify(|i| i.start = max(value, i.start));
        if !reverse { vec![i1, i2] } else { vec![i2, i1] }
    }

}

const TEST_1: TestVals<&str, i64> = TestVals(&"\
px{a<2006:qkq,m>2090:A,rfg}
pv{a>1716:R,A}
lnx{m>1548:A,A}
rfg{s<537:gd,x>2440:R,A}
qs{s>3448:A,lnx}
qkq{x<1416:A,crn}
crn{x>2662:A,R}
in{s<1351:px,qqz}
qqz{s>2770:qs,m<1801:hdj,R}
gd{a>3333:R,R}
hdj{m>838:A,pv}

{x=787,m=2655,a=1222,s=2876}
{x=1679,m=44,a=2067,s=496}
{x=2036,m=264,a=79,s=2244}
{x=2461,m=1339,a=466,s=291}
{x=2127,m=1623,a=2188,s=1013}
", 19114i64);
const TEST_2: TestVals<&str, i64> = TestVals(TEST_1.0, 167409079868000i64);
const FILE: &str = "../resources/advent2023/day19.txt";