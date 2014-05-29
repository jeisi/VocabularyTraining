package com.xrea.s7.jeisi.vocabularytraining.phraseframe;

import com.xrea.s7.jeisi.vocabularytraining.main.VocabularyTraining;
import com.xrea.s7.jeisi.vocabularytraining.phraseframe.model.PhraseFrameModel;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.WordFrame;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.WordFrameBuilder;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.model.WordFrameModel;

public class PhraseFrameBuilder extends WordFrameBuilder {

	@Override
	protected WordFrameModel buildModel() {
		return new PhraseFrameModel();
	}

	@Override
	protected WordFrame buildFrame(VocabularyTraining parent, WordFrameModel model) {
		return new PhraseFrame(parent, model) {
			@Override
			public String getIdentifer() {
				return "phrase";
			}

			private static final long serialVersionUID = 1L;
		};
	}

}
