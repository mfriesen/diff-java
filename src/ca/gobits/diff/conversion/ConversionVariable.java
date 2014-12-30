package ca.gobits.diff.conversion;

public class ConversionVariable {

	private String name;
	private String value;
	
	public ConversionVariable(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return this.name;
	}

	public String getValue() {
		return this.value;
	}
}
