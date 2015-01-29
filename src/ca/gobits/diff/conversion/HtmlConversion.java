package ca.gobits.diff.conversion;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.gobits.diff.Diff;
import ca.gobits.diff.DiffImpl;
import ca.gobits.diff.DiffLine;
import ca.gobits.diff.DiffResult;

public class HtmlConversion implements Conversion {

	private static Diff d = new DiffImpl();
	
	private static Pattern attributeTag = Pattern.compile("<[\\w]*(.*)>");
	private static Pattern attributeKeyValue = Pattern.compile("(\\w+)=['\"]?([^'\"]+)['\"]?");
//	private static final Pattern emptyLine = Pattern.compile("^\\s*$");
	
	// regular html tag
	private static Pattern htmlTag = Pattern.compile("<([^>]*)>([^>]*)</?([^>]*)>");
	
	@Override
	public String pre(String s) {
//		System.out.println  (s);
		return s;
	}

	@Override
	public String post(String s) {
		return s;
	}
	
	public static void main(String[] args) throws Exception {
		
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		PrintStream ps = new PrintStream(baos);
		
		String s0 = new String(Files.readAllBytes(Paths.get("data/index1.html")));
		String s1 = new String(Files.readAllBytes(Paths.get("data/index2.html")));
				
		s0 = formatHtml(s0);
		s1 = formatHtml(s1);
		
		DiffResult result = d.diffByLine(s0, s1);
		List<DiffLine> l0 = result.getList0();
		List<DiffLine> l1 = result.getList1();

		int top = findTopMatch(l0, l1);
		int bottom = findBottomMatch(l0, l1);
		
		System.out.println ("TOP: " + top + " BOTTOM: " + bottom);
//		List<DiffLine> main = l0.size() > l1.size() ? l0 : l1;
//		List<DiffLine> compare = l0.size() > l1.size() ? l1 : l0;
//
//		List<ConversionDiffLine> topDiffs = createTopConversionDiffLines(l0, l1);
		
//		createTemplate(main, compare);
//		for (DiffLine dl : l0) {
//			
//			if (dl.getMatch() == -1) {
//				System.out.println (dl.getMatch() + " - " + dl.getLine());
//			} else {
//				System.out.println (dl.getMatch());
//			}
//		}
//		
//		for (int i = 0; i < l0.size(); i++) {
//			DiffLine d0 = l0.get(i);
//			System.out.println (i + ":" + d0.getMatch());
////			System.out.println (l0.)
//		}
		
//		System.out.println (result.getList0().get(919).getLine());
//		System.out.println (result.getList1().get(970).getLine());
//		createTemplate(result.getList0(), result.getList1());
		
//		System.out.println ("---------------------------------------------");
//		System.out.println (cr.getText());
//		String r = baos.toString();
//		System.out.println(r);
	}
	
	private static int findTopMatch(List<DiffLine> l0, List<DiffLine> l1) {
		
		int pos = 0;
		
		ConversionStatus cs = new ConversionStatus();
		
		for (int i = 0; i < l0.size(); i++) {

			DiffLine d0 = l0.get(i);
			
			if (d0.getMatch() != i) {
				
				DiffLine d1 = l1.get(i);
				
				List<ConversionVariable> vars = checkForMatchWithVariables(cs, d0.getLine(), d1.getLine());
				
				if (vars.isEmpty()) {
//					System.out.println (d0.getPos() + " " + d0.getLine());
//					System.out.println (d1.getPos() + " " + d1.getLine());
					break;
				}	
			}
		
			pos = i;
		}
		
		return pos;
	}

	private static int findBottomMatch(List<DiffLine> l0, List<DiffLine> l1) {
		// TODO Auto-generated method stub
		return 0;
	}

	private static List<ConversionDiffLine> createTopConversionDiffLines(List<DiffLine> l0, List<DiffLine> l1) {

		List<ConversionDiffLine> list = new ArrayList<ConversionDiffLine>();
		
		DiffLine df0 = l0.get(0);
		DiffLine df1 = l1.get(0);
		
		if (df0.getMatch() == df1.getMatch()) {
			
			if (df0.getMatch() != -1 && df1.getMatch() != -1) {
				
			}
			
//			int i1 = 0;
//		
//			for (int i = 0; i < l0.size(); i++) {
//				
//				DiffLine dl0 = l0.get(i);
//				
//				if (dl0.getMatch() != -1) {
//					i1 = dl0.getMatch();
//				}
//				
//				if (dl0.getMatch() != -1) {
//					
//				} else {
//					
//				}
//			}
		}
				
		return list;
	}

