package p3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import support.javaantlr.Java9Lexer;
import support.pythonantlr.Python3Lexer;
import support.stringmatching.GSTMatchTuple;
import support.stringmatching.GreedyStringTiling;

public class TemplateCodeRemoval {
	
	public static void removeTemplateCode(String rootDirFilepath, String ext, String templatePath) {
		String outputRootDirFilepath = rootDirFilepath.substring(0,
				rootDirFilepath.lastIndexOf(File.separator) + 1)
				+ "[template removed] "
				+ rootDirFilepath.substring(
						rootDirFilepath.lastIndexOf(File.separator) + 1,
						rootDirFilepath.length());

		// create output directory
		new File(outputRootDirFilepath).mkdir();
		
		ArrayList<TemplateRemovalTokenForStandardStrange> templateTokenString = null;
		if (ext.endsWith("java")) {
			// java
			templateTokenString = getDefaultTokenStringJava(templatePath);
		} else if (ext.endsWith("py")) {
			// python
			templateTokenString = getDefaultTokenStringPython(templatePath);
		}
		String[] str2 = convertTokenTupleToString(templateTokenString);

		// for each student dir, take the code files and merge them as a file
		File rootDir = new File(rootDirFilepath);
		File[] studentDir = rootDir.listFiles();
		for (File sdir : studentDir) {
			if (sdir.isDirectory()) {
				File outputDir = new File(outputRootDirFilepath + File.separator
						+ sdir.getName());
				outputDir.mkdir();
				_removeTemplateCode(sdir, outputDir, ext, str2);
			}
		}
	}
	
	public static void _removeTemplateCode(File sfile, File tFile, String ext,  String[] templateString) {
		if (sfile.isDirectory()) {
			// copy and create similar dir
			File newTFile = new File(tFile.getAbsolutePath() + File.separator + sfile.getName());
			newTFile.mkdir();
			
			File[] schildren = sfile.listFiles();
			for (File sc : schildren) {
				_removeTemplateCode(sc, newTFile, ext, templateString);
			}
		} else {
			String name = sfile.getName();
			// if the file does not end with the extension, ignore
			if (name.toLowerCase().endsWith(ext) == false)
				return;
			
			String targetFilePath = tFile.getAbsolutePath() + File.separator + sfile.getName();
			
			removeTemplateCodeFromAFile(sfile.getAbsolutePath(), templateString, targetFilePath, ext);
		}
	}

