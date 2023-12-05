#[derive(Debug, PartialEq, Eq, Hash, Clone, Copy)]
pub(crate) struct Coord2 { pub(crate) x: isize, pub(crate) y: isize }

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