package p3.feedbackgenerator.language.python;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import p3.CodeMerger;
import p3.feedbackgenerator.token.FeedbackToken;
import support.pythonantlr.Python3Lexer;
import support.stringmatching.AdditionalKeywordsManager;

public class PythonFeedbackGenerator {

	public static void syntaxTokenStringPreprocessing(ArrayList<FeedbackToken> syntaxTokenString,
			ArrayList<ArrayList<String>> additionalKeywords) {
		/*
		 * rename all Identifiers and StringLiterals as their respective type + merging
		 * all IntegerLiteral and FloatingPointLiteral as number
		 */
		for (int i = 0; i < syntaxTokenString.size(); i++) {
			// if it is the beginning of keywords
			int pos = AdditionalKeywordsManager.indexOf(i, syntaxTokenString, additionalKeywords);
			if (pos != -1) {
				// skip the position, reduced by 1 to deal with "for" loop
				// behaviour
				i += (additionalKeywords.get(pos).size() - 1);
			} else {

				FeedbackToken cur = syntaxTokenString.get(i);
				String type = cur.getType();
				if (type.equals("NAME")) {
					cur.setContentForComparison("identifier");
				} else if (type.equals("STRING_LITERAL"))
					cur.setContentForComparison("string literal");
				else if (type.equals("DECIMAL_INTEGER") || type.equals("FLOAT_NUMBER"))
					cur.setContentForComparison("number literal");
			}
		}
	}

	// this method is dedicated for USTRANGE
	public static void generaliseTokenString(ArrayList<FeedbackToken> syntaxTokenString) {
		for (int i = 0; i < syntaxTokenString.size(); i++) {
			FeedbackToken cur = syntaxTokenString.get(i);
			String type = cur.getType();
			if (type.equals("NAME")) {
				cur.setContentForComparison("identifier");
			} else if (type.equals("STRING_LITERAL"))
				cur.setContentForComparison("string literal");
			else if (type.equals("DECIMAL_INTEGER") || type.equals("FLOAT_NUMBER"))
				cur.setContentForComparison("number literal");
		}
	}

	public static void mergeAdjacentCommentsAndWhitespacesOnTokenString(ArrayList<FeedbackToken> tokenString) {
		// merge all adjacent comments/whitespaces as one comment/whitespace

		// the iteration is based on while as some index will be skipped
		// during the process
		int i = 0;
		while (i < tokenString.size()) {
			FeedbackToken cur = tokenString.get(i);

			// for each comment token
			if (cur.getType().equals("COMMENT")) {
				// if it is a result of code merger, continue
				if (CodeMerger.isGeneratedFromCodeMerging(cur.getContent(), "py")) {
					i++;
					continue;
				}

				/*
				 * check if there is at least one comment token after this, which can be
				 * separated by any whitespaces.
				 */
				int nextCommentPos = -1;
				for (int k = i + 1; k < tokenString.size(); k++) {
					if (tokenString.get(k).getType().equals("COMMENT")
							&& !CodeMerger.isGeneratedFromCodeMerging(tokenString.get(k).getContent(), "py")) {
						nextCommentPos = k;
						break;
					} else if (tokenString.get(k).getType().equals("WS"))
						;
					else
						break;
				}

				// if found, perform the merging
				if (nextCommentPos != -1) {
					for (int k = i + 1; k <= nextCommentPos; k++) {
						cur.setContent(cur.getContent() + tokenString.get(k).getContent());
					}

					// set finish row and col of cur as the comments are merged
					cur.setFinishRow(tokenString.get(nextCommentPos).getFinishRow());
					cur.setFinishCol(tokenString.get(nextCommentPos).getFinishCol());

					// remove all tokens which have been merged to cur
					while (nextCommentPos - i > 0) {
						tokenString.remove(i + 1);
						nextCommentPos--;
					}
					/*
					 * ask the iteration to recheck from merged token. This is different to Java
					 * adjacent comment merging.
					 */
					i--;
				}
			} else if (cur.getType().equals("WS")) {
				// merge the next adjacent whitespace if any
				if (i + 1 < tokenString.size() && tokenString.get(i + 1).getType().equals("WS")) {
					// merge the content
					cur.setContent(cur.getContent() + tokenString.get(i + 1).getContent());
					// set finish post
					cur.setFinishRow(tokenString.get(i + 1).getFinishRow());
					cur.setFinishCol(tokenString.get(i + 1).getFinishCol());
					// remove the token
					tokenString.remove(i + 1);
					// decrement to recheck the newly next element
					i--;
				}
			}

			// increment the index (i)
			i++;
		}
	}

