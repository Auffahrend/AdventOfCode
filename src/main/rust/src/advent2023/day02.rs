use regex::Regex;

use crate::utils::{test_and_run, TestVals};

pub(crate) fn solve() {
    test_and_run(&solution, &TEST_1, &TEST_2, FILE)
}

fn solution(input: &String) -> i64 {
    let parser = Parser {
        num_id: Regex::new(r"Game (\d+)").unwrap(),
        num_red: Regex::new(r"(\d+) red").unwrap(),
        num_green: Regex::new(r"(\d+) green").unwrap(),
        num_blue: Regex::new(r"(\d+) blue").unwrap(),
    };
    input
        .lines()
        .map(|line| parser.parse_game(line))
        // part 1
        // .filter(|(_, outcomes)| outcomes.iter().all(|o| o.is_possible()))
        // .fold(0i64, |acc, game| acc + game.0)
        // part 2
        .map(|(_, outcomes)| minimal_set(outcomes))
        .fold(0i64, |acc, bag| acc + bag.power())
}

fn minimal_set(outcomes: Vec<BagContent>) -> BagContent {
    let mut minimal = if outcomes.is_empty() {
        return BagContent { red: 0, green: 0, blue: 0 };
    } else { BagContent { red: outcomes[0].red, green: outcomes[0].green, blue: outcomes[0].blue } };

    for outcome in outcomes {
        if outcome.red > minimal.red { minimal.red = outcome.red }
        if outcome.green > minimal.green { minimal.green = outcome.green }
        if outcome.blue > minimal.blue { minimal.blue = outcome.blue }
    }
    minimal
}

struct Parser {
    num_id: Regex,
    num_red: Regex,
    num_green: Regex,
    num_blue: Regex,
}

impl Parser {
    fn parse_game(&self, line: &str) -> (i64, Vec<BagContent>) {
        let parts: Vec<&str> = line.split(':').collect();
        let game_id = self.num_id.captures(parts[0])
            .unwrap()[1].parse::<i64>().unwrap();

        let outcomes: Vec<BagContent> = parts[1].split(';')
            .map(|p| self.parse_outcome(p))
            .collect();
        (game_id, outcomes)
    }

    fn parse_outcome(&self, text: &str) -> BagContent {
        let balls = [&self.num_red, &self.num_green, &self.num_blue]
            .map(|p| p
                .captures(text)
                .map(|r| r[1].parse::<u16>().unwrap())
                .unwrap_or_else(|| 0)
            );

        BagContent { red: balls[0], green: balls[1], blue: balls[2] }
    }
}

const LIMIT: BagContent = BagContent { red: 12, green: 13, blue: 14 };

struct BagContent {
    red: u16,
    green: u16,
    blue: u16,
}

impl BagContent {
    fn is_possible(&self) -> bool {
        if self.red <= LIMIT.red && self.green <= LIMIT.green && self.blue <= LIMIT.blue { true } else { false }
    }

    fn power(&self) -> i64 { self.red as i64 * self.green as i64 * self.blue as i64 }
}

const TEST_1: TestVals<&str, i64> = TestVals(&"Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
", 8i64);
const TEST_2: TestVals<&str, i64> = TestVals(&"Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
", 2286i64);
const FILE: &str = "../resources/advent2023/day02.txt";