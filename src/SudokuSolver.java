import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class SudokuSolver {
    private int enumerations;
    private int drafts;
    private ArrayList<Integer>[] storage = new ArrayList[9];
    private int[][] grid;

    public SudokuSolver(int[][] grid) {
        this.grid = grid;
    }

    public void numberOfDrafts() {
        int sum = 0;
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                if (this.grid[x][y] == 0) {
                    sum += countDrafts(this.grid, x, y);
                }
            }
        }
        this.drafts = sum;
    }

    private int countDrafts(int[][] grid, int x, int y) {
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            if (grid[x][i] == 0)
                sum += 1;
            if (grid[i][y] == 0 && i != x)
                sum += 1;
        }

        for (int m = 0; m < 3; m++) {
            for (int n = 0; n < 3; n++) {
                int xPos = x / 3 * 3 + m;
                int yPos = y / 3 * 3 + n;

                if (xPos == x || yPos == y)
                    continue;
                else {
                    if (grid[xPos][yPos] == 0)
                        sum += 1;
                }
            }
        }

        return sum;
    }

    public void solveSudoku() {
        int[][] temp = new int[9][9];
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                temp[x][y] = this.grid[x][y];
            }
        }

        this.enumerations = 0;

        solve(temp);
    }

    private boolean solve(int[][] board) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] != 0) {
                    continue;
                }

                HashSet<Integer> seen = new HashSet<>();
                for (int xy = 0; xy < 9; xy++) {
                    if (board[i][xy] != 0)
                        seen.add(board[i][xy]);
                    if (board[xy][j] != 0)
                        seen.add(board[xy][j]);
                }

                for (int k = 1; k <= 9; k++) {
                    if (seen.contains(k))
                        continue;

                    board[i][j] = k;
                    this.enumerations += 1;
                    if (isValid(board, i, j) && solve(board)) {
                        return true;
                    }
                    board[i][j] = 0;
                }
                return false;
            }
        }
        return true;
    }

    private boolean isValid(int[][] board, int i, int j) {
        HashSet<Integer> set = new HashSet<>();

        for (int k = 0; k < 9; k++) {
            if (set.contains(board[i][k]))
                return false;

            if (board[i][k] != 0) {
                set.add(board[i][k]);
            }
        }

        set.clear();

        for (int k = 0; k < 9; k++) {
            if (set.contains(board[k][j]))
                return false;

            if (board[k][j] != 0) {
                set.add(board[k][j]);
            }
        }

        set.clear();

        for (int m = 0; m < 3; m++) {
            for (int n = 0; n < 3; n++) {
                int x = i / 3 * 3 + m;
                int y = j / 3 * 3 + n;
                if (set.contains(board[x][y]))
                    return false;

                if (board[x][y] != 0) {
                    set.add(board[x][y]);
                }
            }
        }

        return true;
    }

    private ArrayList<Integer> findRegion(int i) {
        int xPos = i % 9;
        int yPos = i / 9;
        int box_Y = yPos / 3;
        int box_X = xPos / 3;
        ArrayList<Integer> region = new ArrayList<>();

        switch (box_X + 3 * box_Y + 1) {
            case 1: {
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y <= 18; y += 9) {
                        region.add(x + y);
                    }
                }
                break;
            }
            case 2: {
                for (int x = 3; x < 6; x++) {
                    for (int y = 0; y <= 18; y += 9) {
                        region.add(x + y);
                    }
                }
                break;
            }
            case 3: {
                for (int x = 6; x < 9; x++) {
                    for (int y = 0; y <= 18; y += 9) {
                        region.add(x + y);
                    }
                }
                break;
            }
            case 4: {
                for (int x = 0; x < 3; x++) {
                    for (int y = 27; y <= (9 * 2) + 27; y += 9) {
                        region.add(x + y);
                    }
                }
                break;
            }
            case 5: {
                for (int x = 3; x < 6; x++) {
                    for (int y = 27; y <= (9 * 2) + 27; y += 9) {
                        region.add(x + y);
                    }
                }
                break;
            }
            case 6: {
                for (int x = 6; x < 9; x++) {
                    for (int y = 27; y <= 27 + (9 * 2); y += 9) {
                        region.add(x + y);
                    }
                }
                break;
            }
            case 7: {
                for (int x = 0; x < 3; x++) {
                    for (int y = 54; y <= 54 + 9 * 2; y += 9) {
                        region.add(x + y);
                    }
                }
                break;
            }
            case 8: {
                for (int x = 3; x < 6; x++) {
                    for (int y = 54; y <= 54 + (9 * 2); y += 9) {
                        region.add(x + y);
                    }
                }
                break;
            }
            case 9: {
                for (int x = 6; x < 9; x++) {
                    for (int y = 54; y <= 54 + (9 * 2); y += 9) {
                        region.add(x + y);
                    }
                }
                break;
            }
        }

        return region;
    }

    public void printStore() {
        for (int i = 0; i < 9; i++) {
            int n = i + 1;
            System.out.print("index: " + n + " locations: ");
            for (int m = 0; m < storage[i].size(); m++) {
                System.out.print("x: " + storage[i].get(m) % 9 + " y: " + storage[i].get(m) / 9 + ", ");
            }
            System.out.println();
        }
    }

    public void getDraftLocations() {
        for (int i = 0; i < 81; i++) {
            int x = i % 9;
            int y = i / 9;

            if (this.grid[x][y] != 0)
                continue;
            else {
                ArrayList<Integer> region = findRegion(i);
                ArrayList<Integer> numberChecker = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
                for (int index = 0; index < 9; index++) {
                    if (this.grid[x][index] != 0) {
                        if (numberChecker.contains(this.grid[x][index]))
                            numberChecker.remove(numberChecker.indexOf(this.grid[x][index]));
                    }

                    if (this.grid[index][y] != 0) {
                        if (numberChecker.contains(this.grid[index][y]))
                            numberChecker.remove(numberChecker.indexOf(this.grid[index][y]));
                    }

                    int xPos = region.get(index) % 9;
                    int yPos = region.get(index) / 9;

                    if (this.grid[xPos][yPos] != 0) {
                        if (numberChecker.contains(this.grid[xPos][yPos]))
                            numberChecker.remove(numberChecker.indexOf(this.grid[xPos][yPos]));
                    }
                }

                for (int index = 0; index < numberChecker.size(); index++) {
                    if (!storage[numberChecker.get(index) - 1].contains(i))
                        storage[numberChecker.get(index) - 1].add(i);
                }
            }
        }
    }

    public int getEnumerations() {
        return enumerations;
    }

    public int getDrafts() {
        return drafts;
    }
}
