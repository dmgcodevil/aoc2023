import java.util.*;

public class Day14 {

    enum Direction {
        North, West, South, East
    }

    public static void main(String[] args) {
        part1();
        part2();
    }

    static void part1() {
        List<char[][]> grids = Utils.readGrids("./day14/input");
        tilt(grids.get(0), Direction.North);
        int load = measureLoad(grids.get(0));
        System.out.println("part-1 answer = " + load);
    }

    static void part2() {
        List<char[][]> grids = Utils.readGrids("./day14/input");
        int load = tiltCycle(grids.get(0));
        System.out.println("part-2 answer = " + load);
    }

    static char[][] checksumToGrid(String checksum) {
        String[] split = checksum.split(";");
        char[][] grid = new char[split.length][];
        for (int i = 0; i < split.length; i++) {
            grid[i] = split[i].toCharArray();
        }
        return grid;
    }

    static int measureLoad(char[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        int load = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 'O') {
                    load += (m - i);
                }
            }
        }
        return load;
    }


    static int tiltCycle(char[][] grid) {
        int cycles = 1000000000;
        Set<String> seen = new HashSet<>();
        ArrayList<String> patterns = new ArrayList<>();
        Direction[] directions = new Direction[]{
                Direction.North,
                Direction.West,
                Direction.South,
                Direction.East
        };
        while (cycles-- > 0) {
            for (Direction direction : directions) {
                tilt(grid, direction);
            }
            String cs = checksum(grid);
            if (!seen.add(cs)) {
                int len = patterns.size() - patterns.indexOf(cs);
                int first = patterns.indexOf(cs) + 1;
                int n = cycles % len + first;
                return measureLoad(checksumToGrid(patterns.get(n - 1)));
            } else {
                patterns.add(cs);
            }
        }
        throw new RuntimeException("unreachable code");
    }

    static String checksum(char[][] grid) {
        StringBuilder builder = new StringBuilder();
        for (char[] chars : grid) {
            builder.append(new String(chars)).append(";");
        }
        return builder.toString();
    }

    static boolean tilt(char[][] grid, Direction direction) {
        int m = grid.length;
        int n = grid[0].length;
        boolean modified = false;
        switch (direction) {
            case North -> {
                for (int i = 1; i < m; i++) {
                    for (int j = 0; j < n; j++) {
                        if (grid[i][j] == 'O') {
                            int k = i;
                            while (k > 0 && grid[k - 1][j] == '.') {
                                modified = true;
                                grid[k][j] = '.';
                                grid[k - 1][j] = 'O';
                                k--;
                            }
                        }
                    }
                }
            }
            case South -> {
                for (int i = m - 2; i >= 0; i--) {
                    for (int j = 0; j < n; j++) {
                        if (grid[i][j] == 'O') {
                            int k = i;
                            while (k < m - 1 && grid[k + 1][j] == '.') {
                                modified = true;
                                grid[k][j] = '.';
                                grid[k + 1][j] = 'O';
                                k++;
                            }
                        }
                    }
                }
            }
            case West -> {
                for (int i = 0; i < m; i++) {
                    for (int j = 1; j < n; j++) {
                        if (grid[i][j] == 'O') {
                            int k = j;
                            while (k > 0 && grid[i][k - 1] == '.') {
                                modified = true;
                                grid[i][k] = '.';
                                grid[i][k - 1] = 'O';
                                k--;
                            }
                        }
                    }
                }
            }
            case East -> {
                for (int i = 0; i < m; i++) {
                    for (int j = n - 2; j >= 0; j--) {
                        if (grid[i][j] == 'O') {
                            int k = j;
                            while (k < n - 1 && grid[i][k + 1] == '.') {
                                modified = true;
                                grid[i][k] = '.';
                                grid[i][k + 1] = 'O';
                                k++;
                            }
                        }
                    }
                }

            }
        }
        return modified;

    }
}
