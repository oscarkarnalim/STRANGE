package p3.feedbackgenerator.comparison;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

import p3.feedbackgenerator.htmlgenerator.HtmlTableStringGenerator;
import p3.feedbackgenerator.language.java.JavaFeedbackGenerator;
import p3.feedbackgenerator.language.python.PythonFeedbackGenerator;
import p3.feedbackgenerator.message.FeedbackMessageGenerator;
import p3.feedbackgenerator.token.FeedbackToken;
import p3.feedbackgenerator.token.FeedbackTokenComparator;
import support.ir.NaturalLanguageProcesser;
import support.stringmatching.GSTMatchTuple;

public class Comparer {

	public static ArrayList<ComparisonPairTuple> getRankedComparisonPairTuples(
			String dirPath, String extension, int minimumMatchLength,
			String languageCode, ArrayList<ArrayList<String>> additionalKeywords) {
		// to store the result
		ArrayList<ComparisonPairTuple> codePairs = new ArrayList<>();

		// start processing
		File[] assignments = (new File(dirPath)).listFiles();
		for (int i = 0; i < assignments.length; i++) {
			// for each code 1
			File code1 = getCode(assignments[i], extension);
			
			// skip if null
			if(code1 == null)
				continue;
			
			for (int j = i + 1; j < assignments.length; j++) {
				// pair code 1 with code 2
				File code2 = getCode(assignments[j], extension);
				
				// skip if null
				if(code2 == null)
					continue;

				// calculate the similarities
				double[] simValues = null;
				if (extension.equals("java")) {
					// for java
					simValues = getJavaComparisonResult(
							code1.getAbsolutePath(), code2.getAbsolutePath(),
							minimumMatchLength, languageCode,
							additionalKeywords);
				} else if (extension.equals("py")) {
					// for python
					simValues = getPythonComparisonResult(
							code1.getAbsolutePath(), code2.getAbsolutePath(),
							minimumMatchLength, languageCode, additionalKeywords);
				}

				String assignmentname1 = assignments[i].getAbsolutePath();
				assignmentname1 = assignmentname1.substring(assignmentname1
						.lastIndexOf(File.separator) + 1);
				String assignmentname2 = assignments[j].getAbsolutePath();
				assignmentname2 = assignmentname2.substring(assignmentname2
						.lastIndexOf(File.separator) + 1);

				// add as a new pair
				codePairs.add(new ComparisonPairTuple(code1.getAbsolutePath(),
						code2.getAbsolutePath(), assignmentname1,
						assignmentname2, simValues));
			}
		}
		// sort in descending order based on average syntax
		Collections.sort(codePairs);

		return codePairs;
	}

