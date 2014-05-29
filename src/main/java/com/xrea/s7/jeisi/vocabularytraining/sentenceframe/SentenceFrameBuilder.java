package com.xrea.s7.jeisi.vocabularytraining.sentenceframe;

import com.xrea.s7.jeisi.vocabularytraining.main.VocabularyTraining;
import com.xrea.s7.jeisi.vocabularytraining.phraseframe.PhraseFrame;
import com.xrea.s7.jeisi.vocabularytraining.sentenceframe.model.SentenceFrameModel;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.WordFrame;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.WordFrameBuilder;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.model.WordFrameModel;

public class SentenceFrameBuilder extends WordFrameBuilder {

	@Override
	protected WordFrameModel buildModel() {
		return new SentenceFrameModel();
	}

	@Override
	protected WordFrame buildFrame(VocabularyTraining parent, WordFrameModel model) {
		return new PhraseFrame(parent, model) {
			@Override
			public String getIdentifer() {
				return "sentence";
			}

			private static final long serialVersionUID = 1L;
		};
	}

}