	static Template createTemplate(List<DiffLine> main, List<DiffLine> compare) {
		List<TemplateEntry> entries = new ArrayList<TemplateEntry>(main.size());
		
		return null;
	}
	
	static String formatHtml(String s) {
		
		StringBuilder sb = new StringBuilder();
		String[] strs = d.splitByNewLine(s);
		
		for (String ss : strs) {

			int lastPos = 0;
			int tpos = isSelfContainedTag(ss, lastPos);
			int pos = tpos != -1 ? tpos : ss.indexOf(">");
			pos++;
				
			while (pos > -1 && pos < ss.length() - 1) {
				
				sb.append(substring(ss, lastPos, pos));
				lastPos = pos;
			
				tpos = isSelfContainedTag(ss, lastPos);
				if (tpos == -1) {
					
					pos = ss.indexOf(">", lastPos);
					if (pos != -1) {
						pos++;
					}
					
				} else {
					pos = tpos;
				}
			}
			
			pos = ss.length();
			sb.append(substring(ss, lastPos, pos));
		}
		
		return sb.toString();
	}
	
	private static String substring(String s, int begin, int end) {
		
		String ss = "";
		if (begin != end && begin != end - 1) {
			ss = s.substring(begin, end) + d.getNewLine();
		}
		
		return ss;
	}
	
	private static int isSelfContainedTag(String ss, int pos) {
		
		int match = -1;
		
		Matcher m = htmlTag.matcher(ss);
		
		if (m.find(pos)) {
			
			if (m.groupCount() == 3) {
				if (m.group(1).startsWith(m.group(3))) {
					match = m.end();
				}
			}
		}
		
		return match;
	}

	private static Template createTemplate3(List<DiffLine> list0, List<DiffLine> list1) {
		
		int i0 = 0, i1 = 0;
		int nomatch = 0;
		Template t = new Template();
		ConversionStatus cs = new ConversionStatus();
				
		while (i0 < list0.size()) {
			
			List<ConversionVariable> vars = Collections.emptyList();

			DiffLine df0 = list0.get(i0);

			if (df0.getMatch() != -1) {
				i1 = df0.getMatch();
			}
			
			DiffLine df1 = list1.get(i1);
			
			if (df0.getMatch() == -1 && df1.getMatch() == -1) {
				
				if (d.isEqual(df0, df1)) {
					df0.setMatch(df1.getPos());
					df1.setMatch(df0.getPos());
				} else {
					vars = checkForMatchWithVariables(cs, df0.getLine(), df1.getLine());
				}
			}
			
			if (df0.getMatch() != -1 /* || vars.size() == 2 || vars.size() == 1*/) {
				
				System.out.println (df0.getLine());
				i0++;
				i1++;
				
			} else {
				System.out.println (vars.size());
				break;
			}
//			int match0 = df0 != null ? df0.getMatch() : -1;
//			int match1 = df1 != null ? df1.getMatch() : -1;
			
//			if (match0)
		}
		
		return t;
	}
	
