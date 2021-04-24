package p3.feedbackgenerator.message;

import java.util.ArrayList;

import p3.feedbackgenerator.token.FeedbackToken;
import p3.feedbackgenerator.token.MismatchCommentSubstringTuple;
import support.ir.NaturalLanguageProcesser;
import support.stringmatching.GSTMatchTuple;
import support.stringmatching.GreedyStringTiling;
import support.stringmatching.StringAlignment;

public class FeedbackMessageGenerator {

	public static ArrayList<FeedbackMessage> generateSyntaxReorderingMessages(
			ArrayList<FeedbackToken> syntaxTokenString1,
			ArrayList<FeedbackToken> syntaxTokenString2,
			ArrayList<FeedbackToken> rawTokenString1,
			ArrayList<FeedbackToken> rawTokenString2, String filePath1,
			String filePath2, int minimumMatchLength, String humanLanguageCode) {

		// get matched tiles with RKRGST
		ArrayList<GSTMatchTuple> simTuples = generateMatchedTuples(
				syntaxTokenString1, syntaxTokenString2, minimumMatchLength);

		ArrayList<String> syntaxStringResult1s = new ArrayList<>();
		ArrayList<String> syntaxStringResult2s = new ArrayList<>();

		/*
		 * This section swap the position of some matched tuples so that their
		 * original token forms are the same. According to our observation, if
		 * the code files share two fragments with the same generalised form, it
		 * is possible that they are matched inaccurately to one another. For
		 * example, int a; can be matched to int b; and vice versa even though
		 * the counterpart code has int a; also. This is because both int a; and
		 * int b; share the same generalised form which is "type ident;".
		 */
		// generate the original content of each similar fragment
		for (int i = 0; i < simTuples.size(); i++) {
			GSTMatchTuple cur = simTuples.get(i);
			String s1 = "";
			String s2 = "";
			// get the real content and concatenate them as two strings.
			for (int j = 0; j < cur.length; j++) {
				s1 = s1
						+ syntaxTokenString1.get(cur.patternPosition + j)
								.getContent() + " ";
				s2 = s2
						+ syntaxTokenString2.get(cur.textPosition + j)
								.getContent() + " ";
			}
			syntaxStringResult1s.add(s1);
			syntaxStringResult2s.add(s2);
		}
		// start swapping.
		for (int i = 0; i < simTuples.size(); i++) {
			GSTMatchTuple cur = simTuples.get(i);
			String s1 = syntaxStringResult1s.get(i);
			String s2 = syntaxStringResult2s.get(i);

			if (s1.equals(s2))
				continue;
			else {
				for (int j = i + 1; j < simTuples.size(); j++) {
					if (cur.length == simTuples.get(j).length
							&& s1.equals(syntaxStringResult2s.get(j))) {
						/*
						 * if the length is the same and s1 equals to the second
						 * string on j pos.
						 */

						// swap text position
						int tmp = cur.textPosition;
						cur.textPosition = simTuples.get(j).textPosition;
						simTuples.get(j).textPosition = tmp;

						// swap the second string
						String tmps = syntaxStringResult2s.get(i);
						syntaxStringResult2s
								.set(i, syntaxStringResult2s.get(j));
						syntaxStringResult2s.set(j, tmps);
					}
				}
			}
		}

		// create a list to store the results
		ArrayList<FeedbackMessage> messages = new ArrayList<>();

		// map the matched tiles to original position and form the messages
		for (int i = 0; i < simTuples.size(); i++) {
			GSTMatchTuple cur = simTuples.get(i);

			// create syntax token lists for storing selected syntax tokens
			ArrayList<FeedbackToken> syntaxTokenList1 = new ArrayList<>();
			ArrayList<FeedbackToken> syntaxTokenList2 = new ArrayList<>();

			// get the syntax tokens
			for (int j = 0; j < cur.length; j++) {
				syntaxTokenList1.add(syntaxTokenString1.get(cur.patternPosition
						+ j));
				syntaxTokenList2.add(syntaxTokenString2.get(cur.textPosition
						+ j));
			}

			// calculate how many modifications on the tokens' whitespaces.
			boolean areWhitespacesModified = false;
			int syntaxwhitespaceStartIdx1 = rawTokenString1
					.indexOf(syntaxTokenList1.get(0));
			int syntaxwhitespaceFinishIdx1 = rawTokenString1
					.indexOf(syntaxTokenList1.get(syntaxTokenList1.size() - 1));
			int syntaxwhitespaceStartIdx2 = rawTokenString2
					.indexOf(syntaxTokenList2.get(0));
			int syntaxwhitespaceFinishIdx2 = rawTokenString2
					.indexOf(syntaxTokenList2.get(syntaxTokenList2.size() - 1));

			/*
			 * if the first code has different number of tokens than the
			 * counterpart, then the whitespaces are modified.
			 */
			if (syntaxwhitespaceFinishIdx1 - syntaxwhitespaceStartIdx1 != syntaxwhitespaceFinishIdx2
					- syntaxwhitespaceStartIdx2)
				areWhitespacesModified = true;
			else {
				/*
				 * otherwise, check for each whitespace pair
				 */
				for (int j = 0; j < syntaxwhitespaceFinishIdx1
						- syntaxwhitespaceStartIdx1; j++) {
					FeedbackToken f1 = rawTokenString1
							.get(syntaxwhitespaceStartIdx1 + j);
					FeedbackToken f2 = rawTokenString2
							.get(syntaxwhitespaceStartIdx2 + j);

					if (f1.getType().equals("WS") && f2.getType().equals("WS")
							&& f1.getContent().equals(f2.getContent()) == false) {
						/*
						 * if both are whitespaces but in different forms, count
						 * as a modification
						 */
						areWhitespacesModified = true;
						break;
					} else if (f1.getType().equals("WS")
							&& f2.getType().equals("WS") == false) {
						/*
						 * if one of them is whitespace while another is not,
						 * count as a modification
						 */
						areWhitespacesModified = true;
						break;
					} else if (f1.getType().equals("WS") == false
							&& f2.getType().equals("WS")) {
						/*
						 * if one of them is whitespace while another is not,
						 * count as a modification
						 */
						areWhitespacesModified = true;
						break;
					}
				}
			}

			// create the message and add it
			SyntaxFeedbackMessage m = new SyntaxFeedbackMessage("copied",
					"syntax token", syntaxTokenList1, syntaxTokenList2,
					areWhitespacesModified, humanLanguageCode);
			messages.add(m);
		}

		return messages;
	}

