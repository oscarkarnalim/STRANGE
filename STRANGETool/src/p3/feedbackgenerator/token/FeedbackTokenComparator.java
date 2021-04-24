package p3.feedbackgenerator.token;

import java.util.Comparator;

public class FeedbackTokenComparator implements Comparator<FeedbackToken> {

	@Override
	public int compare(FeedbackToken arg0, FeedbackToken arg1) {
		// TODO Auto-generated method stub
		if (arg0.getStartRow() != arg1.getStartRow())
			return arg0.getStartRow() - arg1.getStartRow();
		else
			return arg0.getStartCol() - arg1.getStartCol();
	}

}
