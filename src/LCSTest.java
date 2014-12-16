
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Ignore;
import org.junit.Test;

public class LCSTest {

	private static final String newLine = System.getProperty("line.separator");
			
	// calculate Longest Common Subsequences
	@Test
	public void testLcs01() {
		
		// given
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		
		LCS l = new LCS();
		String s0 = "nematode knowledge";
		String s1 = "empty bottle";
		
		// when
		int[][] lcs = l.lcs(s0.toCharArray(), s1.toCharArray());
		l.debug(ps, lcs, s0.toCharArray(), s1.toCharArray());

		// then
		String expected = 
			  "  n e m a t o d e   k n o w l e d g e " + newLine
			+ "e 7 7 6 5 5 5 5 5 4 3 3 3 2 2 2 1 1 1 0 " + newLine
			+ "m 6 6 6 5 5 4 4 4 4 3 3 3 2 2 1 1 1 1 0 " + newLine
			+ "p 5 5 5 5 5 4 4 4 4 3 3 3 2 2 1 1 1 1 0 " + newLine
			+ "t 5 5 5 5 5 4 4 4 4 3 3 3 2 2 1 1 1 1 0 " + newLine
			+ "y 4 4 4 4 4 4 4 4 4 3 3 3 2 2 1 1 1 1 0 " + newLine
			+ "  4 4 4 4 4 4 4 4 4 3 3 3 2 2 1 1 1 1 0 " + newLine
			+ "b 3 3 3 3 3 3 3 3 3 3 3 3 2 2 1 1 1 1 0 " + newLine
			+ "o 3 3 3 3 3 3 3 3 3 3 3 3 2 2 1 1 1 1 0 " + newLine
			+ "t 3 3 3 3 3 2 2 2 2 2 2 2 2 2 1 1 1 1 0 " + newLine
			+ "t 3 3 3 3 3 2 2 2 2 2 2 2 2 2 1 1 1 1 0 " + newLine
			+ "l 2 2 2 2 2 2 2 2 2 2 2 2 2 2 1 1 1 1 0 " + newLine
			+ "e 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 0 " + newLine
			+ "  0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 " + newLine;
		String result = baos.toString();
		assertEquals(expected, result);
	}
	
	// calculate Longest Common Subsequences
	@Test
	public void testLcs02() {
		
		// given
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		
		LCS l = new LCS();
		String s0 = "f1!!f2!!f3!!";
		String s1 = "f1!!f3!!";
		
		// when
		int[][] lcs = l.lcs(s0.toCharArray(), s1.toCharArray());
		l.debug(ps, lcs, s0.toCharArray(), s1.toCharArray());

		// then
		String expected = 
		  "  f 1 ! ! f 2 ! ! f 3 ! ! " + newLine 
		+ "f 8 7 7 7 7 6 6 5 4 3 2 1 0 " + newLine 
		+ "1 7 7 6 6 6 6 6 5 4 3 2 1 0 " + newLine
		+ "! 6 6 6 6 6 6 6 5 4 3 2 1 0 " + newLine
		+ "! 5 5 5 5 5 5 5 5 4 3 2 1 0 " + newLine
		+ "f 4 4 4 4 4 4 4 4 4 3 2 1 0 " + newLine
		+ "3 3 3 3 3 3 3 3 3 3 3 2 1 0 " + newLine
		+ "! 2 2 2 2 2 2 2 2 2 2 2 1 0 " + newLine
		+ "! 1 1 1 1 1 1 1 1 1 1 1 1 0 " + newLine
		+ "  0 0 0 0 0 0 0 0 0 0 0 0 0 " + newLine;
		String result = baos.toString();
		assertEquals(expected, result);
	}
	
	@Test
	public void testSubsequence01() {

		// given
		LCS l = new LCS();
		char[] c0 = "nematode knowledge".toCharArray();
		char[] c1 = "empty bottle".toCharArray();
		
		// when
		int[][] lcs = l.lcs(c0, c1);
		int[][] results = l.subsequence(lcs, c0, c1);
		
		// then
		assertEquals(7, results.length);
		
		assertEquals(1, results[0][0]);
		assertEquals(0, results[0][1]);
		assertEquals('e', c0[results[0][0]]);
		assertEquals('e', c1[results[0][1]]);

		assertEquals(2, results[1][0]);
		assertEquals(1, results[1][1]);
		assertEquals('m', c0[results[1][0]]);
		assertEquals('m', c1[results[1][1]]);
		
		assertEquals(4, results[2][0]);
		assertEquals(3, results[2][1]);
		assertEquals('t', c0[results[2][0]]);
		assertEquals('t', c1[results[2][1]]);

		assertEquals(8, results[3][0]);
		assertEquals(5, results[3][1]);
		assertEquals(' ', c0[results[3][0]]);
		assertEquals(' ', c1[results[3][1]]);

		assertEquals(11, results[4][0]);
		assertEquals(7, results[4][1]);
		assertEquals('o', c0[results[4][0]]);
		assertEquals('o', c1[results[4][1]]);

		assertEquals(13, results[5][0]);
		assertEquals(10, results[5][1]);
		assertEquals('l', c0[results[5][0]]);
		assertEquals('l', c1[results[5][1]]);

		assertEquals(17, results[6][0]);
		assertEquals(11, results[6][1]);
		assertEquals('e', c0[results[6][0]]);
		assertEquals('e', c1[results[6][1]]);
	}
	
	// test add new lines in middle
	@Ignore
	@Test
	public void testRun03() {
		
		// given
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		
		LCS l = new LCS();
		String s0 = "void func1()" + newLine
				+ "x += 1" + newLine
				+ "}" + newLine
				+ "void func2() {" + newLine
				+ "x += 2" + newLine
				+ "}" + newLine
				;
		
		String s1 = "void func1()" + newLine
				+ "x += 1" + newLine
				+ "}" + newLine
				+ "void functhreehalves() {" + newLine
				+ "x += 1.5" + newLine
				+ "}" + newLine
				+ "void func2() {" + newLine
				+ "x += 2" + newLine
				+ "}" + newLine
				;
		
		// when
		l.diff(ps, s0.toCharArray(), s1.toCharArray());
		
		// then
		String expected = "void func1()" + newLine
				+ "x += 1" + newLine
				+ "}" + newLine
				+ "+ void functhreehalves() {" + newLine
				+ "+ x += 1.5" + newLine
				+ "+ }" + newLine
				+ "void func2() {" + newLine
				+ "x += 2" + newLine
				+ "}" + newLine
				;
		
		String result = baos.toString();
		assertEquals(expected, result);
	}
	
	// test removes lines in middle
	@Test
	public void testDiff01() {
		
		// given
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		
		LCS l = new LCS();
		
		String s0 = "f1" + newLine
				+ "f2" + newLine
				+ "f3" + newLine
				;
		
		String s1 = "f1" + newLine
				+ "f3" + newLine
				;
		
		// when
		l.diff(ps, s0.toCharArray(), s1.toCharArray());
		
		// then
		String expected = "f1" + newLine
				+ " - f2" + newLine
				+ "f3" + newLine
				;
		
		String result = baos.toString();
		System.out.println (result);
		assertEquals(expected, result);
	}
}
