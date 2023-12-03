use std::fmt::Display;
use std::fs::File;
use std::io;
use std::io::Read;

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
    if let Some(error) = verify(sol, test1) {
        println!("Test 1 failed: {}", error)
    } else { println!("Test 1 succeeded") }
    if let Some(error) = verify(sol, test2) {
        println!("Test 2 failed: {}", error)
    } else { println!("Test 2 succeeded") }
    let result = sol(&read_file(file_name).unwrap());
    println!("The solution is {}", result)
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