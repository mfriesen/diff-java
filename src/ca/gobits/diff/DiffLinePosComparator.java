package ca.gobits.diff;

import java.util.Comparator;

public class DiffLinePosComparator implements Comparator<DiffLine> {

	public static final DiffLinePosComparator INSTANCE = new DiffLinePosComparator();
	
	@Override
	public int compare(DiffLine o1, DiffLine o2) {
		return Integer.valueOf(o1.getPos()).compareTo(Integer.valueOf(o2.getPos()));
	}

}
