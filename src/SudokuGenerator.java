import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Dylan on 2017-08-03.
 * Not my code taken from github.com/mfgravesjr to create Sudoku puzzle
 */

public class SudokuGenerator {
    private int[][] grid;
    private ArrayList<Integer> removed = new ArrayList<>();
    private ArrayList<Integer> remains = new ArrayList<>();
    private int[][] solution;

    public SudokuGenerator(int level) {
        this.grid = generateGrid();

        switch (level) {
            case 1: {
                int n = new Random().nextInt(30) + 20;
                randomRemoval(n);
                lowestBoundGenerator(5);
                break;
            }
            case 2: {
                int n = new Random().nextInt(13) + 32;
                randomRemoval(n);
                lowestBoundGenerator(4);
                break;
            }
            case 3: {
                int n = new Random().nextInt(3) + 46;
                randomRemoval(n);
                lowestBoundGenerator(3);
                break;
            }
            case 4: {
                int n = new Random().nextInt(3) + 50;
                randomRemoval(n);
                lowestBoundGenerator(2);
                break;
            }
            case 5: {
                int n = new Random().nextInt(4) + 54;
                randomRemoval(n);
                lowestBoundGenerator(0);
                break;
            }
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

            converted[x][y] = grid[i];
        }
        return converted;
    }

    private void randomRemoval(int n) {
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

    public void lowestBoundGenerator(int lowest) {
        boolean lowestBoundExist = false;
        for (int x = 0; x < 9; x++) {
            int rowCount = 0;
            int colCount = 0;

            for (int y = 0; y < 9; y++) {
                if (grid[x][y] == 0)
                    rowCount++;
                if (grid[y][x] == 0)
                    colCount++;
            }

            if (rowCount == lowest || colCount == lowest)
                lowestBoundExist = true;

            if (rowCount < lowest) {
                while (rowCount < lowest) {
                    int r = new Random().nextInt(8);
                    if (grid[x][r] == 0) {
                        grid[x][r] = solution[x][r];
                        rowCount++;
                    }
                }
                lowestBoundExist = true;
            }

            if (colCount < lowest) {
                while (colCount < lowest) {
                    int r = new Random().nextInt(8);
                    if (grid[r][x] == 0) {
                        grid[r][x] = solution[r][x];
                        colCount++;
                    }
                }
                lowestBoundExist = true;
            }
        }

        if (!lowestBoundExist) {
            int randomRowOrColumn = new Random().nextInt(8);
            int rowOrColumn = new Random().nextInt(1);
            ArrayList<Integer> availibleIndexes = new ArrayList<>();

            if (rowOrColumn == 0) {
                for (int i = 0; i < 9; i++) {
                    if (grid[randomRowOrColumn][i] != 0)
                        availibleIndexes.add(i);
                }
                while (availibleIndexes.size() > lowest) {
                    Collections.shuffle(availibleIndexes);
                    int i = availibleIndexes.get(0);
                    availibleIndexes.remove(0);
                    grid[randomRowOrColumn][i] = 0;
                }
            } else {
                for (int i = 0; i < 9; i++) {
                    if (grid[i][randomRowOrColumn] != 0)
                        availibleIndexes.add(i);
                }
                while (availibleIndexes.size() > lowest) {
                    Collections.shuffle(availibleIndexes);
                    int i = availibleIndexes.get(0);
                    availibleIndexes.remove(0);
                    grid[i][randomRowOrColumn] = 0;
                }
            }
        }
    }

    public int[][] getGrid() {
        return this.grid;
    }

    public ArrayList<Integer> getRemoved() {
        return this.removed;
    }

    public ArrayList<Integer> getRemains() {
        return this.remains;
    }
}