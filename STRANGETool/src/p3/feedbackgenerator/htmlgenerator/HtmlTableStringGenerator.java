package p3.feedbackgenerator.htmlgenerator;

import java.util.ArrayList;
import java.util.Collections;

import p3.feedbackgenerator.message.CommentFeedbackMessage;
import p3.feedbackgenerator.message.FeedbackMessage;
import p3.feedbackgenerator.token.FeedbackToken;

public class HtmlTableStringGenerator {
	public static String getTableContentForMatches(
			ArrayList<FeedbackMessage> messages, String tableId,
			String humanLanguageId) {
		String s = "";

		// put all feedback messages as a list
		ArrayList<HtmlTableTuple> list = new ArrayList<>();
		for (FeedbackMessage m : messages) {
			list.add(new HtmlTableTuple(m));
		}

		// sort the result
		Collections.sort(list);

		// this to define concern priority
		int priority = 1;

		// start generating the resulted string
		for (int i = 0; i < list.size(); i++) {
			HtmlTableTuple cur = list.get(i);

			// set the first line
			s += "<tr id=\"" + cur.getEntity().getVisualId()
					+ "hr\" onclick=\"markSelectedWithoutChangingTableFocus('"
					+ cur.getEntity().getVisualId() + "','" + tableId + "')\">";

			/*
			 * Get table ID from visual ID and then aligns it for readability.
			 */
			String visualId = cur.getEntity().getVisualId();
			// search for the numeric ID part
			int curIdNumPos = 0;
			for (int k = 0; k < visualId.length(); k++) {
				if (Character.isLetter(visualId.charAt(k)) == false) {
					curIdNumPos = k;
					break;
				}
			}
			// merge them together
			String alignedTableID = visualId.toUpperCase().charAt(0) + "";
			int curIdNum = Integer.parseInt(visualId.substring(curIdNumPos));
			if (curIdNum < 10) {
				alignedTableID += "00" + curIdNum;
			} else if (curIdNum < 100) {
				alignedTableID += "0" + curIdNum;
			} else {
				alignedTableID += curIdNum;
			}

			/*
			 * if the importance score is lower than previous, add the priority
			 * by one (which means this is less prioritised).
			 */
			if (i >= 1
					&& cur.getImportanceScore() < list.get(i - 1)
							.getImportanceScore()) {
				priority++;
			}

			// visualising the rest of the lines
			s += ("\n\t<td><a href=\"#" + cur.getEntity().getVisualId()
					+ "a\" id=\"" + cur.getEntity().getVisualId() + "hl\">"
					+ alignedTableID + "</a></td>");
			if (humanLanguageId.equalsIgnoreCase("en")) {
				if (cur.getEntity() instanceof CommentFeedbackMessage)
					s += "\n\t<td>Comment</td>";
				else
					s += "\n\t<td>Syntax</td>";
				
				if (cur.getMinCharacterLength() == 1)
					s += ("\n\t<td>" + cur.getMinCharacterLength() + " char</td>");
				else
					s += ("\n\t<td>" + cur.getMinCharacterLength() + " chars</td>");
				
				if (cur.getMatchedTokenLength() == 1)
					s += ("\n\t<td>" + cur.getMatchedTokenLength() + " token</td>");
				else
					s += ("\n\t<td>" + cur.getMatchedTokenLength() + " tokens</td>");
			} else {
				if (cur.getEntity() instanceof CommentFeedbackMessage)
					s += "\n\t<td>Komentar</td>";
				else
					s += "\n\t<td>Sintaks</td>";
				
				s += ("\n\t<td>" + cur.getMinCharacterLength() + " karakter</td>");
				
				s += ("\n\t<td>" + cur.getMatchedTokenLength() + " token</td>");
			}

			s += "\n\t<td>" + priority + "</td>";
			s += "\n</tr>\n";
		}

		return s;
	}
	