	public static ArrayList<FeedbackMessage> generateWhitespaceDisguiseMessages(
			ArrayList<FeedbackToken> tokenString1,
			ArrayList<FeedbackToken> tokenString2) {
		// generate whitespace messages based on two complete token strings.

		// minimum matching length for RKRGST
		int minimumMatchLength = 2;

		// lists to store only whitespaces per string
		ArrayList<FeedbackToken> whitespaceString1 = new ArrayList<>();
		ArrayList<FeedbackToken> whitespaceString2 = new ArrayList<>();

		// take only whitespace tokens from the strings
		for (int i = 0; i < tokenString1.size(); i++) {
			if (tokenString1.get(i).getType().equals("WS")) {
				whitespaceString1.add(tokenString1.get(i));
			}
		}
		for (int i = 0; i < tokenString2.size(); i++) {
			if (tokenString2.get(i).getType().equals("WS")) {
				whitespaceString2.add(tokenString2.get(i));
			}
		}

		// get matched tiles with RKRGST
		ArrayList<GSTMatchTuple> simTuples = generateMatchedTuples(
				whitespaceString1, whitespaceString2, minimumMatchLength);

		// create a list to store the results
		ArrayList<FeedbackMessage> messages = new ArrayList<>();

		// map the matched tiles to original position and form the messages
		for (int i = 0; i < simTuples.size(); i++) {
			GSTMatchTuple cur = simTuples.get(i);

			// create token lists for storing selected whitespaces
			ArrayList<FeedbackToken> tokenList1 = new ArrayList<>();
			ArrayList<FeedbackToken> tokenList2 = new ArrayList<>();

			// get the whitespaces
			for (int j = 0; j < cur.length; j++) {
				tokenList1.add(whitespaceString1.get(cur.patternPosition + j));
				tokenList2.add(whitespaceString2.get(cur.textPosition + j));
			}

			// create the message and add it
			MultipleFeedbackMessage m = new MultipleFeedbackMessage("copied",
					"whitespace", tokenList1, tokenList2);
			messages.add(m);
		}

		return messages;
	}

	public static ArrayList<GSTMatchTuple> generateMatchedTuples(
			ArrayList<FeedbackToken> tokenString1,
			ArrayList<FeedbackToken> tokenString2, int minimumMatchLength) {
		// create array of string for both whitespace strings
		String[] obj1 = new String[tokenString1.size()];
		String[] obj2 = new String[tokenString2.size()];

		for (int i = 0; i < tokenString1.size(); i++) {
			obj1[i] = tokenString1.get(i).getContentForComparison();
		}
		for (int i = 0; i < tokenString2.size(); i++) {
			obj2[i] = tokenString2.get(i).getContentForComparison();
		}
		// get matched tiles with RKRGST to remaining unmatched regions
		ArrayList<GSTMatchTuple> simTuples = GreedyStringTiling
				.getMatchedTiles(obj1, obj2, minimumMatchLength);

		return simTuples;
	}

