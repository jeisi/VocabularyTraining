package com.xrea.s7.jeisi.vocabularytraining.phraseframe;

import com.xrea.s7.jeisi.vocabularytraining.main.VocabularyTraining;
import com.xrea.s7.jeisi.vocabularytraining.phraseframe.model.ListeningPhraseFrameModel;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.ListeningWordFrame;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.WordFrame;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.WordFrameBuilder;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.model.WordFrameModel;

public class ListeningPhraseFrameBuilder extends WordFrameBuilder {

	@Override
	protected WordFrameModel buildModel() {
		return new ListeningPhraseFrameModel();
	}

	@Override
	protected WordFrame buildFrame(VocabularyTraining parent, WordFrameModel model) {
		return new ListeningWordFrame(parent, model) {
			@Override
			public String getIdentifer() {
				return "listening phrase";
			}

			private static final long serialVersionUID = 1L;
		};
	}

}
