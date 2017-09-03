import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/**
 * Created by Dylan on 2017-08-03.
 * Not my code taken from github.com/mfgravesjr to create Sudoku puzzle
 */

public class SudokuGenerator {
    private int[][] grid;
    private ArrayList<Integer> availibleToRemove = new ArrayList<>();
    private ArrayList<Integer> removed = new ArrayList<>();
    private ArrayList<Integer> remains = new ArrayList<>();
    private int[][] solution;
    private int enumerations;
    private int drafts;
    private ArrayList<Integer>[] storage = new ArrayList[9];

    public SudokuGenerator() {
        this.grid = generateGrid();
        for (int i = 0; i < 9; i++) {
            storage[i] = new ArrayList<Integer>();//
        }
    }

    /**
     * Tests an int array of length 81 to see if it is a valid Sudoku grid. i.e. 1 through 9 appearing once each in every row, column, and box
     *
     * @param grid an array with length 81 to be tested
     * @return a boolean representing if the grid is valid
     */
    private static boolean isPerfect(int[] grid) {
        if (grid.length != 81)
            throw new IllegalArgumentException("The grid must be a single-dimension grid of length 81");

        //tests to see if the grid is perfect

        //for every box
        for (int i = 0; i < 9; i++) {
            boolean[] registered = new boolean[10];
            registered[0] = true;
            int boxOrigin = (i * 3) % 9 + ((i * 3) / 9) * 27;
            for (int j = 0; j < 9; j++) {
                int boxStep = boxOrigin + (j / 3) * 9 + (j % 3);
                int boxNum = grid[boxStep];
                registered[boxNum] = true;
            }
            for (boolean b : registered)
                if (!b) return false;
        }

        //for every row
        for (int i = 0; i < 9; i++) {
            boolean[] registered = new boolean[10];
            registered[0] = true;
            int rowOrigin = i * 9;
            for (int j = 0; j < 9; j++) {
                int rowStep = rowOrigin + j;
                int rowNum = grid[rowStep];
                registered[rowNum] = true;
            }
            for (boolean b : registered)
                if (!b) return false;
        }

        //for every column
        for (int i = 0; i < 9; i++) {
            boolean[] registered = new boolean[10];
            registered[0] = true;
            int colOrigin = i;
            for (int j = 0; j < 9; j++) {
                int colStep = colOrigin + j * 9;
                int colNum = grid[colStep];
                registered[colNum] = true;
            }
            for (boolean b : registered)
                if (!b) return false;
        }

        return true;
    }

