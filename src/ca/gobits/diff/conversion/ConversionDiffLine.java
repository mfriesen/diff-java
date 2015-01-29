package ca.gobits.diff.conversion;

import java.util.Collections;
import java.util.List;

import ca.gobits.diff.DiffLine;

public class ConversionDiffLine {

	private DiffLine diffLine;
	private List<ConversionVariable> variables = Collections.emptyList();
	
	public ConversionDiffLine(DiffLine diffLine) {
		this.diffLine = diffLine;
	}

	public DiffLine getDiffLine() {
		return this.diffLine;
	}

	public List<ConversionVariable> getVariables() {
		return this.variables;
	}

	public void setVariables(List<ConversionVariable> variables) {
		this.variables = variables;
	}
}
