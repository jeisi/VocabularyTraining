package com.xrea.s7.jeisi.vocabularytraining.wordsorder;

import com.xrea.s7.jeisi.vocabularytraining.main.WordInfo;

public interface WordsOrderFilter {
	boolean accept(WordInfo info);
}