	public static void mergeAdjacentCommentsAndWhitespacesOnTokenStringUnused(ArrayList<FeedbackToken> tokenString) {
		// merge all adjacent comments/whitespaces as one comment/whitespace

		// the iteration is based on while as some index will be skipped
		// during the process
		int i = 0;
		while (i < tokenString.size()) {
			FeedbackToken cur = tokenString.get(i);

			// get the default content
			String content = cur.getContent();

			// merge all next tokens
			int j = i + 1;

			if (cur.getType().equals("COMMENT")) {
				/*
				 * check whether after this token, there is at least one comment token for
				 * correct merging. Avoiding merging single comment with multiple whitespaces.
				 */
				boolean isNextCommentTokenExist = false;

				/*
				 * if found, merge all next comment and whitespace tokens which positions on
				 * real code are adjacent to current comment
				 */
				while (j < tokenString.size()) {
					/*
					 * it should be merged to current token if their positions are adjacent.
					 */
					if (tokenString.get(j).getStartRow() == tokenString.get(j - 1).getFinishRow()
							&& tokenString.get(j).getStartCol() == tokenString.get(j - 1).getFinishCol()) {
						// check if the token is comment
						if (tokenString.get(j).getType().equals("COMMENT"))
							isNextCommentTokenExist = true;
						content += tokenString.get(j).getContent();
						j++;
					} else
						// otherwise, exit loop
						break;
				}

				// if not found, skip the merging
				if (!isNextCommentTokenExist) {
					i++;
					continue;
				}
			} else {
				/*
				 * for whitespaces, it is similar as for comments except that in addition to
				 * general rules, it stops once it finds non-whitespace tokens (comment tokens).
				 */
				while (j < tokenString.size()) {
					// it should be merged to current token if their positions
					// are adjacent and that token is whitespace
					if (tokenString.get(j).getType().equals("WS")
							&& tokenString.get(j).getStartRow() == tokenString.get(j - 1).getFinishRow()
							&& tokenString.get(j).getStartCol() == tokenString.get(j - 1).getFinishCol()) {
						content += tokenString.get(j).getContent();
						j++;
					} else
						// otherwise, exit loop
						break;
				}
			}
			// set the content
			cur.setContent(content);
			cur.setContentForComparison(content);

			// set finish row and col of cur as the comments are merged.
			// j is decremented by 1 as it is incremented each time the
			// comment
			// or whitespace added
			cur.setFinishRow(tokenString.get(j - 1).getFinishRow());
			cur.setFinishCol(tokenString.get(j - 1).getFinishCol());

			// remove all tokens which have been merged to cur
			while (j - i > 1) {
				tokenString.remove(i + 1);
				j--;
			}

			// increment the index (i)
			i++;
		}
	}

