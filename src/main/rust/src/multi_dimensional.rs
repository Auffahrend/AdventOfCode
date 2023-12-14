#[derive(Debug, PartialEq, Eq, Hash, Clone, Copy)]
pub(crate) struct Coord2 {
    pub(crate) x: isize,
    pub(crate) y: isize,
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
