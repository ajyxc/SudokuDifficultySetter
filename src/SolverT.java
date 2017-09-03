import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by apple on 2017-09-01.
 */
public class SolverT {
    private ArrayList<Integer> possibilities = new ArrayList<Integer>();
    private ArrayList<Integer>[][] draftGrid = new ArrayList[9][9];
    private int[][] grid;

    SolverT(int[][] grid) {
        for (int i = 1; i < 10; i++) {
            possibilities.add(i);
        }
        this.grid = grid;
    }

    public ArrayList<Integer> possibleEntries(int i, int j) {
        ArrayList<Integer> possible = possibilities;

        //check horizontally
        for (int a = 0; a < 9; a++) {
            if (grid[i][a] != 0) {
                possible.remove(Integer.valueOf(grid[i][a]));
            }
        }

        //check vertically
        for (int a = 0; a < 9; a++) {
            if (grid[a][j] != 0) {
                possible.remove(Integer.valueOf(grid[a][j]));
            }
        }

        //check region
        int row = (int) i / 3;
        int col = (int) j / 3;

        for (int x = row * 3; x < row * 3 + 3; x++) {
            for (int y = col * 3; y < col * 3 + 3; y++) {
                if (grid[x][y] != 0) {
                    possible.remove(Integer.valueOf(grid[x][y]));
                }
            }
        }

        draftGrid[i][j] = possible;

        return possible;
    }

