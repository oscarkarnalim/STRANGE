package p3.feedbackgenerator.message;

import java.util.ArrayList;
import java.util.Collections;

import p3.feedbackgenerator.token.FeedbackToken;
import p3.feedbackgenerator.token.MismatchCommentSubstringTuple;

public class CommentFeedbackMessage extends StandardFeedbackMessage {
	/*
	 * contains modified content in pairwise manner with index as the connector
	 * between those lists.
	 */
	protected ArrayList<MismatchCommentSubstringTuple> mismatchCommentSubstrings;
	protected ArrayList<String> alignedComment1;
	protected ArrayList<String> alignedComment2;
	protected String humanLanguageCode;

	public CommentFeedbackMessage(String action, FeedbackToken token1,
			FeedbackToken token2,
			ArrayList<MismatchCommentSubstringTuple> mismatchCommentSubstrings,
			ArrayList<String> alignedComment1, ArrayList<String> alignedComment2, String humanLanguageCode) {
		super(action, "comment", token1, token2);
		// sort the mismatchCommentSubstrings given
		Collections.sort(mismatchCommentSubstrings);
		// set it as an attribute
		this.mismatchCommentSubstrings = mismatchCommentSubstrings;
		// get the aligned version
		this.alignedComment1 = alignedComment1;
		this.alignedComment2 = alignedComment2;
		// set the language
		this.humanLanguageCode = humanLanguageCode;
	}
	
	public String toString(){
		if(humanLanguageCode.equals("en"))
			return toStringEn();
		else
			return toStringId();
	}

	public String toStringEn() {
		String s = "";
		if (action.equals("modified")) {
			if (mismatchCommentSubstrings.size() > 0) {
				s = "<b>The comments are partially similar.</b> "
						+ "This is suspicious because if no patterns have been provided and no automation is involved," +
						" comments are generally unique to each student.";

				s += "<br /><br /><b>The captured differences:</b><ol>";
				for (int i = 0; i < mismatchCommentSubstrings.size(); i++) {
					String mismatchComment1 = mismatchCommentSubstrings.get(i)
							.getMismatchSubstring1();
					String mismatchComment2 = mismatchCommentSubstrings.get(i)
							.getMismatchSubstring2();
					int occurrence = mismatchCommentSubstrings.get(i)
							.getOccurrenceFrequency();

					if (mismatchComment1.length() > 0
							&& mismatchComment2.length() > 0) {
						s += "\n\t<li> <span class=\"commentsim\">\""
								+ mismatchComment1
								+ "\"</span> is replaced with <span class=\"commentsim\">\""
								+ mismatchComment2 + "\"</span> ";
						if (occurrence > 1)
							s += "by " + occurrence + " times.";
						else
							s += "once.";
					} else if (mismatchComment1.length() == 0) {
						s += "\n\t<li> <span class=\"commentsim\"> \""
								+ mismatchComment2
								+ "\"</span> is added on the right comment ";
						if (occurrence > 1)
							s += "by " + occurrence + " times.";
						else
							s += "once.";
					} else if (mismatchComment2.length() == 0) {
						s += "\n\t<li> <span class=\"commentsim\"> \""
								+ mismatchComment1
								+ "\"</span> is added on the left comment ";
						if (occurrence > 1)
							s += "by " + occurrence + " times.";
						else
							s += "once.";
					}
					s += "</li>";
				}
				s += "\n</ol>";

				// explaining how the aligning results
				s += "<b>Aligned comment words (with the differences highlighted):</b> <br /> <br />";
				s += "<table><tr class=\"inexplanation\">"
						+ "<th class=\"inexplanation\">Aligned position</th>"
						+ "<th class=\"inexplanation\">Left form</th>"
						+ "<th class=\"inexplanation\">Left position</th>"
						+ "<th class=\"inexplanation\">Right form</th>"
						+ "<th class=\"inexplanation\">Right position</th>"
						+ "</tr>";
				int comment1Counter = 0;
				int comment2Counter = 0;
				for (int k = 0; k < alignedComment1.size(); k++) {
					String w1 = alignedComment1.get(k);
					String w2 = alignedComment2.get(k);
					s += "<tr class=\"inexplanation\">";
					// print a number, just for referencing clarity
					s += "<td class=\"inexplanation\">" + (k + 1) + "</td>";

					// write comment 1 word
					if (w1.equals("_")) {
						s += "<td class=\"inexplanation\">" + "-" + "</td>";
						s += "<td class=\"inexplanation\">" + "-" + "</td>";
					} else {
						if (w1.equals(w2) == false) {
							s += "<td class=\"inexplanation\"><span class=\"commentsim\">"
									+ w1 + "</span></td>";
						} else {
							s += "<td class=\"inexplanation\">" + w1 + "</td>";
						}
						s += "<td class=\"inexplanation\">"
								+ (comment1Counter + 1) + "</td>";
						comment1Counter++;
					}

					// write comment 2 word
					if (w2.equals("_")) {
						s += "<td class=\"inexplanation\">" + "-" + "</td>";
						s += "<td class=\"inexplanation\">" + "-" + "</td>";
					} else {
						if (w1.equals(w2) == false) {
							s += "<td class=\"inexplanation\"><span class=\"commentsim\">"
									+ w2 + "</span></td>";
						} else {
							s += "<td class=\"inexplanation\">" + w2 + "</td>";
						}
						s += "<td class=\"inexplanation\">"
								+ (comment2Counter + 1) + "</td>";
						comment2Counter++;
					}

					s += "</tr>";
				}
				s += "</table>";
			} else {
				s = "<b>The comments share the same word sequences " +
						"despite differences in white space and non-alphanumeric characters.</b> ";
				s += "This is suspicious because if no patterns have been provided and no automation is involved," +
						" comments are generally unique to each student.";
			}
		} else if (action.equals("copied")) {
			s = "<b>The comments are identical.</b> "
					+ "This is suspicious because if no patterns have been provided and no automation is involved," +
					" comments are generally unique to each student.";
		}

		return s;
	}
	
