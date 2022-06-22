package support.stringmatching;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import p3.feedbackgenerator.token.FeedbackToken;

public class AdditionalKeywordsManager {
	public static int indexOf(int pos, ArrayList<FeedbackToken> tokenString,
			ArrayList<ArrayList<String>> additionalKeywords) {
		/*
		 * This method will return the position of a keyword if any. The keyword
		 * can contain more than one token but the tokens should be separated by
		 * space. Additional keywords should be sorted from the most specific to
		 * general.
		 */
		
		// if no additionalKeywords provided, return not found
		if(additionalKeywords == null)
			return -1;

		for (int i = 0; i < additionalKeywords.size(); i++) {
			ArrayList<String> cur = additionalKeywords.get(i);

			// check whether it is a match
			boolean isMatch = true;
			for (int j = 0; j < cur.size(); j++) {
				String text = tokenString.get(pos + j).getContent();

				if (text.equals(cur.get(j)) == false) {
					// once a pair is mismatched, break the loop
					isMatch = false;
					break;
				}

			}
			
			// if match, return the starting position
			if (isMatch)
				return i;
		}

		return -1;
	}

	public static ArrayList<ArrayList<String>> readAdditionalKeywords(
			String filepath) {
		/*
		 * This method reads a file and returns the additional keywords. Do
		 * nothing if the filepath is null.
		 */
		ArrayList<ArrayList<String>> additionalKeywords = new ArrayList<>();
		
		// if no keywords are given
		if(filepath == null)
			return additionalKeywords;

		try {
			BufferedReader bf = new BufferedReader(new FileReader(new File(
					filepath)));

			String st;
			while ((st = bf.readLine()) != null) {

				// if it is not an empty line
				if (st.length() > 0) {
					// split to an array
					String[] keywordArr = st.trim().split(" ");

					// store it as an arraylist
					ArrayList<String> keyword = new ArrayList<>();
					for (String s : keywordArr) {
						keyword.add(s);
					}

					// add it as a keyword
					additionalKeywords.add(keyword);
				}
			}

			bf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return additionalKeywords;
	}

}
