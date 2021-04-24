package p3.feedbackgenerator.comparison;

public class ComparisonPairTuple implements Comparable<ComparisonPairTuple> {
	private String codePath1, codePath2, assignmentName1, assignmentName2;
	private double[] simResults;

	public ComparisonPairTuple(String codePath1, String codePath2,
			String assignmentName1, String assignmentName2, double[] simResults) {
		super();
		this.codePath1 = codePath1;
		this.codePath2 = codePath2;
		this.assignmentName1 = assignmentName1;
		this.assignmentName2 = assignmentName2;
		this.simResults = simResults;
	}

	public String getCodePath1() {
		return codePath1;
	}

	public void setCodePath1(String codePath1) {
		this.codePath1 = codePath1;
	}

	public String getCodePath2() {
		return codePath2;
	}

	public void setCodePath2(String codePath2) {
		this.codePath2 = codePath2;
	}

	public String getAssignmentName1() {
		return assignmentName1;
	}

	public String getAssignmentName2() {
		return assignmentName2;
	}

	public double[] getSimResults() {
		return simResults;
	}

	public void setSimResults(double[] simResults) {
		this.simResults = simResults;
	}

	public double getAvgSyntax() {
		return simResults[0];
	}

	public double getMaxSyntax() {
		return simResults[1];
	}

	public double getAvgComment() {
		return simResults[2];
	}

	public double getMaxComment() {
		return simResults[3];
	}

	@Override
	public int compareTo(ComparisonPairTuple arg0) {
		// sort based on avg sim in descending order
		return (int) ((- getAvgSyntax() + arg0.getAvgSyntax()) * 100000);
	}

	public String toString() {
		return assignmentName1 + " " + assignmentName2 + " " + getAvgSyntax() + " "
				+ getMaxSyntax() + " " + getAvgComment() + " "
				+ getMaxComment();
	}
}
