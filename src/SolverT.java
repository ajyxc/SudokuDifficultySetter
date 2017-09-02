import java.util.ArrayList;

/**
 * Created by apple on 2017-09-01.
 */
public class SolverT {
    ArrayList<Integer> possibilities = new ArrayList<Integer>();

    SolverT() {
        for (int i = 1; i < 10; i++) {
            possibilities.add(i);
        }
    }

    public static void main(String[] args) {

    }

    public ArrayList<Integer> possibleEntries(int[][] board, int i, int j) {
        ArrayList<Integer> possible = possibilities;


        //check horizontally
        for (int a = 0; a < 9; a++) {
            if (board[i][a] != 0) {
                possible.remove(Integer.valueOf(board[i][a]));
            }
        }

        //check vertically
        for (int a = 0; a < 9; a++) {
            if (board[a][j] != 0) {
                possible.remove(Integer.valueOf(board[a][j]));
            }
        }

        //check region
        int row = (int) i / 3;
        int col = (int) j / 3;

        for (int x = row * 3; x < row * 3 + 3; x++) {
            for (int y = col * 3; y < col * 3 + 3; y++) {
                if (board[x][y] != 0) {
                    possible.remove(Integer.valueOf(board[x][y]));
                }
            }
        }

        return possible;
    }

    public int[][] singleCandidate(int[][] board) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (possibleEntries(board, i, j).size() == 1) {
                    board[i][j] = (Integer) possibleEntries(board, i, j).get(0);
                }
            }
        }
        return board;
    }

    public int[][] singlePosition(int[][] board, int i, int j) {
        int row = (int) i / 3;
        int col = (int) j / 3;
        for (int a : possibleEntries(board, i, j)) {
            for (int y = 3 * row; y < 3 * row + 3; y++) {
                for (int x = 0) ;
            }

        }
    }

    public int[][] candidateLine(int[][] board, int i, int j) {
        //2 grid with only 2 same drafts
        if (possibleEntries(board, i, j).size() == 2) {
            //check that row
            int temp = 0;
            boolean found = false;
            for (int a = 0; a < 9; a++) {
                //if find another grid has exactly same draft as the given one,
                if (compareArrayList(possibleEntries(board, i, j), possibleEntries(board, i, a)) && a != j) {
                    temp = a;
                    found = true;
                    break;
                }
            }

            // if found, get rid of these draft from this line
            if (found) {
                int l = possibleEntries(board, i, j).get(0);
                int k = possibleEntries(board, i, j).get(1);
                for (int a = 0; a < 9; a++) {
                    if (a != temp && a != j) {
                        ArrayList<Integer> drafts = possibleEntries(board, i, a);
                        if (drafts.contains(l)) {
                            possibleEntries(board, i, a).remove(l);
                        }
                        if (drafts.contains(k)) {
                            possibleEntries(board, i, a).remove(k);
                        }
                    }
                }
            }

            //check that col
            int templ = 0;
            boolean foundl = false;
            for (int a = 0; a < 9; a++) {
                //if find another grid has exactly same draft as the given one,
                if (compareArrayList(possibleEntries(board, a, j), possibleEntries(board, i, j)) && a != i) {
                    templ = a;
                    foundl = true;
                    break;
                }
            }

            // if found, get rid of these draft from this line
            if (foundl) {
                int l = possibleEntries(board, i, j).get(0);
                int k = possibleEntries(board, i, j).get(1);
                for (int a = 0; a < 9; a++) {
                    if (a != templ && a != i) {
                        ArrayList<Integer> drafts = possibleEntries(board, a, j);
                        if (drafts.contains(l)) {
                            possibleEntries(board, a, j).remove(l);
                        }
                        if (drafts.contains(k)) {
                            possibleEntries(board, a, j).remove(k);
                        }
                    }
                }
            }

            //check that region
            //check that region
            boolean foundr = false;
            int row = (int)i/3;
            int col = (int)j/3;
            int tempr = 0,tempc = 0;
            for(int x = row*3; x < row*3+3; x++){
                for(int y = col*3; y < col*3+3; y++ ){
                    if(compareArrayList(possibleEntries(board,i,j),possibleEntries(board,x,y))){
                        foundr = true;
                        tempr = x;
                        tempc = y;
                        break;
                    }
                }
            }
            // if found, get rid of these draft from this region
            if(foundr){
                int l = possibleEntries(board, i, j).get(0);
                int k = possibleEntries(board, i, j).get(1);
                for(int x = row*3; x < row*3+3; x++) {
                    for(int y = col*3; y < col*3+3; y++ ){
                        if (x != tempr && y != tempc && x!=i && y!= j) {
                            ArrayList<Integer> drafts = possibleEntries(board, x, y);
                            if (drafts.contains(l)) {
                                possibleEntries(board, x, y).remove(l);
                            }
                            if (drafts.contains(k)) {
                                possibleEntries(board, x, y).remove(k);
                            }
                        }
                    }
                }
            }

        }
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
}
