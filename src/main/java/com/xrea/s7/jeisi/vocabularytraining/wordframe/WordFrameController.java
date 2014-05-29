package com.xrea.s7.jeisi.vocabularytraining.wordframe;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.SwingUtilities;

import com.xrea.s7.jeisi.jlib.errordialog.ErrorDialog;
import com.xrea.s7.jeisi.vocabularytraining.main.DelayThread;
import com.xrea.s7.jeisi.vocabularytraining.main.WordInfo;
import com.xrea.s7.jeisi.vocabularytraining.resultdialog.ResultDialog;
import com.xrea.s7.jeisi.vocabularytraining.resultdialog.ResultInfo;
import com.xrea.s7.jeisi.vocabularytraining.resultdialog.ResultType;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.model.WordFrameModel;

public class WordFrameController {

	public WordFrameController(WordFrame view, WordFrameModel model) {
		this.view = view;
		this.model = model;
		
		try {
			model.loadWordsOrder();
		} catch (FileNotFoundException e) {
			new ErrorDialog(null, e, "ファイルがみつかりませんでした").open();
		} catch (IOException e) {
			new ErrorDialog(null, e, "ファイル読込時にエラーが発生しました").open();
		}
		
		model.makeQuestions();
	}
	
	public WindowListener createWindowListener() {
		return new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					model.saveWordsOrder();
				} catch (IOException e1) {
					new ErrorDialog(view, e1, "ファイル書き込み時にエラーが発生しました").open();
				}
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				view.saveLocation();				
			}
		};
	}
	
	public ActionListener createAnswerButtonActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//view.getAnswerButton().requestFocusInWindow();
				//view.getAnswerButton().setEnabled(false);
				view.showSkip();
				
				String viewText = view.englishField.getText();
				String answer = model.getAnswer();
				if(viewText.equals(answer)) {
					String resultMessage = model.getResultMessage(viewText);
					view.showCorrect(resultMessage);
					startAnswerThread(Answer.CORRECT);
				} else {
					view.resultLabel.setText("Sorry. Wrong answer.");
					view.resultLabel.setVisible(true);
					view.resultLabel.setForeground(Color.RED);
					startAnswerThread(Answer.WRONG);
				}
			}
		};
	}
	
	public ActionListener createHintButtonActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.showSkip();
				startHintThread();
			}
		};
	}
	
	public ActionListener createVoiceButtonActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				startAnswerThread(Answer.VOICE_HINT);
			}
		};
	}
	
	public ActionListener createGiveUpActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.resultLabel.setText("Give up.");
				view.resultLabel.setVisible(true);
				view.resultLabel.setForeground(null);
				view.englishField.setText(model.getAnswer());
				view.englishField.setForeground(Color.RED);
				startAnswerThread(Answer.GIVEUP);
			}
		};
	}
	
	public KeyListener createSkipButtonKeyListener() {
		return new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(answerThread != null && answerThread.isAlive()) {
					if(e.getKeyCode() == KeyEvent.VK_SPACE) {
						return;
					}
					
					view.setPrecedeInput(String.valueOf(e.getKeyChar()));
					Runnable runnable = new Runnable() {
						@Override
						public void run() {
							answerThread.interrupt();
						}
					};
					SwingUtilities.invokeLater(runnable);
				}
			}
		};
	}
	
	public ActionListener createSkipButtonActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				answerThread.interrupt();
			}
		};
	}
	
	void startAnswerThread(Answer answer) {
		// この時点の回答文字列を保存。
//		answeredEnglishText = view.getEnglishField().getText();
		
		AnswerEvent event = new AnswerEvent(answer, model.isWronged(), view.getEnglishField().isDabbling());
		answerThread = new DelayThread(event, getAnswerWaitMillSec()) {
			@Override
			protected void exec(Object param) {
				answered((AnswerEvent) param);
			}
		};
		answerThread.start();
	}
	
	void startHintThread() {
		// この時点の回答文字列を保存。
//		answeredEnglishText = view.getEnglishField().getText();

		view.showHint();
		
		startAnswerThread(Answer.HINT);
	}
	
	void answered(AnswerEvent result) {
		AnswerResultType done = model.answer(result);
		switch(done) {
		case RETRY_CORRECT:
			retryCorrect();
			break;
		case RETRY_WRONG:
			retry();
			break;
		case NEXT:
			changeWordInfo();
			break;
		case COMPLETE:
			onResult();
			break;
		case CONTINUE:
			break;
		default:
			new AssertionError(done + "に対応する case 文がありません。");
		}
	}
	
	void onResult() {
		ResultInfo resultInfo = new ResultInfo();
		resultInfo.setQuestionCount(model.getQuestionCount());
		resultInfo.setTotalScore(model.getTotalScore());
		
		ResultDialog dialog = new ResultDialog(view, resultInfo);
		dialog.open();
		if(dialog.getResultType() == ResultType.RETRY) {
			model.makeQuestions();
			changeWordInfo();
		} else if(dialog.getResultType() == ResultType.QUIT || dialog.getResultType() == ResultType.NONE) {
			try {
				model.saveWordsOrder();
			} catch (IOException e) {
				new ErrorDialog(view, e, "ファイル書き込み時にエラーが発生しました").open();
			}
			view.dispose();
		} else {
			throw new AssertionError("未実装です。");
		}
	}
	
	void changeWordInfo() {
		WordInfo wordInfo = model.getCurrentWordInfo();
		model.setCurrentWordInfo(wordInfo);
//		view.changeWordInfo(wordInfo);
//		model.setWronged(false);
//		model.clearHint();
	}
	
	void retryCorrect() {
		view.retryCorrect();
		model.setWronged(true);
	}
	
	void retry() {
		view.retry();
		model.setWronged(true);
	}
	
	int getAnswerWaitMillSec() {
		return 2 * 1000;
	}
	
	int getHintWaitMillSec() {
		return 2 * 1000;
	}
	
	Thread getAnswerThread() {
		return answerThread;
	}
	
	private WordFrame view;
	protected WordFrameModel model;
	private Thread answerThread;
	
//	private String answeredEnglishText;
}
