package com.xrea.s7.jeisi.vocabularytraining.phraseframe.model;

import java.util.ArrayList;

import com.xrea.s7.jeisi.vocabularytraining.main.WordInfo;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.model.WordFrameModel;
import com.xrea.s7.jeisi.vocabularytraining.wordsorder.AllAcceptFilter;
import com.xrea.s7.jeisi.vocabularytraining.wordsorder.WordsOrderFilter;

public class PhraseFrameModel extends WordFrameModel {

	public PhraseFrameModel() {
		super();
	}

	public PhraseFrameModel(WordInfo wordInfo) {
		super(wordInfo);
	}

	public PhraseFrameModel(ArrayList<WordInfo> wordInfoList) {
		super(wordInfoList);
	}

	@Override
	protected String getWordsFileName() {
		return "words.yaml";
	}
	
	@Override
	protected String getOrderFileName() {
		return "wordsorder.yaml";
	}

	@Override
	protected WordsOrderFilter getFilter() {
		return new AllAcceptFilter();
	}
}