	private static Template createTemplate2(List<DiffLine> list0, List<DiffLine> list1) {
		
		int i0 = 0, i1 = 0;
		int nomatch = 0;
		Template t = new Template();
		ConversionStatus cs = new ConversionStatus();
				
		while (i0 < list0.size() || i1 < list1.size()) {
			
			DiffLine df0 = list0.get(i0);
			DiffLine df1 = list1.get(i1);
			int match0 = df0 != null ? df0.getMatch() : -1;
			int match1 = df1 != null ? df1.getMatch() : -1;
//			System.out.println (df0.getLine() + " || " + df1.getLine());
			if ( (match0 != -1 && match1 != -1) || (d.isEqual(df0, df1)) ) {
								
				System.out.println (df0.getPos() + " " + df1.getPos());
				t.addEntry(new TemplateEntryString(df0.getLine()));
				
				i0++;
				i1++;
				nomatch = 0;
				
			} else if (match0 == -1 && match1 == -1) {
				
				String line0 = df0 != null ? df0.getLine() : null;
				String line1 = df1 != null ? df1.getLine() : null;
				
				List<ConversionVariable> vars = Collections.emptyList();
				
				if (nomatch == 0 && df0 != null && df1 != null) {
					vars = checkForMatchWithVariables(cs, line0, line1);
				}
				
				if (!vars.isEmpty()) {
					
					TemplateEntryVar te = createTemplateEntry(t, vars, line0);
					t.addEntry(te);
					
					System.out.println (df0.getPos() + " " + df0.getMatch() + " " + te.getText());
					
					i0++;
					i1++;
					
				} else {
					
					nomatch++;
					System.out.println (df0.getPos() + " " + df0.getMatch() + " " + df0.getLine());

					i0++;
					i1++;
				}
			} else if (match0 == -1) {
				
				nomatch++;
				i0++;
			} else if (match1 == -1) {
				nomatch++;
				i1++;
			}
		}
		
		return t;	
	}

	private static TemplateEntryVar createTemplateEntry(Template t, List<ConversionVariable> vars, String text) {
		
		List<String> svars = new ArrayList<String>(vars.size());
		
		for (ConversionVariable var : vars) {
			
			String svar = "{{ page." + t.getNextVariableName(var.getName()) + " }}";
			svars.add(svar);
			
			text = text.replaceAll(Pattern.quote(var.getValue()), svar);			
		}

		return new TemplateEntryVar(text, svars);
	}

	public static ConversionResult diff(PrintStream ps, List<DiffLine> list0, List<DiffLine> list1) {
		
		int top = 0;
		int bottom = 1;
		
		DiffLine previousUnmatch = null;
		ConversionResult cr = new ConversionResult();
		ConversionStatus cs = new ConversionStatus();
				
		while (top < list0.size() && top < list1.size()) {

			DiffLine df0 = get(list0, top);
			DiffLine df1 = get(list1, top);
			
			boolean unmatched = processLine(cr, cs, df0, df1, previousUnmatch);
			
			if (unmatched) {
				
				if (previousUnmatch == null) {
					previousUnmatch = df0;
				} else {
					break;
				}
			}
			
			top++;
		}
		
		previousUnmatch = null;
		
		while (bottom < list0.size() && bottom < list1.size()) {
			
			DiffLine df0 = get(list0, list0.size() - bottom);
			DiffLine df1 = get(list1, list1.size() - bottom);
			
			boolean unmatched = processLine(cr, cs, df0, df1, previousUnmatch);
			
			if (unmatched) {
				
				if (previousUnmatch == null) {
					previousUnmatch = df0;
				} else {
					break;
				}
			}
			
			bottom++;
		}
		
		System.out.println ("TOP " + top + " bottom: " + bottom);
		return cr;
	}

	private static boolean processLine(ConversionResult cr, ConversionStatus cs, DiffLine df0, DiffLine df1, DiffLine previousUnmatch) {
		
		boolean unmatched = false;
		
		if ( (df0.getMatch() != -1 && df1.getMatch() != -1) || (d.isEqual(df0, df1)) ) {
			
			processedUnmatched(cr, previousUnmatch);
			
			cr.addTextWithNewLine(df0.getLine());
							
		} else {
			
			String line0 = df0.getLine();
			String line1 = df1.getLine();
							
			List<ConversionVariable> vars = checkForMatchWithVariables(cs, line0, line1);

			if (!vars.isEmpty()) {
				
				processedUnmatched(cr, previousUnmatch);
				
				for (ConversionVariable var : vars) {
					
					line0 = line0.replaceAll(Pattern.quote(var.getValue()), "{{ page." + var.getName() + " }}");						
				}
				
				cr.addTextWithNewLine(line0);
				
			} else {

				unmatched = true;
			}				
		}
		
		return unmatched;
	}
	
