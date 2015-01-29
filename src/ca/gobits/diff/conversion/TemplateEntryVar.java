package ca.gobits.diff.conversion;

import java.util.List;

public class TemplateEntryVar implements TemplateEntry {

	private String text;
	private List<String> vars;
	
	public TemplateEntryVar(String text, List<String> vars) {
		this.text = text;
		this.vars = vars;
	}

	@Override
	public String getText() {
		return this.text;
	}

	@Override
	public List<String> getVars() {
		return this.vars;
	}
}
