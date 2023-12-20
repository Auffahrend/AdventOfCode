use std::collections::{HashMap, VecDeque};
use num_integer::lcm;

use crate::advent2023::day20::Strength::{HIGH, LOW};
use crate::utils::{test_and_run, TestVals};

pub(crate) fn solve() {
    test_and_run(&solution, &TEST_1, &TEST_2, FILE)
}

fn solution(input: &String) -> i64 {
    solve2(input)
}

fn solve2(input: &String) -> i64 {
    let (mut initial_state, cables) = State::parse(input);
    let target_pulses = vec![
        new_pulse(HIGH, "vq".to_string()),
        new_pulse(HIGH, "sn".to_string()),
        new_pulse(HIGH, "rf".to_string()),
        new_pulse(HIGH, "sr".to_string()),
    ];
    let mut loop_lens = vec![];

    for target_pulse in target_pulses {
        let mut state = initial_state.clone();
        let mut button_presses = 0i64;
        loop {
            let pulses = state.press_button(&cables, &target_pulse);
            button_presses += 1;
            if pulses == (-1, -1) {
                println!("found {:?} pulse to target {} after {} steps", target_pulse.str, target_pulse.from, button_presses);
                loop_lens.push(button_presses);
                break;
            }
        }
    }

    loop_lens.iter().fold(1i64, |acc, &x| lcm(acc, x))
}

fn solve1(input: &String) -> i64 {
    let (mut current_state, cables) = State::parse(input);
    let mut states = vec![];
    let mut low_pulses = vec![];
    let mut high_pulses = vec![];

    loop {
        states.push(current_state.clone());
        let pulses = current_state.press_button(&cables, &new_pulse(LOW, "rx".to_string()));
        low_pulses.push(pulses.0);
        high_pulses.push(pulses.1);
        println!("Step {}: emitted {} + {} pulses", states.len(), pulses.0, pulses.1);
        if states.contains(&current_state) { break; }
    }
    let mut offset = 0;
    for i in 0..states.len() {
        if states[i] == current_state {
            offset = i;
            break;
        }
    }
    let n = (CYCLES / states.len()) as i64;
    let low_pulses_per_loop: i64 = low_pulses.iter().sum();
    let high_pulses_per_loop: i64 = high_pulses.iter().sum();
    (low_pulses_per_loop * n + low_pulses[0..offset].iter().sum::<i64>())
        * (high_pulses_per_loop * n + high_pulses[0..offset].iter().sum::<i64>())
}

#[derive(Debug, Clone, Eq, PartialEq)]
struct State {
    modules: HashMap<String, Module>,
}

#[derive(Debug, Clone)]
struct Cables {
    connections: HashMap<String, Vec<String>>,
}

const BUTTON: &str = "button";
const BROADCAST: &str = "broadcaster";
impl State {
    fn press_button(&mut self, cables: &Cables, target_pulse: &Pulse) -> (i64, i64) {
        let mut pulses = VecDeque::new();
        let mut total_lows = 0i64;
        let mut total_highs = 0i64;
        pulses.push_back(new_pulse(LOW, "button".to_string()));
        while let Some(pulse) = pulses.pop_front() {
            let destinations = cables.connections.get(&pulse.from).unwrap();
            if pulse.str == LOW {
                total_lows += destinations.len() as i64;
            } else {
                total_highs += destinations.len() as i64;
            }
            for d in destinations {
                if let Some(module) = self.modules.get_mut(d) {
                    if let Some(new_p) = module.handle(&pulse) {
                        if new_p.from.contains(&target_pulse.from.clone()) && new_p.str == target_pulse.str {
                            return (-1, -1)
                        }
                        pulses.push_back(new_p);
                    }
                }
            }
        }
        (total_lows, total_highs)
    }

    fn parse(input: &str) -> (State, Cables) {
        let mut modules = HashMap::new();
        let mut connections = HashMap::new();
        input.lines()
            .for_each(|l| {
                let parts = l.split(" -> ").collect::<Vec<&str>>();
                let name = parts[0];
                let module = Module {
                    module_type: if name.starts_with("%") {
                        ModuleType::FF
                    } else if name.starts_with("&") {
                        ModuleType::CON
                    } else if name.starts_with(BROADCAST) {
                        ModuleType::BC
                    } else { panic!("Unknown module {}", name) },
                    name: if name.contains(BROADCAST) { name.to_string() } else { name[1..].to_string() },
                    ff_state: false,
                    con_states: HashMap::new(),
                };
                modules.insert(module.name.clone(), module.clone());

                let cons: Vec<String> = parts[1].split(", ").map(|n| n.to_string()).collect();
                connections.insert(module.name.clone(), cons);
            });

        connections.insert(BUTTON.to_string(), vec![BROADCAST.to_string()]);
        // assign sources for all conjunctors

        for mut module in modules.values_mut() {
            if module.module_type == ModuleType::CON {
                connections.iter()
                    .filter(|(_, dest)| dest.contains(&module.name))
                    .for_each(|(source, _)| {
                        module.con_states.insert(source.clone(), LOW);
                    })
            }
        }

        (State { modules }, Cables { connections })
    }
}

#[derive(Debug, Clone)]
#[derive(PartialEq, Eq)]
enum Strength { LOW, HIGH }

#[derive(Debug, Clone)]
#[derive(PartialEq, Eq)]
enum ModuleType { BC, FF, CON }

#[derive(Debug, Clone)]
#[derive(PartialEq, Eq)]
struct Pulse {
    str: Strength,
    from: String,
}

fn new_pulse(str: Strength, from: String) -> Pulse { Pulse { str, from } }


#[derive(Debug, Clone, Eq, PartialEq)]
struct Module {
    name: String,
    module_type: ModuleType,
    ff_state: bool,
    con_states: HashMap<String, Strength>,
}

impl Module {
    fn handle(&mut self, pulse: &Pulse) -> Option<Pulse> {
        match self.module_type {
            ModuleType::BC => Some(new_pulse(pulse.str.clone(), self.name.clone())),
            ModuleType::FF => self.flipflop(pulse),
            ModuleType::CON => self.conjunction(pulse),
        }
    }
    fn flipflop(&mut self, pulse: &Pulse) -> Option<Pulse> {
        if pulse.str == LOW {
            self.ff_state = !self.ff_state;
            Some(new_pulse(if self.ff_state { HIGH } else { LOW }, self.name.clone()))
        } else { None }
    }

    fn conjunction(&mut self, pulse: &Pulse) -> Option<Pulse> {
        self.con_states.entry(pulse.from.clone()).and_modify(|e| *e = pulse.str.clone());
        Some(new_pulse(
            if self.con_states.values().all(|s| s == &HIGH) { LOW } else { HIGH },
            self.name.clone(),
        ))
    }
}

const CYCLES: usize = 1000;

const TEST_1: TestVals<&str, i64> = TestVals(&"\
broadcaster -> a, b, c
%a -> b
%b -> c
%c -> inv
&inv -> a
", 32000000i64);
const TEST_2: TestVals<&str, i64> = TestVals(&"\
broadcaster -> a
%a -> inv, con
&inv -> b
%b -> con
&con -> output
", 11687500i64);
const FILE: &str = "../resources/advent2023/day20.txt";