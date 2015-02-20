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
	
	private SortedSet<Integer>[][] puzzle = new TreeSet[9][9];
	
	public static void main(String [] args)
	{
		Sudoku app = new Sudoku();
		app.run();	// makes methods non-static
	}
	
	// main entry point for app
	private void run()
	{
		System.err.println("sudoku running...");
		puzzleInit();
		puzzlePrint();
		
		// Test my set building methods
//		System.out.println("rowSet(0) = " + rowSet(0));
//		System.out.println("rowSet(8) = " + rowSet(8));
//		System.out.println("colSet(0) = " + colSet(0));
//		System.out.println("colSet(8) = " + colSet(8));
//		System.out.println("blockSet(8, 0) = " + blockSet(8, 0));
//		System.out.println("blockSet(4, 4) = " + blockSet(4, 4));
//		System.out.println("blockSet(1, 7) = " + blockSet(1, 7));
		
		int round = 0;
		boolean solved;
		do {
			solved = true;
			System.err.println("round " + ++round);
			for (int r = 0; r < 9; r++) {
				for (int c = 0; c < 9; c++) {
					if (puzzle[r][c].size() > 1) {
						solved = false;
						System.out.println("Before: ["+r+"]["+c+"]="+puzzle[r][c]);
						SortedSet<Integer> S = new TreeSet<Integer>();
						S.addAll(rowSet(r));
						S.addAll(colSet(c));
						S.addAll(blockSet(r, c));
						puzzle[r][c].removeAll(S);
						System.out.println("After:  ["+r+"]["+c+"]="+puzzle[r][c]);
					}
				}
			} 
		} while (!solved);
		
		puzzlePrint();
	}

	private void puzzleInit() {
		System.err.println("puzzleInit()...");
		
		String stringPuzzle = "5  421   " +		// 9 x 9 sudoku puzzle to solve
							  "3   9 1 4" +
							  " 71 3  2 " +
							  "215  97 6" +
							  "  35784  " +
							  "8 76  395" +
							  " 5  8 93 " +
							  "1 8 6   7" +
							  "   153  8" ;
		
		SortedSet<Integer> U = new TreeSet<Integer>();	// U is the universal sudoku set of digits 1-9
		U.add(1); U.add(2); U.add(3); U.add(4); U.add(5);
		U.add(6); U.add(7); U.add(8); U.add(9);
		
		char ch;
		int digit;
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
					System.err.println("Bad character '" + ch + "' initilizing puzzle.");
				}
				
			}
		}
	}

	private void puzzlePrint() {
		System.err.println("puzzlePrint()...");

		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
//				System.out.print(puzzle[r][c].first() + " ");
				if (puzzle[r][c].size() == 1) {
					System.out.print(puzzle[r][c].first() + " ");
				} else {
					System.out.print("  ");
				}
			}
			System.out.println();
		}
		
	}

	private SortedSet<Integer> rowSet(int r) {
		SortedSet<Integer> set = new TreeSet<Integer>();
		for (int c = 0; c < 9; c++) {
			if (puzzle[r][c].size() == 1) {
				set.add(puzzle[r][c].first());
			}
		}
		return set;
	}
	
	private SortedSet<Integer> colSet(int c) {
		SortedSet<Integer> set = new TreeSet<Integer>();
		for (int r = 0; r < 9; r++) {
			if (puzzle[r][c].size() == 1) {
				set.add(puzzle[r][c].first());
			}
		}
		return set;
	}
			
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
			
}
