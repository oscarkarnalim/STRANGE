package p3.feedbackgenerator.message;

import java.util.ArrayList;

public class MismatchSyntaxMessage implements Comparable<MismatchSyntaxMessage> {
	// stored as a list to record their various positions.
	protected ArrayList<StandardFeedbackMessage> mismatchTokenMessages;

	public MismatchSyntaxMessage() {
		super();
		this.mismatchTokenMessages = new ArrayList<>();
	}

	public ArrayList<StandardFeedbackMessage> getMismatchTokenMessages() {
		return mismatchTokenMessages;
	}

	public void setMismatchTokenMessages(
			ArrayList<StandardFeedbackMessage> mismatchTokenMessage) {
		this.mismatchTokenMessages = mismatchTokenMessage;
	}

	public int getOccurrenceFrequency() {
		return mismatchTokenMessages.size();
	}

	@Override
	public int compareTo(MismatchSyntaxMessage arg0) {
		// TODO Auto-generated method stub
		if (this.getOccurrenceFrequency() == arg0.getOccurrenceFrequency()) {
			// compare in alphabetical way
			if (this.getMismatchTokenMessages().get(0).getStartRowCode1() != arg0
					.getMismatchTokenMessages().get(0).getStartRowCode1()) {
				return this.getMismatchTokenMessages().get(0)
						.getStartRowCode1()
						- arg0.getMismatchTokenMessages().get(0)
								.getStartRowCode1();
			} else {
				return this.getMismatchTokenMessages().get(0)
						.getStartColCode1()
						- arg0.getMismatchTokenMessages().get(0)
								.getStartColCode1();
			}
		} else
			// otherwise compare based on importance
			return -this.getOccurrenceFrequency() + arg0.getOccurrenceFrequency();
	}

	public static int indexOfAList(ArrayList<MismatchSyntaxMessage> list,
			String disguiseTarget, String mismatchContent1,
			String mismatchContent2) {
		/*
		 * search from a given list whether there is a tuple with the same
		 * values as given on the parameters.
		 */
		for (int i = 0; i < list.size(); i++) {
			StandardFeedbackMessage cur = list.get(i)
					.getMismatchTokenMessages().get(0);
			if (cur.getDisguiseTarget().equals(disguiseTarget)
					&& cur.getToken1().getContent().equals(mismatchContent1)
					&& cur.getToken2().getContent().equals(mismatchContent2)) {
				return i;
			}
		}

		return -1;
	}
}
