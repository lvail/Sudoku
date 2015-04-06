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

	// input for a 9 x 9 sudoku puzzle to solve, difficulty 4 of 5
	private String stringPuzzle = 
			  "    74 2 " +
			  "   93  5 " +
			  "49  8   3" +
			  "9 1     2" +
			  "  7   9  " +
			  "2     1 4" +
			  "6   2  95" +
			  " 8  47   " +
			  " 2 39    " ;

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
		while (strategy1() && strategy2()) {
		}
		
		System.out.println("-----------------------------------");
		printPuzzle();
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

}
