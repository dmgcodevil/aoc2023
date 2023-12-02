
function possible(cubes, avail)
    for cube in cubes
        if (avail[cube.color] < cube.count)
            return false
        end
    end
    return true
end

function getfewest(cubes)
    product = 1
    d = Dict("red" => 0, "green" => 0, "blue" => 0)
    for cube in cubes
        d[cube.color] = max(cube.count, d[cube.color])
    end

    for (_, v) in d
        product = product * v
    end
    return product
end

# "6 green, 3 blue; 3 red, 1 green; 4 green, 3 red, 5 blue" -> [(color, count)]
function parsecubes(s)
    cubes = []
    for set in split(s, ";")
        append!(cubes, map(split(set, ",")) do x
            p = split(strip(x), " ")
            (color=strip(p[2]), count=parse(Int, strip(p[1])))
        end)
    end
    return cubes
end

function part1()
    total = 0
    avail = Dict("red" => 12, "green" => 13, "blue" => 14)
    open("input.txt") do f
        while !eof(f)
            line = readline(f)
            p = split(line, ":")
            result = possible(parsecubes(p[2]), avail)
            if (result)
                total = total + parse(Int, split(p[1], " ")[2])
            end
        end
    end
    println("part1: $total")
end


function part2()
    total = 0
    open("input.txt") do f
        while !eof(f)
            line = readline(f)
            p = split(line, ":")
            total += getfewest(parsecubes(p[2]))
        end
    end
    println("part2: $total")
end


part1()
part2()
