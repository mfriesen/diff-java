package ca.gobits.diff;

public class PileItem<T> {
	
	private T item;
	private PileItem<T> link;
	
	public PileItem(T item) {
		this.item = item;
	}

	public T getItem() {
		return this.item;
	}

	public PileItem<T> getLink() {
		return this.link;
	}

	public void setLink(PileItem<T> link) {
		this.link = link;
	}
}

