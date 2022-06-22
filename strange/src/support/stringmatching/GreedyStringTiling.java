package support.stringmatching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import p3.Main;

// adapted from https://github.com/arunjeyapal/GreedyStringTiling
public class GreedyStringTiling {

	// token data
	private static Object[] T;
	private static Object[] P;
	// storing temporary result
	private static ArrayList<Queue<GSTMatchTuple>> matchList;
	// boolean to mark text and pattern
	private static boolean[] markedT;
	private static boolean[] markedP;

	public static String[] convertTokenTupleToString(ArrayList<String> arr) {
		String[] s = new String[arr.size()];
		for (int i = 0; i < arr.size(); i++) {
			s[i] = arr.get(i).toString();
		}
		return s;
	}

	/*
	 * Returns similarity value for token of text A and B using average similarity
	 */
	public static double calcAverageSimilarity(ArrayList<GSTMatchTuple> tiles) {
		double similarity = ((double) (2 * coverage(tiles)) / (double) (P.length + T.length));
		if (P.length + T.length == 0)
			similarity = 0;
		return similarity;
	}

	/*
	 * Returns similarity value for token of text A and B using maximum similarity
	 */
	public static double calcMaximumSimilarity(ArrayList<GSTMatchTuple> tiles) {
		double similarity = ((double) coverage(tiles) / (double) Math.min(P.length, T.length));
		if (P.length == 0 || T.length == 0)
			similarity = 0;
		return similarity;
	}

	/*
	 * Sum of length of all tiles
	 */
	public static int coverage(ArrayList<GSTMatchTuple> tiles) {
		int accu = 0;
		for (int i = 0; i < tiles.size(); i++) {
			GSTMatchTuple tile = tiles.get(i);
			accu += tile.length;
		}
		return accu;
	}

	// get matched tiles
	public static ArrayList<GSTMatchTuple> getMatchedTiles(Object[] s1, Object[] s2, int minimalMatchingLength) {

		P = s1;
		T = s2;
		// renew all data structure needed
		ArrayList<GSTMatchTuple> tiles = new ArrayList<GSTMatchTuple>();
		matchList = new ArrayList<Queue<GSTMatchTuple>>();
		// renew the marker
		markedT = new boolean[T.length];
		markedP = new boolean[P.length];
		// starting from the largest and then divided by two per iteration
		int s = P.length;

		/*
		 * if RKRGST is optimised, s will be assigned with minimal matching length. This
		 * will make the loop below only runs one iteration.
		 */
		if (Main.isRKRGSTOptimised())
			s = minimalMatchingLength;

		while (s >= minimalMatchingLength) {
			scanpattern(s);
			markStrings(s, tiles);
			s = s / 2;
		}
		return tiles;
	}

	public static ArrayList<GSTMatchTuple> getMatchedTilesWithRemainingUnmatchedRegion(Object[] s1, Object[] s2,
			int minimalMatchingLength) {
		/*
		 * This method works similarly as getMatchedTiles except that it should be
		 * executed right after that method and will check the remaining unmatched
		 * regions. The only difference is that it relies on used marker.
		 */
		P = s1;
		T = s2;
		// renew all data structure needed
		ArrayList<GSTMatchTuple> tiles = new ArrayList<GSTMatchTuple>();
		matchList = new ArrayList<Queue<GSTMatchTuple>>();
		int s = P.length;
		
		/*
		 * if RKRGST is optimised, s will be assigned with minimal matching length. This
		 * will make the loop below only runs one iteration.
		 */
		if (Main.isRKRGSTOptimised())
			s = minimalMatchingLength;
		
		while (s >= minimalMatchingLength) {
			scanpattern(s);
			markStrings(s, tiles);
			s = s / 2;
		}
		return tiles;
	}

	/*
	 * Scans the pattern and text string lists for matches. All matches found are
	 * stored in a list of matches in queues.
	 */
	private static void scanpattern(int s) {
		Queue<GSTMatchTuple> queue = new LinkedList<GSTMatchTuple>();
		GSTHashTable hashtable = new GSTHashTable();
		/*
		 * process the text
		 */
		int t = 0;
		while (t < T.length) {
			// continue as long as the current one is marked
			if (markedT[t] == true) {
				t = t + 1;
				continue;
			}

			// calculate distance to the next tile
			int dist = distToNextTile(t, markedT);
			// if the distance <= s
			if (dist <= s) {
				// jump to the next unmarked token after the tile
				t = jumpToNextUnmarkedTokenAfterTile(t, markedT, dist);
			} else {
				// generate hash value and add to hashmap
				StringBuilder sb = new StringBuilder();
				for (int i = t; i <= t + s - 1; i++)
					sb.append(T[i]);
				String substring = sb.toString();
				int h = createKRHashValue(substring);
				hashtable.add(h, t);
				// add 1 to move forward
				t = t + 1;
			}
		}

		/*
		 * process the pattern
		 */
		int p = 0;
		while (p < P.length) {
			// selama yang ditemukan adalah marked, lanjut
			if (markedP[p] == true) {
				p = p + 1;
				continue;
			}

			// calculate distance to next tile
			int dist = distToNextTile(p, markedP);

			// if the distance <= s
			if (dist <= s) {
				// jump to next unmarked token
				p = jumpToNextUnmarkedTokenAfterTile(p, markedP, dist);
			} else {
				// generate hash value
				StringBuilder sb = new StringBuilder();
				for (int i = p; i <= p + s - 1; i++) {
					sb.append(P[i]);
				}
				String substring = sb.toString();
				int h = createKRHashValue(substring);

				// check the values
				ArrayList<Integer> values = hashtable.get(h);
				if (values != null) {
					// per value in values
					for (Integer tt : values) {
						// building value which need to be compared
						StringBuilder newsb = new StringBuilder();
						for (int i = tt; i <= tt + s - 1; i++) {
							newsb.append(T[i]);
						}
						if (newsb.toString().equals(substring)) {
							// if match, starting from s, search longest unmarked sequence
							int k = s;
							while (p + k < P.length && tt + k < T.length && P[p + k].equals(T[tt + k])
									&& markedP[p + k] == false && markedT[tt + k] == false)
								k = k + 1;
							// put to the queue
							GSTMatchTuple mv = new GSTMatchTuple(p, tt, k);
							queue.add(mv);
						}
					}
				}
				// move the pattern
				p += 1;
			}

		}
		if (!queue.isEmpty())
			// add queue to matchlist if that queue is not empty
			matchList.add(queue);
	}

