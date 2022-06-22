package p3.feedbackgenerator.language.python;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;

import p3.feedbackgenerator.htmlgenerator.Code1FeedbackComparator;
import p3.feedbackgenerator.htmlgenerator.Code2FeedbackComparator;
import p3.feedbackgenerator.htmlgenerator.HtmlTableStringGenerator;
import p3.feedbackgenerator.language.java.JavaCodeFormatter;
import p3.feedbackgenerator.language.java.JavaFeedbackGenerator;
import p3.feedbackgenerator.language.java.JavaHtmlGenerator;
import p3.feedbackgenerator.message.FeedbackMessage;
import p3.feedbackgenerator.message.FeedbackMessageGenerator;
import p3.feedbackgenerator.message.SyntaxFeedbackMessage;
import p3.feedbackgenerator.token.FeedbackToken;
import p3.feedbackgenerator.token.FeedbackTokenComparator;

public class PythonHtmlGenerator {
	public static void generateHtml(String codepath1, String codepath2, String assignmentname1, String assignmentname2,
			String templateHTMLPath, String outputHTMLPath, String homePath, int minimumMatchLength,
			String languageCode, ArrayList<ArrayList<String>> additionalKeywords) throws Exception {
		
		boolean isSyntacticSim = true;
		
		// for default strings
		generateRequiredDataForInformation(codepath1, codepath2, minimumMatchLength, languageCode, additionalKeywords,
				isSyntacticSim, true);
		String[] defaultInfoStrings = generateInformationStrings(codepath1, codepath2, "origtablecontent", "", true,
				languageCode);

		String sumTable = HtmlTableStringGenerator.getTableContentForSummary(commentMessage, syntaxMessage,
				tokenString1, tokenString2, languageCode);
		String sumExplanation;
		if (languageCode.equals("en"))
			sumExplanation = HtmlTableStringGenerator.getExplanationContentForSummary();
		else
			sumExplanation = HtmlTableStringGenerator.getExplanationContentForSummaryId();

		// format both code files for further use
		boolean isFormatted1 = PythonCodeFormatter.formatCode(codepath1);
		boolean isFormatted2 = PythonCodeFormatter.formatCode(codepath2);

		// set message if at least one code fails to be formatted.
		String messageFormattedCode = "";
		if (languageCode.equals("en")) { // english
			if (isFormatted1 == false && isFormatted2 == true) {
				messageFormattedCode = "alert('The code formatting for the left panel fails')";
			} else if (isFormatted1 == true && isFormatted2 == false) {
				messageFormattedCode = "alert('The code formatting for the right panel fails')";
			} else if (isFormatted1 == false && isFormatted2 == false) {
				messageFormattedCode = "alert('The code formatting for both panels fails')";
			}
		} else { // indonesian
			if (isFormatted1 == false && isFormatted2 == true) {
				messageFormattedCode = "alert('Kode pada panel kiri tidak dapat diformat')";
			} else if (isFormatted1 == true && isFormatted2 == false) {
				messageFormattedCode = "alert('Kode pada panel kanan tidak dapat diformat')";
			} else if (isFormatted1 == false && isFormatted2 == false) {
				messageFormattedCode = "alert('Kode pada kedua panel tidak dapat diformat')";
			}
		}

		// get the hometab
		String homeTab = "";
		if (homePath != null && homePath.length() > 0) {
			homeTab = "<button class=\"tablinks\" onclick=\"window.open('" + homePath
					+ "', '_self');\">Return to home</button>";
		}

		// for formatted code
		generateRequiredDataForInformation(codepath1 + ".formatted", codepath2 + ".formatted", minimumMatchLength,
				languageCode, additionalKeywords, isSyntacticSim, true);
		String[] formattedInfoStrings = generateInformationStrings(codepath1 + ".formatted", codepath2 + ".formatted",
				"nowstablecontent", "nows", true, languageCode);

		/*
		 * for formatted code without comments. The preprocessing is not required
		 * anymore as it uses the same data as formatted code with comments.
		 */
		String[] formattedInfoStringsWithoutComments = generateInformationStrings(codepath1 + ".formatted",
				codepath2 + ".formatted", "syontablecontent", "syon", false, languageCode);

		/*
		 * for token renaming, it is similar to formatted code without comments except
		 * that some tokens are renamed.
		 */
		String[] formattedAndRenamedInfoStringsWithoutComments = generateInformationStringsTokenRenaming(
				codepath1 + ".formatted", codepath2 + ".formatted", languageCode);

		File templateFile = new File(templateHTMLPath);
		File outputFile = new File(outputHTMLPath);
		BufferedReader fr = new BufferedReader(new FileReader(templateFile));
		BufferedWriter fw = new BufferedWriter(new FileWriter(outputFile));
		String line;
		while ((line = fr.readLine()) != null) {
			if (line.contains("@hometab")) {
				line = line.replace("@hometab", homeTab);
			}

			if (line.contains("@codepath1")) {
				line = line.replace("@codepath1", codepath1);
			}
			if (line.contains("@codepath2")) {
				line = line.replace("@codepath2", codepath2);
			}
			if (line.contains("@assignmentpath1")) {
				line = line.replace("@assignmentpath1", assignmentname1);
			}
			if (line.contains("@assignmentpath2")) {
				line = line.replace("@assignmentpath2", assignmentname2);
			}

			// for default data
			if (line.contains("@code1")) {
				line = line.replace("@code1", defaultInfoStrings[0]);
			}
			if (line.contains("@code2")) {
				line = line.replace("@code2", defaultInfoStrings[1]);
			}
			if (line.contains("@tablecontent")) {
				line = line.replace("@tablecontent", defaultInfoStrings[2]);
			}
			if (line.contains("@explanation")) {
				line = line.replace("@explanation", defaultInfoStrings[3]);
			}

			// for formatted code
			if (line.contains("@messageformattedcode")) {
				line = line.replace("@messageformattedcode", messageFormattedCode);
			}
			if (line.contains("@nowscode1")) {
				line = line.replace("@nowscode1", formattedInfoStrings[0]);
			}
			if (line.contains("@nowscode2")) {
				line = line.replace("@nowscode2", formattedInfoStrings[1]);
			}
			if (line.contains("@nowstablecontent")) {
				line = line.replace("@nowstablecontent", formattedInfoStrings[2]);
			}
			if (line.contains("@nowsexplanation")) {
				line = line.replace("@nowsexplanation", formattedInfoStrings[3]);
			}

			// for formatted code without comments
			if (line.contains("@syoncode1")) {
				line = line.replace("@syoncode1", formattedInfoStringsWithoutComments[0]);
			}
			if (line.contains("@syoncode2")) {
				line = line.replace("@syoncode2", formattedInfoStringsWithoutComments[1]);
			}
			if (line.contains("@syontablecontent")) {
				line = line.replace("@syontablecontent", formattedInfoStringsWithoutComments[2]);
			}
			if (line.contains("@syonexplanation")) {
				line = line.replace("@syonexplanation", formattedInfoStringsWithoutComments[3]);
			}

			// for renamed token mode
			if (line.contains("@renacode1")) {
				line = line.replace("@renacode1", formattedAndRenamedInfoStringsWithoutComments[0]);
			}
			if (line.contains("@renacode2")) {
				line = line.replace("@renacode2", formattedAndRenamedInfoStringsWithoutComments[1]);
			}
			if (line.contains("@renatablecontent")) {
				line = line.replace("@renatablecontent", formattedAndRenamedInfoStringsWithoutComments[2]);
			}
			if (line.contains("@renaexplanation")) {
				line = line.replace("@renaexplanation", formattedAndRenamedInfoStringsWithoutComments[3]);
			}

			if (line.contains("@sumtablecontent")) {
				line = line.replace("@sumtablecontent", sumTable);
			}
			if (line.contains("@sumexplanation")) {
				line = line.replace("@sumexplanation", sumExplanation);
			}

			fw.write(line);
			fw.write(System.lineSeparator());
		}
		fr.close();
		fw.close();

		// remove the formatted files
		(new File(codepath1 + ".formatted")).delete();
		(new File(codepath2 + ".formatted")).delete();
	}

