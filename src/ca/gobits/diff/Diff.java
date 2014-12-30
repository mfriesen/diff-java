package ca.gobits.diff;

import java.io.PrintStream;
import java.util.List;

public interface Diff {

	void diff(PrintStream ps, DiffResult dr);
	
	DiffResult diffByLine(String s0, String s1);
	
	DiffResult diffByChar(String s0, String s1);
	
	int matchFirst(List<DiffLine> list0, List<DiffLine> list1, int start0, int start1);
	
	int matchLast(List<DiffLine> list0, List<DiffLine> list1, int end0, int end1);
}
