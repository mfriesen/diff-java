package ca.gobits.diff;

import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * http://bramcohen.livejournal.com/73318.html
 * There's no coherent explanation of what the advantages of Patience Diff are, so I'll explain now. 
 * First, a quick overview of how Patience Diff works 	-
 * 
 * 1) Match the first lines of both if they're identical, then match the second, third, etc. until a pair doesn't match.
 * 2) Match the last lines of both if they're identical, then match the next to last, second to last, etc. until a pair doesn't match.
 * 3) Find all lines which occur exactly once on both sides, then do longest common subsequence on those lines, matching them up.
 * 4) Do steps 1-2 on each section between matched lines
 */
public class DiffImpl implements Diff {

	private static final String newLine = System.getProperty("line.separator");
	
	private static MessageDigest sha1;
	
	private PatienceSort patience = new PatienceSort();
	
	static {
		try {		
			sha1 = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void diff(PrintStream ps, DiffResult dr) {
		
		int i0 = 0, i1 = 0;
				
		List<DiffLine> list0 = dr.getList0();
		List<DiffLine> list1 = dr.getList1();		
			
		while (i0 < list0.size() || i1 < list1.size()) {

			DiffLine df0 = get(list0, i0);
			DiffLine df1 = get(list1, i1);

			if (df0 != null && df0.getMatch() == -1) {
				ps.println ("+ " + df0.getLine());
				i0++;
				
			} else if (df1 != null && df1.getMatch() == -1) {
				
				ps.println ("- " + df1.getLine());
				i1++;
			} else {
				
				if (df0 != null) {
					ps.println(df0.getLine());
				}
				
				i0++;
				i1++;
			}
		}
	}
		
	@Override
	public DiffResult diffByLine(String s0, String s1) {
		
		List<DiffLine> list0 = createDiffByLine(s0);
		List<DiffLine> list1 = createDiffByLine(s1);

		return diff(s0, s1, list0, list1);
	}
	
	@Override
	public DiffResult diffByChar(String s0, String s1) {
		List<DiffLine> list0 = createDiffByChar(s0);
		List<DiffLine> list1 = createDiffByChar(s1);

		return diff(s0, s1, list0, list1);
	}
	
	private DiffResult diff(String s0, String s1, List<DiffLine> list0, List<DiffLine> list1) {
		
		int start = matchFirst(list0, list1, 0, 0);

		int end = matchLast(list0, list1, list0.size() - 1, list1.size() - 1);
		
		Map<String, DiffLine> unique0 = uniqueLines(list0, start, end);
		Map<String, DiffLine> unique1 = uniqueLines(list1, start, end);

		List<DiffLine> common0 = findCommonLines(list1, unique0, unique1);
		
		List<Pile<PileItem<DiffLine>>> piles = this.patience.sort(common0);
		List<DiffLine> subseq = this.patience.longestIncreasingSubsequence(piles);
		
		for (DiffLine df0 : subseq) {
			DiffLine df1 = unique1.get(new String(df0.getSHA1()));
			updateLinePosition(df0, df1);			
		}
		
		for (DiffLine df0 : subseq) {
			DiffLine df1 = list1.get(df0.getMatch());

			matchLast(list0, list1, df0.getPos() - 1, df1.getPos() - 1);
			matchFirst(list0, list1, df0.getPos() + 1, df1.getPos() + 1);
		}
		
		longestCommonSubsequenceUnmatchedLines(list0, list1);
				
		return new DiffResult(list0, list1);
	}

	private void longestCommonSubsequenceUnmatchedLines(List<DiffLine> list0, List<DiffLine> list1) {
		
		int i = 0;
		int start = -1;
		int end = -1;
		
		while (i < list0.size()) {
			
			DiffLine dl = list0.get(i);
			
			if (dl.getMatch() == -1) {
				
				if (start == -1) {
					start = dl.getPos();
				}
				
				end = dl.getPos();
				
			} else {
				
				if (start > 0) {
										
					updateLinePositionViaLongestCommonSubsequence(list0, list1, start, end);
				}
				
				start = -1;
				end = -1;
			}
			
			i++;
		}
	}

	private void updateLinePositionViaLongestCommonSubsequence(List<DiffLine> list0, List<DiffLine> list1, int start, int end) {
		
		end = Math.min(end + 1, list0.size());
		List<DiffLine> l0 = list0.subList(start, end);
		List<DiffLine> l1 = list1.subList(list0.get(start - 1).getMatch() + 1, 
				list0.get(end).getMatch());

		int[][] lcs = longestCommonSequence(l0, l1);
		int[][] results = subsequence(lcs);

		for (int i = 0; i < results.length; i++) {
			DiffLine d0 = l0.get(results[i][0]);
			DiffLine d1 = l1.get(results[i][1]);

			updateLinePosition(d0, d1);
		}
	}

	public void debug(PrintStream ps, int[][] arr, List<DiffLine> c0, List<DiffLine> c1) {

		int padding = String.valueOf(arr[0][0]).length();

		ps.print(" ");
		for (int i = 0; i < c0.size(); i++) {
			ps.print(c0.get(i).getLine() + " ");
		}
		ps.println();

		for (int i = 0; i < arr.length; i++) {

			if (i < c1.size()) {
				ps.print(c1.get(i).getLine() + " ");
			} else {
				ps.print(" ");
			}

			for (int j = 0; j < arr[i].length; j++) {

				int diff = padding - String.valueOf(arr[i][j]).length();
				for (int jj = 0; jj < diff; jj++) {
					ps.print("0");
				}
				ps.print(arr[i][j]);

				ps.print(" ");
			}

			ps.println();
		}
	}
		
	private int matchFirst(List<DiffLine> list0, List<DiffLine> list1, int start0, int start1) {
		
		while (start0 < list0.size() && start1 < list1.size()) {
			
			DiffLine l0 = list0.get(start0);
			DiffLine l1 = list1.get(start1);
			
			if (l0.getMatch() == -1 && l1.getMatch() == -1 && isEqual(l0, l1)) {
				updateLinePosition(l0, l1);
				start0++;
				start1++;
				continue;
			}
			break;
		}
		
		return start0;
	}

	@Override
	public boolean isEqual(DiffLine l0, DiffLine l1) {
		return l0 != null && l1 != null && Arrays.equals(l0.getSHA1(), l1.getSHA1());
	}	
	
	private int matchLast(List<DiffLine> list0, List<DiffLine> list1, int end0, int end1) {
		
		int count = 0;
		
		while (end0 > -1 && end1 > -1) {

			DiffLine l0 = list0.get(end0);
			DiffLine l1 = list1.get(end1);

			if (l0.getMatch() == -1 && l1.getMatch() == -1
					&& isEqual(l0, l1)) {
				updateLinePosition(l0, l1);
				end0--;
				end1--;
				count++;
				continue;
			}
			break;
		}
		
		return count;
	}
	
	/**
	 * Returns common lines in the order of list1
	 */
	private List<DiffLine> findCommonLines(List<DiffLine> list1,
			Map<String, DiffLine> unique0, Map<String, DiffLine> unique1) {

		List<DiffLine> common0 = new ArrayList<DiffLine>();
		
		for (DiffLine dl : list1) {
			String s = new String(dl.getSHA1());

			if (unique1.containsKey(s) && unique0.containsKey(s)) {
				common0.add(unique0.get(s));
			}
		}
		
		return common0;
	}
	
	private Map<String, DiffLine> uniqueLines(List<DiffLine> list, int start, int end) {

		Set<String> seen = new HashSet<String>();
		Map<String, DiffLine> map = new HashMap<String, DiffLine>();

		for (int i = start; i < list.size() - end; i++) {

			DiffLine dl = list.get(i);
			String s = new String(dl.getSHA1());

			if (map.containsKey(s)) {

				map.remove(s);

			} else if (!seen.contains(s)) {

				map.put(s, dl);
				seen.add(s);
			}
		}

		return map;
	}

	@Override
	public String[] splitByNewLine(String s) {
		return s.split(newLine);
	}
	
	private List<DiffLine> createDiffByLine(String s) {
		
		List<DiffLine> list = new ArrayList<DiffLine>();
		
		String[] sa = splitByNewLine(s);
		for (int i = 0; i < sa.length; i++) {
			
			DiffLine line = createDiff(sa[i], i);
			list.add(line);			
		}
		
		return list;
	}

	private List<DiffLine> createDiffByChar(String s) {
		
		List<DiffLine> list = new ArrayList<DiffLine>();
		
		for (int i = 0; i < s.length(); i++) {
			
			DiffLine line = createDiff(String.valueOf(s.charAt(i)), i);
			list.add(line);			
		}
		
		return list;
	}
	
	DiffLine createDiff(String s, int i) {
		
		DiffLine line = new DiffLine();
		line.setLine(s);
		line.setPos(i);
		line.setSHA1(sha1.digest(s.getBytes()));
		return line;
	}
	
	private void updateLinePosition(DiffLine d0, DiffLine d1) {
		
		if (d0 != null && d1 != null && d0.getMatch() == -1 && d1.getMatch() == -1) {
			d0.setMatch(d1.getPos());
			d1.setMatch(d0.getPos());
		}
	}
	
	protected void debug(Collection<DiffLine> list) {
		for (DiffLine diffLine : list) {
			System.out.println (diffLine.getLine() + " " + diffLine.getPos() + " " + diffLine.getMatch());
		}
	}

	private DiffLine get(List<DiffLine> list, int index) {
		return index < list.size() ? list.get(index) : null;
	}

	@Override
	public String getNewLine() {
		return newLine;
	}
	
	@Override
	public int[][] longestCommonSequence(List<DiffLine> c0, List<DiffLine> c1) {
		
		int[][] arr = new int[c1.size() + 1][c0.size() + 1];

		for (int row = arr.length - 1; row >= 0; row--) {

			for (int col = arr[row].length - 1; col >= 0; col--) {

				if (col >= c0.size() || row >= c1.size()) {
					arr[row][col] = 0;
				} else if (Arrays.equals(c0.get(col).getSHA1(), c1.get(row).getSHA1())) {
					arr[row][col] = 1 + arr[row + 1][col + 1];
				} else {
					arr[row][col] = Math.max(arr[row + 1][col], arr[row][col + 1]);
				}
			}
		}

		return arr;
	}
	
	public int[][] subsequence(int[][] lcs) {

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
}
