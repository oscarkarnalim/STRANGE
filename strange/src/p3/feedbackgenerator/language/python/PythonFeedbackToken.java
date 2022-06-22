package p3.feedbackgenerator.language.python;

import org.antlr.v4.runtime.Token;

import p3.feedbackgenerator.token.FeedbackToken;

public class PythonFeedbackToken extends FeedbackToken{
	public PythonFeedbackToken(String content, String type, int startRow,
			int startCol, int finishRow, int finishCol, Token antlrToken) {
		super(content, type, startRow, startCol, finishRow, finishCol, antlrToken);
		if(this.type.endsWith("COMMENT"))
			this.type = "COMMENT";
	}
	public String toString(){
		String lcontent = content;
		// this is used just to pretty print the output
		if(type.equals("NEWLINE"))
			lcontent = "newline";
		else if (type.equals("DEEPERINDENT")) // deeper indentation
			lcontent = ("deeperindent");
		else if (type.equals("SHALLOWERINDENT")) // shallower indentation
			lcontent = ("shallowerindent");
		
		return "[" + lcontent + " " + type + " " + startRow + " " + startCol + " " + finishRow + " " + finishCol + "]";
	}
}