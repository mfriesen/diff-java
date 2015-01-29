package ca.gobits.diff;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DiffTest {

	private static final String newLine = System.getProperty("line.separator");
	
	private DiffImpl d = new DiffImpl();
	
	// test removes / add lines in middle
	@Test
	public void testDiff01() {
		
		// given
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
				
		String s0 = "f1" + newLine
				+ "f2" + newLine
				+ "f9" + newLine
				+ "f4" + newLine
				+ "f5" + newLine
				+ "f9" + newLine
				+ "f6" + newLine
				;
		
		String s1 = "f1" + newLine
				+ "f8" + newLine
				+ "f4" + newLine
				+ "f7" + newLine
				+ "f6" + newLine
				;
		
		// when
		DiffResult dr = this.d.diffByLine(s0, s1);
		this.d.diff(ps, dr);
				
		// then
		String expected = "f1" + newLine
				+ "+ f2" + newLine
				+ "+ f9" + newLine
				+ "- f8" + newLine
				+ "f4" + newLine
				+ "+ f5" + newLine
				+ "+ f9" + newLine
				+ "- f7" + newLine
				+ "f6" + newLine
				;
		
		String result = baos.toString();
		assertEquals(expected, result);
	}
	
	// test add new lines in middle
	@Test
	public void testDiff02() {
		
		// given
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		
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
		DiffResult dr = this.d.diffByLine(s0, s1);
		this.d.diff(ps, dr);
		
		// then
		String expected = "void func1()" + newLine
				+ "x += 1" + newLine
				+ "}" + newLine
				+ "- void functhreehalves() {" + newLine
				+ "- x += 1.5" + newLine
				+ "- }" + newLine
				+ "void func2() {" + newLine
				+ "x += 2" + newLine
				+ "}" + newLine
				;
		
		String result = baos.toString();
		assertEquals(expected, result);
	}

	@Test
	public void testDiff03() {
		
		// given
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		
		String s0 = "#include <stdio.h>" + newLine
				+ newLine
				+ "// Frobs foo heartily" + newLine
				+ "int frobnitz(int foo)" + newLine
				+ "{" + newLine
				+ "int i;" + newLine
				+ "for(i = 0; i < 10; i++)" + newLine
				+ "{" + newLine
				+ "printf(\"Your answer is: \");" + newLine
				+ "printf(\"%d\n\", foo);" + newLine
				+ "}" + newLine
				+ "}" + newLine
				+ newLine
				+ "int fact(int n)" + newLine
				+ "{" + newLine
				+ "if(n > 1)" + newLine
				+ "{" + newLine
				+ "return fact(n-1) * n;" + newLine
				+ "}" + newLine
				+ "return 1;" + newLine
				+ "}" + newLine
				+ newLine
				+ "int main(int argc, char **argv)" + newLine
				+ "{" + newLine
				+ "frobnitz(fact(10));" + newLine
				+ "}" + newLine 
				;
		
		String s1 = 
				"#include <stdio.h>" + newLine
				+ newLine
				+ "int fib(int n)" + newLine
				+ "{" + newLine
				+ "if(n > 2)" + newLine
				+ "{" + newLine
				+ "return fib(n-1) + fib(n-2);" + newLine
				+ "}" + newLine
				+ "return 1;" + newLine
				+ "}" + newLine
				+ newLine
				+ "// Frobs foo heartily" + newLine
				+ "int frobnitz(int foo)" + newLine
				+ "{" + newLine
				+ "int i;" + newLine
				+ "for(i = 0; i < 10; i++)" + newLine
				+ "{" + newLine
				+ "printf(\"%d\n\", foo);" + newLine
				+ "}" + newLine
				+ "}" + newLine 
				+ newLine
				+ "int main(int argc, char **argv)" + newLine
				+ "{" + newLine
				+ "frobnitz(fib(10));" + newLine
				+ "}" + newLine
				;
		
		// when
		DiffResult dr = this.d.diffByLine(s0, s1);
		this.d.diff(ps, dr);
		
		// then
		String expected = 
		"#include <stdio.h>" + newLine
		+ newLine
		+ "- int fib(int n)" + newLine
		+ "- {" + newLine
		+ "- if(n > 2)" + newLine
		+ "- {" + newLine
		+ "- return fib(n-1) + fib(n-2);" + newLine
		+ "- }" + newLine
		+ "- return 1;" + newLine
		+ "- }" + newLine 
		+ "- " + newLine
		+ "// Frobs foo heartily" + newLine
		+ "int frobnitz(int foo)" + newLine
		+ "{" + newLine
		+ "int i;" + newLine
		+ "for(i = 0; i < 10; i++)" + newLine
		+ "{" + newLine
		+ "+ printf(\"Your answer is: \");" + newLine
		+ "printf(\"%d\n\", foo);" + newLine
		+ "}" + newLine
		+ "}" + newLine
		+ newLine
		+ "+ int fact(int n)" + newLine
		+ "+ {" + newLine
		+ "+ if(n > 1)" + newLine
		+ "+ {" + newLine
		+ "+ return fact(n-1) * n;" + newLine
		+ "+ }" + newLine
		+ "+ return 1;" + newLine
		+ "+ }" + newLine
		+ "+ " + newLine
		+ "int main(int argc, char **argv)" + newLine
		+ "{" + newLine
		+ "+ frobnitz(fact(10));" + newLine
		+ "- frobnitz(fib(10));" + newLine
		+ "}" + newLine; 
		
		String result = baos.toString();
		assertEquals(expected, result);
	}

	@Test
	public void testDiff04() {
		
		// given
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		
		String s0 = "<h3>Direct Download:</h3>" + newLine
				+ "MP3 Audio1" + newLine
				+ "|" + newLine
				+ "OGG Audio1" + newLine
				+ "|" + newLine
				+ "Video1" + newLine
				+ "a" + newLine;

		String s1 = "<h3>Direct Download:</h3>" + newLine
				+ "MP3 Audio2" + newLine
				+ "MP3 Audio3" + newLine
				+ "|" + newLine
				+ "OGG Audio2" + newLine
				+ "|" + newLine
				+ "Video2" + newLine
				+ "a" + newLine;
		
		// when
		DiffResult dr = this.d.diffByLine(s0, s1);
		this.d.diff(ps, dr);
		
		// then
		String expected = "<h3>Direct Download:</h3>" + newLine
				+ "+ MP3 Audio1" + newLine
				+ "- MP3 Audio2" + newLine
				+ "- MP3 Audio3" + newLine
				+ "|" + newLine
				+ "+ OGG Audio1" + newLine
				+ "- OGG Audio2" + newLine
				+ "|" + newLine
				+ "+ Video1" + newLine
				+ "- Video2" + newLine
				+ "a" + newLine;
		
		String result = baos.toString();
		assertEquals(expected, result);
	}

	@Test
	public void testDiffByChar01() {
		// given
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);

		String s0 = "test";
		String s1 = "es";
		
		// when
		DiffResult result = this.d.diffByChar(s0, s1);
		this.d.diff(ps, result);
		
		// then
		assertEquals(4, result.getList0().size());
		assertEquals(2, result.getList1().size());
		
		String expected = "+ t" + newLine
						+ "e" + newLine
						+ "s" + newLine
						+ "+ t" + newLine;
		
		String r = baos.toString();
		assertEquals(expected, r);
	}
	
	// calculate Longest Common Subsequences
	@Test
	public void testLongestCommonSubsequences01() {

		// given
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);

		List<DiffLine> s0 = toDiffLines("nematode knowledge");
		List<DiffLine> s1 = toDiffLines("empty bottle");

		// when
		int[][] lcs = this.d.longestCommonSequence(s0, s1);
		this.d.debug(ps, lcs, s0, s1);

		// then
		String expected = 
				   " n e m a t o d e   k n o w l e d g e " + newLine
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
				+ " 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 " + newLine;
		
		String result = baos.toString();
		assertEquals(expected, result);
	}

	@Test
	public void testSubsequence01() {

		// given
		List<DiffLine> s0 = toDiffLines("nematode knowledge");
		List<DiffLine> s1 = toDiffLines("empty bottle");

		// when
		int[][] lcs = this.d.longestCommonSequence(s0, s1);
		int[][] results = this.d.subsequence(lcs);

		// then
		assertEquals(7, results.length);

		assertEquals(1, results[0][0]);
		assertEquals(0, results[0][1]);
		assertEquals("e", s0.get(results[0][0]).getLine());
		assertEquals("e", s1.get(results[0][1]).getLine());

		assertEquals(2, results[1][0]);
		assertEquals(1, results[1][1]);
		assertEquals("m", s0.get(results[1][0]).getLine());
		assertEquals("m", s1.get(results[1][1]).getLine());

		assertEquals(4, results[2][0]);
		assertEquals(3, results[2][1]);
		assertEquals("t", s0.get(results[2][0]).getLine());
		assertEquals("t", s1.get(results[2][1]).getLine());

		assertEquals(8, results[3][0]);
		assertEquals(5, results[3][1]);
		assertEquals(" ", s0.get(results[3][0]).getLine());
		assertEquals(" ", s1.get(results[3][1]).getLine());

		assertEquals(11, results[4][0]);
		assertEquals(7, results[4][1]);
		assertEquals("o", s0.get(results[4][0]).getLine());
		assertEquals("o", s1.get(results[4][1]).getLine());

		assertEquals(13, results[5][0]);
		assertEquals(10, results[5][1]);
		assertEquals("l", s0.get(results[5][0]).getLine());
		assertEquals("l", s1.get(results[5][1]).getLine());

		assertEquals(17, results[6][0]);
		assertEquals(11, results[6][1]);
		assertEquals("e", s0.get(results[6][0]).getLine());
		assertEquals("e", s1.get(results[6][1]).getLine());
	}

	private List<DiffLine> toDiffLines(String s) {
		List<DiffLine> list = new ArrayList<DiffLine>(s.length());
		
		for (int i = 0; i < s.length(); i++) {
			String ss = "" + s.charAt(i);
			DiffLine dl = this.d.createDiff(ss, i);
			list.add(dl);
		}
		
		return list;
	}
}