	// this method is dedicated for CSTRANGE
	public static void generateHtmlForCSTRANGE(String codepath1, String codepath2, String dirname1, String dirname2, String simTags,
			String templateHTMLPath, String outputHTMLPath, int minimumMatchLength,
			String languageCode,String jplagTag, ArrayList<ArrayList<String>> additionalKeywords, boolean isSyntacticSim)
			throws Exception {

		// for default strings
		generateRequiredDataForInformation(codepath1, codepath2, minimumMatchLength, languageCode, additionalKeywords,
				isSyntacticSim, false);
		String[] defaultInfoStrings = generateInformationStrings(codepath1, codepath2, "origtablecontent", "", true,
				languageCode);

		String sumTable = HtmlTableStringGenerator.getTableContentForSummary(commentMessage, syntaxMessage,
				tokenString1, tokenString2, languageCode);
		String sumExplanation;
		if (languageCode.equals("en"))
			sumExplanation = HtmlTableStringGenerator.getExplanationContentForSummary();
		else
			sumExplanation = HtmlTableStringGenerator.getExplanationContentForSummaryId();
		
		// if syntactic sim is not used (surface sim is used), preprocess all content
		// for comparison on these syntax token lists just for token generalisation
		// layout
		if (isSyntacticSim == false) {
			PythonFeedbackGenerator.generaliseTokenString(tokenString1);
			PythonFeedbackGenerator.generaliseTokenString(tokenString2);
		}

		/*
		 * for code without comments. The preprocessing is not required
		 * anymore as it uses the same data as formatted code with comments.
		 */
		String[] formattedInfoStringsWithoutComments = generateInformationStrings(codepath1,
				codepath2, "syontablecontent", "syon", false, languageCode);

		/*
		 * for token renaming, it is similar to formatted code without comments except
		 * that some tokens are renamed.
		 */
		String[] formattedAndRenamedInfoStringsWithoutComments = generateInformationStringsTokenRenaming(
				codepath1, codepath2, languageCode);

		File templateFile = new File(templateHTMLPath);
		File outputFile = new File(outputHTMLPath);
		BufferedReader fr = new BufferedReader(new FileReader(templateFile));
		BufferedWriter fw = new BufferedWriter(new FileWriter(outputFile));
		String line;
		while ((line = fr.readLine()) != null) {
			if (line.contains("@codepath1")) {
				line = line.replace("@codepath1", dirname1);
			}
			if (line.contains("@codepath2")) {
				line = line.replace("@codepath2", dirname2);
			}
			if (line.contains("@comparisonmode")) {
				if (isSyntacticSim)
					line = line.replace("@comparisonmode", "Syntactic");
				else
					line = line.replace("@comparisonmode", "Surface");
			}
			if (line.contains("@jplagtag")) {
				line = line.replace("@jplagtag", jplagTag);
			}
			if (line.contains("@simtags")) {
				line = line.replace("@simtags", simTags);
			}

			// for default data
			if (line.contains("@code1")) {
				line = line.replace("@code1", defaultInfoStrings[0]);
			}
			if (line.contains("@code2")) {
				line = line.replace("@code2", defaultInfoStrings[1]);
			}
			if (line.contains("@tablecontent")) {
				line = line.replace("@tablecontent", defaultInfoStrings[2]);
			}
			if (line.contains("@explanation")) {
				line = line.replace("@explanation", defaultInfoStrings[3]);
			}

			// for formatted code without comments
			if (line.contains("@syoncode1")) {
				line = line.replace("@syoncode1", formattedInfoStringsWithoutComments[0]);
			}
			if (line.contains("@syoncode2")) {
				line = line.replace("@syoncode2", formattedInfoStringsWithoutComments[1]);
			}
			if (line.contains("@syontablecontent")) {
				line = line.replace("@syontablecontent", formattedInfoStringsWithoutComments[2]);
			}
			if (line.contains("@syonexplanation")) {
				line = line.replace("@syonexplanation", formattedInfoStringsWithoutComments[3]);
			}

			// for renamed token mode
			if (line.contains("@renacode1")) {
				line = line.replace("@renacode1", formattedAndRenamedInfoStringsWithoutComments[0]);
			}
			if (line.contains("@renacode2")) {
				line = line.replace("@renacode2", formattedAndRenamedInfoStringsWithoutComments[1]);
			}
			if (line.contains("@renatablecontent")) {
				line = line.replace("@renatablecontent", formattedAndRenamedInfoStringsWithoutComments[2]);
			}
			if (line.contains("@renaexplanation")) {
				line = line.replace("@renaexplanation", formattedAndRenamedInfoStringsWithoutComments[3]);
			}

			if (line.contains("@sumtablecontent")) {
				line = line.replace("@sumtablecontent", sumTable);
			}
			if (line.contains("@sumexplanation")) {
				line = line.replace("@sumexplanation", sumExplanation);
			}

			fw.write(line);
			fw.write(System.lineSeparator());
		}
		fr.close();
		fw.close();
	}

	private static ArrayList<FeedbackToken> tokenString1, tokenString2;
	private static ArrayList<FeedbackMessage> commentMessage;
	private static ArrayList<FeedbackMessage> syntaxMessage;

	private static void generateRequiredDataForInformation(String filepath1, String filepath2, int minimumMatchLength,
			String languageCode, ArrayList<ArrayList<String>> additionalKeywords, boolean isSyntacticSim, boolean isExplanationDetailed) {

		tokenString1 = new ArrayList<>();
		ArrayList<FeedbackToken> syntaxString1 = PythonFeedbackGenerator.generateSyntaxTokenString(filepath1);
		tokenString1.addAll(syntaxString1);
		ArrayList<FeedbackToken> commentWhitespaceString1 = PythonFeedbackGenerator
				.generateCommentAndWhitespaceTokens(filepath1);
		tokenString1.addAll(commentWhitespaceString1);
		Collections.sort(tokenString1, new FeedbackTokenComparator());
		PythonFeedbackGenerator.mergeAdjacentCommentsAndWhitespacesOnTokenString(tokenString1);

		tokenString2 = new ArrayList<>();
		ArrayList<FeedbackToken> syntaxString2 = PythonFeedbackGenerator.generateSyntaxTokenString(filepath2);
		tokenString2.addAll(syntaxString2);
		ArrayList<FeedbackToken> commentWhitespaceString2 = PythonFeedbackGenerator
				.generateCommentAndWhitespaceTokens(filepath2);
		tokenString2.addAll(commentWhitespaceString2);
		Collections.sort(tokenString2, new FeedbackTokenComparator());
		PythonFeedbackGenerator.mergeAdjacentCommentsAndWhitespacesOnTokenString(tokenString2);

		// get comment messages
		commentMessage = FeedbackMessageGenerator.generateCommentDisguiseMessages(tokenString1, tokenString2,
				"py",languageCode, isExplanationDetailed);

		// preprocessing the syntax tokens
		if (isSyntacticSim) {
			PythonFeedbackGenerator.syntaxTokenStringPreprocessing(syntaxString1, additionalKeywords);
			PythonFeedbackGenerator.syntaxTokenStringPreprocessing(syntaxString2, additionalKeywords);
		}

		// get syntax reordering messages
		syntaxMessage = FeedbackMessageGenerator.generateSyntaxReorderingMessages(syntaxString1, syntaxString2,
				tokenString1, tokenString2, filepath1, filepath2, minimumMatchLength, languageCode, isExplanationDetailed);
	}

	private static String[] generateInformationStrings(String filepath1, String filepath2, String tableId, String mode,
			boolean isCommentIncluded, String humanLanguageId) throws Exception {
		// get the html strings
		String code1, code2;

		// sort syntax messages
		Collections.sort(syntaxMessage);
		// set visual ID for syntax message
		for (int i = 0; i < syntaxMessage.size(); i++) {
			FeedbackMessage fm = syntaxMessage.get(i);
			fm.setVisualId("s" + mode + (i + 1));
		}

		// sort comment messages
		Collections.sort(commentMessage);
		// set visual ID for comment message
		for (int i = 0; i < commentMessage.size(); i++) {
			FeedbackMessage fm = commentMessage.get(i);
			fm.setVisualId("c" + mode + (i + 1));
		}

		ArrayList<FeedbackToken> commentString1 = new ArrayList<>();
		for (int i = 0; i < tokenString1.size(); i++) {
			if (tokenString1.get(i).getType().equals("COMMENT"))
				commentString1.add(tokenString1.get(i));
		}

		ArrayList<FeedbackToken> commentString2 = new ArrayList<>();
		for (int i = 0; i < tokenString2.size(); i++) {
			if (tokenString2.get(i).getType().equals("COMMENT"))
				commentString2.add(tokenString2.get(i));
		}

		if (mode.equals("syon")) {
			code1 = readCode1WithoutComment(filepath1, syntaxMessage, commentString1, tableId);
			code2 = readCode2WithoutComment(filepath2, syntaxMessage, commentString2, tableId);
		} else {
			code1 = readCode1(filepath1, tableId, commentString1);
			code2 = readCode2(filepath2, tableId, commentString2);
		}

		// get table contents
		ArrayList<FeedbackMessage> messages = new ArrayList<>();
		messages.addAll(syntaxMessage);
		if (isCommentIncluded)
			messages.addAll(commentMessage);
		String tableContent = HtmlTableStringGenerator.getTableContentForMatches(messages, tableId, humanLanguageId);

		// get natural language explanation content
		String explanationContent = getExplanationContent(syntaxMessage, commentMessage, mode);

		return new String[] { code1, code2, tableContent, explanationContent };
	}

