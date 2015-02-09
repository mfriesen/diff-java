package ca.gobits.diff.conversion;

import java.util.ArrayList;
import java.util.List;

public class Template {
	
	private List<String> lines = new ArrayList<String>();
	
	public Template()
	{
	}
	
	public void addLine(String line) {
		this.lines.add(line);
	}
	
	public void addLine(int index, String line) {
		this.lines.add(index, line);
	}
	
	public List<String> getLines() {
		return this.lines;
	}
}
