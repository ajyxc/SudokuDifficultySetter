import java.util.Random;

public class Main {
    public static void main(String[] args) {
        SudokuGenerator test = new SudokuGenerator();
        int n = new Random().nextInt(59);
        test.randomRemoval(n);
        test.getDraftLocations();
        test.printStore();
        printGrid(test.getGrid());
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
