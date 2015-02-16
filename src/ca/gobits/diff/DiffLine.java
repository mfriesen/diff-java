package ca.gobits.diff;

public class DiffLine {

	private String 	line;
	private int 	match;
	private int 	pos;
	private byte[] 	sha1;
	
	public DiffLine() {
		this.pos = -1;
		this.match = -1;
	}

	public String getLine() {
		return this.line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public byte[] getSHA1() {
		return this.sha1;
	}

	public void setSHA1(byte[] sha1) {
		this.sha1 = sha1;
	}

	public int getPos() {
		return this.pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public int getMatch() {
		return this.match;
	}

	public void setMatch(int match) {
		this.match = match;
	}

	@Override
	public String toString() {
		return this.line + " pos: " + this.pos + " match: " + this.match;
	}
}