	public String toStringId() {
		String s = "";
		if (action.equals("modified")) {
			if (mismatchCommentSubstrings.size() > 0) {
				s = "<b>Kedua komentar serupa di beberapa bagian.</b> "
						+ "Kesamaan ini mencurigakan karena jika tidak ada pola yang diberikan dan tidak ada otomatisasi terlibat," +
						" komentar umumnya bersifat unik untuk setiap siswa.";

				s += "<br /><br /><b>Kata-kata pembeda yang tertangkap:</b><ol>";
				for (int i = 0; i < mismatchCommentSubstrings.size(); i++) {
					String mismatchComment1 = mismatchCommentSubstrings.get(i)
							.getMismatchSubstring1();
					String mismatchComment2 = mismatchCommentSubstrings.get(i)
							.getMismatchSubstring2();
					int occurrence = mismatchCommentSubstrings.get(i)
							.getOccurrenceFrequency();

					if (mismatchComment1.length() > 0
							&& mismatchComment2.length() > 0) {
						s += "\n\t<li> <span class=\"commentsim\">\""
								+ mismatchComment1
								+ "\"</span> diganti dengan <span class=\"commentsim\">\""
								+ mismatchComment2 + "\"</span> ";
						if (occurrence > 1)
							s += "sebanyak " + occurrence + " kali.";
						else
							s += "sekali.";
					} else if (mismatchComment1.length() == 0) {
						s += "\n\t<li> <span class=\"commentsim\"> \""
								+ mismatchComment2
								+ "\"</span> ditambahkan pada komentar kanan ";
						if (occurrence > 1)
							s += "sebanyak " + occurrence + " kali.";
						else
							s += "sekali.";
					} else if (mismatchComment2.length() == 0) {
						s += "\n\t<li> <span class=\"commentsim\"> \""
								+ mismatchComment1
								+ "\"</span> ditambahkan pada komentar kiri ";
						if (occurrence > 1)
							s += "sebanyak " + occurrence + " kali.";
						else
							s += "sekali.";
					}
					s += "</li>";
				}
				s += "\n</ol>";

				// explaining how the aligning results
				s += "<b>Kata-kata pada komentar yang sudah diratakan (dengan aspek perbedaan ditandai):</b> <br /> <br />";
				s += "<table><tr class=\"inexplanation\">"
						+ "<th class=\"inexplanation\">Posisi perataan</th>"
						+ "<th class=\"inexplanation\">Bentuk kiri</th>"
						+ "<th class=\"inexplanation\">Posisi kiri</th>"
						+ "<th class=\"inexplanation\">Bentuk kanan</th>"
						+ "<th class=\"inexplanation\">Posisi kanan</th>"
						+ "</tr>";
				int comment1Counter = 0;
				int comment2Counter = 0;
				for (int k = 0; k < alignedComment1.size(); k++) {
					String w1 = alignedComment1.get(k);
					String w2 = alignedComment2.get(k);
					s += "<tr class=\"inexplanation\">";
					// print a number, just for referencing clarity
					s += "<td class=\"inexplanation\">" + (k + 1) + "</td>";

					// write comment 1 word
					if (w1.equals("_")) {
						s += "<td class=\"inexplanation\">" + "-" + "</td>";
						s += "<td class=\"inexplanation\">" + "-" + "</td>";
					} else {
						if (w1.equals(w2) == false) {
							s += "<td class=\"inexplanation\"><span class=\"commentsim\">"
									+ w1 + "</span></td>";
						} else {
							s += "<td class=\"inexplanation\">" + w1 + "</td>";
						}
						s += "<td class=\"inexplanation\">"
								+ (comment1Counter + 1) + "</td>";
						comment1Counter++;
					}

					// write comment 2 word
					if (w2.equals("_")) {
						s += "<td class=\"inexplanation\">" + "-" + "</td>";
						s += "<td class=\"inexplanation\">" + "-" + "</td>";
					} else {
						if (w1.equals(w2) == false) {
							s += "<td class=\"inexplanation\"><span class=\"commentsim\">"
									+ w2 + "</span></td>";
						} else {
							s += "<td class=\"inexplanation\">" + w2 + "</td>";
						}
						s += "<td class=\"inexplanation\">"
								+ (comment2Counter + 1) + "</td>";
						comment2Counter++;
					}

					s += "</tr>";
				}
				s += "</table>";
			} else {
				s = "<b>Kedua komentar memiliki kata-kata yang sama " +
						"walaupun berbeda di whitespace dan karakter non-alphanumerik.</b> ";
				s += "Kesamaan ini mencurigakan karena jika tidak ada pola yang diberikan dan tidak ada otomatisasi terlibat," +
						" komentar umumnya bersifat unik untuk setiap siswa.";
			}
		} else if (action.equals("copied")) {
			s = "<b>Kedua komentar identik satu sama lain.</b> "
					+ "Kesamaan ini mencurigakan karena jika tidak ada pola yang diberikan dan tidak ada otomatisasi terlibat," +
					" komentar umumnya bersifat unik untuk setiap siswa.";
		}

		return s;
	}
}
