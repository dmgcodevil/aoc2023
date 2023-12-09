import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File("input");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        String moves = line;
        br.readLine();
        Map<String, String[]> routes = new LinkedHashMap<>();
        String start = null;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(" = ");
            String location = parts[0];
            if (start == null) start = location;
            String[] d = parts[1].substring(1, parts[1].length() - 1).split(", ");
            routes.put(location, d);
        }

        long part1Answer = part1(moves, routes);
        long part2Answer = part2(moves, routes);
        System.out.println("part1 answer=" + part1Answer);
        System.out.println("part2 answer=" + part2Answer);
    }

    static int part1(String moves, Map<String, String[]> routes) {
        int i = 0;
        int steps = 0;
        String cur = "AAA";
        while (!cur.equals("ZZZ")) {
            switch (moves.charAt(i)) {
                case 'L':
                    cur = routes.get(cur)[0];
                    break;
                case 'R':
                    cur = routes.get(cur)[1];
                    break;
            }
            steps++;
            i++;
            i = i % moves.length();
        }
        return steps;
    }

    static long part2(String moves, Map<String, String[]> routes) {
        Queue<String> queue = new LinkedList<>();
        Map<String, Integer> zNodes = new HashMap<>();
        for (String s : routes.keySet()) {
            if (s.charAt(2) == 'A') {
                queue.add(s);
            }
        }
        int startNodesCount = queue.size();
        int i = 0;
        int steps = 0;
        while (zNodes.size() != startNodesCount) {
            int size = queue.size();
            while (size-- > 0) {
                String curr = queue.poll();
                if (curr.charAt(2) == 'Z' && !zNodes.containsKey(curr)) {
                    zNodes.put(curr, steps);
                } else {
                    String next = null;
                    String[] nodes = routes.get(curr);
                    switch (moves.charAt(i)) {
                        case 'L':
                            next = nodes[0];
                            break;
                        case 'R':
                            next = nodes[1];
                            break;
                    }
                    queue.add(next);
                }
            }
            steps++;
            i++;
            i = i % moves.length();
        }
        Iterator<Integer> it = zNodes.values().iterator();
        long ans = it.next();
        while (it.hasNext()) {
            ans = lcm(ans, it.next());
        }
        return ans;
    }

    public static long lcm(long a, long b) {
        return a * (b / gcd(a, b));
    }

    public static long gcd(long a, long b) {
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}