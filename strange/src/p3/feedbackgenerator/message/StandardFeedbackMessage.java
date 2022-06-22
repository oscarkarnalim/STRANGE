package p3.feedbackgenerator.message;

import p3.feedbackgenerator.token.FeedbackToken;

public class StandardFeedbackMessage extends FeedbackMessage {
	// to store a message resulted from comparing two feedback tokens
	
	// involved tokens
	protected FeedbackToken token1;
	protected FeedbackToken token2;

	public StandardFeedbackMessage(String action, String disguiseTarget,
			FeedbackToken token1, FeedbackToken token2) {
		super(action, disguiseTarget);
		this.token1 = token1;
		this.token2 = token2;
	}

	public FeedbackToken getToken1() {
		return token1;
	}

	public void setToken1(FeedbackToken token1) {
		this.token1 = token1;
	}

	public FeedbackToken getToken2() {
		return token2;
	}

	public void setToken2(FeedbackToken token2) {
		this.token2 = token2;
	}

	@Override
	public int getStartRowCode1() {
		// TODO Auto-generated method stub
		return token1.getStartRow();
	}

	@Override
	public int getStartColCode1() {
		// TODO Auto-generated method stub
		return token1.getStartCol();
	}

	@Override
	public int getFinishRowCode1() {
		// TODO Auto-generated method stub
		return token1.getFinishRow();
	}

	@Override
	public int getFinishColCode1() {
		// TODO Auto-generated method stub
		return token1.getFinishCol();
	}

	@Override
	public int getStartRowCode2() {
		// TODO Auto-generated method stub
		return token2.getStartRow();
	}

	@Override
	public int getStartColCode2() {
		// TODO Auto-generated method stub
		return token2.getStartCol();
	}

	@Override
	public int getFinishRowCode2() {
		// TODO Auto-generated method stub
		return token2.getFinishRow();
	}

	@Override
	public int getFinishColCode2() {
		// TODO Auto-generated method stub
		return token2.getFinishCol();
	}

	@Override
	public int getNumOfCoveredTokens() {
		// TODO Auto-generated method stub
		return 1;
	}
}
