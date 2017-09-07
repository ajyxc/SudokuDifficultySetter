import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private ArrayList<Integer> possibleEntries(int i, int j) {
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

    public boolean singleCandidate(int x, int y) {
        ArrayList<Integer> pos = possibleEntries(x, y);

        if (pos.size() == 1) {
            grid[x][y] = pos.get(0);
            return true;
        }

        return false;
    }

    public boolean singlePosition(int x, int y, int rowColRegion) {
        if (rowColRegion == 1) {
            for (int index = 0; index < 9; index++) {
                if (grid[x][index] != 0) {
                    if (singleCandidate(x, index))
                        return true;
                }
            }
        } else if (rowColRegion == 2) {
            for (int index = 0; index < 9; index++) {
                if (grid[index][y] != 0) {
                    if (singleCandidate(index, y))
                        return true;
                }
            }
        } else {
            ArrayList<Integer> nums = possibilities;
            findRegion(x, y, possibilities);

            if (nums.size() == 1) {
                grid[x][y] = nums.get(0);
                return true;
            }
        }
        return false;
    }


    //naked pairs
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
            int row = (int) i / 3;
            int col = (int) j / 3;
            int tempr = 0, tempc = 0;
            for (int x = row * 3; x < row * 3 + 3; x++) {
                for (int y = col * 3; y < col * 3 + 3; y++) {
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
            if (foundr) {
                int l = possibleEntries(i, j).get(0);
                int k = possibleEntries(i, j).get(1);
                //get rid of these draft from the region
                for (int x = row * 3; x < row * 3 + 3; x++) {
                    for (int y = col * 3; y < col * 3 + 3; y++) {
                        if (x != tempr && y != tempc && x != i && y != j) {
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
                if (tempr == i) {
                    for (int x = 0; x < 9; x++) {
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
                if (tempc == j) {
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


    //need to check row, column, region
    //3 cases: 1: 12,13,123; 13,23,123; 12,23,123
    //           2: 123,123,12; 123,123,13; 123,123,23
    //              3:123,123,123
    //naked triple
    public int[][] nakedTriples(int[][] board) {
        //check row
        //case 3
        //todo: not done
        boolean found = false;
        List<Integer> samedraft = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (draftGrid[i][j].size() == 3) {
                    samedraft = draftGrid[i][j];
                }
            }
        }
        case1();

        case2();
        return board;
    }

    private void case2() {

        boolean foundFirst = false;
        boolean foundSecond = false;
        boolean foundThird = false;
        int same1 = 0;
        int same2 = 0;
        int same3 = 0;

        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                int secondcolr = 0;
                int thirdcolr = 0;

                if(draftGrid[i][j].size() == 3){
                    foundFirst = true;
                    same1 = draftGrid[i][j].get(0);
                    same2 = draftGrid[i][j].get(1);
                    same3 = draftGrid[i][j].get(2);
                }

                //case2 start checking row
                if(foundFirst) {
                    for (int k = 0; k < 9; k++) {
                        if (k != j && compareArrayList(draftGrid[i][k], draftGrid[i][j])) {
                            foundSecond = true;
                            secondcolr = k;
                        }
                    }
                }

                if(foundSecond){
                    for(int k =0; k < 9; k++){
                        if(k != j && k != secondcolr && containTwo(draftGrid[i][k], draftGrid[i][j]) && draftGrid[i][k].size() == 2){
                            foundThird = true;
                            thirdcolr = k;
                        }
                    }
                }

                ruleOutFromRow(foundThird, i, j, secondcolr, thirdcolr, same1, same2,same3);

                //end checking row

            }
        }


        //end checking row
    }


    private void case1(){
        //case 1/2
        //todo: make sure the second found not to be considered as the first found, get rid of duplication
        boolean foundFirst = false;
        boolean foundSecondr = false;
        boolean foundThirdr = false;
        boolean foundSecondc = false;
        boolean foundThirdc = false;
        boolean foundSecondg = false;
        boolean foundThirdg = false;
        int same1 = 0, same2 = 0;


        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int secondcolr = j;
                int thirdcolr = j;

                int secondcolc = j;
                int thirdcolc = j;

                int secondcolg = j;
                int thirdcolg = j;

                if (draftGrid[i][j].size() == 2) {
                    foundFirst = true;
                    same1 = draftGrid[i][j].get(0);
                    same2 = draftGrid[i][j].get(1);
                }

                // start checking row case1
                int same3r = 0;
                if (foundFirst) {

                    //test case1
                    //check this row, if another cell contain one and one same number,with size 2
                    for (int k = 0; k < 9; k++) {
                        ArrayList<Integer> temp = draftGrid[i][k];
                        if (k != j && temp.size() == 2 && containOne(temp, same1, same2)) {
                            if (temp.contains(same1)) {
                                temp.remove(Integer.valueOf(same1));
                                same3r = temp.get(0);
                                foundSecondr = true;
                                secondcolr = k;
                            }
                        }
                    }
                }
                //check this row, if another cell same1,2,3{
                if (foundSecondr) {
                    for (int k = 0; k < 9; k++) {
                        ArrayList<Integer> temp = draftGrid[i][k];
                        if (temp.size() == 3 && temp.contains(same1) && temp.contains(same2) && temp.contains(same3r)) {
                            foundThirdr = true;
                            thirdcolr = k;
                        }
                    }
                }
                ruleOutFromRow(foundThirdr, i, j, secondcolr,thirdcolr, same1, same2, same3r);
                //rule out same123 from that row
//                if (foundThirdr) {
//                    for (int k = 0; k < 9; k++) {
//                        if (k != j && k != secondcolr && k != thirdcolr) {
//                            for (int l : draftGrid[i][k]) {
//                                if (l == same1 || l == same2 || l == same3r) {
//                                    draftGrid[i][k].remove(Integer.valueOf(l));
//                                }
//                            }
//                        }
//                    }
//                }
                //end checking row case1

                //todo: double check whether can check col in this for loop or not
                //start checking col case1
                int same3c = 0;
                if (foundFirst) {

                    //test case1
                    //check this row, if another cell contain one and one same number,with size 2
                    for (int k = 0; k < 9; k++) {
                        ArrayList<Integer> temp = draftGrid[k][j];
                        if (k != i && temp.size() == 2 && containOne(temp, same1, same2)) {
                            if (temp.contains(same1)) {
                                temp.remove(Integer.valueOf(same1));
                                same3c = temp.get(0);
                                foundSecondc = true;
                                secondcolc = k;
                            }
                        }
                    }
                }
                //check this row, if another cell same1,2,3{
                if (foundSecondc) {
                    for (int k = 0; k < 9; k++) {
                        ArrayList<Integer> temp = draftGrid[k][j];
                        if (temp.size() == 3 && temp.contains(same1) && temp.contains(same2) && temp.contains(same3c)) {
                            foundThirdc = true;
                            thirdcolc = k;
                        }
                    }
                }

                //rule out same123 from that row
                if (foundThirdc) {
                    for (int k = 0; k < 9; k++) {
                        if (k != j && k != secondcolc && k != thirdcolc) {
                            for (int l : draftGrid[k][j]) {
                                if (l == same1 || l == same2 || l == same3c) {
                                    draftGrid[k][j].remove(Integer.valueOf(l));
                                }
                            }
                        }
                    }
                }
                //end checking col case1


                //start checking region case1
                int row = (int) i / 3;
                int col = (int) j / 3;
                int same3g = 0;
                if (foundFirst) {
                    for (int k = row * 3; k < row * 3 + 3; k++) {
                        for (int l = col * 3; l < col * 3 + 3; l++) {
                            ArrayList<Integer> temp = draftGrid[k][l];
                            if (k != j && temp.size() == 2 && containOne(temp, same1, same2)) {
                                if (temp.contains(same1)) {
                                    temp.remove(Integer.valueOf(same1));
                                    same3g = temp.get(0);
                                    foundSecondg = true;
                                    secondcolg = k;
                                }
                            }
                        }
                    }
                }

                //check this region, if another cell same1,2,3{
                if (foundSecondg) {
                    for (int k = row * 3; k < row * 3 + 3; k++) {
                        for (int l = col * 3; l < col * 3 + 3; l++) {
                            ArrayList<Integer> temp = draftGrid[k][l];
                            if (temp.size() == 3 && temp.contains(same1) && temp.contains(same2) && temp.contains(same3g)) {
                                foundThirdg = true;
                                thirdcolg = k;
                            }
                        }
                    }
                }

                //rule out same123 from that region
                if (foundThirdg) {
                    for (int k = row * 3; k < row * 3 + 3; k++) {
                        for (int l = col * 3; l < col * 3 + 3; l++) {
                            if (k != j && k != secondcolg && k != thirdcolg) {
                                for (int t : draftGrid[k][l]) {
                                    if (t == same1 || t == same2 || t == same3g) {
                                        draftGrid[k][l].remove(Integer.valueOf(t));
                                    }
                                }
                            }
                        }
                    }
                }

                //end checking region



            }
        }
    }

    private void ruleOutFromRow(boolean foundThird, int i, int j, int secondcol, int thirdcol, int same1, int same2, int same3) {
        if (foundThird) {
            for (int k = 0; k < 9; k++) {
                if (k != j && k != secondcol && k != thirdcol) {
                    for (int l : draftGrid[i][k]) {
                        if (l == same1 || l == same2 || l == same3) {
                            draftGrid[i][k].remove(Integer.valueOf(l));
                        }
                    }
                }
            }
        }
    }


    //todo:fix parameter and return type
    public void multipleLine() {
        //check region horizontally,check1,2,3; 4,5,6; 7,8,9
        //check1,2,3
        //regionarowb(a,b);
        //region1
        //check row1,2
        //by the pattern 1 1 2
        checkrowab(0, 1);
        checkrowab(1, 2);
        checkrowab(0, 2);
        checkrowab(3, 4);
        checkrowab(4, 5);
        checkrowab(3, 5);
        checkrowab(6, 7);
        checkrowab(6, 8);
        checkrowab(7, 8);

        checkcolab(0, 1);
        checkcolab(1, 2);
        checkcolab(0, 2);
        checkcolab(3, 4);
        checkcolab(4, 5);
        checkcolab(3, 5);
        checkcolab(6, 7);
        checkcolab(6, 8);
        checkcolab(7, 8);


        //return board;


        //
    }

    //pattern 2 1 1


    //pattern 1 1 2
    //this is for check 3rd region
    public void checkcolab(int a, int b) {
        for (int m = 0; m < 3; m++) {
            int m1 = (3 * m) % 9;
            int m2 = (3 * m + 3) % 9;
            int m3 = (3 * m + 6) % 9;

            int c = getc(a, b);

            boolean region1row12 = false;
            Set<Integer> samedraft = new HashSet<Integer>();
            for (int i = m1; i < m1 + 3; i++) {
                //iterate the draftgrid
                //check row 1,2
                for (int j : draftGrid[i][a]) {
                    for (int k = m1; k < m1 + 3; k++) {
                        if (draftGrid[k][b].contains(j) && !draftGrid[k][c].contains(j)) {
                            samedraft.add(j);
                            region1row12 = true;
                        }
                    }
                }
            }

            //region2
            boolean region2row12 = false;
            Set<Integer> samedraft1 = new HashSet<Integer>();
            if (region1row12) {
                for (int i = m2; i < m2 + 3; i++) {
                    for (int j : draftGrid[i][a]) {
                        for (int k = m2; k < m2 + 3; k++) {
                            if (draftGrid[k][b].contains(j) && samedraft.contains(j) && !draftGrid[c][k].contains(j)) {
                                samedraft1.add(j);
                                region2row12 = true;
                            }
                        }
                    }
                }
            }

            boolean region3row3 = false;
            Set<Integer> samedraft2 = new HashSet<Integer>();

            if (region2row12) {
                for (int j = m3; j < m3 + 3; j++) {
                    for (int l : draftGrid[j][c]) {
                        if (samedraft1.contains(l)) {
                            samedraft2.add(l);
                            //samedraft2 can be at most 2 drafts
                            region3row3 = true;

                        }
                    }
                }
            }

            if (region3row3) {
                for (int same : samedraft2) {
                    if (c == 0 || c == 2 || c == 3 || c == 5 || c == 6 || c == 8) {
                        for (int i = a; i < b + 1; i++) {
                            for (int j = m3; j < m3 + 3; j++) {
                                if (draftGrid[j][i].contains(same)) {
                                    draftGrid[j][i].remove(Integer.valueOf(same));
                                }
                            }
                        }
                    } else {
                        for (int j = m3; j < m3 + 3; j++) {
                            if (draftGrid[j][c - 1].contains(same)) {
                                draftGrid[j][c - 1].remove(Integer.valueOf(same));
                            }
                            if (draftGrid[j][c + 1].contains(same)) {
                                draftGrid[j][c + 1].remove(Integer.valueOf(same));
                            }
                        }
                    }
                }

            }
        }
    }


    //pattern 1 1 2
    //this is for check 3rd region
    public void checkrowab(int a, int b) {
        //for loop 包括 3种自由变化：remove第3个region,第2，第1
        for (int m = 0; m < 3; m++) {
            int m1 = (3 * m) % 9;
            int m2 = (3 * m + 3) % 9;
            int m3 = (3 * m + 6) % 9;

            int c = getc(a, b);

            boolean region1row12 = false;
            Set<Integer> samedraft = new HashSet<Integer>();

            for (int i = m1; i < m1 + 3; i++) {
                //iterate the draftgrid
                //check row 1,2
                for (int j : draftGrid[a][i]) {
                    for (int k = m1; k < m1 + 3; k++) {
                        if (draftGrid[b][k].contains(j) && !draftGrid[c][k].contains(j)) {
                            samedraft.add(j);
                            region1row12 = true;
                        }
                    }
                }
            }


            //region2
            boolean region2row12 = false;
            Set<Integer> samedraft1 = new HashSet<Integer>();
            if (region1row12) {
                for (int i = m2; i < m2 + 3; i++) {
                    for (int j : draftGrid[a][i]) {
                        for (int k = m2; k < m2 + 3; k++) {
                            if (draftGrid[b][k].contains(j) && samedraft.contains(j) && !draftGrid[c][k].contains(j)) {
                                samedraft1.add(j);
                                region2row12 = true;
                            }
                        }
                    }
                }
            }

            boolean region3row3 = false;
            Set<Integer> samedraft2 = new HashSet<Integer>();


            if (region2row12) {
                for (int j = m3; j < m3 + 3; j++) {
                    for (int l : draftGrid[c][j]) {
                        if (samedraft1.contains(l)) {
                            samedraft2.add(l);
                            //samedraft2 can be at most 2 drafts
                            region3row3 = true;

                        }
                    }
                }
            }

            if (region3row3) {
                for (int same : samedraft2) {
                    if (c == 0 || c == 2 || c == 3 || c == 5 || c == 6 || c == 8) {
                        for (int i = a; i < b + 1; i++) {
                            for (int j = m3; j < m3 + 3; j++) {
                                if (draftGrid[i][j].contains(same)) {
                                    draftGrid[i][j].remove(Integer.valueOf(same));
                                }
                            }
                        }
                    } else {
                        for (int j = m3; j < m3 + 3; j++) {
                            if (draftGrid[c - 1][j].contains(same)) {
                                draftGrid[c - 1][j].remove(Integer.valueOf(same));
                            }
                            if (draftGrid[c + 1][j].contains(same)) {
                                draftGrid[c + 1][j].remove(Integer.valueOf(same));
                            }
                        }
                    }
                }

            }
        }

    }

    public int getc(int a, int b) {
        int k = a + b;
        int c = 0;
        switch (k) {
            case 1:
                c = 2;
                break;
            case 3:
                c = 0;
                break;
            case 2:
                c = 1;
                break;
            case 7:
                c = 5;
                break;
            case 8:
                c = 4;
                break;
            case 9:
                c = 3;
                break;
            case 13:
                c = 8;
                break;
            case 14:
                c = 7;
                break;
            case 15:
                c = 6;
                break;
            default:
                break;
        }
        return c;
    }

//    public boolean regionarowb(boolean row12, ArrayList<Integer>samedraft, int a, int b){
//        for(int i = a*3; i < a*3+3; i++){
//            //iterate the draftgrid
//            //check row 1,2
//            for(int j: draftGrid[0][i]){
//                for(int k=0;k<3;k++){
//                    if(draftGrid[1][k].contains(j)) {
//                        samedraft.add(j);
//                        row12 = true;
//                    }
//                }
//            }
//        }
//    }


    private boolean containTwo(ArrayList<Integer>check, ArrayList<Integer>subject){
        int count = 0;
        for(int i : subject){
            if(i == check.get(0) || i == check.get(1)){
                count++;
            }
        }
        return count == 3;
    }


    private boolean containOne(ArrayList<Integer> draft, int a, int b) {
        return ((draft.contains(a) && !draft.contains(b)) || (draft.contains(b) && !draft.contains(a)));
    }

    private boolean compareArrayList(ArrayList<Integer> a, ArrayList<Integer> b) {
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
