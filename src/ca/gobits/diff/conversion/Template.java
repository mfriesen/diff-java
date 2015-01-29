package ca.gobits.diff.conversion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class Template {

	private int uniqueKey = 1;
	private Set<String> usedVariables = new HashSet<String>();
	private List<TemplateEntry> lines = new ArrayList<TemplateEntry>();
	
	public String render(Map<String, String> attrs) {
		
		TemplateEntry te = this.lines.get(0);
		String s = new String(te.getText());
		
		List<String> vars = te.getVars();
		
		if (vars.size() != attrs.size()) {
			throw new RuntimeException("expected variables " + vars.size() + " got " + attrs.size());
		}
		
		for (String var : vars) {
			String value = attrs.get(var);
			if (value != null) {
				s = s.replaceAll(Pattern.quote(var), value);
			} else {
				throw new RuntimeException("missing var " + var);
			}
		}
		
		return s;
	}
	
	public String getNextVariableName(String name) {
		
		if (this.usedVariables.contains(name)) {
			name = name + this.uniqueKey;
			this.uniqueKey++;
		} else {
			this.usedVariables.add(name);
		}
		
		return name;
	}

	public void addEntry(TemplateEntry te) {
		this.lines.add(te);
	}
}
