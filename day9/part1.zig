const std = @import("std");
const print = std.debug.print;
const allocator = std.heap.page_allocator;

pub fn main() !void {
    const file = try std.fs.cwd().openFile("input", .{});
    defer file.close();

    var reader = file.reader();
    var part1_answer: i64 = 0;
    var part2_answer: i64 = 0;
    var buf: [1024]u8 = undefined;
    while (true) {
        const bytesRead = try reader.readUntilDelimiterOrEof(buf[0..], '\n');
        if (bytesRead == null) break; // EOF reached

        //print("{?s}\n", .{bytesRead});
        if (bytesRead) |slice| {
            var tokenizer = std.mem.tokenize(u8, slice, " ");
            var history = std.ArrayList(i64).init(allocator);
            while (tokenizer.next()) |token| {
                const n = try std.fmt.parseInt(i64, token, 10);
                try history.append(n);
            }
            var result = try extrapolate(history.items);
            part1_answer = part1_answer + result[1];
            part2_answer = part2_answer + result[0];
        }
    }
    print("part1={d}\n", .{part1_answer});
    print("part2={d}\n", .{part2_answer});
}

fn extrapolate(history: []i64) ![2]i64 {
    var forward_result: i64 = 0;
    var backward_result: i64 = 0;
    var forward = std.ArrayList(i64).init(allocator);
    var backward = std.ArrayList(i64).init(allocator);
    defer forward.deinit();
    defer backward.deinit();
    try forward.append(history[history.len - 1]);
    try backward.append(history[0]);
    var cur = std.ArrayList(i64).init(allocator);
    try cur.appendSlice(history);
    //print("cur.len={d}\n", .{cur.items.len});
    while (true) {
        var tmp = std.ArrayList(i64).init(allocator);
        defer tmp.deinit();
        for (1..cur.items.len) |i| {
            // print("append{d}\n", .{cur.items[i] - cur.items[i - 1]});
            try tmp.append(cur.items[i] - cur.items[i - 1]);
        }
        var zeros: i32 = 0;
        for (tmp.items) |value| {
            if (value == 0) {
                zeros = zeros + 1;
            }
        }
        // print("tmp.len={d}\n", .{tmp.items.len});
        if (zeros == tmp.items.len) break;
        cur.clearAndFree();
        for (tmp.items) |value| {
            try cur.append(value);
        }
        try forward.append(cur.items[cur.items.len - 1]);
        try backward.append(cur.items[0]);
    }
    for (forward.items) |n| {
        forward_result = forward_result + n;
    }
    var i = backward.items.len;
    while (i >= 1) {
        backward_result = backward.items[i - 1] - backward_result;
        i = i - 1;
    }
    return [2]i64{ backward_result, forward_result };
}
