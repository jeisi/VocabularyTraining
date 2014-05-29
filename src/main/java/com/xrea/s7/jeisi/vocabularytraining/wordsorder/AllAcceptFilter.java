package com.xrea.s7.jeisi.vocabularytraining.wordsorder;

import com.xrea.s7.jeisi.vocabularytraining.main.WordInfo;

public class AllAcceptFilter implements WordsOrderFilter {

	@Override
	public boolean accept(WordInfo info) {
		return true;
	}

}
