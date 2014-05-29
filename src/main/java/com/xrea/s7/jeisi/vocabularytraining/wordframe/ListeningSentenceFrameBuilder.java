package com.xrea.s7.jeisi.vocabularytraining.wordframe;

import com.xrea.s7.jeisi.vocabularytraining.main.VocabularyTraining;
import com.xrea.s7.jeisi.vocabularytraining.sentenceframe.model.ListeningSentenceFrameModel;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.model.WordFrameModel;

public class ListeningSentenceFrameBuilder extends WordFrameBuilder {
	
	@Override
	protected WordFrameModel buildModel() {
		return new ListeningSentenceFrameModel();
	}
	
	@Override
	protected WordFrame buildFrame(VocabularyTraining parent, WordFrameModel model) {
		return new ListeningWordFrame(parent, model) {
			@Override
			public String getIdentifer() {
				return "listening sentence";
			}

			private static final long serialVersionUID = 1L;
		};
	}
}
