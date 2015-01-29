package ca.gobits.diff;

import java.io.PrintStream;
import java.util.List;

public interface Diff {

	void diff(PrintStream ps, DiffResult dr);

	boolean isEqual(DiffLine l0, DiffLine l1);

	DiffResult diffByLine(String s0, String s1);

	DiffResult diffByChar(String s0, String s1);

	String[] splitByNewLine(String s);

	String getNewLine();

	int[][] longestCommonSequence(List<DiffLine> c0, List<DiffLine> c1);
}