	private static String[] generateInformationStringsTokenRenaming(String filepath1, String filepath2,
			String humanLanguageId) throws Exception {
		// get the html strings
		String code1, code2;

		String mode = "rena";
		String tableId = "renatablecontent";

		// sort syntax messages
		Collections.sort(syntaxMessage);
		// set visual ID for syntax message
		for (int i = 0; i < syntaxMessage.size(); i++) {
			FeedbackMessage fm = syntaxMessage.get(i);
			fm.setVisualId("s" + mode + (i + 1));
		}

		// sort comment messages
		Collections.sort(commentMessage);
		// set visual ID for comment message
		for (int i = 0; i < commentMessage.size(); i++) {
			FeedbackMessage fm = commentMessage.get(i);
			fm.setVisualId("c" + mode + (i + 1));
		}

		code1 = readCode1AndRenamed(filepath1, syntaxMessage, tableId);

		code2 = readCode2AndRenamed(filepath2, syntaxMessage, tableId);

		// get table contents
		String tableContent = HtmlTableStringGenerator.getTableContentForMatches(syntaxMessage, tableId,
				humanLanguageId);

		// get natural language explanation content
		String explanationContent = getExplanationContent(syntaxMessage, commentMessage, mode);

		return new String[] { code1, code2, tableContent, explanationContent };
	}

	/*
	 * THESE METHODS ARE DIFFERENT WITH JAVA'S METHODS WITH THE SAME NAME AS THEY
	 * DEAL WITH ONE CHARACTER COMMENT (#). They generate strings representing the
	 * code files with some tags embedded. It applies a little about deterministic
	 * finite automata.
	 */

	private static String readCode1(String filepath, String tableId, ArrayList<FeedbackToken> commentString)
			throws Exception {
		// for source and target id on code
		char sourceId = 'a';
		char targetId = 'b';
		// sort all list based on tow then col on code 1
		Collections.sort(syntaxMessage, new Code1FeedbackComparator());
		Collections.sort(commentMessage, new Code1FeedbackComparator());
		Collections.sort(commentString);

		// embedding comment tags
		String code = "";
		BufferedReader fr = new BufferedReader(new FileReader(filepath));
		String line;
		int row = 1;
		// the first position from comment message list
		int commentMessageIndex = 0;
		// the first position from syntax message list
		int syntaxMessageIndex = 0;
		// the first position on comment list
		int commentIndex = 0;
		// mark that it is not in comment
		boolean isInComment = false;
		// refers to the ID that will be written
		String commentMessageId = null;
		String syntaxMessageId = null;

		// refers to following rows for a particular match

		int commentCounter = 1;
		int syntaxCounter = 1;
		while ((line = fr.readLine()) != null) {
			// to mark whether </a> tag is required at the end of line
			boolean closeTagRequired = false;
			for (int col = 0; col < line.length(); col++) {
				String c = HTMLSafeStringFormat(line.charAt(col));

				// if the position matches with comment message, start to mark
				if (commentMessageIndex < commentMessage.size()
						&& row == commentMessage.get(commentMessageIndex).getStartRowCode1()
						&& col == commentMessage.get(commentMessageIndex).getStartColCode1()) {
					// close previous tag if any
					if (closeTagRequired) {
						/*
						 * This mechanism to guarantee no whitespaces between syntax and comment are
						 * highlighted. Quite messy.
						 */
						// get the trimmed version of the code
						String codeTemp = code.trim();
						// get last non-whitespace char
						char lastChar = codeTemp.charAt(codeTemp.length() - 1);
						// get the position of the last non-whitespace char
						int indexOfLastNonWhitespace = code.lastIndexOf(lastChar);
						// put the closing tag after that last char
						code = code.substring(0, indexOfLastNonWhitespace + 1) + "</a>"
								+ code.substring(indexOfLastNonWhitespace + 1);
						// set no need for close tag
						closeTagRequired = false;
					}
					// set the comment ID
					commentMessageId = commentMessage.get(commentMessageIndex).getVisualId();
					// prepare the beginning tag
					String commentTagHeader = "<a class='commentsim' id='" + commentMessageId + sourceId + "' href=\"#"
							+ commentMessageId + targetId + "\" onclick=\"markSelected('" + commentMessageId + "','"
							+ tableId + "')\" >";
					// put the header tag along with current character
					code += (commentTagHeader + c);
					// inform that </a> needed to close
					closeTagRequired = true;
				} else if (commentMessageIndex < commentMessage.size()
						&& row == commentMessage.get(commentMessageIndex).getFinishRowCode1()
						&& col == commentMessage.get(commentMessageIndex).getFinishColCode1() - 1) {
					// if the end is found
					// add the end
					code += (c + "</a>");
					// set null
					commentMessageId = null;
					// increment comment index
					commentMessageIndex++;
					// set comment counter to 1
					commentCounter = 1;
					// inform that no </a> needed to close
					closeTagRequired = false;
					// add commentindex
					commentIndex++;
				} else if (commentIndex < commentString.size() && row == commentString.get(commentIndex).getStartRow()
						&& col == commentString.get(commentIndex).getStartCol()) {
					// if the position matches with comment message index but
					// not on matches, start to mark

					// close previous tag if any
					if (closeTagRequired) {
						/*
						 * This mechanism to guarantee no whitespaces between syntax and comment are
						 * highlighted. Quite messy.
						 */
						// get the trimmed version of the code
						String codeTemp = code.trim();
						// get last non-whitespace char
						char lastChar = codeTemp.charAt(codeTemp.length() - 1);
						// get the position of the last non-whitespace char
						int indexOfLastNonWhitespace = code.lastIndexOf(lastChar);
						// put the closing tag after that last char
						code = code.substring(0, indexOfLastNonWhitespace + 1) + "</a>"
								+ code.substring(indexOfLastNonWhitespace + 1);
						// set no need for close tag
						closeTagRequired = false;
					}

					// mark that it is in comment
					isInComment = true;
					// add the char
					code += c;

					/*
					 * this only applicable for python actually as the comment can only be one
					 * character long
					 */
					if (row == commentString.get(commentIndex).getFinishRow()
							&& col == commentString.get(commentIndex).getFinishCol() - 1) {
						// if the end is found, mark that it is not comment
						// anymore
						isInComment = false;
						commentIndex++;
					}
				} else if (commentIndex < commentString.size() && row == commentString.get(commentIndex).getFinishRow()
						&& col == commentString.get(commentIndex).getFinishCol() - 1) {
					// if the end is found, mark that it is not comment anymore
					isInComment = false;
					commentIndex++;
					// add the char
					code += c;
				} else if (syntaxMessageIndex < syntaxMessage.size()
						&& row == syntaxMessage.get(syntaxMessageIndex).getStartRowCode1()
						&& col == syntaxMessage.get(syntaxMessageIndex).getStartColCode1()) {
					// set the syntax ID
					syntaxMessageId = syntaxMessage.get(syntaxMessageIndex).getVisualId();
					// prepare the beginning tag
					String syntaxTagHeader = "<a class='syntaxsim' id='" + syntaxMessageId + sourceId + "' href=\"#"
							+ syntaxMessageId + targetId + "\" onclick=\"markSelected('" + syntaxMessageId + "','"
							+ tableId + "')\" >";
					// put the header tag along with current character
					code += (syntaxTagHeader + c);
					// inform that </a> needed to close
					closeTagRequired = true;
				} else if (syntaxMessageIndex < syntaxMessage.size()
						&& row == syntaxMessage.get(syntaxMessageIndex).getFinishRowCode1()
						&& col == syntaxMessage.get(syntaxMessageIndex).getFinishColCode1() - 1) {
					// if the end is found

					if (closeTagRequired == false) {
						/*
						 * if the beginning has not given any link open tag, add it
						 */
						String syntaxTagHeader = "<a class='syntaxsim' id='" + syntaxMessageId + sourceId
								+ syntaxCounter + "' href=\"#" + syntaxMessageId + targetId
								+ "\" onclick=\"markSelected('" + syntaxMessageId + "','" + tableId + "')\" >";
						code += syntaxTagHeader;
						syntaxCounter++;
					}

					// add the end
					code += (c + "</a>");
					// set null
					syntaxMessageId = null;
					// increment syntax index
					syntaxMessageIndex++;
					// set syntax counter to 1
					syntaxCounter = 1;
					// inform that no </a> needed to close
					closeTagRequired = false;
				} else {
					// this to re-highlight new line if it is still a part of
					// matched tokens
					if (commentMessageId != null) {
						if (closeTagRequired == false) {
							if (c.trim().length() > 0) {
								String commentTagHeader = "<a class='commentsim' id='" + commentMessageId + sourceId
										+ commentCounter + "' href=\"#" + commentMessageId + targetId
										+ "\" onclick=\"markSelected('" + commentMessageId + "','" + tableId + "')\" >";
								code += commentTagHeader;
								closeTagRequired = true;
								commentCounter++;
							}
						}
						code += c;
					} else if (syntaxMessageId != null) {
						if (closeTagRequired == false) {
							if (c.trim().length() > 0 && isInComment == false) {
								String syntaxTagHeader = "<a class='syntaxsim' id='" + syntaxMessageId + sourceId
										+ syntaxCounter + "' href=\"#" + syntaxMessageId + targetId
										+ "\" onclick=\"markSelected('" + syntaxMessageId + "','" + tableId + "')\" >";
								code += syntaxTagHeader;
								closeTagRequired = true;
								syntaxCounter++;
							}
						}
						code += c;
					} else {
						code += c;
					}
				}
			}
			if (closeTagRequired == true) {
				/*
				 * This mechanism to guarantee no remaining whitespaces at the end of the line
				 * are highlighted.
				 */
				// get the trimmed version of the code
				String codeTemp = code.trim();
				// get last non-whitespace char
				char lastChar = codeTemp.charAt(codeTemp.length() - 1);
				// get the position of the last non-whitespace char
				int indexOfLastNonWhitespace = code.lastIndexOf(lastChar);
				// put the closing tag after that last char
				code = code.substring(0, indexOfLastNonWhitespace + 1) + "</a>"
						+ code.substring(indexOfLastNonWhitespace + 1) + System.lineSeparator();
			} else {
				code += System.lineSeparator();
			}
			row++;
		}
		fr.close();
		return code;
	}

