package p3.feedbackgenerator.language.python;


public class TokenTuple implements Comparable<TokenTuple> {
		public String term;
		public double freq;

		public TokenTuple(String term, double freq) {
			super();
			this.term = term;
			this.freq = freq;
		}

		public String toString() {
			return term + " " + freq;
		}

		@Override
		public int compareTo(TokenTuple arg0) {
			// TODO Auto-generated method stub
			int score = (int) ((this.freq - arg0.freq) * 100000);
			return -1 * score;
		}
}