	public static String getTableContentForMatchesStd(
			ArrayList<FeedbackMessage> messages, String tableId,
			String humanLanguageId) {
		String s = "";

		// put all feedback messages as a list
		ArrayList<HtmlTableTuple> list = new ArrayList<>();
		for (FeedbackMessage m : messages) {
			list.add(new HtmlTableTuple(m));
		}

		// sort the result
		Collections.sort(list);

		// this to define concern priority
		int priority = 1;

		// start generating the resulted string
		for (int i = 0; i < list.size(); i++) {
			HtmlTableTuple cur = list.get(i);

			// set the first line
			s += "<tr id=\"" + cur.getEntity().getVisualId()
					+ "hr\" onclick=\"markSelectedWithoutChangingTableFocus('"
					+ cur.getEntity().getVisualId() + "','" + tableId + "')\">";

			/*
			 * Get table ID from visual ID and then aligns it for readability.
			 */
			String visualId = cur.getEntity().getVisualId();
			// search for the numeric ID part
			int curIdNumPos = 0;
			for (int k = 0; k < visualId.length(); k++) {
				if (Character.isLetter(visualId.charAt(k)) == false) {
					curIdNumPos = k;
					break;
				}
			}
			// merge them together
			String alignedTableID = visualId.toUpperCase().charAt(0) + "";
			int curIdNum = Integer.parseInt(visualId.substring(curIdNumPos));
			if (curIdNum < 10) {
				alignedTableID += "00" + curIdNum;
			} else if (curIdNum < 100) {
				alignedTableID += "0" + curIdNum;
			} else {
				alignedTableID += curIdNum;
			}

			/*
			 * if the importance score is lower than previous, add the priority
			 * by one (which means this is less prioritised).
			 */
			if (i >= 1
					&& cur.getImportanceScore() < list.get(i - 1)
							.getImportanceScore()) {
				priority++;
			}

			// visualising the rest of the lines
			s += ("\n\t<td><a href=\"#" + cur.getEntity().getVisualId()
					+ "a\" id=\"" + cur.getEntity().getVisualId() + "hl\">"
					+ alignedTableID + "</a></td>");
			if (humanLanguageId.equalsIgnoreCase("en")) {
				if (cur.getEntity() instanceof CommentFeedbackMessage)
					s += "\n\t<td>Comment</td>";
				else
					s += "\n\t<td>Syntax</td>";
				
				if (cur.getMinCharacterLength() == 1)
					s += ("\n\t<td>" + cur.getMinCharacterLength() + " char</td>");
				else
					s += ("\n\t<td>" + cur.getMinCharacterLength() + " chars</td>");
			} else {
				if (cur.getEntity() instanceof CommentFeedbackMessage)
					s += "\n\t<td>Komentar</td>";
				else
					s += "\n\t<td>Sintaks</td>";
				
				s += ("\n\t<td>" + cur.getMinCharacterLength() + " karakter</td>");
			}

			s += "\n\t<td>" + priority + "</td>";
			s += "\n</tr>\n";
		}

		return s;
	}

	public static String getTableContentForSummary(
			ArrayList<FeedbackMessage> commentMessages,
			ArrayList<FeedbackMessage> syntaxMessages,
			ArrayList<FeedbackToken> tokenString1,
			ArrayList<FeedbackToken> tokenString2, String humanLanguageCode) {
		String s = "";

		int matchedComment = 0;
		for (FeedbackMessage fm : commentMessages) {
			matchedComment += fm.getNumOfCoveredTokens();
		}
		int matchedSyntax = 0;
		for (FeedbackMessage fm : syntaxMessages) {
			matchedSyntax += fm.getNumOfCoveredTokens();
		}
		int totalMatches = matchedComment + matchedSyntax;

		int commentSize1 = 0;
		int syntaxSize1 = 0;
		for (FeedbackToken ft : tokenString1) {
			if (ft.getType().equals("COMMENT"))
				commentSize1++;
			else if (!ft.getType().equals("WS"))
				syntaxSize1++;
		}
		int totalSize1 = commentSize1 + syntaxSize1;

		int commentSize2 = 0;
		int syntaxSize2 = 0;
		for (FeedbackToken ft : tokenString2) {
			if (ft.getType().equals("COMMENT"))
				commentSize2++;
			else if (!ft.getType().equals("WS"))
				syntaxSize2++;
		}
		int totalSize2 = commentSize2 + syntaxSize2;

		// average similarity
		double syntaxSimDegree = getAverageSim(matchedSyntax, syntaxSize1,
				syntaxSize2);
		double commentSimDegree = getAverageSim(matchedComment, commentSize1,
				commentSize2);
		double generalSimDegree = getAverageSim(totalMatches, totalSize1,
				totalSize2);
		String t = "Average similarity";
		if (humanLanguageCode.equals("id"))
			t = "Kesamaan rerata";
		s += getTableEntryForSummary("sum01", t, syntaxSimDegree,
				commentSimDegree, generalSimDegree);

		// maximum similarity
		syntaxSimDegree = getMaximumSim(matchedSyntax, syntaxSize1, syntaxSize2);
		commentSimDegree = getMaximumSim(matchedComment, commentSize1,
				commentSize2);
		generalSimDegree = getMaximumSim(totalMatches, totalSize1, totalSize2);
		t = "Maximum similarity";
		if (humanLanguageCode.equals("id"))
			t = "Kesamaan maksimum";
		s += getTableEntryForSummary("sum02", t, syntaxSimDegree,
				commentSimDegree, generalSimDegree);

		// left-to-right similarity
		syntaxSimDegree = getBSim(matchedSyntax, syntaxSize1, syntaxSize2);
		commentSimDegree = getBSim(matchedComment, commentSize1, commentSize2);
		generalSimDegree = getBSim(totalMatches, totalSize1, totalSize2);
		t = "Left-to-right similarity";
		if (humanLanguageCode.equals("id"))
			t = "Kesamaan kiri-ke-kanan";
		s += getTableEntryForSummary("sum03", t, syntaxSimDegree,
				commentSimDegree, generalSimDegree);

		// right-to-left similarity
		syntaxSimDegree = getASim(matchedSyntax, syntaxSize1, syntaxSize2);
		commentSimDegree = getASim(matchedComment, commentSize1, commentSize2);
		generalSimDegree = getASim(totalMatches, totalSize1, totalSize2);
		t = "Right-to-left similarity";
		if (humanLanguageCode.equals("id"))
			t = "Kesamaan kanan-ke-kiri";
		s += getTableEntryForSummary("sum04", t, syntaxSimDegree,
				commentSimDegree, generalSimDegree);

		s += "\n\t</tr>";

		return s;
	}

