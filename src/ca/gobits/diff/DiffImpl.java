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
	
	private PatienceSort patience = new PatienceSort();
	private static MessageDigest sha1;
	
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
			
			int pos0 = df0.getPos();
			int pos1 = df1.getPos();
			matchLast(list0, list1, pos0 - 1, pos1 - 1);
			matchFirst(list0, list1, pos0 + 1, pos1 + 1);
		}
		
//		debug(list0);
//		System.out.println ("-----------------------------------");
//		debug(list1);
		
		return new DiffResult(list0, list1);
	}
	
	@Override
	public int matchFirst(List<DiffLine> list0, List<DiffLine> list1, int start0, int start1) {
		
		while (start0 < list0.size() && start1 < list1.size()) {
			
			DiffLine l0 = list0.get(start0);
			DiffLine l1 = list1.get(start1);
			
			if (Arrays.equals(l0.getSHA1(), l1.getSHA1())) {
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
	public int matchLast(List<DiffLine> list0, List<DiffLine> list1, int end0, int end1) {
		
		int count = 0;
		
		while (end0 > -1 && end1 > -1) {

			DiffLine l0 = list0.get(end0);
			DiffLine l1 = list1.get(end1);

			if (l0.getMatch() == -1 && l1.getMatch() == -1
					&& Arrays.equals(l0.getSHA1(), l1.getSHA1())) {
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

	private List<DiffLine> createDiffByLine(String s) {
		
		List<DiffLine> list = new ArrayList<DiffLine>();
		String[] sa = s.split(newLine);
		
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
	
	private DiffLine createDiff(String s, int i) {
		
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
}
