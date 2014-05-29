package com.xrea.s7.jeisi.vocabularytraining.wordsorder;

import com.xrea.s7.jeisi.vocabularytraining.main.WordInfo;

public class ListeningFilter implements WordsOrderFilter {

	@Override
	public boolean accept(WordInfo info) {
		if(info.getMp3() != null) {
			return true;
		} else {
			return false;
		}
	}

}
