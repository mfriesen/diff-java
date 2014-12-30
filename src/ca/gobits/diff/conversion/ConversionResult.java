package ca.gobits.diff.conversion;

import java.util.ArrayList;
import java.util.List;

public class ConversionResult {

	private static final String newLine = System.getProperty("line.separator");
		
	private StringBuilder text = new StringBuilder();
	private List<ConversionVariable> variables = new ArrayList<ConversionVariable>();
	
	public void addVariable(String name, String value) {
		this.variables.add(new ConversionVariable(name, value));
	}

	public void addText(String s) {
		this.text.append(s);
	}

	public void addTextWithNewLine(String s) {
		addText(s);
		this.text.append(newLine);
	}

	public String getText() {
		return this.text.toString();
	}	
}