	private static String readCode2(String filepath, String tableId, ArrayList<FeedbackToken> commentString)
			throws Exception {
		/*
		 * this function cannot be merged to readCode1 since all get start and finish
		 * pos methods are different.
		 */
		// for source and target id on code
		char sourceId = 'b';
		char targetId = 'a';
		// sort all list based on tow then col on code 2
		Collections.sort(syntaxMessage, new Code2FeedbackComparator());
		Collections.sort(commentMessage, new Code2FeedbackComparator());
		Collections.sort(commentString);

		// embedding comment tags
		String code = "";
		BufferedReader fr = new BufferedReader(new FileReader(filepath));
		String line;
		int row = 1;
		// the first position from comment list
		int commentMessageIndex = 0;
		// the first position from syntax list
		int syntaxMessageIndex = 0;
		// the first position on comment list
		int commentIndex = 0;
		// mark that it is not in comment
		boolean isInComment = false;
		// refers to the ID that will be written
		String commentMessageId = null;
		String syntaxMessageId = null;

		// refers to following rows for a particular match
		int commentCounter = 1;
		int syntaxCounter = 1;
		while ((line = fr.readLine()) != null) {
			// to mark whether </a> tag is required at the end of line
			boolean closeTagRequired = false;
			for (int col = 0; col < line.length(); col++) {
				String c = HTMLSafeStringFormat(line.charAt(col));
				// if the position matches with comment message, start to mark
				if (commentMessageIndex < commentMessage.size()
						&& row == commentMessage.get(commentMessageIndex).getStartRowCode2()
						&& col == commentMessage.get(commentMessageIndex).getStartColCode2()) {
					// close previous tag if any
					if (closeTagRequired) {
						/*
						 * This mechanism to guarantee no whitespaces between syntax and comment are
						 * highlighted. Quite messy.
						 */
						// get the trimmed version of the code
						String codeTemp = code.trim();
						// get last non-whitespace char
						char lastChar = codeTemp.charAt(codeTemp.length() - 1);
						// get the position of the last non-whitespace char
						int indexOfLastNonWhitespace = code.lastIndexOf(lastChar);
						// put the closing tag after that last char
						code = code.substring(0, indexOfLastNonWhitespace + 1) + "</a>"
								+ code.substring(indexOfLastNonWhitespace + 1);
						// set no need for close tag
						closeTagRequired = false;
					}
					// set the comment ID
					commentMessageId = commentMessage.get(commentMessageIndex).getVisualId();
					// prepare the beginning tag
					String commentTagHeader = "<a class='commentsim' id='" + commentMessageId + sourceId + "' href=\"#"
							+ commentMessageId + targetId + "\" onclick=\"markSelected('" + commentMessageId + "','"
							+ tableId + "')\" >";
					// put the header tag along with current character
					code += (commentTagHeader + c);
					// inform that </a> needed to close
					closeTagRequired = true;
				} else if (commentMessageIndex < commentMessage.size()
						&& row == commentMessage.get(commentMessageIndex).getFinishRowCode2()
						&& col == commentMessage.get(commentMessageIndex).getFinishColCode2() - 1) {
					// if the end is found
					// add the end
					code += (c + "</a>");
					// set null
					commentMessageId = null;
					// increment comment index
					commentMessageIndex++;
					commentCounter = 1;
					// inform that no </a> needed to close
					closeTagRequired = false;
					// add commentindex
					commentIndex++;
				} else if (commentIndex < commentString.size() && row == commentString.get(commentIndex).getStartRow()
						&& col == commentString.get(commentIndex).getStartCol()) {
					// if the position matches with comment message index but
					// not on matched message, start to mark

					// close previous tag if any
					if (closeTagRequired) {
						/*
						 * This mechanism to guarantee no whitespaces between syntax and comment are
						 * highlighted. Quite messy.
						 */
						// get the trimmed version of the code
						String codeTemp = code.trim();
						// get last non-whitespace char
						char lastChar = codeTemp.charAt(codeTemp.length() - 1);
						// get the position of the last non-whitespace char
						int indexOfLastNonWhitespace = code.lastIndexOf(lastChar);
						// put the closing tag after that last char
						code = code.substring(0, indexOfLastNonWhitespace + 1) + "</a>"
								+ code.substring(indexOfLastNonWhitespace + 1);
						// set no need for close tag
						closeTagRequired = false;
					}

					// mark that it is in comment
					isInComment = true;
					// add the char
					code += c;

					/*
					 * this only applicable for python actually as the comment can only be one
					 * character long
					 */
					if (row == commentString.get(commentIndex).getFinishRow()
							&& col == commentString.get(commentIndex).getFinishCol() - 1) {
						// if the end is found, mark that it is not comment
						// anymore
						isInComment = false;
						commentIndex++;
					}

				} else if (commentIndex < commentString.size() && row == commentString.get(commentIndex).getFinishRow()
						&& col == commentString.get(commentIndex).getFinishCol() - 1) {
					// if the end is found, mark that it is not comment anymore
					isInComment = false;
					commentIndex++;
					// add the char
					code += c;
				} else if (syntaxMessageIndex < syntaxMessage.size()
						&& row == syntaxMessage.get(syntaxMessageIndex).getStartRowCode2()
						&& col == syntaxMessage.get(syntaxMessageIndex).getStartColCode2()) {
					// set the syntax ID
					syntaxMessageId = syntaxMessage.get(syntaxMessageIndex).getVisualId();
					// prepare the beginning tag
					String syntaxTagHeader = "<a class='syntaxsim' id='" + syntaxMessageId + sourceId + "' href=\"#"
							+ syntaxMessageId + targetId + "\" onclick=\"markSelected('" + syntaxMessageId + "','"
							+ tableId + "')\" >";
					// put the header tag along with current character
					code += (syntaxTagHeader + c);
					// inform that </a> needed to close
					closeTagRequired = true;
				} else if (syntaxMessageIndex < syntaxMessage.size()
						&& row == syntaxMessage.get(syntaxMessageIndex).getFinishRowCode2()
						&& col == syntaxMessage.get(syntaxMessageIndex).getFinishColCode2() - 1) {
					// if the end is found

					if (closeTagRequired == false) {
						/*
						 * if the beginning has not given any link open tag, add it
						 */
						String syntaxTagHeader = "<a class='syntaxsim' id='" + syntaxMessageId + sourceId
								+ syntaxCounter + "' href=\"#" + syntaxMessageId + targetId
								+ "\" onclick=\"markSelected('" + syntaxMessageId + "','" + tableId + "')\" >";
						code += syntaxTagHeader;
						syntaxCounter++;
					}

					// add the end
					code += (c + "</a>");
					// set null
					syntaxMessageId = null;
					// increment syntax index
					syntaxMessageIndex++;
					syntaxCounter = 1;
					// inform that no </a> needed to close
					closeTagRequired = false;
				} else {
					// this to re-highlight new line if it is still a part of
					// matched tokens
					if (commentMessageId != null) {
						if (closeTagRequired == false) {
							if (c.trim().length() > 0) {
								String commentTagHeader = "<a class='commentsim' id='" + commentMessageId + sourceId
										+ commentCounter + "' href=\"#" + commentMessageId + targetId
										+ "\" onclick=\"markSelected('" + commentMessageId + "','" + tableId + "')\" >";
								code += commentTagHeader;
								closeTagRequired = true;
								commentCounter++;
							}
						}
						code += c;
					} else if (syntaxMessageId != null) {
						if (closeTagRequired == false) {
							if (c.trim().length() > 0 && isInComment == false) {
								String syntaxTagHeader = "<a class='syntaxsim' id='" + syntaxMessageId + sourceId
										+ syntaxCounter + "' href=\"#" + syntaxMessageId + targetId
										+ "\" onclick=\"markSelected('" + syntaxMessageId + "','" + tableId + "')\" >";
								code += syntaxTagHeader;
								closeTagRequired = true;
								syntaxCounter++;
							}
						}
						code += c;
					} else {
						code += c;
					}
				}
			}
			if (closeTagRequired == true) {
				/*
				 * This mechanism to guarantee no remaining whitespaces at the end of the line
				 * are highlighted.
				 */
				// get the trimmed version of the code
				String codeTemp = code.trim();
				// get last non-whitespace char
				char lastChar = codeTemp.charAt(codeTemp.length() - 1);
				// get the position of the last non-whitespace char
				int indexOfLastNonWhitespace = code.lastIndexOf(lastChar);
				// put the closing tag after that last char
				code = code.substring(0, indexOfLastNonWhitespace + 1) + "</a>"
						+ code.substring(indexOfLastNonWhitespace + 1) + System.lineSeparator();
			} else {
				code += System.lineSeparator();
			}
			row++;
		}
		fr.close();
		return code;
	}

