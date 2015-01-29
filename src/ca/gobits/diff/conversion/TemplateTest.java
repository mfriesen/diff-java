package ca.gobits.diff.conversion;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TemplateTest {

	@Test
	public void testRender01() {
		
		// given
		Template tl = new Template();
		String line = "This is a {{ test }}";
		tl.addEntry(new TemplateEntryVar(line, Arrays.asList("{{ test }}")));
		
		Map<String, String> attrs = new HashMap<String, String>();
		attrs.put("{{ test }}", "sample");
		
		// when
		String result = tl.render(attrs);
		
		// then
		assertEquals("This is a sample", result);
	}

}
