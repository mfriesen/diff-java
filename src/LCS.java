
import java.io.PrintStream;

public class LCS {

	private static final char[] newLine = System.getProperty("line.separator").toCharArray();
	
	public int[][] lcs(char[] c0, char[] c1) {
		int[][] arr = new int[c1.length + 1][c0.length + 1];

		for (int row = arr.length - 1; row >= 0; row--) {

			for (int col = arr[row].length - 1; col >= 0; col--) {

				if (col >= c0.length || row >= c1.length) {
					arr[row][col] = 0;
				} else if (c0[col] == c1[row]) {
					arr[row][col] = 1 + arr[row + 1][col + 1];
				} else {
					arr[row][col] = Math.max(arr[row + 1][col], arr[row][col + 1]);
				}
			}
		}
		
		return arr;
	}		
	
	public int[][] subsequence(int[][] lcs, char[] c0, char[] c1) {
		
		int col = 0, row = 0;
		int rowmax = lcs.length - 1, colmax = lcs[0].length - 1;
		
		int value = lcs[row][col];
		
		StringBuffer sb = new StringBuffer();

		while (row < rowmax && col < colmax) {

			if (value == lcs[row][col + 1]) {
				col++;
			} else if (value == lcs[row + 1][col]) {
				row++;
			} else {
//				System.out.print (c1[col]);
				sb.append(col + "," + row + ",");

				col++;
				row++;
				value = lcs[row][col];
			}
		}

		return stringToCoordinates(sb.toString());
	}

	private int[][] stringToCoordinates(String sb) {
		
		String[] strs = sb.split(",");
		int[][] coord = new int[strs.length / 2][2];
		
		for (int i = 0; i < coord.length; i++) {
			coord[i][0] = Integer.parseInt(strs[i * 2]);
			coord[i][1] = Integer.parseInt(strs[i * 2 + 1]);
		}

		return coord;
	}

	public void debug(PrintStream ps, int[][] arr, char[] c0, char[] c1) {

		int padding = String.valueOf(arr[0][0]).length();

		ps.print ("  ");
		for (int i = 0; i < c0.length; i++) {
			ps.print (c0[i] + " ");
		}
		ps.println();
		
		for (int i = 0; i < arr.length; i++) {

			if (i < c1.length) {
				ps.print (c1[i] + " ");
			} else {
				ps.print ("  ");
			}
				
							
			for (int j = 0; j < arr[i].length; j++) {
				
				int diff = padding - String.valueOf(arr[i][j]).length();
				for (int jj = 0; jj < diff; jj++) {
					ps.print("0");
				}
				ps.print (arr[i][j]);
				
				ps.print(" ");
			}
			
			ps.println();
		}
	}

	public void diff(PrintStream ps, char[] c0, char[] c1) {
		
		boolean removed = false;
		int[][] lcs = lcs(c0, c1);
//		debug(System.out, lcs, c0, c1);
//		print(lcs, c0, c1);
//		System.out.println ("lcs: " + lcs.length + " " + lcs[0].length);
		int[][] seq = subsequence(lcs, c0, c1);
		debug(System.out, seq, c0, c1);
//		System.out.println ("SEQ: " + seq.length + " " + seq[0].length);
		int seqpos = 0;
		int length = Math.max(c0.length, c1.length);
		
		StringBuilder sb0 = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		
		for (int i = 0; i < c0.length; i++) {

			if (i == seq[seqpos][0]) {
				seqpos++;
				sb0.append(c0[i]);
//				ps.print(c0[i]);
//				System.out.println (c0[i]);
			} else {
				removed = true;
				sb0.append(c0[i]);
//				ps.print(" - " + c0[i]);
			}
			
			if (endsWithNewLine(sb0)) {

				if (removed) {
					ps.print (" - ");
				}
				
				ps.print(sb0);
				sb0 = new StringBuilder();
				removed = false;
			}
		}
		
//		for (int i = 0; i < seq.length; i++) {
//			
//			if (i < c1.length) {
//				sb1.append(c1[i]);
//			}
//			
//			if (seqpos < c0.length && seq[seqpos][0] == i) {
//				sb0.append(c0[seqpos]);
//				seqpos++;
//			}
//			
//			if (endsWithNewLine(sb0)) {
//				
//				if (sb0.length() > 0) {
//					ps.append(sb0);
//					sb0 = new StringBuilder();
//					sb1 = new StringBuilder();
//				}
//				
//			} else if (endsWithNewLine(sb1)) {
//				
////				if (sb0.length() > 0) {
////					System.out.println ("-- " + sb0);
////				}
//				
//				if (sb1.length() > 0) {
////					System.out.print ("++ " + sb1);
//					ps.append("+ " + sb1);
//				}
//
////				sb0 = new StringBuilder();
//				sb1 = new StringBuilder();
//			}
//		}
	}
	
	private boolean endsWithNewLine(StringBuilder sb) {
		
		boolean match = false;
		int len = sb.length();
		
		if (len >= newLine.length) {
			for (int i = 0; i < newLine.length; i++) {
				match = sb.charAt(len - newLine.length + i) == newLine[i];
			}
		}
		
		return match;
	}

	private void diffPrint(StringBuilder sb, String delim) {
		if (sb.length() != 0) {
			System.out.println (delim + sb.toString());
		}		
	}
	
}