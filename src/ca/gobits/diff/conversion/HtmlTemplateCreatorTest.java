package ca.gobits.diff.conversion;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class HtmlTemplateCreatorTest {
	
	private static final String newLine = System.getProperty("line.separator");
	
	private HtmlTemplateCreator creator = new HtmlTemplateCreator();
	
	@Test
	public void testCheckForMatchWithVariables01() {
		// given
		ConversionStatus cs = new ConversionStatus();
		String s0 = "<link rel='shortlink1' href='http://localhost/?p=74337' />";
		String s1 = "<link rel='shortlink2' href='http://localhost/?p=74336' />";
		
		// when
		List<ConversionVariable> result = this.creator.checkForMatchWithVariables(cs, s0, s1);
		
		// then
		assertEquals(2, result.size());

		ConversionVariable v0 = result.get(0);
		assertEquals("shortlink1_rel", v0.getName());
		assertEquals("shortlink1", v0.getValue());

		ConversionVariable v1 = result.get(1);
		assertEquals("shortlink1_href", v1.getName());
		assertEquals("http://localhost/?p=74337", v1.getValue());
	}
	
	@Test
	public void testCheckForMatchWithVariables02() {
		// given
		ConversionStatus cs = new ConversionStatus();
		String s0 = "<link rel='shortlink' href='http://localhost/?p=74337' />";
		String s1 = "<link rel='shortlink' href='http://localhost/?p=74336' />";
		
		// when
		List<ConversionVariable> result = this.creator.checkForMatchWithVariables(cs, s0, s1);
		
		// then
		assertEquals(1, result.size());

		ConversionVariable v1 = result.get(0);
		assertEquals("shortlink_href", v1.getName());
		assertEquals("http://localhost/?p=74337", v1.getValue());
	}
	
	@Test
	public void testCheckForMatchWithVariables03() {
		// given
		ConversionStatus cs = new ConversionStatus();
		String s0 = "<title>Test #1</title>";
		String s1 = "<title>Junk #2</title>";
		
		// when
		List<ConversionVariable> result = this.creator.checkForMatchWithVariables(cs, s0, s1);
		
		// then
		assertEquals(1, result.size());
		
		ConversionVariable v = result.iterator().next();
		assertEquals("title", v.getName());
		assertEquals("Test #1", v.getValue());
	}
	
	@Test
	public void testCheckForMatchWithVariables04() {
		// given
		ConversionStatus cs = new ConversionStatus();
		String s0 = "<body class=\"single single-post postid-73887 single-format-standard\">";
		String s1 = "<body class=\"single single-post postid-73889 single-format-standard\">";
		
		// when
		List<ConversionVariable> result = this.creator.checkForMatchWithVariables(cs, s0, s1);
		
		// then
		assertEquals(1, result.size());
		
		ConversionVariable v = result.iterator().next();
		assertEquals("staticText", v.getName());
		assertEquals("postid-73887", v.getValue());
	}
	
	@Test
	public void testCheckForMatchWithVariables05() {
		// given
		ConversionStatus cs = new ConversionStatus();
		String s0 = "<li><img src=\"http://engine/delicious.gif\" alt=\"del\" /> <a href=\"http://del.icio.us/post?url=http://localhost/74337/best-of-133/\" target=\"_blank\">del.icio.us</a></li>";
		String s1 = "<li><img src=\"http://engine/delicious.gif\" alt=\"del\" /> <a href=\"http://del.icio.us/post?url=http://localhost/73887/git-132/\" target=\"_blank\">del.icio.us</a></li>";
		
		// when
		List<ConversionVariable> result = this.creator.checkForMatchWithVariables(cs, s0, s1);
		
		// then
		assertEquals(1, result.size());
		
		ConversionVariable v = result.iterator().next();
		assertEquals("href", v.getName());
		assertEquals("http://del.icio.us/post?url=http://localhost/74337/best-of-133/", v.getValue());
	}
	
	@Test
	public void testFormatHtml01() {
		// given
		String s = "<title>Best Of Coder Radio 2014 | CR 133 | Jupiter Broadcasting</title>";
		
		// when
		String result = this.creator.formatHtml(s);
		
		// then
		assertEquals(s, result.trim());
	}
	
	@Test
	public void testFormatHtml02() {
		// given
		String s = "<a href=\"http://localhost/click\">Think</a> <strong>|</strong>";
		
		// when
		String result = this.creator.formatHtml(s);
		
		// then
		assertEquals("<a href=\"http://localhost/click\">Think</a>" + newLine + " <strong>|</strong>", result.trim());
	}
	
	@Test
	public void testFormatHtml03() {
		// given
		String s = "<a href=\"http://www.jupiterbroadcasting.com/\" title=\"Jupiter Broadcasting\"><img src=\"http://jb6.cdn.scaleengine.net/wp-content/themes/jb2014/images/logo.png\" alt=\"Jupiter Broadcasting\" /></a>";
		
		// when
		String result = this.creator.formatHtml(s);
		
		// then
		assertEquals("<a href=\"http://www.jupiterbroadcasting.com/\" title=\"Jupiter Broadcasting\">" + newLine
			+ "<img src=\"http://jb6.cdn.scaleengine.net/wp-content/themes/jb2014/images/logo.png\" alt=\"Jupiter Broadcasting\" />" + newLine
			+ "</a>" + newLine, result);
	}
	
	@Test
	public void testFormatHtml04() {
		// given
		String s = "<h2><a href=\"http://www.jupiterbroadcasting.com/74337/best-of-coder-radio-2014-cr-133/\" title=\"Best Of Coder Radio 2014 | CR 133\">Best Of Coder Radio 2014 | CR 133</a></h2>";
			
		// when
		String result = this.creator.formatHtml(s);
		
		// then
		assertEquals("<h2>" + newLine
			+ "<a href=\"http://www.jupiterbroadcasting.com/74337/best-of-coder-radio-2014-cr-133/\" title=\"Best Of Coder Radio 2014 | CR 133\">Best Of Coder Radio 2014 | CR 133</a>" + newLine
			+ "</h2>" + newLine, result);
	}
	
	@Test
	public void testFormatHtml05() {
		// given
		String s = "			<ul><li><a href=\"http://localhost\" rel=\"bookmark\" title=\"May 20, 2013\">Me</a></li>";
			
		// when
		String result = this.creator.formatHtml(s);
		
		// then
		assertEquals("			" + newLine 
				+ "<ul>" + newLine
				+ "<li>" + newLine
				+ "<a href=\"http://localhost\" rel=\"bookmark\" title=\"May 20, 2013\">Me</a>" + newLine
				+ "</li>" + newLine
				, result);
	}
	
	@Test
	public void testFormatHtml06() {
		// given
		String s = "adasda<!-- test -->ssdfsdf";
			
		// when
		String result = this.creator.formatHtml(s);
		
		// then
		assertEquals("adasda" + newLine
				+ "<!-- test -->" + newLine 
				+ "ssdfsdf" + newLine
				, result);
	}
	
	@Test
	public void testFormatHtml07() {
		// given
		String s = "<a href='#'>adasda</a><!-- test -->ssdfsdf";
			
		// when
		String result = this.creator.formatHtml(s);
		
		// then
		assertEquals("<a href='#'>adasda</a>" + newLine
				+ "<!-- test -->" + newLine 
				+ "ssdfsdf" + newLine
				, result);
	}
	
	@Test
	public void testFormatHtml08() {
		// given
		String s = "<!-- test -->";
			
		// when
		String result = this.creator.formatHtml(s);
		
		// then
		assertEquals("<!-- test -->" + newLine, result);
	}
	
	@Test
	public void testFormatHtml09() {
		// given
		String s = "<!--[if lte IE 7]><script type=\"text/javascript\" src=\"http://localhost/ie.js\"></script><![endif]-->";
		
		// when
		String result = this.creator.formatHtml(s);
		
		// then
		assertEquals("<!--[if lte IE 7]>" + newLine
				+ "<script type=\"text/javascript\" src=\"http://localhost/ie.js\"></script>" + newLine
				+ "<![endif]-->" + newLine
				, result);
	}
	
	@Test
	public void testFormatHtml10() {
		// given
		String s = "        <link type=\"text/html\" title=\"Flattr this!\" href=\"https://localhost\" />" + newLine
				+ "	<!--Simple Popup Plugin v4.0 / RH Mods-->";
		
		// when
		String result = this.creator.formatHtml(s);
		
		// then
		assertEquals("        " + newLine
				+ "<link type=\"text/html\" title=\"Flattr this!\" href=\"https://localhost\" />" + newLine
				+ "<!--Simple Popup Plugin v4.0 / RH Mods-->" + newLine, result);
	}
	
	@Test
	public void testFormatHtml11() {
		// given
		String s = "<body>Test</body>";
		
		// when
		String result = this.creator.formatHtml(s);
		
		// then
		assertEquals("<body>" + newLine
				+ "Test" + newLine
				+ "</body>" + newLine, result);
	}
	
	@Test
	public void testFormatHtml12() {
		// given
		String s = "<body>" + newLine + "Test</body>";
		
		// when
		String result = this.creator.formatHtml(s);
		
		// then
		assertEquals("<body>" + newLine
				+ "Test" + newLine
				+ "</body>" + newLine, result);
	}
	
	// Simple Template
	@Test
	public void testCreate01() {
		// given
		String s0 = "<html><head></head><body>Simple</body></html>";
		String s1 = "<html><head></head><body>Not Simple</body></html>";
		
		// when
		Template template = this.creator.create(s0, s1);
		List<String> results = template.getLines();
		
		// then
		assertEquals(6, results.size());
		assertEquals("<html>", results.get(0));
		assertEquals("<head></head>", results.get(1));
		assertEquals("<body>", results.get(2));
		assertEquals("{{ content }}", results.get(3));
		assertEquals("</body>", results.get(4));
		assertEquals("</html>", results.get(5));
	}
	
	// Same Template
	@Test
	public void testCreate02() {
		// given
		String s0 = "<html><head></head><body>Simple</body></html>";
		String s1 = "<html><head></head><body>Simple</body></html>";
		
		// when
		Template template = this.creator.create(s0, s1);
		List<String> results = template.getLines();
		
		// then
		assertEquals(6, results.size());
		assertEquals("<html>", results.get(0));
		assertEquals("<head></head>", results.get(1));
		assertEquals("<body>", results.get(2));
		assertEquals("Simple", results.get(3));
		assertEquals("</body>", results.get(4));
		assertEquals("</html>", results.get(5));
	}
	
	// test <head> variable.
	@Test
	public void testCreate03() {
		fail();
	}
}
