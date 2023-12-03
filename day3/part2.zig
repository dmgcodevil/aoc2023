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

    // for (grid.items) |row| {
    //     print("{s}\n", .{row});
    // }

    const m = grid.items.len;
    const n = grid.items[0].len;

    print("rows={d}\n", .{m});
    print("columns={d}\n", .{n});
    var total: i32 = 0;
    var num: i32 = 0;
    var x_attached: usize = 0;
    var y_attached: usize = 0;
    var attached: bool = false;
    var connected = std.StringHashMap([2]i32).init(allocator);
    defer connected.deinit();
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
                        if (cell == '*') {
                            attached = true;
                            x_attached = x_usize;
                            y_attached = y_usize;
                        }
                    }
                }
                num = num * 10 + (ch - '0');
            } else {
                if (num != 0) {
                    if (attached) {
                        print("num={}, attached={}\n", .{ num, attached });
                        var keyBuf = try allocator.alloc(u8, 1000);
                        const key = try std.fmt.bufPrint(keyBuf[0..], "{d}-{d}", .{ x_attached, y_attached });
                        if (connected.get(key) == null) {
                            try connected.put(key, [2]i32{ 0, 0 });
                        }
                        var res = try connected.getOrPut(key);
                        if (res.value_ptr[0] == 0) {
                            res.value_ptr[0] = num;
                        } else if (res.value_ptr[1] == 0) {
                            res.value_ptr[1] = num;
                        }
                    }
                }
                attached = false;
                num = 0;
            }
        }
    }
    var it = connected.iterator();
    while (it.next()) |entry| {
        var a = entry.value_ptr[0];
        var b = entry.value_ptr[1];
        std.debug.print("Key: {s}, a: {}, b: {}\n", .{ entry.key_ptr.*, a, b });
        if (a != 0 and b != 0) {
            total = total + (a * b);
        }
    }

    for (grid.items) |line| {
        allocator.free(line);
    }

    print("total={}\n", .{total});
}
