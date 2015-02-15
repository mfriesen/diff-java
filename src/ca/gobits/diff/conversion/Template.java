package ca.gobits.diff.conversion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Template {
	
	private List<String> lines = new ArrayList<String>();
	private Set<String> usedVariables = new HashSet<String>();
	
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

	public ConversionVariable addVariable(ConversionVariable var) {
		
		int count = 0;
		String name = var.getName();
		
		while (this.usedVariables.contains(name)) {
			
			count++;
			name = name + count;
		}

		return new ConversionVariable(name, var.getValue());
	}
}
