package ca.gobits.diff.conversion;

import java.util.Collections;
import java.util.List;

public class TemplateEntryString implements TemplateEntry {

	private String text;
	
	public TemplateEntryString(String text) {
		this.text = text;
	}

	@Override
	public String getText() {
		return this.text;
	}

	@Override
	public List<String> getVars() {
		return Collections.emptyList();
	}
}