	public static double[] getPythonComparisonResult(String codepath1,
			String codepath2, int minimumMatchLength, String languageCode,
			ArrayList<ArrayList<String>> additionalKeywords) {
		double[] result = new double[4];
		/*
		 * For storing the results, an array depicting avg-syntax, max-syntax,
		 * avg-comment, max-comment
		 */

		// get tokens from the first code
		ArrayList<FeedbackToken> tokenString1 = new ArrayList<>();
		ArrayList<FeedbackToken> syntaxString1 = PythonFeedbackGenerator
				.generateSyntaxTokenString(codepath1);
		tokenString1.addAll(syntaxString1);
		ArrayList<FeedbackToken> commentWhitespaceString1 = PythonFeedbackGenerator
				.generateCommentAndWhitespaceTokens(codepath1);
		tokenString1.addAll(commentWhitespaceString1);
		Collections.sort(tokenString1, new FeedbackTokenComparator());
		PythonFeedbackGenerator
				.mergeAdjacentCommentsAndWhitespacesOnTokenString(tokenString1);
		ArrayList<FeedbackToken> commentString1 = new ArrayList<>();
		for (int i = 0; i < tokenString1.size(); i++) {
			if (tokenString1.get(i).getType().equals("COMMENT"))
				commentString1.add(tokenString1.get(i));
		}
		PythonFeedbackGenerator.syntaxTokenStringPreprocessing(syntaxString1,additionalKeywords);

		// get tokens from the second code
		ArrayList<FeedbackToken> tokenString2 = new ArrayList<>();
		ArrayList<FeedbackToken> syntaxString2 = PythonFeedbackGenerator
				.generateSyntaxTokenString(codepath2);
		tokenString2.addAll(syntaxString2);
		ArrayList<FeedbackToken> commentWhitespaceString2 = PythonFeedbackGenerator
				.generateCommentAndWhitespaceTokens(codepath2);
		tokenString2.addAll(commentWhitespaceString2);
		Collections.sort(tokenString2, new FeedbackTokenComparator());
		PythonFeedbackGenerator
				.mergeAdjacentCommentsAndWhitespacesOnTokenString(tokenString2);
		ArrayList<FeedbackToken> commentString2 = new ArrayList<>();
		for (int i = 0; i < tokenString2.size(); i++) {
			if (tokenString2.get(i).getType().equals("COMMENT"))
				commentString2.add(tokenString2.get(i));
		}
		PythonFeedbackGenerator.syntaxTokenStringPreprocessing(syntaxString2, additionalKeywords);

		int matchedComment = countMatchedComments(commentString1,
				commentString2, languageCode);
		int matchedSyntax = countMatchedSyntaxTokens(syntaxString1,
				syntaxString2, minimumMatchLength);

		result[0] = HtmlTableStringGenerator.getAverageSim(matchedSyntax,
				syntaxString1.size(), syntaxString2.size());
		result[1] = HtmlTableStringGenerator.getMaximumSim(matchedSyntax,
				syntaxString1.size(), syntaxString2.size());
		result[2] = HtmlTableStringGenerator.getAverageSim(matchedComment,
				commentString1.size(), commentString2.size());
		result[3] = HtmlTableStringGenerator.getMaximumSim(matchedComment,
				commentString1.size(), commentString2.size());

		return result;
	}

	public static double[] getJavaComparisonResult(String codepath1,
			String codepath2, int minimumMatchLength, String languageCode,
			ArrayList<ArrayList<String>> additionalKeywords) {
		double[] result = new double[4];
		/*
		 * For storing the results, an array depicting avg-syntax, max-syntax,
		 * avg-comment, max-comment
		 */

		// get tokens from the first code
		ArrayList<FeedbackToken> tokenString1 = JavaFeedbackGenerator
				.generateFeedbackTokenString(codepath1);
		JavaFeedbackGenerator.mergeAdjacentCommentsOnTokenString(tokenString1);
		ArrayList<FeedbackToken> commentString1 = new ArrayList<>();
		for (int i = 0; i < tokenString1.size(); i++) {
			if (tokenString1.get(i).getType().equals("COMMENT"))
				commentString1.add(tokenString1.get(i));
		}
		ArrayList<FeedbackToken> syntaxString1 = JavaFeedbackGenerator
				.syntaxTokenStringPreprocessing(tokenString1,
						additionalKeywords, true);

		// get tokens from the second code
		ArrayList<FeedbackToken> tokenString2 = JavaFeedbackGenerator
				.generateFeedbackTokenString(codepath2);
		JavaFeedbackGenerator.mergeAdjacentCommentsOnTokenString(tokenString2);
		ArrayList<FeedbackToken> commentString2 = new ArrayList<>();
		for (int i = 0; i < tokenString2.size(); i++) {
			if (tokenString2.get(i).getType().equals("COMMENT"))
				commentString2.add(tokenString2.get(i));
		}
		ArrayList<FeedbackToken> syntaxString2 = JavaFeedbackGenerator
				.syntaxTokenStringPreprocessing(tokenString2,
						additionalKeywords, true);

		int matchedComment = countMatchedComments(commentString1,
				commentString2, languageCode);
		int matchedSyntax = countMatchedSyntaxTokens(syntaxString1,
				syntaxString2, minimumMatchLength);

		result[0] = HtmlTableStringGenerator.getAverageSim(matchedSyntax,
				syntaxString1.size(), syntaxString2.size());
		result[1] = HtmlTableStringGenerator.getMaximumSim(matchedSyntax,
				syntaxString1.size(), syntaxString2.size());
		result[2] = HtmlTableStringGenerator.getAverageSim(matchedComment,
				commentString1.size(), commentString2.size());
		result[3] = HtmlTableStringGenerator.getMaximumSim(matchedComment,
				commentString1.size(), commentString2.size());

		return result;
	}