	/*
	 * this method will mark matched tokens
	 */
	private static void markStrings(int s, ArrayList<GSTMatchTuple> tiles) {
		for (Queue<GSTMatchTuple> queue : matchList) {
			while (!queue.isEmpty()) {
				GSTMatchTuple match = queue.poll();
				if (!isOccluded(match, tiles)) {
					for (int j = 0; j < match.length; j++) {
						markedP[match.patternPosition + j] = true;
						markedT[match.textPosition + j] = true;
					}
					tiles.add(match);
				}
			}
		}
		matchList = new ArrayList<Queue<GSTMatchTuple>>();
	}

	/*
	 * Creates a Karp-Rabin Hash Value for the given substring and returns it. Based
	 * on: http://www-igm.univ-mlv.fr/~lecroq/string/node5.html
	 */

	private static int createKRHashValue(String substring) {
		int hashValue = 0;
		for (int i = 0; i < substring.length(); i++)
			hashValue = ((hashValue << 1) + (int) substring.charAt(i));
		return hashValue;
	}

	/*
	 * Returns true if the match is already occluded by another match in the tiles
	 * list. "Note that "not occluded" is taken to mean that none of the tokens Pp
	 * to Pp+maxmatch-1 and Tt to Tt+maxmatch-1 has been marked during the creation
	 * of an earlier tile. However, given that smaller tiles cannot be created
	 * before larger ones, it suffices that only the ends of each new putative tile
	 * be testet for occlusion, rather than the whole maxmimal match." [
	 * "String Similarity via Greedy String Tiling and Running Karp-Rabin Matching"
	 * http://www.pam1.bcs.uwa.edu.au/~michaelw/ftp/doc/RKR_GST.ps]
	 */
	private static boolean isOccluded(GSTMatchTuple match, ArrayList<GSTMatchTuple> tiles) {
		if (tiles.equals(null) || tiles == null || tiles.size() == 0)
			return false;
		for (GSTMatchTuple matches : tiles) {
			int x1p = match.patternPosition;
			int x2p = x1p + match.length - 1;
			int y1p = matches.patternPosition;
			int y2p = y1p + matches.length - 1;
			int x1t = match.textPosition;
			int x2t = x1t + match.length - 1;
			int y1t = matches.textPosition;
			int y2t = y1t + matches.length - 1;
			if (isOverlap(x1p, x2p, y1p, y2p) || isOverlap(x1t, x2t, y1t, y2t))
				return true;
		}
		return false;
	}

	private static boolean isOverlap(int x1, int x2, int y1, int y2) {
		return (x1 >= y1 && x1 <= y2) || (x2 >= y1 && x2 <= y2) || (y1 >= x1 && y1 <= x2) || (y2 >= x1 && y2 <= x2);
	}

	/*
	 * Returns distance to next tile, i.e. to next marked token. If no tile was
	 * found, it returns None. case 1: there is a next tile -> pos + dist = first
	 * marked token -> return dist case 2: there is no next tile -> pos + dist =
	 * len(stringList) -> return None dist is also number of unmarked token 'til
	 * next tile
	 */
	private static int distToNextTile(int pos, boolean[] markedList) {
		// jika sudah sampe ujung, return -1
		if (pos == markedList.length)
			return markedList.length - pos;
		else {
			int dist = 0;
			while (pos + dist + 1 < markedList.length && markedList[pos + dist + 1] == false)
				dist += 1;
			if (pos + dist + 1 == markedList.length)
				return markedList.length - pos;
			else
				return dist + 1;
		}
	}

	/*
	 * Returns the first position of an unmarked token after the next tile. case 1:
	 * -> normal case -> tile exists -> there is an unmarked token after the tile
	 * case 2: -> tile exists -> but NO unmarked token after the tile case 3: -> NO
	 * tile exists
	 */
	private static int jumpToNextUnmarkedTokenAfterTile(int pos, boolean[] markedList, int dist) {
		pos = pos + dist;
		while (pos + 1 < markedList.length && (markedList[pos + 1] == true))
			pos = pos + 1;
		if (pos + 1 == markedList.length)
			return markedList.length;
		else
			return pos + 1;
	}
}
