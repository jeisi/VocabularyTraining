package com.xrea.s7.jeisi.vocabularytraining.wordframe;

import com.xrea.s7.jeisi.vocabularytraining.main.VocabularyTraining;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.model.WordFrameModel;

public abstract class WordFrameBuilder {
	public final WordFrame build(VocabularyTraining parent) {
		WordFrameModel model = buildModel();
		model.load(parent.getResourceJar());
		return buildFrame(parent, model);
	}
	
	protected abstract WordFrameModel buildModel();
	protected abstract WordFrame buildFrame(VocabularyTraining parent, WordFrameModel model);
}
