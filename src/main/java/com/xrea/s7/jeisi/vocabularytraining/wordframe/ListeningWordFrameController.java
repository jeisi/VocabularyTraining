package com.xrea.s7.jeisi.vocabularytraining.wordframe;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.xrea.s7.jeisi.vocabularytraining.wordframe.model.WordFrameModel;

public class ListeningWordFrameController extends WordFrameController {

	public ListeningWordFrameController(WordFrame view, WordFrameModel model) {
		super(view, model);
	}

	public ActionListener createShowJapaneseButtonActionListener() {
		return new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent arg0) {
				model.hintUsed();
			}
		};
	}
	
	@Override
	public ActionListener createVoiceButtonActionListener() {
		return null;
	}
}
