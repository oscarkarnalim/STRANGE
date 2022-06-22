package p3.feedbackgenerator.message;

import java.util.ArrayList;

import p3.feedbackgenerator.token.FeedbackToken;

public class MultipleFeedbackMessage extends FeedbackMessage{
	// to store a message resulted from comparing two lists of feedback tokens
	
	// involved lists of tokens
	protected ArrayList<FeedbackToken> tokenList1;
	protected ArrayList<FeedbackToken> tokenList2;

	public MultipleFeedbackMessage(String action, String disguiseTarget, ArrayList<FeedbackToken> tokenList1, ArrayList<FeedbackToken> tokenList2) {
		super(action, disguiseTarget);
		// TODO Auto-generated constructor stub
		this.tokenList1 = tokenList1;
		this.tokenList2 = tokenList2;
	}

	public ArrayList<FeedbackToken> getTokenList1() {
		return tokenList1;
	}

	public void setTokenList1(ArrayList<FeedbackToken> tokenList1) {
		this.tokenList1 = tokenList1;
	}

	public ArrayList<FeedbackToken> getTokenList2() {
		return tokenList2;
	}

	public void setTokenList2(ArrayList<FeedbackToken> tokenList2) {
		this.tokenList2 = tokenList2;
	}

	@Override
	public int getStartRowCode1() {
		// TODO Auto-generated method stub
		return tokenList1.get(0).getStartRow();
	}

	@Override
	public int getStartColCode1() {
		// TODO Auto-generated method stub
		return tokenList1.get(0).getStartCol();
	}

	@Override
	public int getFinishRowCode1() {
		// TODO Auto-generated method stub
		return tokenList1.get(tokenList1.size()-1).getFinishRow();
	}

	@Override
	public int getFinishColCode1() {
		// TODO Auto-generated method stub
		return tokenList1.get(tokenList1.size()-1).getFinishCol();
	}

	@Override
	public int getStartRowCode2() {
		// TODO Auto-generated method stub
		return tokenList2.get(0).getStartRow();
	}

	@Override
	public int getStartColCode2() {
		// TODO Auto-generated method stub
		return tokenList2.get(0).getStartCol();
	}

	@Override
	public int getFinishRowCode2() {
		// TODO Auto-generated method stub
		return tokenList2.get(tokenList2.size()-1).getFinishRow();
	}

	@Override
	public int getFinishColCode2() {
		// TODO Auto-generated method stub
		return tokenList2.get(tokenList2.size()-1).getFinishCol();
	}

	@Override
	public int getNumOfCoveredTokens() {
		// TODO Auto-generated method stub
		return tokenList1.size();
	}


}
