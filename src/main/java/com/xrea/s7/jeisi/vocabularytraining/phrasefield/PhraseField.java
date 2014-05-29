package com.xrea.s7.jeisi.vocabularytraining.phrasefield;

import java.awt.AWTKeyStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentListener;

import com.xrea.s7.jeisi.vocabularytraining.phrasefield.model.PhraseFieldModel;
import com.xrea.s7.jeisi.vocabularytraining.phrasefield.model.PhraseInfo;



public class PhraseField extends JPanel {

	public PhraseField() {
		super();
		
		model = new PhraseFieldModel();
		setupFocusKey();
	}
	
	public PhraseField(String phraseText) {
		super();
		
		model = new PhraseFieldModel(phraseText);
		setupFocusKey();
		
		build();
	}
	
	public void setPhrase(String phrase) {
		model.setPhrase(phrase);
		
		removeAll();
		build();
	}
	
	public String getText() {
		String[] texts = getTexts();
		return model.getText(texts);
	}
	
	public String[] getTexts() {
		String[] texts = new String[words.size()];
		int index = 0;
		for(JTextField word : words) {
			texts[index++] = word.getText();
		}
		return texts;
	}
	
	public boolean setText(String phraseText) {
		boolean isChanged = model.setText(phraseText, getTexts());
		
		ArrayList<PhraseInfo> wordInfos = model.getWordInfos();
		int index = 0;
		for(PhraseInfo info : wordInfos) {
			JTextField field = words.get(index);
			field.setText(info.getViewText());
			++index;
		}
		
		PhraseInfo currentPhraseInfo = model.getCurrentPhraseInfo();
		if(currentPhraseInfo != null) {
			index = wordInfos.indexOf(currentPhraseInfo);
			currentTextField = words.get(index);
			currentTextField.setCaretPosition(currentTextField.getText().length());
		} else {
			currentTextField = null;
		}
		
		return isChanged;
	}
	
	public void setPrecedeText(String precedeText) {
		for(JTextField textField : words) {
			if(textField.isEnabled()) {
				textField.setText(precedeText);
				textField.setCaretPosition(precedeText.length());
				break;
			}
		}
	}
	
	public JTextField[] getTextFields() {
		return words.toArray(new JTextField[words.size()]);
	}
	
	public void checkCorrect() {
		String[] inputtedTexts = getTexts();
		model.checkCorrect(inputtedTexts);
		
		int wordIndex = 0;
		for(PhraseInfo info : model.getWordInfos()) {
			words.get(wordIndex++).setEnabled(info.isViewEnabled());
		}
	}
	
	public void clearCorrect() {
		model.clearCorrect();
		
		for(JTextField field : words) {
			field.setEnabled(true);
		}
	}
	
	public void showHint() {
		int index = 0;
		for(PhraseInfo info : model.getWordInfos()) {
			JTextField field = words.get(index);
			field.setText(info.getWord());
			field.setEnabled(false);
			++index;
		}
	}
	
	/**
	 * 非入力の JTextField があるかどか。
	 * @return
	 */
	public boolean isDabbling() {
		for(JTextField field : words) {
			if(field.isEnabled() == false) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void setFont(Font font) {
		this.font = font;
	}
	
	public void addDocumentListener(DocumentListener listener) {
		documentListeners.add(listener);
	}
	
	@Override
	public void addKeyListener(KeyListener listener) {
		keyListeners.add(listener);
	}
	
	@Override
	public boolean requestFocusInWindow() {
		JTextField textField = (currentTextField != null) ? currentTextField : words.get(0);
//		System.out.print(textField == currentTextField ? "* " : "- ");
//		System.out.println(textField);
//		if(!textField.isValid()) {
//			System.out.println("invalid!");
//		}
		return textField.requestFocusInWindow();
	}
	
	private void build() {
		currentTextField = null;
		
		BoxLayout layout = new BoxLayout(this, BoxLayout.LINE_AXIS);
		setLayout(layout);
		
		FocusListener focusListener = new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				currentTextField = (JTextField) e.getSource();
			}
		};
		
		boolean isFirst = true;
		words.clear();
		for(PhraseInfo phraseInfo : model.getPhraseInfos()) {
			if(phraseInfo.getKind() == PhraseInfo.Kind.WORD) {
				JTextField field = new JTextField(phraseInfo.getWord().length() + 1);
				field.setFont(font);
				field.addFocusListener(focusListener);
				for(DocumentListener listener : documentListeners) {
					field.getDocument().addDocumentListener(listener);
				}
				for(KeyListener listener : keyListeners) {
					field.addKeyListener(listener);
				}
				words.add(field);
				if(!isFirst) {
					addBlank();
				}
				add(field);
			} else if(phraseInfo.getKind() == PhraseInfo.Kind.SUB) {
				JLabel label = new JLabel(phraseInfo.getWord());
				if(!isFirst) {
					addBlank();
				}
				add(label);
			} else if(phraseInfo.getKind() == PhraseInfo.Kind.PERIOD) {
				add(new JLabel(phraseInfo.getWord()));
			} else {
				throw new AssertionError(phraseInfo.getKind() + "は対応していません。");
			}
			isFirst = false;
		}
		
		for(int i = 0; i < words.size() - 1; ++i) {
			JTextField field = words.get(i);
			field.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);
		}
		
//		CaretListener caretListener = new CaretListener() {
//			@Override
//			public void caretUpdate(CaretEvent arg0) {
//				JTextField field = (JTextField) arg0.getSource();
//				if(field.getDocument().getLength() == 0) {
//					field.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);
//				} else {
//					field.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
//				}
//			}
//		};
		BackwardFocusListener listener = new BackwardFocusListener();
		for(int i = 1; i < words.size(); ++i) {
			JTextField field = words.get(i);
			field.addCaretListener(listener);
			field.addFocusListener(listener);
		}
	}
	
	private void setupFocusKey() {
		KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();

		forwardKeys = new HashSet<AWTKeyStroke>(
		    focusManager.getDefaultFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
		forwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
		
		backwardKeys = new HashSet<AWTKeyStroke>(
			focusManager.getDefaultFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
		backwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0));
		backwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK));
	}
	
	private void addBlank() {
		add(Box.createRigidArea(new Dimension(3,3)));
	}
	
	private class BackwardFocusListener implements CaretListener, FocusListener {
		@Override
		public void focusGained(FocusEvent e) {
			JTextField field = (JTextField) e.getSource();
			setupBackwardTraversalKeys(field);
		}

		@Override
		public void focusLost(FocusEvent e) {
		}

		@Override
		public void caretUpdate(CaretEvent e) {
			JTextField field = (JTextField) e.getSource();
			setupBackwardTraversalKeys(field);
		}
		
		private void setupBackwardTraversalKeys(JTextField field) {
			if(field.getDocument().getLength() == 0) {
				field.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);
			} else {
				field.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
			}
		}
	}
	
	void setViewTexts(String[] viewTexts) {
		for(int i = 0; i < viewTexts.length; ++i) {
			words.get(i).setText(viewTexts[i]);
		}
	}
	
	private static final long serialVersionUID = 1L;
	
	private PhraseFieldModel model;
	private ArrayList<JTextField> words = new ArrayList<JTextField>();
	private ArrayList<DocumentListener> documentListeners = new ArrayList<>();
	private ArrayList<KeyListener> keyListeners = new ArrayList<>();
	private Font font;
	private JTextField currentTextField;

	private Set<AWTKeyStroke> forwardKeys;
	private Set<AWTKeyStroke> backwardKeys;
}
