package ca.gobits.diff;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PatienceSort {

	public <T extends Comparable<? super T>> List<Pile<PileItem<T>>> sort(List<T> source) {
		
		PileComparator<T> comparator = new PileComparator<T>();
		List<Pile<PileItem<T>>> piles = new ArrayList<Pile<PileItem<T>>>();
		
		for (T t : source) {
			
			Pile<PileItem<T>> pile = new Pile<PileItem<T>>();
			PileItem<T> pileItem = new PileItem<T>(t);
			pile.push(pileItem);			
			
			int pos = Collections.binarySearch(piles, pile, comparator);			
			
			if (pos < 0) {
				pos = ~pos;
			}
						
			if (pos != piles.size()) {
				piles.get(pos).push(pileItem);
			} else {
				piles.add(pile);
			}
			
			if (pos > 0) {
				PileItem<T> link = piles.get(pos - 1).peek();
				pileItem.setLink(link);
			}
		}
	        
		return piles;
	}
	
	public <T extends Comparable<? super T>> List<T> longestIncreasingSubsequence(List<Pile<PileItem<T>>> pile) {
		
		int size = pile.size();
		List<T> list = new ArrayList<T>(size);
		
		if (!pile.isEmpty()) {
			PileItem<T> last = pile.get(size - 1).peekLast();
			while (last != null) {
				list.add(0, last.getItem());
				last = last.getLink();
			}
		}
				
		return list;
	}
	
	public <T extends Comparable<? super T>> void debugPiles(PrintStream ps, List<Pile<PileItem<T>>> piles) {

		int row = 0;
		boolean done = false;

		while (!done) {
			
			done = true;
			
			for (Pile<PileItem<T>> pile : piles) {
				if (row < pile.size()) {
					done = false;
					ps.print (pile.get(row).getItem());
				}
				ps.print ("\t");
			}
			row++;
			ps.println();
		}
	}

}