	private static void processedUnmatched(ConversionResult cr, DiffLine dl) {
		
		if (dl != null) {
			
			String line = dl.getLine();
			ConversionVariable var = createStaticTextVar(line);

			line = line.replaceAll(Pattern.quote(var.getValue()), "{{ page." + var.getName() + " }}");
			cr.addTextWithNewLine(line);
		}
	}

	static List<ConversionVariable> checkForMatchWithVariables(ConversionStatus cs, String s0, String s1) {
		
		List<ConversionVariable> vars = checkForRegexMatchWithVariables(s0, s1, htmlTag);

		if (vars.isEmpty()) {
			vars = checkForRegexMatchWithVariables(s0, s1, attributeTag, attributeKeyValue);
		}

		if (vars.isEmpty()) {		
			vars = checkForStringMatchWithVariables(s0, s1);
		}

		return vars;
	}

	private static List<ConversionVariable> checkForStringMatchWithVariables(String s0, String s1) {

		int maxMatchCount = 1;
		List<ConversionVariable> vars = new ArrayList<ConversionVariable>();
		
		List<ConversionVariable> list = new ArrayList<ConversionVariable>();
			
		String[] args0 = s0.split("\\s");
		String[] args1 = s1.split("\\s");
		
		if (args0.length == args1.length && args0.length > 1) {
			
			for (int i = 0; i < args0.length; i++) {
				
				if (!args0[i].equals(args1[i])) {
					
					list.add(new ConversionVariable("staticText", args0[i]));
				}
			}
		}
		
		if (list.size() <= maxMatchCount) {
			
			for (ConversionVariable cv : list) {
				vars.add(new ConversionVariable(cv.getName(), cv.getValue()));
			}
		}
		
		return vars;
	}

	static List<ConversionVariable> checkForRegexMatchWithVariables(String s0, String s1, Pattern... patterns) {
		
		List<ConversionVariable> vars = new ArrayList<ConversionVariable>();
		
		for (Pattern pattern : patterns) {

			Matcher m0 = pattern.matcher(s0);
			Matcher m1 = pattern.matcher(s1);

			if (m0.matches() && m1.matches()) {
				
				ConversionVariable var = createVar(m0, m1);
				if (var != null) {
					vars.add(var);
				}
				
			} else {
				
				Map<String, String> map0 = createMap(m0);
				Map<String, String> map1 = createMap(m1);
				
				if (map0.size() == map1.size() && map0.keySet().containsAll(map1.keySet())) {
					
					for (Map.Entry<String, String> e : map0.entrySet()) {
						
						String value0 = e.getValue();
						String value1 = map1.get(e.getKey());
						
						if (!value1.equals(value0)) {
							
							List<ConversionVariable> svars = checkForStringMatchWithVariables(value0, value1);
							
							if (!svars.isEmpty()) {
								
								vars.addAll(svars);
								
							} else {
								
								String name = map0.containsKey("name") ? map0.get("name") : map0.containsKey("rel") ? map0.get("rel") + "_" + e.getKey() : e.getKey();
								ConversionVariable var = new ConversionVariable(name, e.getValue());
								vars.add(var);
							}
						}
					}
				}
			}
		}
		
		return vars;
	}
	
	private static Map<String, String> createMap(Matcher m) {
		Map<String, String> map = new HashMap<String, String>();
		
		while (m.find()) {
			
			if (m.groupCount() == 2) {
				map.put(m.group(1), m.group(2));
			}
		}
		
		return map;
	}
	
	static ConversionVariable createVar(Matcher m0, Matcher m1) {
		
		ConversionVariable var = null;
		int c0 = m0.groupCount();
		int c1 = m1.groupCount();
		
		if (c0 > 1 && c0 == c1) {
			
			if (c0 == 2 || (c0 == 3 && m0.group(1).equals(m1.group(3)))) {
				var = createVar(m0.group(1), m0.group(2));
			}
		}

		return var;
	}

	private static ConversionVariable createStaticTextVar(String value) {
		
		return createVar("statictext", value);
	}

	private static ConversionVariable createVar(String name, String value) {
		
		return new ConversionVariable(name, value);
	}
	
	private static DiffLine get(List<DiffLine> list, int index) {
		return index > -1 && index < list.size() ? list.get(index) : null;
	}
}
