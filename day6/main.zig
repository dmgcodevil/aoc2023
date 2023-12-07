const std = @import("std");

pub fn main() !void {
    // const input = [3][2]u32{
    //     [2]u32{ 7, 9 },
    //     [2]u32{ 15, 40 },
    //     [2]u32{ 30, 200 },
    // };

    // const input = [4][2]u32{
    //     [2]u32{ 44, 277 },
    //     [2]u32{ 89, 1136 },
    //     [2]u32{ 96, 1890 },
    //     [2]u32{ 91, 1768 },
    // };

    // part2
    // Time:        44     89     96     91
    // Distance:   277   1136   1890   1768
    const input = [1][2]u64{[2]u64{ 44899691, 277113618901768 }};

    var answer: u32 = 1;
    for (input) |p| {
        var speed = @divTrunc(p[1], p[0]) + 1;
        while ((p[0] - speed) * speed <= p[1]) {
            speed = speed + 1;
        }
        std.debug.print("least_speed={}\n", .{speed});
        var ways: u32 = 0;
        while ((p[0] - speed) * speed > p[1]) {
            speed = speed + 1;
            ways = ways + 1;
        }
        std.debug.print("most_speed={}\n", .{speed});
        std.debug.print("ways={}\n", .{ways});
        answer = answer * ways;
    }

    std.debug.print("answer={}\n", .{answer});
}