    public int[][] singleCandidate(int[][] board) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (possibleEntries(i, j).size() == 1) {
                    board[i][j] = (Integer) possibleEntries(i, j).get(0);
                }
            }
        }
        return board;
    }

    public void singlePosition(int i, int j) {
        ArrayList<Integer> numbersInRow = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        for (int x = 0; x < 9; x++) {
            if (grid[x][j] != 0)
                numbersInRow.remove(Integer.valueOf(grid[x][j]));
        }
    }

    public int[][] candidateLine(int[][] board, int i, int j) {
        //2 grid with only 2 same drafts
        if (possibleEntries(i, j).size() == 2) {
            //check that row
            int temp = 0;
            boolean found = false;
            for (int a = 0; a < 9; a++) {
                //if find another grid has exactly same draft as the given one,
                if (compareArrayList(possibleEntries(i, j), possibleEntries(i, a)) && a != j) {
                    temp = a;
                    found = true;
                    break;
                }
            }

            // if found, get rid of these draft from this line
            if (found) {
                int l = possibleEntries(i, j).get(0);
                int k = possibleEntries(i, j).get(1);
                for (int a = 0; a < 9; a++) {
                    if (a != temp && a != j) {
                        ArrayList<Integer> drafts = possibleEntries(i, a);
                        if (drafts.contains(l)) {
                            possibleEntries(i, a).remove(l);
                        }
                        if (drafts.contains(k)) {
                            possibleEntries(i, a).remove(k);
                        }
                    }
                }
            }

            //check that col
            int templ = 0;
            boolean foundl = false;
            for (int a = 0; a < 9; a++) {
                //if find another grid has exactly same draft as the given one,
                if (compareArrayList(possibleEntries(a, j), possibleEntries(i, j)) && a != i) {
                    templ = a;
                    foundl = true;
                    break;
                }
            }

            // if found, get rid of these draft from this line
            if (foundl) {
                int l = possibleEntries(i, j).get(0);
                int k = possibleEntries(i, j).get(1);
                for (int a = 0; a < 9; a++) {
                    if (a != templ && a != i) {
                        ArrayList<Integer> drafts = possibleEntries(a, j);
                        if (drafts.contains(l)) {
                            possibleEntries(a, j).remove(l);
                        }
                        if (drafts.contains(k)) {
                            possibleEntries(a, j).remove(k);
                        }
                    }
                }
            }


            //check that region
            boolean foundr = false;
            int row = (int)i/3;
            int col = (int)j/3;
            int tempr = 0,tempc = 0;
            for(int x = row*3; x < row*3+3; x++){
                for(int y = col*3; y < col*3+3; y++ ){
                    if (compareArrayList(possibleEntries(i, j), possibleEntries(x, y))) {
                        foundr = true;
                        tempr = x;
                        tempc = y;
                        break;
                    }
                }
            }
            // if found, get rid of these draft from this region
            //from line if possible
            if(foundr){
                int l = possibleEntries(i, j).get(0);
                int k = possibleEntries(i, j).get(1);
                //get rid of these draft from the region
                for(int x = row*3; x < row*3+3; x++) {
                    for(int y = col*3; y < col*3+3; y++ ){
                        if (x != tempr && y != tempc && x!=i && y!= j) {
                            ArrayList<Integer> drafts = possibleEntries(x, y);
                            if (drafts.contains(l)) {
                                possibleEntries(x, y).remove(l);
                            }
                            if (drafts.contains(k)) {
                                possibleEntries(x, y).remove(k);
                            }
                        }
                    }
                }
                //get rid of these draft from the line if possible
                //from the horizontal line
                if(tempr == i){
                    for(int x =0; x<9; x++){
                        if (x != tempr && x != j) {
                            ArrayList<Integer> drafts = possibleEntries(i, x);
                            if (drafts.contains(l)) {
                                possibleEntries(i, x).remove(l);
                            }
                            if (drafts.contains(k)) {
                                possibleEntries(i, x).remove(k);
                            }
                        }
                    }
                }
                //from the vertical line
                if(tempc == j){
                    for (int a = 0; a < 9; a++) {
                        if (a != tempc && a != i) {
                            ArrayList<Integer> drafts = possibleEntries(a, j);
                            if (drafts.contains(l)) {
                                possibleEntries(a, j).remove(l);
                            }
                            if (drafts.contains(k)) {
                                possibleEntries(a, j).remove(k);
                            }
                        }
                    }
                }
            }

        }
        return board;
    }

    public boolean compareArrayList(ArrayList<Integer> a, ArrayList<Integer> b) {
        if (a.size() != b.size()) {
            return false;
        }

        for (int i : a) {
            if (!b.contains(i)) {
                return false;
            }
        }
        return true;
    }

    private void findRegion(int xPos, int yPos, ArrayList<Integer> a) {
        int box_Y = yPos / 3;
        int box_X = xPos / 3;

        switch (box_X + 3 * box_Y + 1) {
            case 1: {
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        a.remove(Integer.valueOf(grid[x][y]));
                    }
                }
                break;
            }
            case 2: {
                for (int x = 3; x < 6; x++) {
                    for (int y = 0; y < 3; y++) {
                        a.remove(Integer.valueOf(grid[x][y]));
                    }
                }
                break;
            }
            case 3: {
                for (int x = 6; x < 9; x++) {
                    for (int y = 0; y <= 18; y += 9) {
                        a.remove(Integer.valueOf(grid[x][y]));
                    }
                }
                break;
            }
            case 4: {
                for (int x = 0; x < 3; x++) {
                    for (int y = 27; y <= (9 * 2) + 27; y += 9) {
                        a.remove(Integer.valueOf(grid[x][y]));
                    }
                }
                break;
            }
            case 5: {
                for (int x = 3; x < 6; x++) {
                    for (int y = 27; y <= (9 * 2) + 27; y += 9) {
                        a.remove(Integer.valueOf(grid[x][y]));
                    }
                }
                break;
            }
            case 6: {
                for (int x = 6; x < 9; x++) {
                    for (int y = 27; y <= 27 + (9 * 2); y += 9) {
                        a.remove(Integer.valueOf(grid[x][y]));
                    }
                }
                break;
            }
            case 7: {
                for (int x = 0; x < 3; x++) {
                    for (int y = 54; y <= 54 + 9 * 2; y += 9) {
                        a.remove(Integer.valueOf(grid[x][y]));
                    }
                }
                break;
            }
            case 8: {
                for (int x = 3; x < 6; x++) {
                    for (int y = 54; y <= 54 + (9 * 2); y += 9) {
                        a.remove(Integer.valueOf(grid[x][y]));
                    }
                }
                break;
            }
            case 9: {
                for (int x = 6; x < 9; x++) {
                    for (int y = 54; y <= 54 + (9 * 2); y += 9) {
                        a.remove(Integer.valueOf(grid[x][y]));
                    }
                }
                break;
            }
        }
    }
}
