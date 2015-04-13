/*
 * 
 * Sudoku - puzzle solver
 * by Larry Vail
 * 
 */

package sudoku;

import java.util.TreeSet;
import java.util.SortedSet;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;


public class Sudoku {

	// input for a 9 x 9 sudoku puzzle to solve
	private String stringPuzzle;

	// main data structure
	@SuppressWarnings("unchecked")
	private SortedSet<Integer>[][] puzzle = new TreeSet[9][9];
	
	public static void main(String [] args)
	{
		Sudoku app = new Sudoku();
		app.run();	// makes methods non-static
	}
	
	// main entry point for app
	private void run()
	{
		System.out.println("sudoku run()...");
		initPuzzle();
		printPuzzle();
		while (strategy1() || strategy2() || strategy3()) {
			if (done())
				break;
		}
		
		System.out.println("-----------------------------------");
		printPuzzle();
	}
	
	private boolean done() {
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				if (puzzle[r][c].size() != 1)
					return false;
			}
		}
		return true;
	}

	// initialize puzzle array to known and "could be" sets
	private void initPuzzle() {
		System.out.println("initPuzzle()()...");
		openFile();
		
		SortedSet<Integer> U = new TreeSet<Integer>();	// U is the universal sudoku set of digits 1-9
		U.add(1); U.add(2); U.add(3); U.add(4); U.add(5);
		U.add(6); U.add(7); U.add(8); U.add(9);
	
		char ch;
		int digit;
		
		// load stringPuzzle into 9x9 array puzzle
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				ch = stringPuzzle.charAt(r*9+c);
				if (Character.isDigit(ch)) {
					digit = Integer.parseInt(""+ch);
					puzzle[r][c] = new TreeSet<Integer>();
					puzzle[r][c].add(digit);
				} else if (ch == ' ') {
					puzzle[r][c] = new TreeSet<Integer>(U);
				} else {
					System.out.println("Bad character '" + ch + "' initilizing puzzle.");
				}
			}
		}
	}

	private void openFile() {
		String puzzleFileName;
	    JFileChooser chooser = new JFileChooser(".");
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Sudoku puzzles", "sudoku");

	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	puzzleFileName = chooser.getSelectedFile().getName();
	    	System.out.println("You chose to open this file: " + puzzleFileName);
			Scanner scanner;
			try {
				scanner = new Scanner(new FileReader(puzzleFileName));
				stringPuzzle = "";
				for (int r = 0; r < 9; r++) {
					stringPuzzle += scanner.nextLine();
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(0);
			}
	    } else {
	    	System.out.println("Must choose a valid sudoku puzzle file.  Cannot continue, Good-bye!");
	    	System.exit(0);
	    }
	}

	private void printPuzzle() {
		System.out.println("printPuzzle()()...");
	
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				if (puzzle[r][c].size() == 1) {
					System.out.print(puzzle[r][c].first() + " ");
				} else {
					System.out.print("  ");
				}
			}
			System.out.println();
		}
	}
	
	private static int pass1; 
	// remove known row, column, block integers from each cell
	private boolean strategy1() {
		boolean changes = false;
		changes = false;
		System.out.println("pass " + ++pass1 + " strategy1()...");
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
//					System.out.println("["+(r+1)+(c+1)+"]="+puzzle[r][c]);
				if (puzzle[r][c].size() > 1) {
					SortedSet<Integer> S = new TreeSet<Integer>();
					S.addAll(rowSet(r));
					S.addAll(colSet(c));
					S.addAll(blockSet(r, c));
					if (puzzle[r][c].removeAll(S)) {
						changes = true;
					}
				}
			}
		}
		return changes;	
	}

	// returns set of known integers on row r
	private SortedSet<Integer> rowSet(int r) {
		SortedSet<Integer> set = new TreeSet<Integer>();
		for (int c = 0; c < 9; c++) {
			if (puzzle[r][c].size() == 1) {
				set.add(puzzle[r][c].first());
			}
		}
		return set;
	}

	// returns set of known integers on column c
	private SortedSet<Integer> colSet(int c) {
		SortedSet<Integer> set = new TreeSet<Integer>();
		for (int r = 0; r < 9; r++) {
			if (puzzle[r][c].size() == 1) {
				set.add(puzzle[r][c].first());
			}
		}
		return set;
	}

	// returns set of known integers in 3 x 3 block containing row r, column c
	private SortedSet<Integer> blockSet(int r, int c) {
		SortedSet<Integer> set = new TreeSet<Integer>();
		int br = (r / 3) * 3;
		int bc = (c / 3) * 3;
		for (r = br; r < br + 3; r++) {
			for (c = bc; c < bc + 3; c++) {
				if (puzzle[r][c].size() == 1) {
					set.add(puzzle[r][c].first());
				}
			}
		}
		return set;
	}

	private static int pass2 = 0;
	// remove "could be" integers from every other row or column in each cell if only one possible
	private boolean strategy2() {
		SortedSet<Integer> S = new TreeSet<Integer>();
		boolean changes = false;
		System.out.println("pass " + ++pass2 + " strategy2()...");
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
	//					System.out.println("["+(r+1)+(c+1)+"]="+puzzle[r][c]);
				if (puzzle[r][c].size() > 1) {
					// try row first
					S.clear();
					S.addAll(puzzle[r][c]);
					S.removeAll(rowSet2(r, c));
					if (S.size() == 1) {
						puzzle[r][c].clear();
						puzzle[r][c].addAll(S);
						changes = true;
					} else {
						// try column second
						S.clear();
						S.addAll(puzzle[r][c]);
						S.removeAll(colSet2(r, c));
						if (S.size() == 1) {
							puzzle[r][c].clear();
							puzzle[r][c].addAll(S);
							changes = true;
						}
					}
				}
			}
		}
		return changes;
	}
	
	// returns set of other "could be" sets of integers on row except col
	private SortedSet<Integer> rowSet2(int row, int col) {
		SortedSet<Integer> T = new TreeSet<Integer>();
		for (int c = 0; c < 9; c++) {
			if (c != col) {
				T.addAll(puzzle[row][c]);
			}
		}
		return T;
	}
	
	// returns set of other "could be" sets of integers on col except row
	private SortedSet<Integer> colSet2(int row, int col) {
		SortedSet<Integer> T = new TreeSet<Integer>();
		for (int r = 0; r < 9; r++) {
			if (r != row)
				T.addAll(puzzle[r][col]);
		}
		return T;
	}

	private static int pass3 = 0;
	// if 2 cells in a row or column have equal "could be" sets of size 2, 
	// remove them from all other cells in the row or column 
	private boolean strategy3() {
		SortedSet<Integer> S = new TreeSet<Integer>();
		boolean changes = false;
		System.out.println("pass " + ++pass3 + " strategy3()...");
		
		printPuzzle();	//debug
		
		// try each row first
		for (int r = 0; r < 9; r++) {
			S.clear();
			for (int c = 0; c < 9; c++) {
				if (puzzle[r][c].size() == 2) {
					if (S.isEmpty()) {
						S.add(c);
					} else if (puzzle[r][S.first()].equals(puzzle[r][c])) {
						S.add(c);
					}
				}
			}
			if (S.size() == 2) {
				changes = removeFromRow3(r, S);
			}
		}
		// now try each column
		for (int c = 0; c < 9; c++) {
			S.clear();
			for (int r = 0; r < 9; r++) {
				if (puzzle[r][c].size() == 2) {
					if (S.isEmpty()) {
						S.add(r);
					} else if (puzzle[S.first()][c].equals(puzzle[r][c])) {
						S.add(r);
					}
				}
			}
			if (S.size() == 2) {
				changes = removeFromCol3(c, S);
			}
		}
		return changes;
	}
	
	// removes "could be" in all row cells except 2 columns in S which both contain 2 and are equal
	private boolean removeFromRow3(int row, SortedSet<Integer> S) {
		boolean changes = false;
		SortedSet<Integer> T = new TreeSet<Integer>();
		SortedSet<Integer> save = new TreeSet<Integer>();
		T.addAll(puzzle[row][S.first()]);
		for (int c = 0; c < 9; c++) {
			if (!S.contains(c)) {
				save.clear();
				save.addAll(puzzle[row][c]);	// before removeAll()
				puzzle[row][c].removeAll(T);
				if (!save.equals(puzzle[row][c])) {
					changes = true;
				}
			}
		}
		return changes;
	}
	
	// removes "could be" in all column cells except 2 rows in S which both contain 2 and are equal
	private boolean removeFromCol3(int col, SortedSet<Integer> S) {
		boolean changes = false;
		SortedSet<Integer> T = new TreeSet<Integer>();
		SortedSet<Integer> save = new TreeSet<Integer>();
		T.addAll(puzzle[S.first()][col]);
		for (int r = 0; r < 9; r++) {
			if (!S.contains(r)) {
				save.clear();
				save.addAll(puzzle[r][col]);	// before removeAll()
				puzzle[r][col].removeAll(T);
				if (!save.equals(puzzle[r][col])) {
					changes = true;
				}
			}
		}
		return changes;
	}
}
