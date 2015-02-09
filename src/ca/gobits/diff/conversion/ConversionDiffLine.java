package ca.gobits.diff.conversion;

import ca.gobits.diff.DiffLine;

public class ConversionDiffLine {
	
	private boolean isComment;
	private DiffLine diffLine;
	
	public ConversionDiffLine(DiffLine diffLine) {
		this.diffLine = diffLine;
	}

	public boolean isComment() {
		return this.isComment;
	}

	public void setComment(boolean isComment) {
		this.isComment = isComment;
	}

	public DiffLine getDiffLine() {
		return this.diffLine;
	}

	public String getLine() {
		return this.diffLine.getLine();
	}

	public int getMatch() {
		return this.diffLine.getMatch();
	}
}
