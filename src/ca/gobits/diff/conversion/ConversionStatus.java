package ca.gobits.diff.conversion;

import java.util.HashSet;
import java.util.Set;

public class ConversionStatus {

	private int uniqueKey = 1;
	private Set<String> usedVariables = new HashSet<String>();
	
	public ConversionStatus() {		
	}

	public void addUsedVariable(String name) {
		this.usedVariables.add(name);
	}

	public String getNextVariableName(String name) {
		
		if (this.usedVariables.contains(name)) {
			name = name + this.uniqueKey;
			this.uniqueKey++;
		}
		
		return name;
	}
}
