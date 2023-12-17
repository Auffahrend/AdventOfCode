use std::fmt::{Display, Formatter};
use std::ops::{Add, Mul, Neg};

#[derive(Debug, PartialEq, Eq, Hash, Clone, Copy)]
pub(crate) struct Coord2 {
    pub(crate) x: isize,
    pub(crate) y: isize,
}

#[macro_export]
macro_rules! coord2 {
    ($x:expr, $y: expr) => { Coord2 { x: $x as isize, y: $y as isize} };
}
#[macro_export]
macro_rules! coord2_valid_on {
    ($coord:expr, $grid: expr) => {
        $coord.x >= 0 && $coord.y >= 0 &&
        ($coord.y as usize) < $grid.len() && ($coord.x as usize) < $grid[0].len()
    };
}

#[macro_export]
macro_rules! value_at_coord2 {
    ($coord:expr, $grid: expr) => {
        $grid[$coord.y as usize][$coord.x as usize]
    };
}

impl Add for Coord2 {
    type Output = Coord2;
    fn add(self, other: Coord2) -> Self::Output { coord2!(self.x + other.x, self.y + other.y) }
}

impl Neg for Coord2 {
    type Output = Coord2;
    fn neg(self) -> Self::Output { coord2!(-self.x, -self.y) }
}

impl Display for Coord2 {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        write!(f, "({}, {})", self.x, self.y)
    }
}

// pub(crate) type Grid2<'a, T> = Vec<Vec<&'a T>>;

pub(crate) fn neighbours8<T>(from: &Coord2, grid2: &Vec<Vec<T>>) -> Vec<Coord2> {
    (-1..2)
        .flat_map(|dx| (-1..2)
            .map(move |dy| Coord2 { x: from.x + dx, y: from.y + dy })
            .filter(|c| c != from)
        )
        .filter(|&coord|
            (0..grid2.len() as isize).contains(&coord.y) &&
                (0..grid2[0].len() as isize).contains(&coord.x)
        ).collect()
}

#[macro_export]
macro_rules! transpose_2d {
    ($matrix:expr) => {{
    let row_len = $matrix.get(0).map_or(0, |row| row.len());
    let mut transposed = Vec::with_capacity(row_len);

    for x in 0..row_len {
        let mut new_row = Vec::with_capacity($matrix.len());
        for row in &$matrix {
            new_row.push(*row.get(x).expect("All rows must have the same length").clone());
        }
        transposed.push(new_row);
    }

    transposed
    }};
}