	public static int countMatchedSyntaxTokens(
			ArrayList<FeedbackToken> syntaxTokenString1,
			ArrayList<FeedbackToken> syntaxTokenString2, int minimumMatchLength) {
		// return the number of shared syntax tokens
		int counter = 0;
		// get matched tiles with RKRGST
		ArrayList<GSTMatchTuple> simTuples = FeedbackMessageGenerator
				.generateMatchedTuples(syntaxTokenString1, syntaxTokenString2,
						minimumMatchLength);
		for (int i = 0; i < simTuples.size(); i++) {
			counter += simTuples.get(i).length;
		}
		return counter;
	}

	public static int countMatchedComments(
			ArrayList<FeedbackToken> tokenString1,
			ArrayList<FeedbackToken> tokenString2, String languageCode) {
		// return the number of shared comments
		int counter = 0;

		// create lists to store processed comments per string
		ArrayList<String> commentProcessedString1 = new ArrayList<>();
		ArrayList<String> commentProcessedString2 = new ArrayList<>();

		// filter all the comments and process its content
		for (int i = 0; i < tokenString1.size(); i++) {
			// take the raw content
			String content = tokenString1.get(i).getContent();
			// lowercase and split based on non-alphanumeric
			String[] contentTerm = content.toLowerCase().split("[^a-zA-Z0-9']");
			// stem
			String processedContent = "";
			// generate processed content
			for (String s : contentTerm) {
				// if it is not stopword
				if (NaturalLanguageProcesser.isStopWord(s, languageCode) == false)
					processedContent += NaturalLanguageProcesser.getStem(s,
							languageCode);
			}
			// add the result
			commentProcessedString1.add(processedContent);
			tokenString1.get(i).setContentForComparison(processedContent);
			// System.out.print(processedContent + "\t");
		}

		// filter all the comments and process its content but for code 2
		for (int i = 0; i < tokenString2.size(); i++) {
			// take the raw content
			String content = tokenString2.get(i).getContent();
			// lowercase and split based on non-alphanumeric
			String[] contentTerm = content.toLowerCase().split("[^a-zA-Z0-9']");
			// stem
			String processedContent = "";
			// generate processed content
			for (String s : contentTerm) {
				// if it is not stopword
				if (NaturalLanguageProcesser.isStopWord(s, languageCode) == false)
					processedContent += NaturalLanguageProcesser.getStem(s,
							languageCode);
			}
			// add the result
			commentProcessedString2.add(processedContent);
			// System.out.print(processedContent + "\t");
		}

		// count the shared tokens
		for (int i = 0; i < commentProcessedString1.size(); i++) {
			String cur = commentProcessedString1.get(i);

			// search on the second list
			int j = FeedbackMessageGenerator.subStringMatchSearch(cur,
					commentProcessedString2);

			if (j != -1) {
				// add the number of matches
				counter++;
				// nullify to anticipate redundant comments paired with the same
				// counterpart. It is like marking the comment as "used"
				commentProcessedString2.set(j, null);
			}
		}

		return counter;
	}

	public static File getCode(File assignment, String extension) {
		/*
		 * get the first code with the same extension from given directory. It
		 * searches on all sub directories and returns the first file with such
		 * an extension.
		 */
		Stack<File> s = new Stack<>();
		s.push(assignment); // add the main directory
		while (s.isEmpty() == false) {
			File cur = s.pop();
			if (cur.isDirectory()) {
				/*
				 * if it is a directory, add all the children.
				 */
				File[] curchildren = cur.listFiles();
				for (File c : curchildren)
					s.push(c);
			} else if (cur.getName().toLowerCase().endsWith(extension)) {
				// if it is the file, return it as the result.
				return cur;
			}
		}
		return null;
	}
}
