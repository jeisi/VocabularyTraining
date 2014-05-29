package com.xrea.s7.jeisi.vocabularytraining.sentenceframe.model;

import java.util.ArrayList;

import com.xrea.s7.jeisi.vocabularytraining.main.WordInfo;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.model.WordFrameModel;
import com.xrea.s7.jeisi.vocabularytraining.wordsorder.ListeningFilter;
import com.xrea.s7.jeisi.vocabularytraining.wordsorder.WordsOrderFilter;

public class ListeningSentenceFrameModel extends WordFrameModel {

	public ListeningSentenceFrameModel() {
		super();
	}

	public ListeningSentenceFrameModel(WordInfo wordInfo) {
		super(wordInfo);
	}
	
	public ListeningSentenceFrameModel(ArrayList<WordInfo> loadedWords) {
		super(loadedWords);
	}
	
	@Override
	protected String getWordsFileName() {
		return "sentence.yaml";
	}

	@Override
	protected String getOrderFileName() {
		return "listening_sentence_order.yaml";
	}

	@Override
	protected WordsOrderFilter getFilter() {
		return new ListeningFilter();
	}

}