	public static ArrayList<FeedbackMessage> generateCommentDisguiseMessages(
			ArrayList<FeedbackToken> tokenString1,
			ArrayList<FeedbackToken> tokenString2, String languageCode) {
		// create comment messages based on given two complete token strings.

		// create lists to store comment tokens per string
		ArrayList<FeedbackToken> commentString1 = new ArrayList<>();
		ArrayList<FeedbackToken> commentString2 = new ArrayList<>();

		// create lists to store processed comments per string
		ArrayList<String> commentProcessedString1 = new ArrayList<>();
		ArrayList<String> commentProcessedString2 = new ArrayList<>();

		// filter all the comments and process its content
		for (int i = 0; i < tokenString1.size(); i++) {
			if (tokenString1.get(i).getType().equals("COMMENT")) {
				// take the raw content
				String content = tokenString1.get(i).getContent();
				// lowercase and split based on non-alphanumeric
				String[] contentTerm = content.toLowerCase().split(
						"[^a-zA-Z0-9']");
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

				// add the token
				commentString1.add(tokenString1.get(i));
			}
		}

		// filter all the comments and process its content but for code 2
		for (int i = 0; i < tokenString2.size(); i++) {
			if (tokenString2.get(i).getType().equals("COMMENT")) {
				// take the raw content
				String content = tokenString2.get(i).getContent();
				// lowercase and split based on non-alphanumeric
				String[] contentTerm = content.toLowerCase().split(
						"[^a-zA-Z0-9']");
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

				// add the token
				commentString2.add(tokenString2.get(i));
			}
		}

		// create a list to store the results
		ArrayList<FeedbackMessage> messages = new ArrayList<>();

		// start iterating the first comment list
		for (int i = 0; i < commentProcessedString1.size(); i++) {
			String cur = commentProcessedString1.get(i);

			// search on the second list
			int j = subStringMatchSearch(cur, commentProcessedString2);

			if (j != -1) {
				FeedbackMessage tobeadded;
				if (commentString1.get(i).getContent()
						.equals(commentString2.get(j).getContent())) {
					// if similar, message: copied
					tobeadded = new CommentFeedbackMessage("copied",
							commentString1.get(i), commentString2.get(j),
							new ArrayList<MismatchCommentSubstringTuple>(),
							new ArrayList<String>(), new ArrayList<String>(),
							languageCode);
				} else {
					// if different, message: modified
					Object[] results = MismatchCommentSubstringTuple
							.getNonSharedString(commentString1.get(i)
									.getContent(), commentString2.get(j)
									.getContent(), languageCode);
					ArrayList<MismatchCommentSubstringTuple> mismatchedComments = (ArrayList<MismatchCommentSubstringTuple>) results[0];
					ArrayList<String> alignedString1 = (ArrayList<String>) results[1];
					ArrayList<String> alignedString2 = (ArrayList<String>) results[2];
					tobeadded = new CommentFeedbackMessage("modified",
							commentString1.get(i), commentString2.get(j),
							mismatchedComments, alignedString1, alignedString2,
							languageCode);
				}
				messages.add(tobeadded);

				// nullify to anticipate redundant comments paired with the same
				// counterpart. It is like marking the comment as "used"
				commentProcessedString2.set(j, null);
			}
		}

		return messages;
	}

	public static int subStringMatchSearch(String cur, ArrayList<String> strings) {
		// this method check whether cur is on strings or vice versa.

		// intialsing penalties of different types
		int misMatchPenalty = 3;
		int gapPenalty = 2;

		// search whether cur is one of the string.
		int j = strings.indexOf(cur);
		if (j == -1) {
			// If not found, check whether cur is part of one of them or vice
			// versa.
			for (int i = 0; i < strings.size(); i++) {
				String s = strings.get(i);
				if (s != null) {
					// get the aligned strings
					String[] strArr = StringAlignment.getAlignedStrings(cur, s,
							misMatchPenalty, gapPenalty);
					// check how many characters are similar
					int simCount = 0;
					for (int k = 0; k < strArr[0].length(); k++) {
						char c1 = strArr[0].charAt(k);
						char c2 = strArr[1].charAt(k);
						if (c1 != '_' && c2 != '_' && c1 == c2)
							simCount++;
					}

					// if simCount proportion (via avg sim) is more than half,
					// add it to the result
					if (simCount * 2.0 / (cur.length() + s.length()) >= 0.5) {
						j = i;
						break;
					}
				}
			}
		}
		return j;
	}
}
