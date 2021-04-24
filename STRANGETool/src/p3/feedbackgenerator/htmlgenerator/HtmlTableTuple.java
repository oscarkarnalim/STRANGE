package p3.feedbackgenerator.htmlgenerator;

import java.util.ArrayList;

import p3.feedbackgenerator.message.CommentFeedbackMessage;
import p3.feedbackgenerator.message.FeedbackMessage;
import p3.feedbackgenerator.message.SyntaxFeedbackMessage;
import p3.feedbackgenerator.token.FeedbackToken;

public class HtmlTableTuple implements Comparable<HtmlTableTuple> {
	protected FeedbackMessage entity;
	protected int importanceScore;
	// to store how many matched characters
	protected int minCharacterLength;
	// to store how many matched tokens
	protected int matchedTokenLength;

	public HtmlTableTuple(FeedbackMessage entity) {
		super();
		this.entity = entity;
		calculateImportanceScoreAndSetMatches();
	}

	private void calculateImportanceScoreAndSetMatches() {
		// simply count how many characters involved.
		importanceScore = 0;
		if (entity instanceof CommentFeedbackMessage) {
			// if comment, each character is weighted double as it is more
			// suspicious
			CommentFeedbackMessage cf = (CommentFeedbackMessage) entity;
			importanceScore = 2 * (cf.getToken1().getContent().length() + cf
					.getToken2().getContent().length());
			minCharacterLength = Math.min(cf.getToken1().getContent()
					.length(), cf.getToken2().getContent().length());
			matchedTokenLength = 1;
		} else if (entity instanceof SyntaxFeedbackMessage) {
			// if syntax match
			SyntaxFeedbackMessage sf = (SyntaxFeedbackMessage) entity;
			// set the number of char as 0
			int minCharacterLength1 = 0;
			int minCharacterLength2 = 0;
			// get the lists
			ArrayList<FeedbackToken> list1 = sf.getTokenList1();
			ArrayList<FeedbackToken> list2 = sf.getTokenList2();
			for (int i = 0; i < list1.size(); i++) {
				FeedbackToken ft1 = list1.get(i);
				FeedbackToken ft2 = list2.get(i);
				// set importance score
				importanceScore += ft1.getContent().length();
				minCharacterLength1 += ft1.getContent().length();
				importanceScore += ft2.getContent().length();
				minCharacterLength2 += ft2.getContent().length();
			}
			// set the number of min char
			minCharacterLength = Math.min(minCharacterLength1, minCharacterLength2);
			// set the number of matched token
			matchedTokenLength = list1.size();
		}
	}

	@Override
	public int compareTo(HtmlTableTuple arg0) {
		// TODO Auto-generated method stub
		return -this.getImportanceScore() + arg0.getImportanceScore();
	}

	public FeedbackMessage getEntity() {
		return entity;
	}

	public void setEntity(FeedbackMessage entity) {
		this.entity = entity;
	}

	public int getImportanceScore() {
		return importanceScore;
	}

	public void setImportanceScore(int importanceScore) {
		this.importanceScore = importanceScore;
	}

	public int getMinCharacterLength() {
		return minCharacterLength;
	}

	public int getMatchedTokenLength() {
		return matchedTokenLength;
	}

}
