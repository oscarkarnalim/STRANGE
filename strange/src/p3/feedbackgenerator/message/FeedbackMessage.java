package p3.feedbackgenerator.message;

public abstract class FeedbackMessage implements Comparable<FeedbackMessage>{
	// either removed, added, modified, or copied
	protected String action;
	// describe which part of code is targeted for this disguise
	protected String disguiseTarget;
	// set the id of message for visualisation
	protected String visualId;
	
	public FeedbackMessage(String action, String disguiseTarget) {
		super();
		this.action = action;
		this.disguiseTarget = disguiseTarget;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String type) {
		this.action = type;
	}

	public String getDisguiseTarget() {
		return disguiseTarget;
	}

	public void setDisguiseTarget(String disguiseTarget) {
		this.disguiseTarget = disguiseTarget;
	}
	
	public String getVisualId() {
		return visualId;
	}

	public void setVisualId(String visualId) {
		this.visualId = visualId;
	}

	public abstract int getStartRowCode1();
	public abstract int getStartColCode1();
	public abstract int getFinishRowCode1();
	public abstract int getFinishColCode1();

	public abstract int getStartRowCode2();
	public abstract int getStartColCode2();
	public abstract int getFinishRowCode2();
	public abstract int getFinishColCode2();
	public abstract int getNumOfCoveredTokens();
	
	public int compareTo(FeedbackMessage f){
		if(this.getStartRowCode1() == f.getStartRowCode1()){
			return this.getStartColCode1()-f.getStartColCode1();
		}
		else{
			return this.getStartRowCode1()-f.getStartRowCode1();
		}
	}
	
	public String toStringWithPosition() {
		if (action.equals("removed"))
			return (disguiseTarget + "(s) on the first code from line "
					+ getStartRowCode1() + " column " + getStartColCode1()
					+ " to line " + getFinishRowCode1() + " column " + getFinishColCode1()
					+ " only occurs on that code.");
		else if (action.equals("added"))
			return (disguiseTarget + "(s) on the second code from line "
					+ getStartRowCode2() + " column " + getStartColCode2()
					+ " to line " + getFinishRowCode2() + " column " + getFinishColCode2()
					+ " only occurs on that code.");
		else if (action.equals("copied"))
			return (disguiseTarget
					+ "(s) on the first code from line "
					+ getStartRowCode1() + " column " + getStartColCode1()
					+ " to line " + getFinishRowCode1() + " column " + getFinishColCode1()
					+ " occurs with the same form on the second code (see from line "
					+ getStartRowCode2() + " column " + getStartColCode2()
					+ " to line " + getFinishRowCode2() + " column " + getFinishColCode2()
					+ ").");
		else if (action.equals("modified"))
			return (disguiseTarget
					+ "(s) on the first code from line "
					+ getStartRowCode1() + " column " + getStartColCode1()
					+ " to line " + getFinishRowCode1() + " column " + getFinishColCode1()
					+ " occurs with some modification on the second code (see from line "
					+ getStartRowCode2() + " column " + getStartColCode2()
					+ " to line " + getFinishRowCode2() + " column " + getFinishColCode2()
					+ ").");
		else
			return "undefined";
	}
	
}