	public static double getAverageSim(int matches, int size1, int size2) {
		double result = (matches * 2.0) / (size1 + size2);
		if(Double.isNaN(result))
			return 0;
		else
			return result;
	}

	public static double getMaximumSim(int matches, int size1, int size2) {
		double result = (matches * 1.0) / Math.min(size1, size2);
		if(Double.isNaN(result))
			return 0;
		else
			return result;
	}

	private static double getASim(int matches, int size1, int size2) {
		double result = (matches * 1.0) / size1;
		if(Double.isNaN(result))
			return 0;
		else
			return result;
	}

	private static double getBSim(int matches, int size1, int size2) {
		// System.out.println(matches + " " + size1 + " " + size2);
		double result = (matches * 1.0) / size2;
		if(Double.isNaN(result))
			return 0;
		else
			return result;
	}

	private static String getTableEntryForSummary(String entryID,
			String rowTitle, double syntaxSimDegree, double commentSimDegree,
			double generalSimDegree) {
		String s = "";
		s += "<tr id=\"" + entryID + "hr\" onclick=\"displaySelectedSummary('"
				+ entryID + "','sumtablecontent')\">";
		s += ("\n\t<td class='tdsum'><a>" + rowTitle + "</a></td>");
		s += ("\n\t<td class='tdsum'>"
				+ String.format("%.2f", syntaxSimDegree * 100) + " %</td>");
		s += ("\n\t<td class='tdsum'>"
				+ String.format("%.2f", commentSimDegree * 100) + " %</td>");
		s += ("\n\t<td class='tdsum'>"
				+ String.format("%.2f", generalSimDegree * 100) + " %</td>");
		s += "</tr>\n";
		return s;
	}

	public static String getExplanationContentForSummary() {
		String s = "";

		// average similarity
		s += "<div class=\"explanationcontent\" id=\"sum01he\">\n\t";
		s += "The characteristics of <b>average similarity</b>:<ul>";
		s += "<li>It considers all differences in its calculation.</li>";
		s += "<li>It only leads to 100% similarity if both code files are similar, even if some fragments have been swapped.</li>";
		s += "<li>It is best used when all differences are equally important.</li>";
		s += "<li>It is calculated as <b>2M&nbsp;/&nbsp;(&nbsp;A&nbsp;+&nbsp;B&nbsp;)</b> where <b>M</b> is the number of matches, ";
		s += "<b>A</b> is the number of tokens on the left code, and <b>B</b> is the number of tokens on the right code.</li>";
		s = s + "</ul>\n</div>\n";

		// maximum similarity
		s += "<div class=\"explanationcontent\" id=\"sum02he\">\n\t";
		s += "The characteristics of <b>maximum similarity</b>:<ul>";
		s += "<li>It can lead to 100% similarity either if both code files are similar, "
				+ "or if one code file is a part of another.</li>";
		s += "<li>It is best used when many unnecessary tokens can be added to the code without changing its meaning.</li>";
		s += "<li>It is calculated as <b>M&nbsp;/&nbsp;Min(&nbsp;A&nbsp;,&nbsp;B&nbsp;)&nbsp;</b> where <b>M</b> is the number of matches, ";
		s += "<b>A</b> is the number of tokens on the left code, and <b>B</b> is the number of tokens on the right code.</li>";
		s += "</ul>\n</div>\n";

		// left-to-right similarity
		s += "<div class=\"explanationcontent\" id=\"sum03he\">\n\t";
		s += "The characteristics of <b>left-to-right similarity</b>:<ul>";
		s += "<li>It is the proportion of left code tokens shared by right code.</li>";
		s += "<li>It is calculated as <b>M&nbsp;/&nbsp;B&nbsp;</b> where <b>M</b> is the number of matches, and ";
		s += "<b>B</b> is the number of tokens on the right code.</li>";
		s += "</ul>\n</div>\n";

		// right-to-left similarity
		s += "<div class=\"explanationcontent\" id=\"sum04he\">\n\t";
		s += "The characteristics of <b>right-to-left similarity</b>:<ul>";
		s += "<li>It is the proportion of right code tokens shared by left code.</li>";
		s += "<li>It is calculated as <b>M&nbsp;/&nbsp;A&nbsp;</b> where <b>M</b> is the number of matches, and ";
		s += "<b>A</b> is the number of tokens on the left code.</li>";
		s += "</ul>\n</div>\n";

		return s;
	}

