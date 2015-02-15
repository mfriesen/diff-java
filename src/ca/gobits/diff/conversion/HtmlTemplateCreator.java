package ca.gobits.diff.conversion;

import java.util.ArrayList;
import java.util.Arrays;
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

public class HtmlTemplateCreator {

	// html attribute
	private static Pattern attributeTag = Pattern.compile("<[\\w]*(.*)>");
	
	// html attribute value
	private static Pattern attributeKeyValue = Pattern.compile("(\\w+)=['\"]?([^'\"]+)['\"]?");

	// regular html tag
	private static Pattern htmlTag = Pattern.compile("<([^>]*)>([^>]*)</?([^>]*)>");
	
	// html single line comment
	private static Pattern singleLineComment = Pattern.compile("^(<!--)(.|\\s)*?(-->)");

	// <body> not by itself
	private static String bodyNotEndOfLine = "<body>(?!$)";
	
	private Diff d = new DiffImpl();

	public Template create(String s0, String s1) {
		
		DiffResult result = createDiff(s0, s1);

		List<ConversionDiffLine> l0 = toConversionDiffLine(result.getList0());
		List<ConversionDiffLine> l1 = toConversionDiffLine(result.getList1());
				
		Template template = new Template();
		
		int topMatch = matchFromTop(template, l0, l1);
		int bottomMatch = matchFromBottom(template, topMatch, l0, l1);
		
		if (bottomMatch > 0) {
			template.addLine(topMatch, "{{ content }}");
		}
				
		return template;
	}

	private int matchFromTop(Template template, List<ConversionDiffLine> list0, List<ConversionDiffLine> list1) {

		int match = 0;
		boolean createVariables = true;

		for (int i = 0; i < list0.size(); i++) {

			ConversionDiffLine d0 = list0.get(i);
			String l0 = d0.getLine();

			if (d0.getDiffLine().getMatch() != i) {

				List<ConversionVariable> vars = Collections.emptyList();

				if (createVariables) {
					
					ConversionDiffLine d1 = list1.get(i);
					String l1 = d1.getLine();
					
					if (d0.isComment() && d1.isComment()) {
						vars = null;
					} else {
						vars = checkForMatchWithVariables(l0, l1);
					}
				}

				if (vars != null && vars.isEmpty()) {
//					DiffLine d1 = l1.get(i);
					// System.out.println (d0.getPos() + " " + d0.getLine());
					// System.out.println (d1.getPos() + " " + d1.getLine());
					break;
				} 
							
				if (vars != null) {
					
					for (ConversionVariable var : vars) {
						var = template.addVariable(var);
						l0 = l0.replaceAll(Pattern.quote(var.getValue()), "{{ " + var.getName() + " }}");
					}
				}
				
				template.addLine(l0);
				
			} else {
				template.addLine(d0.getLine());
			}

			if (l0.startsWith("<body")) {
				createVariables = false;
			}
			
			match++;
		}
				
		return match;
	}

	private int matchFromBottom(Template template, int topMatches, List<ConversionDiffLine> list0, List<ConversionDiffLine> list1) {

		int match = 0;
		int maxMatches = list0.size() - topMatches;
		
		if (maxMatches > 0) {
			int i0 = list0.size() - 1;
			int i1 = list1.size() - 1;
			
			while (i0 > 0 && i1 > 0) {
	
				ConversionDiffLine d0 = list0.get(i0);
				ConversionDiffLine d1 = list1.get(i1);
				
				if (d0.isComment() && d1.isComment()) {
					
				} else if (d0.getMatch() != i1) {
	//				System.out.println (d0.getLine());
	//				System.out.println (d1.getLine());
					break;
				}
				
				template.addLine(template.getLines().size() - match, d0.getLine());
				i0--;
				i1--;
				match++;
			}
		}

		return match;
	}
	
	private List<ConversionDiffLine> toConversionDiffLine(List<DiffLine> list) {
		
		List<ConversionDiffLine> l0 = new ArrayList<ConversionDiffLine>(list.size());
		
		for (DiffLine diffLine : list) {
			l0.add(new ConversionDiffLine(diffLine));
		}
		
		markComments(l0);

		return l0;
	}

	private void markComments(List<ConversionDiffLine> list) {

		boolean comment = false;
		
		for (ConversionDiffLine dl : list) {
			
			String l = dl.getDiffLine().getLine();
			if (l.startsWith("<!--")) {
				comment = true;
			}
			
			if (comment) {
				dl.setComment(true);
			}
			
			if (l.endsWith("-->")) {
				comment = false;
			}
		}
	}
	
