package ca.gobits.diff;

import java.util.Comparator;

public class PileComparator<T extends Comparable<? super T>> implements Comparator<Pile<PileItem<T>>> {

	@Override
	public int compare(Pile<PileItem<T>> o1, Pile<PileItem<T>> o2) {
		return o1.peek().getItem().compareTo(o2.peek().getItem());
	}

}
