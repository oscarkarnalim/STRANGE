package p3.feedbackgenerator.token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.common.collect.MinMaxPriorityQueue;

import support.ir.NaturalLanguageProcesser;
import support.stringmatching.StringAlignment;

public class MismatchCommentSubstringTuple implements
		Comparable<MismatchCommentSubstringTuple> {
	protected String mismatchSubstring1, mismatchSubstring2;
	protected ArrayList<Integer> positions1, positions2;

	public MismatchCommentSubstringTuple(String mismatchSubstring1,
			String mismatchSubstring2) {
		super();
		this.mismatchSubstring1 = mismatchSubstring1;
		this.mismatchSubstring2 = mismatchSubstring2;
		this.positions1 = new ArrayList<>();
		this.positions2 = new ArrayList<>();
	}

	public String getMismatchSubstring1() {
		return mismatchSubstring1;
	}

	public void setMismatchSubstring1(String mismatchSubstring1) {
		this.mismatchSubstring1 = mismatchSubstring1;
	}

	public String getMismatchSubstring2() {
		return mismatchSubstring2;
	}

	public void setMismatchSubstring2(String mismatchSubstring2) {
		this.mismatchSubstring2 = mismatchSubstring2;
	}

	public ArrayList<Integer> getPositions1() {
		return positions1;
	}

	public ArrayList<Integer> getPositions2() {
		return positions2;
	}

	public int getOccurrenceFrequency() {
		return positions1.size();
	}

	public void addOccurrence(int pos1, int pos2) {
		this.positions1.add(pos1);
		this.positions2.add(pos2);
	}

	@Override
	public int compareTo(MismatchCommentSubstringTuple arg0) {
		// TODO Auto-generated method stub
		int pos1 = this.positions1.get(0);
		if (pos1 == -1)
			pos1 = Integer.MAX_VALUE;
		int pos2 = arg0.positions1.get(0);
		if (pos2 == -1)
			pos2 = Integer.MAX_VALUE;
		return pos1 - pos2;
	}

	public static int indexOfAList(
			ArrayList<MismatchCommentSubstringTuple> list,
			String mismatchSubstring1, String mismatchSubstring2) {
		/*
		 * search from a given list whether there is a tuple with the same
		 * mismatchSubstring as given on the parameters.
		 */
		for (int i = 0; i < list.size(); i++) {
			MismatchCommentSubstringTuple cur = list.get(i);
			if (cur.getMismatchSubstring1().equals(mismatchSubstring1)
					&& cur.getMismatchSubstring2().equals(mismatchSubstring2))
				return i;
		}

		return -1;
	}

	public static Object[] getNonSharedString(String str1, String str2,
			String languageCode) {
		/*
		 * this method returns three results, represented as an array. The first
		 * one is mismatch information while the other two are the aligned
		 * strings (in which each word has not been preprocessed).
		 */

		// intialsing penalties of different types
		int misMatchPenalty = 3;
		int gapPenalty = 2;

		// split based on non-alphanumeric characters and remove any empty
		// substring
		String[] arr1 = getSplittedString(str1);
		String[] arr2 = getSplittedString(str2);

		// lowercase and stem
		String[] processedArr1 = new String[arr1.length];
		String[] processedArr2 = new String[arr2.length];
		for (int j = 0; j < arr1.length; j++)
			processedArr1[j] = NaturalLanguageProcesser.getStem(
					arr1[j].toLowerCase(), languageCode);
		for (int j = 0; j < arr2.length; j++)
			processedArr2[j] = NaturalLanguageProcesser.getStem(
					arr2[j].toLowerCase(), languageCode);

		// do the alignment
		ArrayList<ArrayList<String>> alignmentResult = StringAlignment
				.getAlignedStrings(processedArr1, processedArr2,
						misMatchPenalty, gapPenalty);
		ArrayList<String> alignment1 = alignmentResult.get(0);
		ArrayList<String> alignment2 = alignmentResult.get(1);

		// set the output
		ArrayList<MismatchCommentSubstringTuple> result = new ArrayList<>();
		int index1 = 0, index2 = 0;
		for (int i = 0; i < alignment1.size(); i++) {
			String s1 = alignment1.get(i);
			String s2 = alignment2.get(i);
			// System.out.println(s1 + " " + s2);
			if (s1.equals("_")) {
				// if s1 is empty, add s2 raw form
				int index = MismatchCommentSubstringTuple.indexOfAList(result,
						"", arr2[index2]);

				// get the corresponding tuple
				MismatchCommentSubstringTuple nmtuple;
				if (index == -1) {
					nmtuple = new MismatchCommentSubstringTuple("",
							arr2[index2]);
					result.add(nmtuple);
				} else {
					nmtuple = result.get(index);
				}

				// get the occurrence
				nmtuple.addOccurrence(-1, index2 + 1);

				// set the aligned form's content with their corresponding
				// processed form
				alignment2.set(i, arr2[index2]);

				index2++;
			} else if (s2.equals("_")) {
				// if s2 is empty, add s1 raw form
				int index = MismatchCommentSubstringTuple.indexOfAList(result,
						arr1[index1], "");

				MismatchCommentSubstringTuple nmtuple;
				if (index == -1) {
					nmtuple = new MismatchCommentSubstringTuple(arr1[index1],
							"");
					result.add(nmtuple);
				} else {
					nmtuple = result.get(index);
				}

				nmtuple.addOccurrence(index1 + 1, -1);

				// set the aligned form's content with their corresponding
				// processed form
				alignment1.set(i, arr1[index1]);

				index1++;
			} else {
				// if their original forms are the same
				if (arr1[index1].equals(arr2[index2]) == false) {
					int index = MismatchCommentSubstringTuple.indexOfAList(
							result, arr1[index1], arr2[index2]);

					MismatchCommentSubstringTuple nmtuple;
					if (index == -1) {
						nmtuple = new MismatchCommentSubstringTuple(
								arr1[index1], arr2[index2]);
						result.add(nmtuple);
					} else {
						nmtuple = result.get(index);
					}

					nmtuple.addOccurrence(index1 + 1, index2 + 1);
				}

				// set the aligned form's content with their corresponding
				// processed form
				alignment1.set(i, arr1[index1]);
				alignment2.set(i, arr2[index2]);

				index1++;
				index2++;
			}
		}

		Object[] completeResults = new Object[3];
		completeResults[0] = result;
		completeResults[1] = alignment1;
		completeResults[2] = alignment2;

		return completeResults;
	}

	public static String[] getSplittedString(String s) {
		// split and remove empty substring

		// split
		String[] arr = s.split("[^A-Za-z0-9]");

		// get only non-empty substring
		ArrayList<String> arrlist = new ArrayList<>();
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].length() > 0)
				arrlist.add(arr[i]);
		}

		// convert back to array
		arr = new String[arrlist.size()];
		arrlist.toArray(arr);

		return arr;
	}

}
