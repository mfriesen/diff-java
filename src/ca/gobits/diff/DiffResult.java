package ca.gobits.diff;

import java.util.List;

public class DiffResult {
	
	private List<DiffLine> list0;
	private List<DiffLine> list1;		

	public DiffResult(List<DiffLine> list0, List<DiffLine> list1) {
		this.list0 = list0;
		this.list1 = list1;
	}

	public List<DiffLine> getList0() {
		return list0;
	}

	public List<DiffLine> getList1() {
		return list1;
	}
}
