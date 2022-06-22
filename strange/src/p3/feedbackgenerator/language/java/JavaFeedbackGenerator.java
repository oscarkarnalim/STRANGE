package p3.feedbackgenerator.language.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import p3.CodeMerger;
import p3.feedbackgenerator.token.FeedbackToken;
import support.javaantlr.Java9Lexer;
import support.stringmatching.AdditionalKeywordsManager;

public class JavaFeedbackGenerator {

	public static ArrayList<FeedbackToken> syntaxTokenStringPreprocessing(ArrayList<FeedbackToken> tokenString,
			ArrayList<ArrayList<String>> additionalKeywords, boolean isSyntacticSim) {
		/*
		 * If isSyntacticSim is true, rename all Identifiers as their respective type +
		 * string and character literals as string literals + considering String data
		 * type as string data type instead of identifier + merging all IntegerLiteral
		 * and FloatingPointLiteral as number + merging all numeric data types to
		 * NumberType, including the object version.
		 * 
		 * If isSyntacticSim is false, only filter syntax tokens.
		 */
		ArrayList<FeedbackToken> result = new ArrayList<>();

		for (int i = 0; i < tokenString.size(); i++) {
			FeedbackToken cur = tokenString.get(i);
			String type = cur.getType();

			if (isSyntacticSim) {
				if (type.equals("Identifier")) {
					if (cur.getContent().equals("Integer") || cur.getContent().equals("Short")
							|| cur.getContent().equals("Long") || cur.getContent().equals("Byte")
							|| cur.getContent().equals("Float") || cur.getContent().equals("Double")) {
						cur.setContentForComparison("number data type");
					} else if (cur.getContent().equals("String") || cur.getContent().equals("Character")) {
						cur.setContentForComparison("string data type");
					} else
						cur.setContentForComparison("identifier");
				} else if (type.equals("StringLiteral") || type.equals("CharacterLiteral")) {
					cur.setContentForComparison("string literal");
				} else if (type.equals("IntegerLiteral") || type.equals("FloatingPointLiteral"))
					cur.setContentForComparison("number literal");
				else if (type.equals("'char'"))
					cur.setContentForComparison("string data type");
				else if (type.equals("'int'") || type.equals("'short'") || type.equals("'long'")
						|| type.equals("'byte'") || type.equals("'float'") || type.equals("'double'"))
					cur.setContentForComparison("number data type");
			}

			if (type.equals("WS") == false && type.equals("COMMENT") == false) {
				result.add(cur);
			}
		}

		// check from the results (which are free to comments and whitespaces).
		for (int i = 0; i < result.size(); i++) {
			// if it is the beginning of keywords
			int pos = AdditionalKeywordsManager.indexOf(i, result, additionalKeywords);
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

	// this method is dedicated for USTRANGE
	public static ArrayList<FeedbackToken> generaliseTokens(ArrayList<FeedbackToken> tokenString) {
		for (int i = 0; i < tokenString.size(); i++) {
			FeedbackToken cur = tokenString.get(i);
			String type = cur.getType();
			if (type.equals("Identifier")) {
				if (cur.getContent().equals("Integer") || cur.getContent().equals("Short")
						|| cur.getContent().equals("Long") || cur.getContent().equals("Byte")
						|| cur.getContent().equals("Float") || cur.getContent().equals("Double")) {
					cur.setContentForComparison("number data type");
				} else if (cur.getContent().equals("String") || cur.getContent().equals("Character")) {
					cur.setContentForComparison("string data type");
				} else
					cur.setContentForComparison("identifier");
			} else if (type.equals("StringLiteral") || type.equals("CharacterLiteral")) {
				cur.setContentForComparison("string literal");
			} else if (type.equals("IntegerLiteral") || type.equals("FloatingPointLiteral"))
				cur.setContentForComparison("number literal");
			else if (type.equals("'char'"))
				cur.setContentForComparison("string data type");
			else if (type.equals("'int'") || type.equals("'short'") || type.equals("'long'") || type.equals("'byte'")
					|| type.equals("'float'") || type.equals("'double'"))
				cur.setContentForComparison("number data type");
		}

		return tokenString;
	}

	public static void mergeAdjacentCommentsOnTokenString(ArrayList<FeedbackToken> tokenString) {
		// merge all adjacent comments as one comment

		// the iteration is based on while as some index will be skipped
		// during the process
		int i = 0;
		while (i < tokenString.size()) {
			FeedbackToken cur = tokenString.get(i);

			// for each comment token
			if (cur.getType().equals("COMMENT")) {

				// if it is a result of code merger, continue
				if (CodeMerger.isGeneratedFromCodeMerging(cur.getContent(), "java")) {
					i++;
					continue;
				}

				/*
				 * check if there is at least one comment token after this, which can be
				 * separated by any whitespaces. The comment should not be part of automatically
				 * generated comments during code merger.
				 */
				boolean isNextCommentTokenExist = false;
				for (int k = i + 1; k < tokenString.size(); k++) {
					if (tokenString.get(k).getType().equals("COMMENT")
							&& !CodeMerger.isGeneratedFromCodeMerging(tokenString.get(k).getContent(), "java")) {
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
						if (tokenString.get(j).getType().equals("WS") && j + 1 < tokenString.size()
								&& tokenString.get(j + 1).getType().equals("COMMENT")) {
							// if whitespace followed by comment, embed
							// the content and move
							cur.setContent(cur.getContent() + tokenString.get(j).getContent());
							cur.setContent(cur.getContent() + tokenString.get(j + 1).getContent());
							j += 2;
						} else if (tokenString.get(j).getType().equals("COMMENT")) {
							// if comment, embed the content and move
							cur.setContent(cur.getContent() + tokenString.get(j).getContent());
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

	public static ArrayList<FeedbackToken> generateFeedbackTokenString(String filePath) {

		// including comment and whitespaces
		try {

			ArrayList<FeedbackToken> result = new ArrayList<>();
			// build the lexer
			Lexer lexer = new Java9Lexer(CharStreams.fromFileName(filePath));
			lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
			// extract the tokens
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			tokens.fill();
			// for each element including eof, form the token and embed start
			// finish location
			for (int index = 0; index < tokens.size(); index++) {
				Token token = tokens.get(index);
				// set the finish row and col for previously added token
				if (result.size() >= 1) {
					FeedbackToken pastToken = result.get(index - 1);
					pastToken.setFinishRow(token.getLine());
					pastToken.setFinishCol(token.getCharPositionInLine());
				}
				String type = Java9Lexer.VOCABULARY.getDisplayName(token.getType());

				result.add(new JavaFeedbackToken(token.getText(), type, token.getLine(), token.getCharPositionInLine(),
						0, 0, token));
			}
			/*
			 * for the last token which is eof. this is intentionally created just to set
			 * the finish position of the previous token
			 */
			result.remove(result.size() - 1);

			// get all the content as line of strings for separating each token with its
			// trailing whitespace. Indexes start from 0 while row in the tokens start from
			// 1
			ArrayList<String> lines = new ArrayList<String>();
			FileReader fr = new FileReader(new File(filePath));
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				lines.add(line + "\n");
			}
			br.close();

			// split each token from its trailing whitespace
			// except the last token as it has no trailing whitespace anyway
			for (int i = 0; i < result.size() - 1; i++) {
				FeedbackToken curToken = result.get(i);
				
				// BELOM KASUS SATU TOKEN TAPI ADA TRAILING WHITESPACE

				// split the token with its trailing whitespace
				int realFinishRow = curToken.getFinishRow();
				int realFinishCol = curToken.getFinishCol();
				int wsFinishRow = realFinishRow;
				int wsFinishCol = realFinishCol;

				// minus one pos before searching as we do not need the beginning of next token
				realFinishCol--;
				// if the col becomes invalid, go back to the last character of previous line
				if (realFinishCol < 0) {
					realFinishRow--;
					realFinishCol = lines.get(realFinishRow - 1).length() - 1;
				}

				// search the real finish position for the token
				boolean isDone = false;
				while (isDone == false) {
					
					// take each line from the finish row
					String curLine = lines.get(realFinishRow - 1);
					// if its trimmed version contains no content, move to upper line
					if (curLine.trim().length() == 0) {
						realFinishRow--;
						realFinishCol = lines.get(realFinishRow - 1).length() - 1;
					} else {

						// check the position whether it is whitespace
						if (Character.isWhitespace(curLine.charAt(realFinishCol)) == false) {
							// if not, consider it found
							isDone = true;

							// add real finish col by one as the end should be the start of another token
							realFinishCol++;
							if (realFinishCol == curLine.length()) {
								// if the position is the last column of the current line, put the position as
								// the first char of next line
								realFinishRow = realFinishRow + 1;
								realFinishCol = 0;
							}
						} else {
							// other than that, reduce the position by one
							realFinishCol = realFinishCol - 1;

							// if invalid, go to the last char of previous line
							if (realFinishCol < 0) {
								realFinishRow--;
								realFinishCol = lines.get(realFinishRow - 1).length() - 1;
							}
						}

						// System.out.println(curLine + " " + realFinishCol + " " + wsStartCol);
					}
				}

				// if the trailing whitespace is found and its start pos is not the finish pos
				// (contain something), set the real finish pos for the token and create
				// whitespace token
				if (!(realFinishRow == wsFinishRow && realFinishCol == wsFinishCol)) {

					// set real finish pos for the token
					curToken.setFinishRow(realFinishRow);
					curToken.setFinishCol(realFinishCol);

					// add new whitespace token
					result.add(i + 1, new JavaFeedbackToken("", "WS", realFinishRow, realFinishCol, wsFinishRow,
							wsFinishCol, null));
					// skip the added whitespace token
					i++;
				}
			}

			return result;
		} catch (

		Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
