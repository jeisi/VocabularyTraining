package com.xrea.s7.jeisi.vocabularytraining.phraseframe.model;

import java.util.ArrayList;

import com.xrea.s7.jeisi.vocabularytraining.main.WordInfo;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.model.WordFrameModel;
import com.xrea.s7.jeisi.vocabularytraining.wordsorder.ListeningFilter;
import com.xrea.s7.jeisi.vocabularytraining.wordsorder.WordsOrderFilter;

public class ListeningPhraseFrameModel extends WordFrameModel {

	public ListeningPhraseFrameModel() {
		super();
	}

	public ListeningPhraseFrameModel(ArrayList<WordInfo> wordInfoList) {
		super(wordInfoList);
	}

	@Override
	protected String getWordsFileName() {
		return "words.yaml";
	}
	
	@Override
	protected String getOrderFileName() {
		return "listening_phrase_order.yaml";
	}

	@Override
	protected WordsOrderFilter getFilter() {
		return new ListeningFilter();
	}

}
