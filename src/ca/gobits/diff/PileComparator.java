package ca.gobits.diff;

import java.util.Comparator;

public class PileComparator<T> implements Comparator<Pile<PileItem<T>>> {

	private Comparator<T> comparator;
	
	public PileComparator(Comparator<T> comparator) {
		this.comparator = comparator;
	}
	
	@Override
	public int compare(Pile<PileItem<T>> o1, Pile<PileItem<T>> o2) {
		return this.comparator.compare(o1.peek().getItem(), o2.peek().getItem());
	}
}