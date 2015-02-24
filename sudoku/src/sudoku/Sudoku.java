/*
 * 
 * Sudoku - puzzle solver
 * by Larry Vail
 * 
 */

package sudoku;

import java.util.TreeSet;
import java.util.SortedSet;

public class Sudoku {

	// main data structure
	private SortedSet<Integer>[][] puzzle = new TreeSet[9][9];
	
	public static void main(String [] args)
	{
		Sudoku app = new Sudoku();
		app.run();	// makes methods non-static
	}
	
	// remove known row, column, block integers from each cell
	private boolean strategy1() {
		boolean changes = false;
		changes = false;
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

	// remove "could be" integers from every other row or column in each cell if only one possible
	private boolean strategy2() {
		SortedSet<Integer> S = new TreeSet<Integer>();
		boolean changes = false;
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
	
	// main entry point for app
	private void run()
	{
		System.out.println("sudoku running...");
		initPuzzle();
		printPuzzle();

		strategy1();
		printPuzzle();
		
		strategy2();
		
		System.out.println("-----------------------------------");
		printPuzzle();
	}

	// initialize puzzle array to known and "could be" sets
	private void initPuzzle() {
		System.out.println("puzzleInit()...");
		
		String stringPuzzle = "  8 72 96" +		// 9 x 9 sudoku difficulty 2 puzzle to solve
							  " 2 68   5" +
							  "    31  2" +
							  "  6 2 5  " +
							  "   8 3   " +
							  "  3 4 1  " +
							  "4  39    " +
							  "3   65 7 " +
							  "26 71 8  " ;
		
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

	private void printPuzzle() {
		System.out.println("puzzlePrint()...");

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