	public static String getExplanationContentForSummaryId() {
		String s = "";

		// average similarity
		s += "<div class=\"explanationcontent\" id=\"sum01he\">\n\t";
		s += "Karakteristik <b>kesamaan rerata</b>:<ul>";
		s += "<li>Memperhitungkan semua aspek perbedaan dalam prosesnya.</li>";
		s += "<li>Hanya menghasilkan kesamaan 100% jika konten kedua file serupa, meskipun sebagian fragmennya bertukar posisi.</li>";
		s += "<li>Cocok digunakan jika semua perbedaan kode dianggap penting.</li>";
		s += "<li>Dihitung dengan persamaan: <b>2M&nbsp;/&nbsp;(&nbsp;A&nbsp;+&nbsp;B&nbsp;)</b>; <b>M</b> adalah jumlah token sama, ";
		s += "<b>A</b> adalah jumlah token pada kode kiri, dan <b>B</b> adalah jumlah token pada kode kanan.</li>";
		s = s + "</ul>\n</div>\n";

		// maximum similarity
		s += "<div class=\"explanationcontent\" id=\"sum02he\">\n\t";
		s += "Karakteristik <b>kesamaan maksimum</b>:<ul>";
		s += "<li>Dapat menghasilkan kesamaan 100% jika konten kedua file serupa, "
				+ "atau salah satu kode merupakan bagian dari kode satunya.</li>";
		s += "<li>Cocok digunakan jika banyak token tambahan dapat disertakan pada kode tanpa mengubah makna.</li>";
		s += "<li>Dihitung dengan persamaan: <b>M&nbsp;/&nbsp;Min(&nbsp;A&nbsp;,&nbsp;B&nbsp;)&nbsp;</b>; <b>M</b> adalah jumlah token sama, ";
		s += "<b>A</b> adalah jumlah token pada kode kiri, dan <b>B</b> adalah jumlah token pada kode kanan.</li>";
		s += "</ul>\n</div>\n";

		// left-to-right similarity
		s += "<div class=\"explanationcontent\" id=\"sum03he\">\n\t";
		s += "Karakteristik <b>kesamaan kiri-ke-kanan</b>:<ul>";
		s += "<li>Merupakan proporsi token pada kode kiri yang dimiliki oleh kode kanan.</li>";
		s += "<li>Dihitung dengan persamaan: <b>M&nbsp;/&nbsp;B&nbsp;</b>; <b>M</b> adalah jumlah token sama, dan ";
		s += "<b>B</b> adalah jumlah token pada kode kanan.</li>";
		s += "</ul>\n</div>\n";

		// right-to-left similarity
		s += "<div class=\"explanationcontent\" id=\"sum04he\">\n\t";
		s += "Karakteristik <b>kesamaan kanan-ke-kiri</b>:<ul>";
		s += "<li>Merupakan proporsi token pada kode kanan yang dimiliki oleh kode kiri.</li>";
		s += "<li>Dihitung dengan persamaan: <b>M&nbsp;/&nbsp;A&nbsp;</b>; <b>M</b> adalah jumlah token sama, dan ";
		s += "<b>A</b> adalah jumlah token pada kode kiri.</li>";
		s += "</ul>\n</div>\n";

		return s;
	}
}
