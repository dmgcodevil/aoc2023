const std = @import("std");
const allocator = std.heap.page_allocator;

const Direction = enum { Up, Down, Left, Right };
const Part2 = false;
const Pos = struct {
    x: i32,
    y: i32,
};

var moves = [4][2]i32{
    [2]i32{ 0, 1 },
    [2]i32{ 0, -1 },
    [2]i32{ -1, 0 },
    [2]i32{ 1, 0 },
};

const Cmd = struct {
    direction: Direction,
    len: i32,
    color: []const u8,
};

fn fill(grid: std.ArrayList(std.ArrayList(u8)), startX: i32, startY: i32, visited: std.ArrayList(std.ArrayList(bool))) void {
    const m = grid.items.len;
    const n = grid.items[0].items.len;

    var stack = std.ArrayList(Pos).init(allocator);
    defer stack.deinit();

    stack.append(Pos{ .x = startX, .y = startY }) catch unreachable;

    while (stack.items.len > 0) {
        const pos = stack.pop();

        if (pos.x < 0 or pos.x >= @as(i32, @intCast(m)) or pos.y < 0 or pos.y >= @as(i32, @intCast(n))) {
            continue;
        }

        const usize_x = @as(usize, @intCast(pos.x));
        const usize_y = @as(usize, @intCast(pos.y));

        if (visited.items[usize_x].items[usize_y]) {
            continue;
        }

        if (grid.items[usize_x].items[usize_y] != '.') {
            continue;
        }

        visited.items[usize_x].items[usize_y] = true;
        grid.items[usize_x].items[usize_y] = '@';

        stack.append(Pos{ .x = pos.x + 1, .y = pos.y }) catch unreachable;
        stack.append(Pos{ .x = pos.x - 1, .y = pos.y }) catch unreachable;
        stack.append(Pos{ .x = pos.x, .y = pos.y + 1 }) catch unreachable;
        stack.append(Pos{ .x = pos.x, .y = pos.y - 1 }) catch unreachable;
    }
}

