package p3.feedbackgenerator.language.java;

import java.util.ArrayList;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import p3.feedbackgenerator.token.FeedbackToken;
import support.javaantlr.Java8Lexer;
import support.stringmatching.AdditionalKeywordsManager;

public class JavaFeedbackGenerator {

	public static ArrayList<FeedbackToken> syntaxTokenStringPreprocessing(
			ArrayList<FeedbackToken> tokenString,
			ArrayList<ArrayList<String>> additionalKeywords) {
		/*
		 * rename all Identifiers as their respective type + string and
		 * character literals as string literals + considering String data type
		 * as string data type instead of identifier + merging all
		 * IntegerLiteral and FloatingPointLiteral as number + merging all
		 * numeric data types to NumberType, including the object version
		 */
		ArrayList<FeedbackToken> result = new ArrayList<>();

		for (int i = 0; i < tokenString.size(); i++) {
			FeedbackToken cur = tokenString.get(i);
			String type = cur.getType();
			if (type.equals("Identifier")) {
				if (cur.getContent().equals("Integer")
						|| cur.getContent().equals("Short")
						|| cur.getContent().equals("Long")
						|| cur.getContent().equals("Byte")
						|| cur.getContent().equals("Float")
						|| cur.getContent().equals("Double")) {
					cur.setContentForComparison("number data type");
				} else if (cur.getContent().equals("String")
						|| cur.getContent().equals("Character")) {
					cur.setContentForComparison("string data type");
				} else
					cur.setContentForComparison("identifier");
			} else if (type.equals("StringLiteral") || type.equals("CharacterLiteral")){
				cur.setContentForComparison("string literal");
			} else if (type.equals("IntegerLiteral")
					|| type.equals("FloatingPointLiteral"))
				cur.setContentForComparison("number literal");
			else if (type.equals("'char'"))
				cur.setContentForComparison("string data type");
			else if (type.equals("'int'") || type.equals("'short'")
					|| type.equals("'long'") || type.equals("'byte'")
					|| type.equals("'float'") || type.equals("'double'"))
				cur.setContentForComparison("number data type");

			if (type.equals("WS") == false && type.equals("COMMENT") == false) {
				result.add(cur);
			}
		}

		// check from the results (which are free to comments and whitespaces).
		for (int i = 0; i < result.size(); i++) {
			// if it is the beginning of keywords
			int pos = AdditionalKeywordsManager.indexOf(i, result,
					additionalKeywords);
			if (pos != -1) {
				// for each involved token
				for (int j = 0; j < additionalKeywords.get(pos).size(); j++) {
					FeedbackToken cur = result.get(i + j);
					// return back to its original value
					cur.setContentForComparison(cur.getContent());
				}

				// skip the position, reduced by 1 to deal with "for" loop
				// behaviour
				i += (additionalKeywords.get(pos).size() - 1);
			}
		}
		return result;
	}

	public static void mergeAdjacentCommentsOnTokenString(
			ArrayList<FeedbackToken> tokenString) {
		// merge all adjacent comments as one comment

		// the iteration is based on while as some index will be skipped
		// during the process
		int i = 0;
		while (i < tokenString.size()) {
			FeedbackToken cur = tokenString.get(i);

			// for each comment token
			if (cur.getType().equals("COMMENT")) {
				/*
				 * check if there is at least one comment token after this,
				 * which can be separated by any whitespaces.
				 */
				boolean isNextCommentTokenExist = false;
				for (int k = i + 1; k < tokenString.size(); k++) {
					if (tokenString.get(k).getType().equals("COMMENT")) {
						isNextCommentTokenExist = true;
						break;
					} else if (tokenString.get(k).getType().equals("WS"))
						;
					else
						break;
				}

				// if found, perform the merging
				if (isNextCommentTokenExist) {
					// merge all next comment tokens
					int j = i + 1;
					while (j < tokenString.size()) {
						if (tokenString.get(j).getType().equals("WS")
								&& j + 1 < tokenString.size()
								&& tokenString.get(j + 1).getType()
										.equals("COMMENT")) {
							// if whitespace followed by comment, embed
							// the content and move
							cur.setContent(cur.getContent()
									+ tokenString.get(j).getContent());
							cur.setContent(cur.getContent()
									+ tokenString.get(j + 1).getContent());
							j += 2;
						} else if (tokenString.get(j).getType()
								.equals("COMMENT")) {
							// if comment, embed the content and move
							cur.setContent(cur.getContent()
									+ tokenString.get(j).getContent());
							j++;
						} else
							// otherwise, exit loop
							break;
					}

					// set finish row and col of cur as the comments are merged
					// j is decremented by 1 as each time the comment or
					// whitespace
					// added, j is incremented
					cur.setFinishRow(tokenString.get(j - 1).getFinishRow());
					cur.setFinishCol(tokenString.get(j - 1).getFinishCol());

					// remove all tokens which have been merged to cur
					while (j - i > 1) {
						tokenString.remove(i + 1);
						j--;
					}
				}
			}

			// increment the index (i)
			i++;
		}
	}

	public static ArrayList<FeedbackToken> generateFeedbackTokenString(
			String filePath) {
		// including comment and whitespaces
		try {
			ArrayList<FeedbackToken> result = new ArrayList<>();
			// build the lexer
			Lexer lexer = new Java8Lexer(new ANTLRFileStream(filePath));
			lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
			// extract the tokens
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			tokens.fill();
			// for each element including eof, form the token and embed start
			// finish location
			for (int index = 0; index < tokens.size(); index++) {
				Token token = tokens.get(index);
				// set the finish row and col for previously added token.
				if (result.size() >= 1) {
					result.get(result.size() - 1).setFinishRow(token.getLine());
					result.get(result.size() - 1).setFinishCol(
							token.getCharPositionInLine());
				}
				String type = Java8Lexer.VOCABULARY.getDisplayName(token
						.getType());
				
				result.add(new JavaFeedbackToken(token.getText(), type, token
						.getLine(), token.getCharPositionInLine(), 0, 0, token));
			}
			/*
			 * for the last token which is eof. this is intentionally created
			 * just to set the finish position of the previous token
			 */
			result.remove(result.size() - 1);

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
