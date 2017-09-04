import java.util.Random;

public class Main {
    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            int n = new Random().nextInt(4) + 1;
            SudokuGenerator test = new SudokuGenerator(n);
            SudokuSolver soln = new SudokuSolver(test.getGrid());
            System.out.println(soln.getEnumerations());
        }
    }

    public static void printGrid(int[][] grid) {
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                if (grid[x][y] == 0)
                    System.out.print("[" + grid[x][y] + "]");
                else
                    System.out.print("[" + grid[x][y] + "]");
            }

            System.out.println();
        }
        System.out.println();
    }
}
