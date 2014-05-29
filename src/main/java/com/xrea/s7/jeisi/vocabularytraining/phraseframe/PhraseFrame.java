package com.xrea.s7.jeisi.vocabularytraining.phraseframe;

import com.xrea.s7.jeisi.vocabularytraining.main.VocabularyTraining;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.WordFrame;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.model.WordFrameModel;

public abstract class PhraseFrame extends WordFrame {

	public PhraseFrame(VocabularyTraining parent, WordFrameModel model) {
		super(parent, model);
	}

	private static final long serialVersionUID = 1L;
}