	private DiffResult createDiff(String s0, String s1) {

		s0 = formatHtml(s0);
		s1 = formatHtml(s1);

		DiffResult result = this.d.diffByLine(s0, s1);
		
		return result;
	}

	String formatHtml(String s) {

		StringBuilder sb = new StringBuilder();
		
		s = s.replaceAll(bodyNotEndOfLine, "<body>" + this.d.getNewLine());
		
		String[] strs = this.d.splitByNewLine(s);

		for (String ss : strs) {

			int lastPos = 0;
			int pos = isSelfContainedTag(ss, lastPos);

			while (pos > -1 && pos < ss.length() - 1) {

				String substr = substring(ss, lastPos, pos);
				append(sb, substr);
				lastPos = pos;

				int tpos = isSelfContainedTag(ss, lastPos);
				if (tpos > -1) {
					pos = tpos + lastPos;
				} else {
					break;
				}
			}

			pos = ss.length();
			append(sb, substring(ss, lastPos, pos));
		}

		return sb.toString();
	}
	
	private void append(StringBuilder sb, String s) {
		sb.append(s);
	}

	private String substring(String s, int begin, int end) {

		String ss = "";
		if (begin != end && begin != end - 1) {
			ss = s.substring(begin, end) + this.d.getNewLine();
		}

		return ss;
	}

	private int isSelfContainedTag(String ss, int pos) {
		
		String sss = ss.substring(pos);
		int match = isSelfContainedTag(htmlTag, sss);
		
		if (match == -1) {
			
			int m0 = isSelfContainedTag(singleLineComment, sss);
			int m1 = sss.indexOf(">");
			int m2 = sss.indexOf("<");
			
			if (m1 > -1) {
				m1++;
			}
			
			if (m2 == 0) {
				m2 = -1;
			}
	 
			int arr[] = new int[] { m0, m1, m2 };
			Arrays.sort(arr);
			
			for (int i : arr) {
				if (i > -1) {
					match = i;
					break;
				}
			}
		}
		
		return match;
	}
	
	private int isSelfContainedTag(Pattern pattern, String ss) {

		int match = -1;

		Matcher m = pattern.matcher(ss);

		if (m.find()) {

			if (m.groupCount() == 3) {
				if (m.group(1).startsWith(m.group(3)) || isComment(m.group(1))) {
					match = m.end();
				}
			}
		}

		return match;
	}

	private boolean isComment(String s) {
		return s.equals("<!--");
	}
	
	List<ConversionVariable> checkForMatchWithVariables(String s0, String s1) {

		List<ConversionVariable> vars = checkForRegexMatchWithVariables(s0, s1,
				htmlTag);

		if (vars.isEmpty()) {
			vars = checkForRegexMatchWithVariables(s0, s1, attributeTag,
					attributeKeyValue);
		}

		if (vars.isEmpty()) {
			vars = checkForStringMatchWithVariables(s0, s1);
		}

		return vars;
	}

	private List<ConversionVariable> checkForStringMatchWithVariables(
			String s0, String s1) {

		int maxMatchCount = 2;
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

	private List<ConversionVariable> checkForRegexMatchWithVariables(String s0,
			String s1, Pattern... patterns) {

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

				if (map0.size() == map1.size()
						&& map0.keySet().containsAll(map1.keySet())) {

					for (Map.Entry<String, String> e : map0.entrySet()) {

						String value0 = e.getValue();
						String value1 = map1.get(e.getKey());

						if (!value1.equals(value0)) {

							List<ConversionVariable> svars = checkForStringMatchWithVariables(
									value0, value1);

							if (!svars.isEmpty()) {

								vars.addAll(svars);

							} else {

								String name = map0.containsKey("name") ? map0
										.get("name")
										: map0.containsKey("rel") ? map0
												.get("rel") + "_" + e.getKey()
												: e.getKey();
								ConversionVariable var = new ConversionVariable(
										name, e.getValue());
								vars.add(var);
							}
						}
					}
				}
			}
		}

		return vars;
	}

	private ConversionVariable createVar(Matcher m0, Matcher m1) {

		ConversionVariable var = null;
		int c0 = m0.groupCount();
		int c1 = m1.groupCount();

		if (c0 > 1 && c0 == c1) {

			if (c0 == 2 || (c0 == 3 && m0.group(1).equals(m1.group(3)))) {
				var = new ConversionVariable(m0.group(1), m0.group(2));
			}
		}

		return var;
	}
	
	private Map<String, String> createMap(Matcher m) {
		Map<String, String> map = new HashMap<String, String>();

		while (m.find()) {

			if (m.groupCount() == 2) {
				map.put(m.group(1), m.group(2));
			}
		}

		return map;
	}

}
