package ca.gobits.diff;

import java.io.PrintStream;

public interface Diff {

	void diff(PrintStream ps, DiffResult dr);
	
	DiffResult diffByLine(String s0, String s1);
	
	DiffResult diffByChar(String s0, String s1);
}
