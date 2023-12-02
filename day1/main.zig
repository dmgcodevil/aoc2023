const std = @import("std");
const print = std.debug.print;
const allocator = std.heap.page_allocator;

pub fn main() !void {
    const file = try std.fs.cwd().openFile("input", .{});
    defer file.close();

    var reader = file.reader();
    var total: i32 = 0;
    var buf: [1024]u8 = undefined;
    while (true) {
        const bytesRead = try reader.readUntilDelimiterOrEof(buf[0..], '\n');
        if (bytesRead == null) break; // EOF reached

        // Process the line here
        print("{?s}\n", .{bytesRead});
        if (bytesRead) |slice| {
            var first: i32 = 0;
            var second: i32 = 0;
            for (slice) |byte| {
                if (std.ascii.isDigit(byte)) {
                    const digit = byte - '0';
                    if (first == 0) {
                        first = digit;
                    }
                    second = digit;
                }
            }
            total += first * 10 + second;
        }
    }
    print("total {d}\n", .{total});
}