	/*
	 * THESE METHODS ARE DIFFERENT WITH JAVA'S METHODS WITH THE SAME NAME. This is
	 * the same as the previous two except that these methods automatically filter
	 * the comments so that they are not embedded on the strings.
	 */
	private static String readCode1WithoutComment(String filepath, ArrayList<FeedbackMessage> syntaxMessage,
			ArrayList<FeedbackToken> comments, String tableId) throws Exception {
		// for source and target id on code
		char sourceId = 'a';
		char targetId = 'b';
		// sort all list based on tow then col on code 1
		Collections.sort(syntaxMessage, new Code1FeedbackComparator());
		Collections.sort(comments);

		// embedding comment tags
		String code = "";
		BufferedReader fr = new BufferedReader(new FileReader(filepath));
		String line;
		int row = 1;
		// the first position from comment list
		int commentIndex = 0;
		// the first position from syntax message list
		int syntaxMessageIndex = 0;
		// refers to the ID that will be written
		String syntaxMessageId = null;

		boolean isInComment = false;

		// refers to following rows for a particular match
		int syntaxCounter = 1;
		while ((line = fr.readLine()) != null) {
			/*
			 * if the line is by default empty, put it as it is since that line is a result
			 * of Python formatting.
			 */
			if (line.length() == 0) {
				/*
				 * only add if the code is not empty. This to prevent empty line at the
				 * beginning of file.
				 */
				if (code.length() > 0) {
					code += System.lineSeparator();
				}
				row++;
				continue;
			}

			// string to store the row
			String rcode = "";
			// to mark whether </a> tag is required at the end of line
			boolean closeTagRequired = false;
			for (int col = 0; col < line.length(); col++) {
				String c = HTMLSafeStringFormat(line.charAt(col));
				// if the position matches with comment message, start to mark
				if (commentIndex < comments.size() && row == comments.get(commentIndex).getStartRow()
						&& col == comments.get(commentIndex).getStartCol()) {
					// close previous tag if any
					if (closeTagRequired) {
						/*
						 * This mechanism to guarantee that per line, no whitespaces after the last
						 * matched syntax are highlighted. Quite messy.
						 */
						// get the trimmed version of the code
						String codeTemp = rcode.trim();
						// get last non-whitespace char
						char lastChar = codeTemp.charAt(codeTemp.length() - 1);
						// get the position of the last non-whitespace char
						int indexOfLastNonWhitespace = rcode.lastIndexOf(lastChar);
						// put the closing tag after that last char
						rcode = rcode.substring(0, indexOfLastNonWhitespace + 1) + "</a>"
								+ rcode.substring(indexOfLastNonWhitespace + 1);
						// set no need for close tag
						closeTagRequired = false;
					}
					// mark that it is in comment
					isInComment = true;
					/*
					 * this only applicable for python actually as the comment can only be one
					 * character long
					 */
					if (row == comments.get(commentIndex).getFinishRow()
							&& col == comments.get(commentIndex).getFinishCol() - 1) {
						// if the end is found, mark that it is not comment
						// anymore
						isInComment = false;
						commentIndex++;
					}
				} else if (commentIndex < comments.size() && row == comments.get(commentIndex).getFinishRow()
						&& col == comments.get(commentIndex).getFinishCol() - 1) {
					// if the end is found, mark that it is not comment anymore
					isInComment = false;
					commentIndex++;
				} else if (syntaxMessageIndex < syntaxMessage.size()
						&& row == syntaxMessage.get(syntaxMessageIndex).getStartRowCode1()
						&& col == syntaxMessage.get(syntaxMessageIndex).getStartColCode1()) {
					// set the syntax ID
					syntaxMessageId = syntaxMessage.get(syntaxMessageIndex).getVisualId();
					// prepare the beginning tag
					String syntaxTagHeader = "<a class='syntaxsim' id='" + syntaxMessageId + sourceId + "' href=\"#"
							+ syntaxMessageId + targetId + "\" onclick=\"markSelected('" + syntaxMessageId + "','"
							+ tableId + "')\" >";
					// put the header tag along with current character
					rcode += (syntaxTagHeader + c);
					// inform that </a> needed to close
					closeTagRequired = true;
				} else if (syntaxMessageIndex < syntaxMessage.size()
						&& row == syntaxMessage.get(syntaxMessageIndex).getFinishRowCode1()
						&& col == syntaxMessage.get(syntaxMessageIndex).getFinishColCode1() - 1) {
					// if the end is found

					if (closeTagRequired == false) {
						/*
						 * if the beginning has not given any link open tag, add it
						 */
						String syntaxTagHeader = "<a class='syntaxsim' id='" + syntaxMessageId + sourceId
								+ syntaxCounter + "' href=\"#" + syntaxMessageId + targetId
								+ "\" onclick=\"markSelected('" + syntaxMessageId + "','" + tableId + "')\" >";
						rcode += syntaxTagHeader;
						syntaxCounter++;
					}

					// add the end
					rcode += (c + "</a>");
					// set null
					syntaxMessageId = null;
					// increment syntax index
					syntaxMessageIndex++;
					// set syntax counter to 1
					syntaxCounter = 1;
					// inform that no </a> needed to close
					closeTagRequired = false;
				} else {
					// this to re-highlight new line if it is still a part of
					// matched tokens
					if (isInComment) {
						// do nothing
					} else if (syntaxMessageId != null) {
						if (closeTagRequired == false) {
							if (c.trim().length() > 0) {
								String syntaxTagHeader = "<a class='syntaxsim' id='" + syntaxMessageId + sourceId
										+ syntaxCounter + "' href=\"#" + syntaxMessageId + targetId
										+ "\" onclick=\"markSelected('" + syntaxMessageId + "','" + tableId + "')\" >";
								rcode += syntaxTagHeader;
								closeTagRequired = true;
								syntaxCounter++;
							}
						}
						rcode += c;
					} else {
						rcode += c;
					}
				}
			}
			if (closeTagRequired == true) {
				rcode += ("</a>" + System.lineSeparator());
			} else {
				rcode += System.lineSeparator();
			}
			// if the line is not empty, add it
			if (rcode.trim().length() > 0)
				code += rcode;

			row++;
		}
		fr.close();
		return code;
	}

