package ca.gobits.diff.conversion;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
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
	
	private static Pattern titlePattern = Pattern.compile("<title>(.*)</title>");
	
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
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		
		String s0 = new String(Files.readAllBytes(Paths.get("data/index1-win.html")));
		String s1 = new String(Files.readAllBytes(Paths.get("data/index2-win.html")));
		
		Conversion conversion = new HtmlConversion();
//		s0 = conversion.pre(s0);
//		s0 = conversion.sort(s0);
//		s1 = conversion.sort(s1);
		
		DiffResult result = d.diffByLine(s0, s1);
		ConversionResult cr = diff(ps, result);
		
		System.out.println ("---------------------------------------------");
		System.out.println (cr.getText());
//		String r = baos.toString();
//		System.out.println(r);
	}
	
	public static ConversionResult diff(PrintStream ps, DiffResult dr) {
		
		ConversionResult cr = new ConversionResult();
		ConversionStatus cs = new ConversionStatus();
		
		int i0 = 0, i1 = 0;
				
		List<DiffLine> list0 = dr.getList0();
		List<DiffLine> list1 = dr.getList1();		
			
		while (i0 < list0.size() || i1 < list1.size()) {

			DiffLine df0 = get(list0, i0);
			DiffLine df1 = get(list1, i1);

			if (df0 != null && df0.getMatch() == -1) {
				
				String line0 = df0.getLine();
				String line1 = df1.getLine();
				
				DiffLine next0 = get(list0, i0 + 1);
				boolean singleDiff = next0 != null && next0.getMatch() != -1; // if not single diff process by itself.. probably needs to be an includes group of text.
				
				Collection<ConversionVariable> vars = checkIfVariablePossible(cs, line0, line1, singleDiff);
				
				if (!vars.isEmpty()) {
					
					for (ConversionVariable var : vars) {
						
						cs.addUsedVariable(var.getName());
						line0 = line0.replaceAll(Pattern.quote(var.getValue()), "{{ page." + var.getName() + " }}");
					}
					
					cr.addTextWithNewLine(line0);
					
					i1++;
				} else {
					
					System.out.println ("---------------------------------------------");
					System.out.println (cr.getText());
					throw new RuntimeException("need to do something...");
				}
				
				i0++;
				
			} else if (df1 != null && df1.getMatch() == -1) {
				
//				ps.println ("- " + df1.getLine());
//				System.out.println ("- " + df1.getLine());
				i1++;
			} else {
				
				if (df0 != null) {
					
					cr.addTextWithNewLine(df0.getLine());
				}
				
				i0++;
				i1++;
			}
		}
		
		return cr;
	}

	private static String getTitle(String s0, String s1) {
		
		String text = null;
		
		Matcher m0 = titlePattern.matcher(s0);
		Matcher m1 = titlePattern.matcher(s1);
		
		if (m0.matches() && m1.matches()) {
			text = m0.group(1);
		}
		
		return text;
	}
	
	private static Collection<ConversionVariable> checkIfVariablePossible(ConversionStatus cs, String s0, String s1, boolean singleDiff) {
		
		Collection<ConversionVariable> list = new ArrayList<ConversionVariable>();

		String title = getTitle(s0, s1);
		
		if (title != null) {
			
			ConversionVariable var = new ConversionVariable("title", title);
			list.add(var);
			
		} else {
//			System.out.println (s0);
//			System.out.println (s1);
			
			DiffResult dr = d.diffByChar(s0, s1);
			
			int start = d.matchFirst(dr.getList0(), dr.getList1(), 0, 0);
			
			if (start > 0) {
				
				Map<String, String> m0 = attributes(s0);
				Map<String, String> m1 = attributes(s1);
				
				if (m0.size() == m1.size() && m0.keySet().containsAll(m1.keySet())) {
					
					for (Map.Entry<String, String> e : m0.entrySet()) {
						
						if (!m1.get(e.getKey()).equals(e.getValue())) {
							String name = cs.getNextVariableName(e.getKey());
							ConversionVariable var = new ConversionVariable(name, e.getValue());
							list.add(var);							
						}
					}
				}
			}
			
			if (singleDiff && list.isEmpty()) {

				String name = cs.getNextVariableName("staticText");
				ConversionVariable var = new ConversionVariable(name, s0);
				list.add(var);
			}
			//			int end = d.matchLast(dr.getList0(), dr.getList1(), dr.getList0().size() - 1, dr.getList1().size() - 1);
//			System.out.println ("!!! " + start + " " + end + " " + join(dr.getList0(), start, end));
//			
//			System.out.println ("-----------------------------------------");
//			System.exit(0);
		}

//		List<DiffLine> list0 = dr.getList0();
		
//		for (DiffLine df : list0) {
//			System.out.println (df.getLine() + " " + df.getMatch());
//		}
		
		return list;
	}
	
	private static Pattern attributes = Pattern.compile("(\\S+)=[\"']?((?:.(?![\"']?\\s+(?:\\S+)=|[>\"']))+.)[\"']?");
	private static Pattern splitEquals = Pattern.compile("(.*)=['\"](.*)['\"]");
	private static Map<String, String> attributes(String s) {
				
		Map<String, String> map = new HashMap<String, String>();	
		
		Matcher m = attributes.matcher(s);
		while (m.find()) {
			
			String ss = m.group();
			Matcher mm = splitEquals.matcher(ss);
			
			if (mm.matches()) {
				map.put(mm.group(1), mm.group(2));
			}
			
		}

		return map;
	}

	private static String join(List<DiffLine> list, int start, int end) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = start; i < list.size() - end - 1; i++) {
			sb.append(list.get(i).getLine());
		}
		
		return sb.toString();
	}
	
	private static DiffLine get(List<DiffLine> list, int index) {
		return index < list.size() ? list.get(index) : null;
	}
}
