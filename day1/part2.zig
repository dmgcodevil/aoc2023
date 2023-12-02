const std = @import("std");
const print = std.debug.print;
const allocator = std.heap.page_allocator;

const words = [_][]const u8{ "one", "two", "three", "four", "five", "six", "seven", "eight", "nine" };

pub fn concatenateIntArray(array: []const i32) ![]u8 {
    var result = std.ArrayList(u8).init(allocator);

    for (array) |item| {
        // Convert each integer to a string
        var str = try std.fmt.allocPrint(allocator, "{},", .{item});
        defer allocator.free(str);

        // Append this string to the result
        try result.appendSlice(str);
    }

    return result.toOwnedSlice();
}

pub fn main() !void {
    const file = try std.fs.cwd().openFile("input", .{});
    defer file.close();
    var reader = file.reader();
    var total: i32 = 0;

    var buf: [1024]u8 = undefined;
    while (true) {
        const bytesRead = try reader.readUntilDelimiterOrEof(buf[0..], '\n');
        if (bytesRead == null) break; // EOF reached

        if (bytesRead) |line| {
            var numbers = std.ArrayList(i32).init(std.heap.page_allocator);
            defer numbers.deinit();
            var i: usize = 0;
            //print("line={s}", .{line});
            while (i < line.len) {
                const ch: u8 = line[i];
                if (std.ascii.isDigit(ch)) {
                    const digit = ch - '0';
                    try numbers.append(digit);
                    //print("found digit={d}\n", .{digit});
                    i = i + 1;
                } else {
                    for (words, 0..) |word, j| {
                        if (word[0] == ch and (i + word.len <= line.len) and std.mem.eql(u8, line[i .. i + word.len], word)) {
                            //print("found number {s}\n", .{line[i .. i + word.len]});
                            try numbers.append(@as(i32, @intCast(j)) + 1);
                            i = i + word.len - 1;
                            //print("pitr\n", .{});
                        }
                    } else {
                        i = i + 1;
                    }
                }
                //print("numbers {!s}\n", .{concatenateIntArray(numbers.items)});
            }
            //print("first={d}\n", .{numbers.items[0]});
            //print("last={d}\n", .{numbers.items[numbers.items.len - 1]});
            total += (numbers.items[0] * 10 + numbers.items[numbers.items.len - 1]);
        }
    }

    print("total {d}\n", .{total});
}