	private static String readCode2WithoutComment(String filepath, ArrayList<FeedbackMessage> syntaxMessage,
			ArrayList<FeedbackToken> comments, String tableId) throws Exception {
		/*
		 * this function cannot be merged to readCode1 since all get start and finish
		 * pos methods are different.
		 */
		// for source and target id on code
		char sourceId = 'b';
		char targetId = 'a';
		// sort all list based on tow then col on code 1
		Collections.sort(syntaxMessage, new Code2FeedbackComparator());
		Collections.sort(comments);

		// embedding comment tags
		String code = "";
		BufferedReader fr = new BufferedReader(new FileReader(filepath));
		String line;
		int row = 1;
		// the first position from comment list
		int commentIndex = 0;
		// the first position from syntax message list
		int syntaxMessageIndex = 0;
		// refers to the ID that will be written
		String syntaxMessageId = null;

		boolean isInComment = false;

		// refers to following rows for a particular match
		int syntaxCounter = 1;
		while ((line = fr.readLine()) != null) {

			/*
			 * if the line is by default empty, put it as it is since that line is a result
			 * of Python formatting.
			 */
			if (line.length() == 0) {
				/*
				 * only add if the code is not empty. This to prevent empty line at the
				 * beginning of file.
				 */
				if (code.length() > 0) {
					code += System.lineSeparator();
				}
				row++;
				continue;
			}

			// string to store the row
			String rcode = "";
			// to mark whether </a> tag is required at the end of line
			boolean closeTagRequired = false;
			for (int col = 0; col < line.length(); col++) {
				String c = HTMLSafeStringFormat(line.charAt(col));
				// if the position matches with comment message, start to mark
				if (commentIndex < comments.size() && row == comments.get(commentIndex).getStartRow()
						&& col == comments.get(commentIndex).getStartCol()) {
					// close previous tag if any
					if (closeTagRequired) {
						/*
						 * This mechanism to guarantee that per line, no whitespaces after the last
						 * matched syntax are highlighted. Quite messy.
						 */
						// get the trimmed version of the code
						String codeTemp = rcode.trim();
						// get last non-whitespace char
						char lastChar = codeTemp.charAt(codeTemp.length() - 1);
						// get the position of the last non-whitespace char
						int indexOfLastNonWhitespace = rcode.lastIndexOf(lastChar);
						// put the closing tag after that last char
						rcode = rcode.substring(0, indexOfLastNonWhitespace + 1) + "</a>"
								+ rcode.substring(indexOfLastNonWhitespace + 1);
						// set no need for close tag
						closeTagRequired = false;
					}
					// mark that it is in comment
					isInComment = true;
					/*
					 * this only applicable for python actually as the comment can only be one
					 * character long
					 */
					if (row == comments.get(commentIndex).getFinishRow()
							&& col == comments.get(commentIndex).getFinishCol() - 1) {
						// if the end is found, mark that it is not comment
						// anymore
						isInComment = false;
						commentIndex++;
					}
				} else if (commentIndex < comments.size() && row == comments.get(commentIndex).getFinishRow()
						&& col == comments.get(commentIndex).getFinishCol() - 1) {
					// if the end is found, mark that it is not comment anymore
					isInComment = false;
					commentIndex++;
				} else if (syntaxMessageIndex < syntaxMessage.size()
						&& row == syntaxMessage.get(syntaxMessageIndex).getStartRowCode2()
						&& col == syntaxMessage.get(syntaxMessageIndex).getStartColCode2()) {
					// set the syntax ID
					syntaxMessageId = syntaxMessage.get(syntaxMessageIndex).getVisualId();
					// prepare the beginning tag
					String syntaxTagHeader = "<a class='syntaxsim' id='" + syntaxMessageId + sourceId + "' href=\"#"
							+ syntaxMessageId + targetId + "\" onclick=\"markSelected('" + syntaxMessageId + "','"
							+ tableId + "')\" >";
					// put the header tag along with current character
					rcode += (syntaxTagHeader + c);
					// inform that </a> needed to close
					closeTagRequired = true;
				} else if (syntaxMessageIndex < syntaxMessage.size()
						&& row == syntaxMessage.get(syntaxMessageIndex).getFinishRowCode2()
						&& col == syntaxMessage.get(syntaxMessageIndex).getFinishColCode2() - 1) {
					// if the end is found

					if (closeTagRequired == false) {
						/*
						 * if the beginning has not given any link open tag, add it
						 */
						String syntaxTagHeader = "<a class='syntaxsim' id='" + syntaxMessageId + sourceId
								+ syntaxCounter + "' href=\"#" + syntaxMessageId + targetId
								+ "\" onclick=\"markSelected('" + syntaxMessageId + "','" + tableId + "')\" >";
						rcode += syntaxTagHeader;
						syntaxCounter++;
					}

					// add the end
					rcode += (c + "</a>");
					// set null
					syntaxMessageId = null;
					// increment syntax index
					syntaxMessageIndex++;
					// set syntax counter to 1
					syntaxCounter = 1;
					// inform that no </a> needed to close
					closeTagRequired = false;
				} else {
					// this to re-highlight new line if it is still a part of
					// matched tokens
					if (isInComment) {
						// do nothing
					} else if (syntaxMessageId != null) {
						if (closeTagRequired == false) {
							if (c.trim().length() > 0) {
								String syntaxTagHeader = "<a class='syntaxsim' id='" + syntaxMessageId + sourceId
										+ syntaxCounter + "' href=\"#" + syntaxMessageId + targetId
										+ "\" onclick=\"markSelected('" + syntaxMessageId + "','" + tableId + "')\" >";
								rcode += syntaxTagHeader;
								closeTagRequired = true;
								syntaxCounter++;
							}
						}
						rcode += c;
					} else {
						rcode += c;
					}
				}
			}
			if (closeTagRequired == true) {
				rcode += ("</a>" + System.lineSeparator());
			} else {
				rcode += System.lineSeparator();
			}
			// if the line is not empty, add it
			if (rcode.trim().length() > 0)
				code += rcode;

			row++;
		}
		fr.close();
		return code;
	}

