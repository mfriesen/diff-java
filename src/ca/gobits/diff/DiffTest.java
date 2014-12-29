package ca.gobits.diff;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

public class DiffTest {

	private static final String newLine = System.getProperty("line.separator");
	
	private Diff d = new DiffImpl();
	
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
		DiffResult dr = d.diffByLine(s0, s1);
		d.diff(ps, dr);
				
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
		DiffResult dr = d.diffByLine(s0, s1);
		d.diff(ps, dr);
		
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
		DiffResult dr = d.diffByLine(s0, s1);
		d.diff(ps, dr);
		
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
	public void testDiffByChar01() {
		// given
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);

		String s0 = "test";
		String s1 = "es";
		
		// when
		DiffResult result = d.diffByChar(s0, s1);
		d.diff(ps, result);
		
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
}