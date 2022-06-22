package support.stringmatching;

import java.util.ArrayList;
import java.util.Arrays;

public class StringAlignment {
	// taken and adapted from
	// https://www.geeksforgeeks.org/sequence-alignment-problem/
	public static String[] getAlignedStrings(String x, String y, int pxy,
			int pgap) {
		int i, j; // intialising variables

		int m = x.length(); // length of gene1
		int n = y.length(); // length of gene2

		// table for storing optimal
		// substructure answers
		int dp[][] = new int[n + m + 1][n + m + 1];

		for (int[] x1 : dp)
			Arrays.fill(x1, 0);

		// intialising the table
		for (i = 0; i <= (n + m); i++) {
			dp[i][0] = i * pgap;
			dp[0][i] = i * pgap;
		}

		// calculating the minimum penalty
		for (i = 1; i <= m; i++) {
			for (j = 1; j <= n; j++) {
				if (x.charAt(i - 1) == y.charAt(j - 1)) {
					dp[i][j] = dp[i - 1][j - 1];
				} else {
					dp[i][j] = Math.min(
							Math.min(dp[i - 1][j - 1] + pxy, dp[i - 1][j]
									+ pgap), dp[i][j - 1] + pgap);
				}
			}
		}

		// Reconstructing the solution
		int l = n + m; // maximum possible length

		i = m;
		j = n;

		int xpos = l;
		int ypos = l;

		// Final answers for
		// the respective strings
		int xans[] = new int[l + 1];
		int yans[] = new int[l + 1];

		while (!(i == 0 || j == 0)) {
			if (x.charAt(i - 1) == y.charAt(j - 1)) {
				xans[xpos--] = (int) x.charAt(i - 1);
				yans[ypos--] = (int) y.charAt(j - 1);
				i--;
				j--;
			} else if (dp[i - 1][j - 1] + pxy == dp[i][j]) {
				xans[xpos--] = (int) x.charAt(i - 1);
				yans[ypos--] = (int) y.charAt(j - 1);
				i--;
				j--;
			} else if (dp[i - 1][j] + pgap == dp[i][j]) {
				xans[xpos--] = (int) x.charAt(i - 1);
				yans[ypos--] = (int) '_';
				i--;
			} else if (dp[i][j - 1] + pgap == dp[i][j]) {
				xans[xpos--] = (int) '_';
				yans[ypos--] = (int) y.charAt(j - 1);
				j--;
			}
		}
		while (xpos > 0) {
			if (i > 0)
				xans[xpos--] = (int) x.charAt(--i);
			else
				xans[xpos--] = (int) '_';
		}
		while (ypos > 0) {
			if (j > 0)
				yans[ypos--] = (int) y.charAt(--j);
			else
				yans[ypos--] = (int) '_';
		}

		// Since we have assumed the
		// answer to be n+m long,
		// we need to remove the extra
		// gaps in the starting id
		// represents the index from
		// which the arrays xans,
		// yans are useful
		int id = 1;
		for (i = l; i >= 1; i--) {
			if ((char) yans[i] == '_' && (char) xans[i] == '_') {
				id = i + 1;
				break;
			}
		}

		// put the final answer as strings
		String s1 = "";
		for (i = id; i <= l; i++) {
			s1 += (char) xans[i];
		}
		String s2 = "";
		for (i = id; i <= l; i++) {
			s2 += (char) yans[i];
		}
		return new String[] { s1, s2 };
	}
	
	// modified from the above method so that it can compare array of tokens / words
	public static ArrayList<ArrayList<String>> getAlignedStrings(String[] x, String[] y, int pxy,
			int pgap) {
		int i, j; // intialising variables

		int m = x.length; // length of gene1
		int n = y.length; // length of gene2

		// table for storing optimal
		// substructure answers
		int dp[][] = new int[n + m + 1][n + m + 1];

		for (int[] x1 : dp)
			Arrays.fill(x1, 0);

		// intialising the table
		for (i = 0; i <= (n + m); i++) {
			dp[i][0] = i * pgap;
			dp[0][i] = i * pgap;
		}

		// calculating the minimum penalty
		for (i = 1; i <= m; i++) {
			for (j = 1; j <= n; j++) {
				if (x[i - 1].equals(y[j - 1])) {
					dp[i][j] = dp[i - 1][j - 1];
				} else {
					dp[i][j] = Math.min(
							Math.min(dp[i - 1][j - 1] + pxy, dp[i - 1][j]
									+ pgap), dp[i][j - 1] + pgap);
				}
			}
		}

		// Reconstructing the solution
		int l = n + m; // maximum possible length

		i = m;
		j = n;

		int xpos = l;
		int ypos = l;

		// Final answers for
		// the respective strings
		int xans[] = new int[l + 1];
		int yans[] = new int[l + 1];

		while (!(i == 0 || j == 0)) {
			if (x[i - 1].equals(y[j - 1])) {
				xans[xpos--] = x[i - 1].hashCode();
				yans[ypos--] = y[j - 1].hashCode();
				i--;
				j--;
			} else if (dp[i - 1][j - 1] + pxy == dp[i][j]) {
				xans[xpos--] = x[i - 1].hashCode();
				yans[ypos--] = y[j - 1].hashCode();
				i--;
				j--;
			} else if (dp[i - 1][j] + pgap == dp[i][j]) {
				xans[xpos--] = x[i - 1].hashCode();
				yans[ypos--] = "_".hashCode();
				i--;
			} else if (dp[i][j - 1] + pgap == dp[i][j]) {
				xans[xpos--] = "_".hashCode();
				yans[ypos--] = y[j - 1].hashCode();
				j--;
			}
		}
		while (xpos > 0) {
			if (i > 0)
				xans[xpos--] = x[--i].hashCode();
			else
				xans[xpos--] = "_".hashCode();
		}
		while (ypos > 0) {
			if (j > 0)
				yans[ypos--] = y[--j].hashCode();
			else
				yans[ypos--] = "_".hashCode();
		}

		// Since we have assumed the
		// answer to be n+m long,
		// we need to remove the extra
		// gaps in the starting id
		// represents the index from
		// which the arrays xans,
		// yans are useful
		int id = 1;
		for (i = l; i >= 1; i--) {
			if (yans[i] == "_".hashCode() && xans[i] == "_".hashCode()) {
				id = i + 1;
				break;
			}
		}

		// put the final answer as arraylists of string
		ArrayList<String> s1 = new ArrayList<>();
		int arrIdx = 0;
		for (i = id; i <= l; i++) {
			if(xans[i] == "_".hashCode())
				s1.add("_");
			else{
				s1.add(x[arrIdx]);
				arrIdx++;
			}
		}
		
		ArrayList<String> s2 = new ArrayList<>();
		arrIdx = 0;
		for (i = id; i <= l; i++) {
			if(yans[i] == "_".hashCode())
				s2.add("_");
			else{
				s2.add(y[arrIdx]);
				arrIdx++;
			}
		}
		
		ArrayList<ArrayList<String>> result = new ArrayList<>();
		result.add(s1);
		result.add(s2);
		
		return result;
	}

}
