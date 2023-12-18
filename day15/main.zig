const std = @import("std");
const print = std.debug.print;
const allocator = std.heap.page_allocator;

const Node = struct {
    prev: ?*Node,
    next: ?*Node,
    label: []const u8,
    focal_len: u32,

    pub fn init(label: []const u8, focal_len: u32) Node {
        return Node{
            .prev = null,
            .next = null,
            .label = label,
            .focal_len = focal_len,
        };
    }
};

const Box = struct {
    head: *Node,
    tail: *Node,
    index: std.StringHashMap(*Node),
    size: u32 = 0,

    pub fn init() !Box {
        var dummy = try allocator.create(Node);
        dummy.label = "dummy";
        dummy.next = null;
        dummy.prev = null;
        return Box{ .head = dummy, .tail = dummy, .index = std.StringHashMap(*Node).init(allocator) };
    }

    pub fn insert(self: *Box, label: []const u8, focal_len: u32) !void {
        if (self.index.get(label)) |n| {
            n.focal_len = focal_len;
        } else {
            var n = try allocator.create(Node);
            n.label = label;
            n.focal_len = focal_len;
            n.next = null;
            n.prev = self.tail;
            self.tail.next = n;
            self.tail = n;
            self.size = self.size + 1;
            try self.index.putNoClobber(label, n);
        }
    }

    pub fn remove(self: *Box, label: []const u8) void {
        if (self.index.get(label)) |node| {
            if (node.prev) |prevNode| {
                prevNode.next = node.next;
            } else if (node.next) |n| {
                self.head = n; // if node is the first element
            }

            if (node.next) |nextNode| {
                nextNode.prev = node.prev;
            } else if (node.prev) |n| {
                self.tail = n; // if node is the last element
            }
            node.prev = null;
            node.next = null;
            allocator.destroy(node);
            self.size = self.size - 1;
            _ = self.index.remove(label);
        }
    }

    pub fn print_nodes(self: Box) void {
        var cur = self.head.next;
        while (cur) |n| {
            std.debug.print(">>> label={s}, len={d}\n", .{ n.label, n.focal_len });
            cur = n.next;
        }
    }

    pub fn deinit(self: *Box) void {
        var cur = self.head.next;
        while (cur) |n| {
            cur = n.next;
            n.prev = null;
            n.next = null;
            allocator.destroy(n);
        }
        self.index.deinit();
    }
};

fn hash(str: []const u8) u32 {
    var h: u32 = 0;

    for (str) |ch| {
        const code: u32 = @intCast(ch);
        h = h + code;
        h = h * 17;
        h = h % 256;
    }

    return h;
}

pub fn main() !void {
    const file = try std.fs.cwd().openFile("input", .{});
    defer file.close();
    var reader = file.reader();
    var buf: [100000]u8 = undefined;
    const bytesRead = try reader.readUntilDelimiterOrEof(buf[0..], '\n');
    var boxes: [257]Box = undefined;

    // Initialize each Box in the array
    for (&boxes) |*box| {
        box.* = try Box.init();
    }
    var i: usize = 0;
    while (i <= 256) {
        boxes[i] = try Box.init();
        i += 1;
    }
    if (bytesRead) |input| {
        //print("{s}\n", .{input});
        var tokenizer = std.mem.tokenize(u8, input, ",");
        var answer: u32 = 0;
        while (tokenizer.next()) |init_token| {
            var set_op_index = std.mem.indexOf(u8, init_token, "=");
            var rm_op_index = std.mem.indexOf(u8, init_token, "-");
            if (set_op_index) |idx| {
                var label = init_token[0..idx];
                var focal_len = try std.fmt.parseUnsigned(u32, init_token[idx + 1 ..], 10); // u32(init_token[idx + 1] - '0');
                var h = hash(label);
                try boxes[h].insert(label, focal_len);
                //std.debug.print("{s}={d}, hashcode={d}\n", .{ label, focal_len, h });
            } else if (rm_op_index) |idx| {
                var label = init_token[0..idx];
                var h = hash(label);
                boxes[h].remove(label);
                //std.debug.print("{s}-\n", .{label});
            } else {
                return error.SomeError;
            }
        }
        for (boxes, 0..) |box, idx| {
            if (box.size > 0) {
                var res: u32 = 0;
                var j: u32 = 1;
                var cur = box.head.next;
                while (cur) |node| {
                    res = res + node.focal_len * j * @as(u32, @intCast(idx + 1));
                    cur = node.next;
                    j = j + 1;
                }
                //std.debug.print("box-{d} = {d}\n", .{ idx, res });
                //box.print_nodes();
                answer = answer + res;
            }
        }
        for (&boxes) |*box| {
            box.deinit();
        }
        std.debug.print("answer={d}\n", .{answer});
    }
}
