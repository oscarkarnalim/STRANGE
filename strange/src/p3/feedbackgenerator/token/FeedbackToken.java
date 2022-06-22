package p3.feedbackgenerator.token;

import org.antlr.v4.runtime.Token;

public class FeedbackToken implements Comparable<FeedbackToken> {
	protected String content, type;
	protected int startRow, startCol, finishRow, finishCol;
	protected Token antlrToken;
	protected String contentForComparison;

	public FeedbackToken(String content, String type, int startRow,
			int startCol, int finishRow, int finishCol, Token antlrToken) {
		super();
		this.content = content;
		this.type = type;
		this.startRow = startRow;
		this.startCol = startCol;
		this.finishRow = finishRow;
		this.finishCol = finishCol;
		this.antlrToken = antlrToken;
		this.contentForComparison = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getStartCol() {
		return startCol;
	}

	public void setStartCol(int col) {
		this.startCol = col;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int row) {
		this.startRow = row;
	}

	public int getFinishRow() {
		return finishRow;
	}

	public void setFinishRow(int finishRow) {
		this.finishRow = finishRow;
	}

	public int getFinishCol() {
		return finishCol;
	}

	public void setFinishCol(int finishCol) {
		this.finishCol = finishCol;
	}

	public String getContentForComparison() {
		return contentForComparison;
	}

	public void setContentForComparison(String contentForComparison) {
		this.contentForComparison = contentForComparison;
	}

	@Override
	public int compareTo(FeedbackToken arg0) {
		// TODO Auto-generated method stub
		if (this.startRow != arg0.startRow)
			return this.startRow - arg0.startRow;
		else
			return this.startCol - arg0.startCol;
	}
}