	public static ArrayList<FeedbackToken> generateCommentAndWhitespaceTokens(String filePath) {
		/*
		 * generate comment and whitespace token list and return them as one
		 */
		ArrayList<FeedbackToken> commentWhitespaceTokens = new ArrayList<>();

		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));

			boolean isInTripleQuoteString = false;

			int curLine = 1; // row starts from 1 but column from 0
			String line;
			String lineWithoutComment;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains("'''")) {
					if (isInTripleQuoteString) {
						// closing triple quote
						int closingPos = line.indexOf("'''");
						// replace all chars in that region as -, we will skip
						// that anyway
						String o = "";
						for (int k = 0; k < closingPos + 3; k++)
							o += "-";
						o = o + line.substring(closingPos + 3, line.length());
						// set as the line
						line = o;
						// mark to be out of that quote string
						isInTripleQuoteString = false;
					} else {
						// opening triple quote
						int openingPos = line.lastIndexOf("'''");
						// replace all chars in that region as -, we will skip
						// that anyway
						String o = "";
						for (int k = openingPos; k < line.length(); k++)
							o += "-";
						o = line.substring(0, openingPos) + o;
						// set as the line
						line = o;
						// mark to be out of that quote string
						isInTripleQuoteString = true;
					}
				} else {
					// if it is still in triple quote, skip
					if (isInTripleQuoteString) {
						// increment curLine
						curLine++;
						// skip this iteration
						continue;
					}
				}

				int commentPos = -1;
				if (isInTripleQuoteString == false)
					commentPos = getCommentStartCol(line);

				if (commentPos != -1) {
					// if there is a comment, create a line which comment is
					// removed
					lineWithoutComment = line.substring(0, commentPos);
				} else {
					lineWithoutComment = line;
				}

				// embed all whitespace tokens on that line
				String whitespacecontent = "";
				boolean isInSingleQuoteString = false;
				boolean isInDoubleQuoteString = false;
				for (int col = 0; col < lineWithoutComment.length(); col++) {
					char c = lineWithoutComment.charAt(col);
					if ((c == ' ' || c == '\t') && isInDoubleQuoteString == false && isInSingleQuoteString == false) {
						whitespacecontent += c;
					} else {
						if (c == '\'') {
							// dealing if that is escape character
							if (col > 0 && lineWithoutComment.charAt(col - 1) == '\\')
								continue;

							// dealing with spacing in single quote string
							// literal
							if (isInSingleQuoteString) {
								isInSingleQuoteString = false;
							} else if (isInDoubleQuoteString) {
								// do nothing
							} else {
								isInSingleQuoteString = true;
							}
						} else if (c == '\"') {
							// dealing if that is escape character
							if (col > 0 && lineWithoutComment.charAt(col - 1) == '\\')
								continue;

							// dealing with spacing in single quote string
							// literal
							if (isInDoubleQuoteString) {
								isInDoubleQuoteString = false;
							} else if (isInSingleQuoteString) {
								// do nothing
							} else {
								isInDoubleQuoteString = true;
							}
						}
						if (whitespacecontent.length() > 0) {
							commentWhitespaceTokens.add(new PythonFeedbackToken(whitespacecontent, "WS", curLine,
									col - whitespacecontent.length(), curLine, col + 1, null));
							whitespacecontent = "";

						}
					}
				}

				if (commentPos != -1) {
					// add the last whitespace content
					if (whitespacecontent.length() > 0) {
						int col = lineWithoutComment.length();
						commentWhitespaceTokens.add(new PythonFeedbackToken(whitespacecontent, "WS", curLine,
								col - whitespacecontent.length() + 1, curLine, col, null));
					}

					// add the comment
					commentWhitespaceTokens.add(new PythonFeedbackToken(line.substring(commentPos), "COMMENT", curLine,
							commentPos, curLine, line.length(), null));

					/*
					 * add newline as it ends the line now. startPos is assured to be non-negative
					 * as for each row transition, we assume it ends at the first column of the next
					 * line.
					 */
					commentWhitespaceTokens.add(new PythonFeedbackToken("\n", "WS", curLine, Math.max(0, line.length()),
							curLine + 1, 0, null));
				} else {
					/*
					 * add the last whitespace content with a newline. startPos is assured to be
					 * non-negative as for each row transition, we assume it ends at the first
					 * column of the next line.
					 */
					if (isInTripleQuoteString == false) {
						whitespacecontent += "\n";
						commentWhitespaceTokens.add(new PythonFeedbackToken(whitespacecontent, "WS", curLine,
								Math.max(0, line.length() - 1), curLine + 1, 0, null));
					}
				}

				// increment curLine
				curLine++;

			}

			// Always close files.
			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return commentWhitespaceTokens;
	}

	private static int getCommentStartCol(String line) {
		boolean isInDoubleQuote = false;
		boolean isInSingleQuote = false;
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (isInDoubleQuote) {
				if (c == '\"')
					isInDoubleQuote = false;
			} else if (isInSingleQuote) {
				if (c == '\'')
					isInSingleQuote = false;
			} else {
				if (c == '\"')
					isInDoubleQuote = true;
				else if (c == '\'')
					isInSingleQuote = true;
				else if (c == '#')
					return i;
			}
		}

		return -1;
	}

	public static ArrayList<FeedbackToken> generateSyntaxTokenString(String filePath) {
		// excluding comment and whitespaces
		try {
			ArrayList<FeedbackToken> result = new ArrayList<>();
			// build the lexer
			Lexer lexer = new Python3Lexer(CharStreams.fromFileName(filePath));
			lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
			// extract the tokens
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			tokens.fill();
			// for each element excluding eof, form the token and embed start
			// finish location
			for (int index = 0; index < tokens.size() - 1; index++) {
				Token token = tokens.get(index);
				String type = Python3Lexer.VOCABULARY.getDisplayName(token.getType());
				// remove all whitespace tokens as these tokens are the
				// summarised version of whitespaces
				if (type.equals("93") || type.equals("94") || type.equals("NEWLINE"))
					continue;
				result.add(
						new PythonFeedbackToken(token.getText(), type, token.getLine(), token.getCharPositionInLine(),
								token.getLine(), token.getCharPositionInLine() + token.getText().length(), token));
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