	public static void removeTemplateCodeFromAFile(String sourceFilePath,
			String[] templateString, String targetFilePath, String ext) {

		// generate the token string
		ArrayList<TemplateRemovalTokenForStandardStrange> sourceTokenString = null;
		String openingComment = "";
		String closingComment = "";
		String openingCommentForRemovedContent = "";

		if (ext.endsWith("java")) {
			// java
			sourceTokenString = getDefaultTokenStringJava(sourceFilePath);
			openingComment = "/*";
			closingComment = "*/";
			openingCommentForRemovedContent = "//";
		} else if (ext.endsWith("py")) {
			// python
			sourceTokenString = getDefaultTokenStringPython(sourceFilePath);
			openingComment = "#";
			closingComment = "#";
			openingCommentForRemovedContent = "#";
		}

		// convert to array as RKRGST needs such kind of input
		String[] str1 = convertTokenTupleToString(sourceTokenString);

		// RKRGST
		ArrayList<GSTMatchTuple> tiles = GreedyStringTiling.getMatchedTiles(
				str1, templateString, 2);
		// mark per token whether that token should be removed
		boolean[] isRemovedSyntax = new boolean[str1.length];
		// iterate and mark all tokens that will be excluded
		for (GSTMatchTuple tile : tiles) {
			for (int i = 0; i < tile.length; i++) {
				isRemovedSyntax[tile.patternPosition + i] = true;
			}
		}

		// store all code lines affected by the template code removal
		ArrayList<Integer> targetLinesForWritingTheRemovedFragments = new ArrayList<>();

		// generate the code with the template code removed
		StringBuilder sbBlank = new StringBuilder();
		// store the original code for reporting which ones will have been
		// removed
		StringBuilder sbOrig = new StringBuilder();

		// syntax counter refers to the position in syntax string used for
		// RKRGST comparison
		int syntaxIndexCounter = 0;
		// general counter refers to the position in general string taken from
		// the code (including whitespaces and comments)
		int generalIndexCounter = 0;

		// iterate the general token string
		while (generalIndexCounter < sourceTokenString.size()) {
			// increment syntax index only if the visited token is syntax
			if (!sourceTokenString.get(generalIndexCounter).getType()
					.equals("WS")
					&& !sourceTokenString.get(generalIndexCounter).getType()
							.endsWith("COMMENT")) {

				// if the token should be removed
				if (isRemovedSyntax[syntaxIndexCounter] == true) {
					// put spaces as a replacement for token text
					TemplateRemovalTokenForStandardStrange t = sourceTokenString
							.get(generalIndexCounter);
					int spacesize = t.getText().length();
					String spaces = "";
					for (int i = 0; i < spacesize; i++)
						spaces += " ";
					sbBlank.append(spaces);

					// for orig code, just put the original text
					sbOrig.append(sourceTokenString.get(generalIndexCounter)
							.getText());

					// save the target lines
					if (targetLinesForWritingTheRemovedFragments.size() == 0) {
						targetLinesForWritingTheRemovedFragments.add(t
								.getLine());
					} else {
						// check whether the line is the same as previous
						int lastIndex = targetLinesForWritingTheRemovedFragments
								.size() - 1;
						int lastLine = targetLinesForWritingTheRemovedFragments
								.get(lastIndex);
						if (lastLine != t.getLine()) {
							// add as a new entry if different
							targetLinesForWritingTheRemovedFragments.add(t
									.getLine());
						}
					}
				} else {
					// set both code strings for blank and orig with the text
					sbBlank.append(sourceTokenString.get(generalIndexCounter)
							.getText());
					sbOrig.append(sourceTokenString.get(generalIndexCounter)
							.getText());
				}

				// increase syntax counter
				syntaxIndexCounter++;

			} else {
				// set both code strings for blank and orig with the text
				sbBlank.append(sourceTokenString.get(generalIndexCounter)
						.getText());
				sbOrig.append(sourceTokenString.get(generalIndexCounter)
						.getText());
			}

			// increase general index
			generalIndexCounter++;
		}

		// split both code strings based on newlines
		String[] codeInLines = sbBlank.toString().split(System.lineSeparator());
		String[] codeInLinesOrig = sbOrig.toString().split(
				System.lineSeparator());

		// write the string to target file
		try {
			int removedFragmentCounter = 0;
			FileWriter fw = new FileWriter(new File(targetFilePath));
			for (int i = 0; i < codeInLines.length; i++) {
				// if there are still target lines available for processing
				if (removedFragmentCounter < targetLinesForWritingTheRemovedFragments
						.size()) {
					// one line before the removed fragment
					if (i == targetLinesForWritingTheRemovedFragments
							.get(removedFragmentCounter) - 1) {

						// title of the comment
						String title = "line below prior to template removal:";
						// the content, taken from original code string
						String origContent = codeInLinesOrig[i];

						// get the max length between those two strings
						int maxLength = Math.max(title.length(),
								origContent.length());

						// generate headerfooter
						String headerfooter = "";
						for (int k = 0; k < maxLength; k++) {
							headerfooter += "=";
						}

						// header
						fw.write(openingComment + " " + headerfooter + " "
								+ closingComment + System.lineSeparator());

						// title
						fw.write(openingCommentForRemovedContent + " " + title
								+ System.lineSeparator());

						// the removed fragment
						fw.write(openingCommentForRemovedContent + " "
								+ origContent + System.lineSeparator());

						// footer
						fw.write(openingComment + " " + headerfooter + " "
								+ closingComment + System.lineSeparator());

						// move to the next fragment
						removedFragmentCounter++;
					}
				}
				fw.write(codeInLines[i] + System.lineSeparator());
			}
			// fw.write(sb.toString());
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String[] convertTokenTupleToString(
			ArrayList<TemplateRemovalTokenForStandardStrange> arr) {
		int size = 0;
		for (int i = 0; i < arr.size(); i++) {
			if (arr.get(i).getType().equals("WS")
					|| arr.get(i).getType().endsWith("COMMENT"))
				continue;
			else
				size++;
		}

		String[] s = new String[size];
		int count = 0;
		for (int i = 0; i < arr.size(); i++) {
			if (arr.get(i).getType().equals("WS")
					|| arr.get(i).getType().endsWith("COMMENT"))
				continue;
			else {
				s[count] = arr.get(i).getText();
				count++;
			}
		}
		return s;
	}

	public static ArrayList<TemplateRemovalTokenForStandardStrange> getDefaultTokenStringJava(
			String filePath) {
		// take all tokens including comments and whitespaces and keep some
		// tokens as keywords
		try {
			ArrayList<TemplateRemovalTokenForStandardStrange> result = new ArrayList<>();
			// build the lexer
			Lexer lexer = new Java9Lexer(new ANTLRFileStream(filePath));
			// extract the tokens
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			tokens.fill();
			// only till size-1 as the last one is EOF token
			for (int index = 0; index < tokens.size() - 1; index++) {
				Token token = tokens.get(index);
				// take all tokens including whitespaces
				result.add(new TemplateRemovalTokenForStandardStrange(token
						.getText(), Java9Lexer.VOCABULARY.getDisplayName(token
						.getType()), token.getLine()));
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ArrayList<TemplateRemovalTokenForStandardStrange> getDefaultTokenStringPython(
			String filePath) {
		// take all tokens including comments and whitespaces and keep some
		// tokens as keywords
		try {
			ArrayList<TemplateRemovalTokenForStandardStrange> result = new ArrayList<>();
			// build the lexer
			Lexer lexer = new Python3Lexer(new ANTLRFileStream(filePath));
			// extract the tokens
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			tokens.fill();
			// only till size-1 as the last one is EOF token
			for (int index = 0; index < tokens.size() - 1; index++) {
				Token token = tokens.get(index);
				String type = Python3Lexer.VOCABULARY.getDisplayName(token
						.getType());

				// this is used to make the generated tokens similar to Java's
				if (type.equals("NAME"))
					type = "Identifier";
				else if (type.equals("FLOAT_NUMBER"))
					type = "FloatingPointLiteral";

				// remove all whitespace tokens as these tokens are the
				// summarised version of whitespaces
				if (type.equals("93") || type.equals("94")
						|| type.equals("NEWLINE"))
					continue;

				// take all tokens excluding whitespaces
				result.add(new TemplateRemovalTokenForStandardStrange(token
						.getText(), type, token.getLine(), token
						.getCharPositionInLine()));
			}

			// add whitespace and comment tokens
			result.addAll(_generateCommentAndWhitespaceTokens(filePath));

			// sort the result
			Collections.sort(result);

			// merging adjacent whitespaces
			for (int i = 0; i < result.size() - 1; i++) {
				TemplateRemovalTokenForStandardStrange cur = result.get(i);
				TemplateRemovalTokenForStandardStrange next = result.get(i + 1);
				// if there are two adjacent whitespaces, merge them
				if (cur.getType().equals("WS") && next.getType().equals("WS")) {
					// merge the text for next token
					next.setText(cur.getText() + next.getText());
					next.setLine(cur.getLine());
					// and remove the current one
					result.remove(i);
					i--;
				}
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static ArrayList<TemplateRemovalTokenForStandardStrange> _generateCommentAndWhitespaceTokens(
			String filePath) {
		/*
		 * generate comment and whitespace token list and return them as one
		 */
		ArrayList<TemplateRemovalTokenForStandardStrange> commentWhitespaceTokens = new ArrayList<>();

		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					filePath));

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
					commentPos = getCommentStartColPython(line);

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
					if ((c == ' ' || c == '\t')
							&& isInDoubleQuoteString == false
							&& isInSingleQuoteString == false) {
						whitespacecontent += c;
					} else {
						if (c == '\'') {
							// dealing if that is escape character
							if (col > 0
									&& lineWithoutComment.charAt(col - 1) == '\\')
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
							if (col > 0
									&& lineWithoutComment.charAt(col - 1) == '\\')
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
							commentWhitespaceTokens
									.add(new TemplateRemovalTokenForStandardStrange(
											whitespacecontent, "WS", curLine,
											col - whitespacecontent.length()));
							whitespacecontent = "";

						}
					}
				}

				if (commentPos != -1) {
					// add the last whitespace content
					if (whitespacecontent.length() > 0) {
						int col = lineWithoutComment.length();
						commentWhitespaceTokens
								.add(new TemplateRemovalTokenForStandardStrange(
										whitespacecontent, "WS", curLine, col
												- whitespacecontent.length()
												+ 1));
					}

					// add the comment
					commentWhitespaceTokens
							.add(new TemplateRemovalTokenForStandardStrange(
									line.substring(commentPos), "COMMENT",
									curLine, commentPos));

					/*
					 * add newline as it ends the line now. startPos is assured
					 * to be non-negative as for each row transition, we assume
					 * it ends at the first column of the next line.
					 */
					commentWhitespaceTokens
							.add(new TemplateRemovalTokenForStandardStrange(
									System.lineSeparator(), "WS", curLine, Math
											.max(0, line.length())));
				} else {
					/*
					 * add the last whitespace content with a newline. startPos
					 * is assured to be non-negative as for each row transition,
					 * we assume it ends at the first column of the next line.
					 */
					if (isInTripleQuoteString == false) {
						whitespacecontent += System.lineSeparator();
						commentWhitespaceTokens
								.add(new TemplateRemovalTokenForStandardStrange(
										whitespacecontent, "WS", curLine, Math
												.max(0, line.length() - 1)));
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

	private static int getCommentStartColPython(String line) {
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
}

// private class just for this process, copied and adapted from
// P4TupleForObfuscationAndFragmentRemoval
class TemplateRemovalTokenForStandardStrange implements
		Comparable<TemplateRemovalTokenForStandardStrange> {
	private String text, type;
	private int line;

	// store the text prior generalisation
	private String rawText;

	// only used for python
	private int column;

	public TemplateRemovalTokenForStandardStrange(String text, String type,
			int line) {
		this(text, type, line, -1);
	}

	public TemplateRemovalTokenForStandardStrange(String text, String type,
			int line, int column) {
		super();
		this.text = text;
		this.rawText = text;
		this.type = type;
		this.line = line;
		this.column = column;
	}

	public void incrementLine() {
		this.line++;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public String getRawText() {
		return rawText;
	}

	public void setRawText(String rawText) {
		this.rawText = rawText;
	}

	@Override
	public int compareTo(TemplateRemovalTokenForStandardStrange arg0) {
		// TODO Auto-generated method stub
		if (this.getLine() != arg0.getLine())
			return this.getLine() - arg0.getLine();
		else {
			return this.getColumn() - arg0.getColumn();
		}
	}

	public String toString() {
		return this.getText();
	}

}
