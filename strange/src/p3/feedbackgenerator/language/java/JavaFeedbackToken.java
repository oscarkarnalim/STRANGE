package p3.feedbackgenerator.language.java;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import p3.feedbackgenerator.token.FeedbackToken;

public class JavaFeedbackToken extends FeedbackToken{
	public JavaFeedbackToken(String content, String type, int startRow,
			int startCol, int finishRow, int finishCol, Token antlrToken) {
		super(content, type, startRow, startCol, finishRow, finishCol, antlrToken);
		if(this.type.endsWith("COMMENT"))
			this.type = "COMMENT";
	}
	public String toString(){
		String lcontent = content;
		// this is used just to pretty print the output
		if(type.equals("WS"))
			lcontent = "whitepace";
		else if(type.equals("COMMENT"))
			lcontent = "comment";
		
		return "[" + lcontent + " " + type + " " + startRow + " " + startCol + " " + finishRow + " " + finishCol + "]";
	}
}