	/*
	 * THESE METHODS ARE DIFFERENT WITH JAVA'S METHODS WITH THE SAME NAME. This is
	 * expanded from previous two in terms of renaming some specific tokens.
	 */
	private static String readCode1AndRenamed(String filepath, ArrayList<FeedbackMessage> syntaxMessage, String tableId)
			throws Exception {
		// for source and target id on code
		char sourceId = 'a';
		char targetId = 'b';

		// generate comment and renamed token lists
		ArrayList<FeedbackToken> commentString = new ArrayList<>();
		ArrayList<FeedbackToken> renamedTokens = new ArrayList<>();
		for (int i = 0; i < tokenString1.size(); i++) {
			FeedbackToken curt = tokenString1.get(i);
			if (curt.getType().equals("COMMENT"))
				commentString.add(curt);
			else if (curt.getType().equals("WS") == false) {
				if (curt.getContentForComparison().equals("number data type")
						|| curt.getContentForComparison().equals("string data type")
						|| curt.getContentForComparison().equals("identifier")
						|| curt.getContentForComparison().equals("string literal")
						|| curt.getContentForComparison().equals("number literal")) {
					renamedTokens.add(curt);
				}
			}
		}

		// sort all list based on tow then col on code 1
		Collections.sort(syntaxMessage, new Code1FeedbackComparator());
		Collections.sort(commentString);
		Collections.sort(renamedTokens);

		// embedding comment tags
		String code = "";
		BufferedReader fr = new BufferedReader(new FileReader(filepath));
		String line;
		int row = 1;
		// the first position from comment list
		int commentIndex = 0;
		// the first position from syntax list
		int syntaxIndex = 0;
		// the first position from syntax message list
		int syntaxMessageIndex = 0;
		// refers to the ID that will be written
		String syntaxMessageId = null;
		// check whether such a position is in comment
		boolean isInComment = false;

		int skipCounter = 0;

		// refers to following rows for a particular match
		int syntaxCounter = 1;
		while ((line = fr.readLine()) != null) {
			/*
			 * if the line is by default empty, put it as it is since that line is a result
			 * of Python formatting.
			 */
			if (line.length() == 0) {
				/*
				 * only add if the code is not empty. This to prevent empty line at the
				 * beginning of file.
				 */
				if (code.length() > 0) {
					code += System.lineSeparator();
				}
				row++;
				continue;
			}

			// string to store the row
			String rcode = "";
			// to mark whether </a> tag is required at the end of line
			boolean closeTagRequired = false;
			for (int col = 0; col < line.length(); col++) {
				String c = HTMLSafeStringFormat(line.charAt(col));
				/*
				 * if skipcounter is not zero, this iteration should not put any chars BUT it
				 * still needs to check its state toward other components. Hence, c is assigned
				 * with an empty string.
				 */
				if (skipCounter > 0) {
					c = "";
					skipCounter--;
				}

				// if the position matches with comment message, start to mark
				if (commentIndex < commentString.size() && row == commentString.get(commentIndex).getStartRow()
						&& col == commentString.get(commentIndex).getStartCol()) {
					// close previous tag if any
					if (closeTagRequired) {
						/*
						 * This mechanism to guarantee that per line, no whitespaces after the last
						 * matched syntax are highlighted. Quite messy.
						 */
						// get the trimmed version of the code
						String codeTemp = rcode.trim();
						// get last non-whitespace char
						char lastChar = codeTemp.charAt(codeTemp.length() - 1);
						// get the position of the last non-whitespace char
						int indexOfLastNonWhitespace = rcode.lastIndexOf(lastChar);
						// put the closing tag after that last char
						rcode = rcode.substring(0, indexOfLastNonWhitespace + 1) + "</a>"
								+ rcode.substring(indexOfLastNonWhitespace + 1);
						// set no need for close tag
						closeTagRequired = false;
					}
					// mark that it is in comment
					isInComment = true;
					/*
					 * this only applicable for python actually as the comment can only be one
					 * character long
					 */
					if (row == commentString.get(commentIndex).getFinishRow()
							&& col == commentString.get(commentIndex).getFinishCol() - 1) {
						// if the end is found, mark that it is not comment
						// anymore
						isInComment = false;
						commentIndex++;
					}
				} else if (commentIndex < commentString.size() && row == commentString.get(commentIndex).getFinishRow()
						&& col == commentString.get(commentIndex).getFinishCol() - 1) {
					// if the end is found, mark that it is not comment anymore
					isInComment = false;
					commentIndex++;
				} else if (syntaxMessageIndex < syntaxMessage.size()
						&& row == syntaxMessage.get(syntaxMessageIndex).getStartRowCode1()
						&& col == syntaxMessage.get(syntaxMessageIndex).getStartColCode1()) {
					// get content
					String curContent = getSyntaxContent(row, col, renamedTokens, syntaxIndex);
					if (curContent != null) {
						/*
						 * if it is one of the renamed tokens, add the col. The row is not changed as
						 * all syntax tokens start and end at the same row.
						 */
						skipCounter = (renamedTokens.get(syntaxIndex).getContent().length() - 1);
						// add the index for renamed tokens
						syntaxIndex++;
					} else {
						// otherwise, it is a regular character
						curContent = c + "";
					}
					// set the syntax ID
					syntaxMessageId = syntaxMessage.get(syntaxMessageIndex).getVisualId();
					// prepare the beginning tag
					String syntaxTagHeader = "<a class='syntaxsim' id='" + syntaxMessageId + sourceId + "' href=\"#"
							+ syntaxMessageId + targetId + "\" onclick=\"markSelected('" + syntaxMessageId + "','"
							+ tableId + "')\" >";
					// put the header tag along with current character
					rcode += (syntaxTagHeader + curContent);
					// inform that </a> needed to close
					closeTagRequired = true;
				} else if (syntaxMessageIndex < syntaxMessage.size()
						&& row == syntaxMessage.get(syntaxMessageIndex).getFinishRowCode1()
						&& col == syntaxMessage.get(syntaxMessageIndex).getFinishColCode1() - 1) {
					// if the end is found

					if (closeTagRequired == false) {
						/*
						 * if the beginning has not given any link open tag, add it
						 */
						String syntaxTagHeader = "<a class='syntaxsim' id='" + syntaxMessageId + sourceId
								+ syntaxCounter + "' href=\"#" + syntaxMessageId + targetId
								+ "\" onclick=\"markSelected('" + syntaxMessageId + "','" + tableId + "')\" >";
						rcode += syntaxTagHeader;
						syntaxCounter++;
					}

					// get content
					String curContent = getSyntaxContent(row, col, renamedTokens, syntaxIndex);
					if (curContent != null) {
						/*
						 * if it is one of the renamed tokens, add the col. The row is not changed as
						 * all syntax tokens start and end at the same row.
						 */
						skipCounter = (renamedTokens.get(syntaxIndex).getContent().length() - 1);
						// add the index for renamed tokens
						syntaxIndex++;
					} else {
						// otherwise, it is a regular character
						curContent = c + "";
					}
					// add the end
					rcode += (curContent + "</a>");
					// set null
					syntaxMessageId = null;
					// increment syntax index
					syntaxMessageIndex++;
					// set syntax counter to 1
					syntaxCounter = 1;
					// inform that no </a> needed to close
					closeTagRequired = false;
				} else {
					// this to re-highlight new line if it is still a part of
					// matched tokens
					if (isInComment) {
						// do nothing
					} else if (syntaxMessageId != null) {
						if (closeTagRequired == false) {
							if (c.trim().length() > 0) {
								String syntaxTagHeader = "<a class='syntaxsim' id='" + syntaxMessageId + sourceId
										+ syntaxCounter + "' href=\"#" + syntaxMessageId + targetId
										+ "\" onclick=\"markSelected('" + syntaxMessageId + "','" + tableId + "')\" >";
								rcode += syntaxTagHeader;
								closeTagRequired = true;
								syntaxCounter++;
							}
						}
						String curContent = getSyntaxContent(row, col, renamedTokens, syntaxIndex);
						if (curContent != null) {
							/*
							 * if it is one of the renamed tokens, add the content, increase the col and
							 * syntaxIndex.
							 */
							rcode += curContent;
							skipCounter = (renamedTokens.get(syntaxIndex).getContent().length() - 1);
							syntaxIndex++;
						} else {
							// add the current character only
							rcode += c;
						}
					} else {
						String curContent = getSyntaxContent(row, col, renamedTokens, syntaxIndex);
						if (curContent != null) {
							/*
							 * if it is one of the renamed tokens, add the content, increase the col and
							 * syntaxIndex.
							 */
							rcode += curContent;
							skipCounter = (renamedTokens.get(syntaxIndex).getContent().length() - 1);
							syntaxIndex++;
						} else {
							// add the current character only
							rcode += c;
						}

					}
				}
			}
			if (closeTagRequired == true) {
				rcode += ("</a>" + System.lineSeparator());
			} else {
				rcode += System.lineSeparator();
			}
			// if the line is not empty, add it
			if (rcode.trim().length() > 0)
				code += rcode;

			row++;
		}
		fr.close();
		return code;
	}