pub fn main() !void {
    var file = try std.fs.cwd().openFile("input", .{});
    defer file.close();
    var reader = file.reader();
    var buffer: [1000]u8 = undefined;
    var commands = std.ArrayList(Cmd).init(allocator);
    defer commands.deinit();

    while (try reader.readUntilDelimiterOrEof(buffer[0..], '\n')) |line| {
        var tokenizer = std.mem.tokenize(u8, line, " ");
        var len: i32 = 0;
        var color: []const u8 = undefined;
        var direction: Direction = undefined;
        if (tokenizer.next()) |d_str| {
            if (std.mem.eql(u8, d_str, "U")) {
                direction = Direction.Up;
            } else if (std.mem.eql(u8, d_str, "D")) {
                direction = Direction.Down;
            } else if (std.mem.eql(u8, d_str, "R")) {
                direction = Direction.Right;
            } else if (std.mem.eql(u8, d_str, "L")) {
                direction = Direction.Left;
            } else {
                return error.Failure;
            }
        }
        if (tokenizer.next()) |len_str| {
            len = try std.fmt.parseInt(i32, len_str, 10);
        }
        if (tokenizer.next()) |color_str| {
            // part-2
            color = color_str[2 .. color_str.len - 1];
            if (Part2) {
                len = try std.fmt.parseInt(i32, color[0 .. color.len - 1], 16);
                var d_int: i32 = color[color.len - 1] - '0';
                if (d_int == 0) {
                    direction = Direction.Right;
                } else if (d_int == 1) {
                    direction = Direction.Down;
                } else if (d_int == 2) {
                    direction = Direction.Left;
                } else {
                    direction = Direction.Up;
                }
            }
        }
        std.debug.print("len={d}, direction={any}\n", .{ len, direction });
        try commands.append(Cmd{
            .direction = direction,
            .len = len,
            .color = color,
        });
        std.debug.print("{s}\n", .{line});
    }
    var min_x: i32 = 0;
    var max_x: i32 = 0;
    var min_y: i32 = 0;
    var max_y: i32 = 0;
    var x: i32 = 0;
    var y: i32 = 0;
    for (commands.items) |cmd| {
        switch (cmd.direction) {
            Direction.Up => {
                x = x - cmd.len;
            },
            Direction.Down => {
                x = x + cmd.len;
            },
            Direction.Left => {
                y = y - cmd.len;
            },
            Direction.Right => {
                y = y + cmd.len;
            },
        }
        min_x = @min(min_x, x);
        max_x = @max(max_x, x);
        min_y = @min(min_y, y);
        max_y = @max(max_y, y);
    }
    var m = @as(usize, @intCast(try std.math.absInt(min_x) + max_x + 1));
    var n = @as(usize, @intCast(try std.math.absInt(min_y) + max_y + 1));
    std.debug.print("m={d}, n={d}\n", .{ m, n });
    var grid = std.ArrayList(std.ArrayList(u8)).init(allocator);
    try grid.resize(m);
    defer {
        for (grid.items) |row| {
            row.deinit();
        }
        grid.deinit();
    }

    for (grid.items) |*row| {
        var elements = std.ArrayList(u8).init(allocator);
        try elements.resize(n);
        for (elements.items) |*e| {
            e.* = '.';
        }
        row.* = elements;
        //try grid.append(row);
    }

    x = min_x * -1;
    y = min_y * -1;
    std.debug.print("start [{d}, {d}]\n", .{ x, y });
    for (commands.items) |cmd| {
        switch (cmd.direction) {
            Direction.Up => {
                for (@as(usize, @intCast(x - cmd.len))..@as(usize, @intCast(x))) |i| {
                    grid.items[i].items[@as(usize, @intCast(y))] = '#';
                }
                x = x - cmd.len;
            },
            Direction.Down => {
                for (@as(usize, @intCast(x))..@as(usize, @intCast(x + cmd.len + 1))) |i| {
                    grid.items[i].items[@as(usize, @intCast(y))] = '#';
                }
                x = x + cmd.len;
            },
            Direction.Left => {
                for (@as(usize, @intCast(y - cmd.len))..@as(usize, @intCast(y))) |j| {
                    grid.items[@as(usize, @intCast(x))].items[j] = '#';
                }
                y = y - cmd.len;
            },
            Direction.Right => {
                for (@as(usize, @intCast(y))..@as(usize, @intCast(y + cmd.len + 1))) |j| {
                    grid.items[@as(usize, @intCast(x))].items[j] = '#';
                }
                y = y + cmd.len;
            },
        }
    }
    var visited = std.ArrayList(std.ArrayList(bool)).init(allocator);
    for (0..m) |_| {
        var row = std.ArrayList(bool).init(allocator);
        for (0..n) |_| {
            try row.append(false);
        }
        try visited.append(row);
    }

    for (0..m) |i| {
        fill(grid, @as(i32, @intCast(i)), 0, visited);
        fill(grid, @as(i32, @intCast(i)), @as(i32, @intCast(n - 1)), visited);
    }

    for (0..n) |j| {
        fill(grid, 0, @as(i32, @intCast(j)), visited);
        fill(grid, @as(i32, @intCast(m - 1)), @as(i32, @intCast(j)), visited);
    }
    var cubics: i64 = 0;
    for (0..m) |i| {
        for (0..n) |j| {
            if (grid.items[i].items[j] != '@') {
                cubics = cubics + 1;
            }
        }
    }
    std.debug.print("cubics={d}\n", .{cubics});

    // const output = try std.fs.cwd().openFile("output", .{ .mode = std.fs.File.OpenMode.write_only });
    // defer output.close();

    // for (grid.items) |innerList| {
    //     for (innerList.items) |byte| {
    //         try output.writeAll(&[_]u8{byte});
    //     }
    //     try output.writeAll("\n");
    // }
}
