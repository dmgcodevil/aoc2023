use std::cmp::min;
use std::collections::HashMap;
use std::fs::File;
use std::io::{self, BufRead};
use std::path::Path;
use std::slice::Iter;

#[derive(Debug)]
struct Mapping<'a> {
    name: &'a str,
    source: u64,
    dist: u64,
    range: u64,
}

fn add_mappings<'a>(name_: &'a str, vec: &mut Vec<Mapping<'a>>, it: &mut Iter<String>) {
    while let Some(line) = it.next() {
        if line == "" {
            break;
        }
        let nums: Vec<u64> = line
            .split_whitespace()
            .filter_map(|n| n.parse::<u64>().ok())
            .collect();
        vec.push(Mapping {
            name: name_,
            source: *nums.get(1).unwrap(),
            dist: *nums.get(0).unwrap(),
            range: *nums.get(2).unwrap(),
        })
    }
}

fn find_lowest(seed: u64, mappings: &[Vec<Mapping>; 7]) -> u64 {
    let mut curr = seed;

    for m_vec in mappings {
        for m in m_vec {
            if (curr >= m.source && curr <= m.source + m.range) {
                let diff = curr - m.source;
                assert!(diff + m.dist >= m.dist && diff + m.dist <= m.dist + m.range,
                        "seed={}, m={:?}, diff={}", seed, m, diff);
                curr = m.dist + diff;
                break;
            }
        }
    }
    return curr;
}


// this is a brut-force solution
// i have not figured out an efficient solution yet
// second parts take ~10min to complete
fn main() -> io::Result<()> {
    let path = Path::new("input");

    let file = File::open(&path)?;

    let reader = io::BufReader::new(file);

    let mut lines: Vec<String> = Vec::new();

    for line in reader.lines() {
        lines.push(line?);
    }

    let mut seed_to_soil: Vec<Mapping> = Vec::new();
    let mut soil_to_fertilizer: Vec<Mapping> = Vec::new();
    let mut fertilizer_to_water: Vec<Mapping> = Vec::new();
    let mut water_to_light: Vec<Mapping> = Vec::new();
    let mut light_to_temperature: Vec<Mapping> = Vec::new();
    let mut temperature_to_humidity: Vec<Mapping> = Vec::new();
    let mut humidity_to_location: Vec<Mapping> = Vec::new();


    let mut it: Iter<String> = lines.iter();

    let seeds: Vec<u64> = it.next().unwrap()[7..]
        .split_whitespace()
        .filter_map(|n| n.parse::<u64>().ok())
        .collect();

    let _ = it.next();


    assert_eq!(it.next().unwrap(), "seed-to-soil map:");
    add_mappings("seed_to_soil", &mut seed_to_soil, &mut it);

    assert_eq!(it.next().unwrap(), "soil-to-fertilizer map:");
    add_mappings("soil_to_fertilizer", &mut soil_to_fertilizer, &mut it);

    assert_eq!(it.next().unwrap(), "fertilizer-to-water map:");
    add_mappings("fertilizer_to_water", &mut fertilizer_to_water, &mut it);

    assert_eq!(it.next().unwrap(), "water-to-light map:");
    add_mappings("water_to_light", &mut water_to_light, &mut it);

    assert_eq!(it.next().unwrap(), "light-to-temperature map:");
    add_mappings("light_to_temperature", &mut light_to_temperature, &mut it);

    assert_eq!(it.next().unwrap(), "temperature-to-humidity map:");
    add_mappings("temperature_to_humidity", &mut temperature_to_humidity, &mut it);

    assert_eq!(it.next().unwrap(), "humidity-to-location map:");
    add_mappings("humidity_to_location", &mut humidity_to_location, &mut it);


    let mut answer = u64::MAX;

    let mappings: [Vec<Mapping>; 7] = [seed_to_soil,
        soil_to_fertilizer,
        fertilizer_to_water,
        water_to_light,
        light_to_temperature,
        temperature_to_humidity,
        humidity_to_location];


    let mut seed_to_location: HashMap<u64, u64> = HashMap::new();

    for seed in &seeds {
        let location = find_lowest(*seed, &mappings);
        seed_to_location.insert(*seed, location);
        answer = min(answer, location);
    }

    println!("part1 answer={}", answer);
    answer = u64::MAX;

    for range in seeds.chunks(2) {
        let mut lowest = find_lowest(range[0], &mappings);
        let start = range[0] + 1;
        let end = range[0] + range[1];
        for seed in start..end {
            let tmp = find_lowest(seed, &mappings);
            if tmp < lowest {
                lowest = tmp;
                break;
            }
        }
        println!("[{}-{}] = {}", range[0], range[1], lowest);
        answer = min(answer, lowest);
    }
    println!("part2 answer={}", answer); // 77435348


    Ok(())
}
