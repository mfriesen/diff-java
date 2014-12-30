package ca.gobits.diff;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;


public class PatienceSortTest {

	private static final String newLine = System.getProperty("line.separator");
	private PatienceSort patience = new PatienceSort();
	
	// test string 0 8 4 12 2 10 6 14 1 9 5 13 3 11 7 15
	// expected result 0 2 6 9 11 15
	@Test
	public void testSort01() {
		// given
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		
		List<Integer> ints = Arrays.asList(Integer.valueOf(0), Integer.valueOf(8), Integer.valueOf(4), Integer.valueOf(12), 
				Integer.valueOf(2), Integer.valueOf(10), Integer.valueOf(6), Integer.valueOf(14), Integer.valueOf(1), 
				Integer.valueOf(9), Integer.valueOf(5), Integer.valueOf(13), Integer.valueOf(3), Integer.valueOf(11), Integer.valueOf(7), Integer.valueOf(15));		
		
		// when
		List<Pile<PileItem<Integer>>> results = this.patience.sort(ints);
		this.patience.debugPiles(ps, results);
		List<Integer> longestSeq = this.patience.longestIncreasingSubsequence(results);
		
		// then
		assertEquals(6, results.size());
		
		String expected = "0\t1\t3\t7\t11\t15\t" + newLine
				+ "\t2\t5\t9\t13\t\t" + newLine
				+ "\t4\t6\t14\t\t\t" + newLine
				+ "\t8\t10\t\t\t\t" + newLine
				+ "\t\t12\t\t\t\t" + newLine
				+ "\t\t\t\t\t\t" + newLine
				;
		
		String result = baos.toString();
		assertEquals(expected, result);
		
		assertEquals(6, longestSeq.size());
		
		assertEquals("0", longestSeq.get(0).toString());
		assertEquals("2", longestSeq.get(1).toString());
		assertEquals("6", longestSeq.get(2).toString());
		assertEquals("9", longestSeq.get(3).toString());
		assertEquals("11", longestSeq.get(4).toString());
		assertEquals("15", longestSeq.get(5).toString());		
	}

}

