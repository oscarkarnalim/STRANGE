package p3.feedbackgenerator.htmlgenerator;

import java.util.Comparator;

import p3.feedbackgenerator.message.FeedbackMessage;

public class Code2FeedbackComparator implements Comparator<FeedbackMessage> {

	@Override
	public int compare(FeedbackMessage arg0, FeedbackMessage arg1) {
		// TODO Auto-generated method stub
		if (arg0.getStartRowCode2() != arg1.getStartRowCode2())
			return arg0.getStartRowCode2() - arg1.getStartRowCode2();
		else
			return arg0.getStartColCode2() - arg1.getStartColCode2();
	}

}