    private int[][] generateGrid() {
        ArrayList<Integer> arr = new ArrayList<Integer>(9);
        int[] grid = new int[81];
        for (int i = 1; i <= 9; i++) arr.add(i);

        //loads all boxes with numbers 1 through 9
        for (int i = 0; i < 81; i++) {
            if (i % 9 == 0) Collections.shuffle(arr);
            int perBox = ((i / 3) % 3) * 9 + ((i % 27) / 9) * 3 + (i / 27) * 27 + (i % 3);
            grid[perBox] = arr.get(i % 9);
        }

        //tracks rows and columns that have been sorted
        boolean[] sorted = new boolean[81];

        for (int i = 0; i < 9; i++) {
            boolean backtrack = false;
            //0 is row, 1 is column
            for (int a = 0; a < 2; a++) {
                //every number 1-9 that is encountered is registered
                boolean[] registered = new boolean[10]; //index 0 will intentionally be left empty since there are only number 1-9.
                int rowOrigin = i * 9;
                int colOrigin = i;

                ROW_COL:
                for (int j = 0; j < 9; j++) {
                    //row/column stepping - making sure numbers are only registered once and marking which cells have been sorted
                    int step = (a % 2 == 0 ? rowOrigin + j : colOrigin + j * 9);
                    int num = grid[step];

                    if (!registered[num]) registered[num] = true;
                    else //if duplicate in row/column
                    {
                        //box and adjacent-cell swap (BAS method)
                        //checks for either unregistered and unsorted candidates in same box,
                        //or unregistered and sorted candidates in the adjacent cells
                        for (int y = j; y >= 0; y--) {
                            int scan = (a % 2 == 0 ? i * 9 + y : i + 9 * y);
                            if (grid[scan] == num) {
                                //box stepping
                                for (int z = (a % 2 == 0 ? (i % 3 + 1) * 3 : 0); z < 9; z++) {
                                    if (a % 2 == 1 && z % 3 <= i % 3)
                                        continue;
                                    int boxOrigin = ((scan % 9) / 3) * 3 + (scan / 27) * 27;
                                    int boxStep = boxOrigin + (z / 3) * 9 + (z % 3);
                                    int boxNum = grid[boxStep];
                                    if ((!sorted[scan] && !sorted[boxStep] && !registered[boxNum])
                                            || (sorted[scan] && !registered[boxNum] && (a % 2 == 0 ? boxStep % 9 == scan % 9 : boxStep / 9 == scan / 9))) {
                                        grid[scan] = boxNum;
                                        grid[boxStep] = num;
                                        registered[boxNum] = true;
                                        continue ROW_COL;
                                    } else if (z == 8) //if z == 8, then break statement not reached: no candidates available
                                    {
                                        //Preferred adjacent swap (PAS)
                                        //Swaps x for y (preference on unregistered numbers), finds occurence of y
                                        //and swaps with z, etc. until an unregistered number has been found
                                        int searchingNo = num;

                                        //noting the location for the blindSwaps to prevent infinite loops.
                                        boolean[] blindSwapIndex = new boolean[81];

                                        //loop of size 18 to prevent infinite loops as well. Max of 18 swaps are possible.
                                        //at the end of this loop, if continue or break statements are not reached, then
                                        //fail-safe is executed called Advance and Backtrack Sort (ABS) which allows the
                                        //algorithm to continue sorting the next row and column before coming back.
                                        //Somehow, this fail-safe ensures success.
                                        for (int q = 0; q < 18; q++) {
                                            SWAP:
                                            for (int b = 0; b <= j; b++) {
                                                int pacing = (a % 2 == 0 ? rowOrigin + b : colOrigin + b * 9);
                                                if (grid[pacing] == searchingNo) {
                                                    int adjacentCell = -1;
                                                    int adjacentNo = -1;
                                                    int decrement = (a % 2 == 0 ? 9 : 1);

                                                    for (int c = 1; c < 3 - (i % 3); c++) {
                                                        adjacentCell = pacing + (a % 2 == 0 ? (c + 1) * 9 : c + 1);

                                                        //this creates the preference for swapping with unregistered numbers
                                                        if ((a % 2 == 0 && adjacentCell >= 81)
                                                                || (a % 2 == 1 && adjacentCell % 9 == 0))
                                                            adjacentCell -= decrement;
                                                        else {
                                                            adjacentNo = grid[adjacentCell];
                                                            if (i % 3 != 0
                                                                    || c != 1
                                                                    || blindSwapIndex[adjacentCell]
                                                                    || registered[adjacentNo])
                                                                adjacentCell -= decrement;
                                                        }
                                                        adjacentNo = grid[adjacentCell];

                                                        //as long as it hasn't been swapped before, swap it
                                                        if (!blindSwapIndex[adjacentCell]) {
                                                            blindSwapIndex[adjacentCell] = true;
                                                            grid[pacing] = adjacentNo;
                                                            grid[adjacentCell] = searchingNo;
                                                            searchingNo = adjacentNo;

                                                            if (!registered[adjacentNo]) {
                                                                registered[adjacentNo] = true;
                                                                continue ROW_COL;
                                                            }
                                                            break SWAP;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        //begin Advance and Backtrack Sort (ABS)
                                        backtrack = true;
                                        break ROW_COL;
                                    }
                                }
                            }
                        }
                    }
                }

                if (a % 2 == 0)
                    for (int j = 0; j < 9; j++) sorted[i * 9 + j] = true; //setting row as sorted
                else if (!backtrack)
                    for (int j = 0; j < 9; j++) sorted[i + j * 9] = true; //setting column as sorted
                else //reseting sorted cells through to the last iteration
                {
                    backtrack = false;
                    for (int j = 0; j < 9; j++) sorted[i * 9 + j] = false;
                    for (int j = 0; j < 9; j++) sorted[(i - 1) * 9 + j] = false;
                    for (int j = 0; j < 9; j++) sorted[i - 1 + j * 9] = false;
                    i -= 2;
                }
            }
        }

        if (!isPerfect(grid)) throw new RuntimeException("ERROR: Imperfect grid generated.");

        for (int i = 0; i < 81; i++)
            remains.add(i);

        this.solution = convertTo2D(grid);

        return this.solution;
    }

    private int[][] convertTo2D(int[] grid) {
        int[][] converted = new int[9][9];
        for (int i = 0; i < 81; i++) {
            int x = i % 9;
            int y = i / 9;

            availibleToRemove.add(i);
            converted[x][y] = grid[i];
        }
        return converted;
    }

    public void randomRemoval(int n) {
        while (n != 0) {
            Collections.shuffle(remains);
            int index = remains.get(0);
            this.remains.remove(0);

            int x = index % 9;
            int y = index / 9;

            if (grid[x][y] != 0) {
                this.grid[x][y] = 0;
                this.removed.add(index);
                n--;
            }
        }
    }

    public ArrayList<Integer> getAvailibleToRemove() {
        return availibleToRemove;
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

    public int getNumMissing() {
        return 81 - availibleToRemove.size();
    }

    public int getNumRemaing() {
        return availibleToRemove.size();
    }

    public int getDrafts() {
        return drafts;
    }

    public int[][] getGrid() {
        return this.grid;
    }
}