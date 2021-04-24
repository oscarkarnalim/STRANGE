package p3.feedbackgenerator.htmlgenerator;

import java.util.Comparator;

import p3.feedbackgenerator.message.FeedbackMessage;

public class Code1FeedbackComparator implements Comparator<FeedbackMessage> {

	@Override
	public int compare(FeedbackMessage arg0, FeedbackMessage arg1) {
		// TODO Auto-generated method stub
		if (arg0.getStartRowCode1() != arg1.getStartRowCode1())
			return arg0.getStartRowCode1() - arg1.getStartRowCode1();
		else
			return arg0.getStartColCode1() - arg1.getStartColCode1();
	}

}
