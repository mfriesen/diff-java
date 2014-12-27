package ca.gobits.diff;

public class DiffLine implements Comparable<DiffLine> {

	private String line;
	private byte[] sha1;
	private int pos;
	private int match;
	
	public DiffLine() {
		this.pos = -1;
		this.match = -1;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public byte[] getSHA1() {
		return sha1;
	}

	public void setSHA1(byte[] sha1) {
		this.sha1 = sha1;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public int getMatch() {
		return match;
	}

	public void setMatch(int match) {
		this.match = match;
	}

	public String toString() {
		return line + " pos: " + pos + " match: " + match;
	}

	@Override
	public int compareTo(DiffLine o) {
		return Integer.valueOf(pos).compareTo(Integer.valueOf(o.getPos()));
	}
}
