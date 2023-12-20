use std::fmt::Display;
use std::fs::File;
use std::io;
use std::io::Read;
use std::time::Instant;

#[macro_export]
macro_rules! parse_as_chars {
    ($input:expr) => {
        $input.lines()
              .map(|line| line.chars().collect::<Vec<char>>())
              .collect::<Vec<Vec<char>>>()
    };
}

#[macro_export]
macro_rules! parse_as_digits {
    ($input:expr) => {
        $input.lines()
              .map(|line| line.chars().map(|c| c.to_string().parse::<i64>().unwrap()).collect::<Vec<i64>>())
              .collect::<Vec<Vec<i64>>>()
    };
}

pub struct TestVals<I, O>(pub I, pub O)
    where O: Ord + Display
;

pub fn test_and_run<O>(
    sol: &impl Fn(&String) -> O,
    test1: &TestVals<&str, O>, test2: &TestVals<&str, O>,
    file_name: &str,
)
    where O: Ord + Display
{
    let mut start = Instant::now();
    // if let Some(error) = verify(sol, test1) {
    //     println!("Test 1 failed: {}", error)
    // } else { println!("Test 1 succeeded in {} ms", (Instant::now() - start).as_millis()) }

    // start = Instant::now();
    // if let Some(error) = verify(sol, test2) {
    //     println!("Test 2 failed: {}", error)
    // } else { println!("Test 2 succeeded in {} ms", (Instant::now() - start).as_millis()) }

    start = Instant::now();
    let result = sol(&read_file(file_name).unwrap());
    println!("The solution is {} in {} ms", result, (Instant::now() - start).as_millis())
}

pub fn verify<O>(sol: &impl Fn(&String) -> O, vals: &TestVals<&str, O>) -> Option<String>
    where O: Ord + Display
{
    let actual = sol(&vals.0.to_string());
    if actual != vals.1 {
        Some(format!("Expected {}, but was {}", vals.1, actual))
    } else { None }
}

pub fn read_file(file_path: &str) -> io::Result<String> {
    // Open the file in read-only mode
    let mut file = File::open(file_path)?;

    // Read the entire contents of the file into a String
    let mut contents = String::new();
    file.read_to_string(&mut contents)?;

    Ok(contents.trim().to_string())
}