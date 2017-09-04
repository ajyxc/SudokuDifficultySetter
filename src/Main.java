import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int totalLevel1 = 0;
        int Level1Count = 0;
        int totalLevel2 = 0;
        int Level2Count = 0;
        int totalLevel3 = 0;
        int Level3Count = 0;
        int totalLevel4 = 0;
        int Level4Count = 0;
        int totalLevel5 = 0;
        int Level5Count = 0;

        for (int i = 0; i < 100; i++) {
            int n = new Random().nextInt(5) + 1;
            SudokuGenerator test = new SudokuGenerator(n);
            SudokuSolver soln = new SudokuSolver(test.getGrid());
            switch (n) {
                case 1: {
                    if (soln.getEnumerations() < 100) {
                        Level1Count++;
                    }
                    totalLevel1++;
                    break;
                }
                case 2: {
                    if (soln.getEnumerations() > 100 && soln.getEnumerations() < 999)
                        Level2Count++;
                    totalLevel2++;
                    break;
                }
                case 3: {
                    if (soln.getEnumerations() > 999 && soln.getEnumerations() < 9999)
                        Level3Count++;
                    totalLevel3++;
                    break;
                }
                case 4: {
                    if (soln.getEnumerations() > 9999 && soln.getEnumerations() < 99999)
                        Level4Count++;
                    totalLevel4++;
                    break;
                }
                case 5: {
                    if (soln.getEnumerations() > 99999)
                        Level5Count++;
                    totalLevel5++;
                    break;
                }
            }
        }
        System.out.println((double) Level1Count / totalLevel1);
        System.out.println((double) Level2Count / totalLevel2);
        System.out.println((double) Level3Count / totalLevel3);
        System.out.println((double) Level4Count / totalLevel4);
        System.out.println((double) Level5Count / totalLevel5);
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
