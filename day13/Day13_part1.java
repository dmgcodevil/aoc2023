import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Day13_part1 {

    public static void main(String[] args) throws IOException {
        solution();
    }

    private static void solution() throws IOException {
        List<String[]> grids = readInput("input");
        int summary = 0;
        for (String[] grid : grids) {
            int s = findReflectionSummary(grid);
            System.out.println(s);
            summary += s;
        }
        System.out.println("answer=" + summary);
    }


    private static List<String[]> readInput(String filename) throws IOException {
        List<String[]> grids = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        List<String> grid = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                grids.add(grid.toArray(new String[0]));
                grid.clear();
            } else {
                grid.add(line);
            }
        }
        if (!grid.isEmpty()) {
            grids.add(grid.toArray(new String[0]));
        }

        reader.close();
        return grids;
    }


    private static int findReflectionSummary(String[] grid) {
        int horizReflection = findHorizontalReflection(grid);
        int vertReflection = findVerticalReflection(grid);
        return vertReflection + 100 * horizReflection;
    }

    private static int findVerticalReflection(String[] grid) {
        String[] rotated = new String[grid[0].length()];
        for (int j = 0; j < grid[0].length(); j++) {
            StringBuilder builder = new StringBuilder(grid.length);
            for (int i = 0; i < grid.length; i++) {
                builder.append(grid[i].charAt(j));
            }
            rotated[j] = builder.toString();
        }

        return findHorizontalReflection(rotated);
    }

    private static int findHorizontalReflection(String[] grid) {
        int m = grid.length;
        if (m <= 1) return -1;
        int res = -1;
        for (int mid = 0; mid < m - 1; mid++) {
            if (isHorizontalMirror(grid, mid)) {
                res = mid + 1;
            }
        }
        return Math.max(res, 0);
    }


    private static boolean isHorizontalMirror(String[] grid, int row) {
        final int m = grid.length;
        // to find a max number of rows above the row we need to do row - 1
        // however row is 0 indexed we can simply use row
        // to find max number of rows below mirror we need to do: m - row - 1
        final int height = Math.min(row, m - row - 1);
        final int leftmost = row - height;
        final int rightmost = Math.min(row + height + 1, m - 1);
        int top = row;
        int bottom = row + 1;
        while (top >= leftmost && bottom <= rightmost) {
            if (!Objects.equals(grid[top], grid[bottom])) {
                return false;
            }
            top--;
            bottom++;
        }
        return true;
    }

}