	private static String readCode2AndRenamed(String filepath, ArrayList<FeedbackMessage> syntaxMessage, String tableId)
			throws Exception {
		// for source and target id on code
		char sourceId = 'b';
		char targetId = 'a';

		// generate comment and renamed token lists
		ArrayList<FeedbackToken> commentString = new ArrayList<>();
		ArrayList<FeedbackToken> renamedTokens = new ArrayList<>();
		for (int i = 0; i < tokenString2.size(); i++) {
			FeedbackToken curt = tokenString2.get(i);
			if (curt.getType().equals("COMMENT"))
				commentString.add(curt);
			else if (curt.getType().equals("WS") == false) {
				if (curt.getContentForComparison().equals("number data type")
						|| curt.getContentForComparison().equals("string data type")
						|| curt.getContentForComparison().equals("identifier")
						|| curt.getContentForComparison().equals("string literal")
						|| curt.getContentForComparison().equals("number literal")) {
					renamedTokens.add(curt);
				}
			}
		}

		// sort all list based on tow then col on code 1
		Collections.sort(syntaxMessage, new Code2FeedbackComparator());
		Collections.sort(commentString);
		Collections.sort(renamedTokens);

		// embedding comment tags
		String code = "";
		BufferedReader fr = new BufferedReader(new FileReader(filepath));
		String line;
		int row = 1;
		// the first position from comment list
		int commentIndex = 0;
		// the first position from syntax list
		int syntaxIndex = 0;
		// the first position from syntax message list
		int syntaxMessageIndex = 0;
		// refers to the ID that will be written
		String syntaxMessageId = null;
		// check whether such a position is in comment
		boolean isInComment = false;

		int skipCounter = 0;

		// refers to following rows for a particular match
		int syntaxCounter = 1;
		while ((line = fr.readLine()) != null) {

			/*
			 * if the line is by default empty, put it as it is since that line is a result
			 * of Python formatting.
			 */
			if (line.length() == 0) {
				/*
				 * only add if the code is not empty. This to prevent empty line at the
				 * beginning of file.
				 */
				if (code.length() > 0) {
					code += System.lineSeparator();
				}
				row++;
				continue;
			}

			// string to store the row
			String rcode = "";
			// to mark whether </a> tag is required at the end of line
			boolean closeTagRequired = false;
			for (int col = 0; col < line.length(); col++) {
				String c = HTMLSafeStringFormat(line.charAt(col));
				/*
				 * if skipcounter is not zero, this iteration should not put any chars BUT it
				 * still needs to check its state toward other components. Hence, c is assigned
				 * with an empty string.
				 */
				if (skipCounter > 0) {
					c = "";
					skipCounter--;
				}

				// if the position matches with comment message, start to mark
				if (commentIndex < commentString.size() && row == commentString.get(commentIndex).getStartRow()
						&& col == commentString.get(commentIndex).getStartCol()) {
					// close previous tag if any
					if (closeTagRequired) {
						/*
						 * This mechanism to guarantee that per line, no whitespaces after the last
						 * matched syntax are highlighted. Quite messy.
						 */
						// get the trimmed version of the code
						String codeTemp = rcode.trim();
						// get last non-whitespace char
						char lastChar = codeTemp.charAt(codeTemp.length() - 1);
						// get the position of the last non-whitespace char
						int indexOfLastNonWhitespace = rcode.lastIndexOf(lastChar);
						// put the closing tag after that last char
						rcode = rcode.substring(0, indexOfLastNonWhitespace + 1) + "</a>"
								+ rcode.substring(indexOfLastNonWhitespace + 1);
						// set no need for close tag
						closeTagRequired = false;
					}
					// mark that it is in comment
					isInComment = true;
					/*
					 * this only applicable for python actually as the comment can only be one
					 * character long
					 */
					if (row == commentString.get(commentIndex).getFinishRow()
							&& col == commentString.get(commentIndex).getFinishCol() - 1) {
						// if the end is found, mark that it is not comment
						// anymore
						isInComment = false;
						commentIndex++;
					}
				} else if (commentIndex < commentString.size() && row == commentString.get(commentIndex).getFinishRow()
						&& col == commentString.get(commentIndex).getFinishCol() - 1) {
					// if the end is found, mark that it is not comment anymore
					isInComment = false;
					commentIndex++;
				} else if (syntaxMessageIndex < syntaxMessage.size()
						&& row == syntaxMessage.get(syntaxMessageIndex).getStartRowCode2()
						&& col == syntaxMessage.get(syntaxMessageIndex).getStartColCode2()) {
					// get content
					String curContent = getSyntaxContent(row, col, renamedTokens, syntaxIndex);
					if (curContent != null) {
						/*
						 * if it is one of the renamed tokens, add the col. The row is not changed as
						 * all syntax tokens start and end at the same row.
						 */
						skipCounter = (renamedTokens.get(syntaxIndex).getContent().length() - 1);
						// add the index for renamed tokens
						syntaxIndex++;
					} else
						// otherwise, it is a regular character
						curContent = c + "";

					// set the syntax ID
					syntaxMessageId = syntaxMessage.get(syntaxMessageIndex).getVisualId();
					// prepare the beginning tag
					String syntaxTagHeader = "<a class='syntaxsim' id='" + syntaxMessageId + sourceId + "' href=\"#"
							+ syntaxMessageId + targetId + "\" onclick=\"markSelected('" + syntaxMessageId + "','"
							+ tableId + "')\" >";
					// put the header tag along with current character
					rcode += (syntaxTagHeader + curContent);
					// inform that </a> needed to close
					closeTagRequired = true;
				} else if (syntaxMessageIndex < syntaxMessage.size()
						&& row == syntaxMessage.get(syntaxMessageIndex).getFinishRowCode2()
						&& col == syntaxMessage.get(syntaxMessageIndex).getFinishColCode2() - 1) {
					// if the end is found

					if (closeTagRequired == false) {
						/*
						 * if the beginning has not given any link open tag, add it
						 */
						String syntaxTagHeader = "<a class='syntaxsim' id='" + syntaxMessageId + sourceId
								+ syntaxCounter + "' href=\"#" + syntaxMessageId + targetId
								+ "\" onclick=\"markSelected('" + syntaxMessageId + "','" + tableId + "')\" >";
						rcode += syntaxTagHeader;
						syntaxCounter++;
					}

					// get content
					String curContent = getSyntaxContent(row, col, renamedTokens, syntaxIndex);
					if (curContent != null) {
						/*
						 * if it is one of the renamed tokens, add the col. The row is not changed as
						 * all syntax tokens start and end at the same row.
						 */
						skipCounter = (renamedTokens.get(syntaxIndex).getContent().length() - 1);
						// add the index for renamed tokens
						syntaxIndex++;
					} else
						// otherwise, it is a regular character
						curContent = c + "";
					// add the end
					rcode += (curContent + "</a>");
					// set null
					syntaxMessageId = null;
					// increment syntax index
					syntaxMessageIndex++;
					// set syntax counter to 1
					syntaxCounter = 1;
					// inform that no </a> needed to close
					closeTagRequired = false;
				} else {
					// this to re-highlight new line if it is still a part of
					// matched tokens
					if (isInComment) {
						// do nothing
					} else if (syntaxMessageId != null) {
						if (closeTagRequired == false) {
							if (c.trim().length() > 0) {
								String syntaxTagHeader = "<a class='syntaxsim' id='" + syntaxMessageId + sourceId
										+ syntaxCounter + "' href=\"#" + syntaxMessageId + targetId
										+ "\" onclick=\"markSelected('" + syntaxMessageId + "','" + tableId + "')\" >";
								rcode += syntaxTagHeader;
								closeTagRequired = true;
								syntaxCounter++;
							}
						}
						String curContent = getSyntaxContent(row, col, renamedTokens, syntaxIndex);
						if (curContent != null) {
							/*
							 * if it is one of the renamed tokens, add the content, increase the col and
							 * syntaxIndex.
							 */
							rcode += curContent;
							skipCounter = (renamedTokens.get(syntaxIndex).getContent().length() - 1);
							syntaxIndex++;
						} else {
							// add the current character only
							rcode += c;
						}
					} else {
						String curContent = getSyntaxContent(row, col, renamedTokens, syntaxIndex);
						if (curContent != null) {
							/*
							 * if it is one of the renamed tokens, add the content, increase the col and
							 * syntaxIndex.
							 */
							rcode += curContent;
							skipCounter = (renamedTokens.get(syntaxIndex).getContent().length() - 1);
							syntaxIndex++;
						} else {
							// add the current character only
							rcode += c;
						}

					}
				}
			}
			if (closeTagRequired == true) {
				rcode += ("</a>" + System.lineSeparator());
			} else {
				rcode += System.lineSeparator();
			}
			// if the line is not empty, add it
			if (rcode.trim().length() > 0)
				code += rcode;

			row++;
		}
		fr.close();
		return code;
	}

	private static String getSyntaxContent(int row, int col, ArrayList<FeedbackToken> renamedTokens, int syntaxIndex) {
		if (syntaxIndex < renamedTokens.size() && row == renamedTokens.get(syntaxIndex).getStartRow()
				&& col == renamedTokens.get(syntaxIndex).getStartCol()) {
			String content = renamedTokens.get(syntaxIndex).getContentForComparison();
			if (content.equals("number data type"))
				content = "num_type";
			else if (content.equals("string data type"))
				content = "str_type";
			else if (content.equals("identifier"))
				content = "ident";
			else if (content.equals("string literal"))
				content = "str_const";
			else if (content.equals("number literal")) {
				content = "num_const";
			}
			return content;
		} else
			return null;
	}

	private static String getExplanationContent(ArrayList<FeedbackMessage> syntaxMessage,
			ArrayList<FeedbackMessage> commentMessage, String mode) {
		String s = "";

		// add for syntax messages
		for (FeedbackMessage m : syntaxMessage) {
			s = s + "<div class=\"explanationcontent\" id=\"" + m.getVisualId() + "he\">";
			s = s + "\n\t" + ((SyntaxFeedbackMessage) m).toString(mode);
			s = s + "\n</div>";
		}
		// add for content messages
		for (FeedbackMessage m : commentMessage) {
			s = s + "<div class=\"explanationcontent\" id=\"" + m.getVisualId() + "he\">";
			s = s + "\n\t" + m.toString();
			s = s + "\n</div>";
		}

		return s;
	}

	public static String HTMLSafeStringFormat(char in) {
		String out = "";
		if (in == '<')
			out = "&lt;";
		else if (in == '>')
			out = "&gt;";
		else if (in == '\"')
			out = "&quot;";
		else
			out = in + "";
		return out;
	}

	public static String HTMLSafeStringFormatWithSpace(char in) {
		String out = HTMLSafeStringFormat(in);
		if (in == ' ')
			out = "&nbsp;";
		return out;
	}

	public static String HTMLSafeStringFormatWithSpace(String in) {
		String out = "";
		for (int i = 0; i < in.length(); i++) {
			out += HTMLSafeStringFormatWithSpace(in.charAt(i));
		}
		return out;
	}
}
