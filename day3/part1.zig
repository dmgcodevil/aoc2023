const std = @import("std");
const print = std.debug.print;
const allocator = std.heap.page_allocator;

pub fn main() !void {
    const directions = [8][2]i32{
        [2]i32{ -1, 0 }, // Up
        [2]i32{ 1, 0 }, // Down
        [2]i32{ 0, -1 }, // Left
        [2]i32{ 0, 1 }, // Right
        [2]i32{ -1, -1 }, // Upper-left
        [2]i32{ -1, 1 }, // Upper-right
        [2]i32{ 1, -1 }, // Lower-left
        [2]i32{ 1, 1 }, // Lower-right
    };

    //print("lol={d}", .{directions[0][0]});
    const file = try std.fs.cwd().openFile("input", .{});
    defer file.close();

    var reader = file.reader();
    //var total: i32 = 0;
    var grid = std.ArrayList([]const u8).init(std.heap.page_allocator);
    defer grid.deinit();
    var buf: [2048]u8 = undefined;
    while (true) {
        const bytesRead = try reader.readUntilDelimiterOrEof(buf[0..], '\n');
        if (bytesRead == null) break; // EOF reached

        if (bytesRead) |line| {
            //print("{?s}\n", .{line});
            var lineCopy = try allocator.dupe(u8, line);
            try grid.append(lineCopy);
        }
    }
    for (grid.items) |row| {
        print("{s}\n", .{row});
    }

    const m = grid.items.len;
    const n = grid.items[0].len;

    print("rows={d}\n", .{m});
    print("columns={d}\n", .{n});
    var total: i32 = 0;
    var num: i32 = 0;
    var attached: bool = false;
    for (grid.items, 0..) |row, i| {
        for (row, 0..) |ch, j| {
            if (std.ascii.isDigit(ch)) {
                for (directions) |d| {
                    const x = @as(i32, @intCast(i)) + d[0];
                    const y = @as(i32, @intCast(j)) + d[1];
                    if (x >= 0 and x < m and y >= 0 and y < n) {
                        const x_usize = @as(usize, @intCast(x));
                        const y_usize = @as(usize, @intCast(y));
                        const cell = grid.items[x_usize][y_usize];
                        if (!std.ascii.isDigit(cell) and cell != '.') {
                            attached = true;
                        }
                    }
                }

                num = num * 10 + (ch - '0');
            } else {
                if (num != 0) {
                    //print("num={}, attached={}\n", .{ num, attached });
                    if (attached) {
                        total += num;
                    }
                }
                attached = false;
                num = 0;
            }
        }
    }
    // last number grid[m-1][n-1]
    if (num != 0) {
        //print("num={}, attached={}\n", .{ num, attached });
        if (attached) {
            total += num;
        }
    }
    for (grid.items) |line| {
        allocator.free(line);
    }

    print("total={}\n", .{total});
}
