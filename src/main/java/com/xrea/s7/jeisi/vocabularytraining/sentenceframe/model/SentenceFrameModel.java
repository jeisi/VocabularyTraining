package com.xrea.s7.jeisi.vocabularytraining.sentenceframe.model;

import com.xrea.s7.jeisi.vocabularytraining.main.WordInfo;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.model.WordFrameModel;
import com.xrea.s7.jeisi.vocabularytraining.wordsorder.AllAcceptFilter;
import com.xrea.s7.jeisi.vocabularytraining.wordsorder.WordsOrderFilter;

public class SentenceFrameModel extends WordFrameModel {

	public SentenceFrameModel() {
		super();
	}

	public SentenceFrameModel(WordInfo wordInfo) {
		super(wordInfo);
	}
	
	@Override
	protected String getWordsFileName() {
		return "sentence.yaml";
	}

	@Override
	protected String getOrderFileName() {
		return "sentenceorder.yaml";
	}

	@Override
	protected WordsOrderFilter getFilter() {
		return new AllAcceptFilter();
	}
}
