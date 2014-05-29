package com.xrea.s7.jeisi.vocabularytraining.wordframe;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.xrea.s7.jeisi.vocabularytraining.main.VocabularyTraining;
import com.xrea.s7.jeisi.vocabularytraining.main.WordInfo;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.model.WordFrameModel;

public abstract class ListeningWordFrame extends WordFrame {
	public ListeningWordFrame(VocabularyTraining parent, WordFrameModel model) {
		super(parent, model);
	}
	
	@Override
	protected WordFrameController newController(WordFrame wordFrame, WordFrameModel model) {
		return new ListeningWordFrameController(wordFrame, model);
	}

	@Override
	protected void doChildChangeWordInfo() {
		m_layout.show(m_panel, "button");
	}
	
	@Override
	protected void onStartQuestion() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				playMp3();
			}
		});
	}

	@Override
	protected JPanel createJapaneseCardPanel() {
		m_layout = new CardLayout();
		m_panel = new JPanel(m_layout);
		
		WordInfo wordInfo = getWordInfo();
		JTextArea japaneseLabel = new JTextArea(wordInfo.getJapanese());
		setJapaneseLabel(japaneseLabel);
		japaneseLabel.setEditable(false);
		japaneseLabel.setBackground(null);
		japaneseLabel.setColumns(20);
		m_panel.add(japaneseLabel, "label");
		
		m_showJapaneseButton = new JButton("If you want to look at japanese, click this button.");
		m_showJapaneseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				m_layout.show(m_panel, "label");
			}
		});
		m_showJapaneseButton.addActionListener(((ListeningWordFrameController)controller).createShowJapaneseButtonActionListener());
		m_panel.add(m_showJapaneseButton, "button");
		
		m_layout.last(m_panel);
		return m_panel;
	}

	JButton getShowJapaneseButton() {
		return m_showJapaneseButton;
	}
	
	private static final long serialVersionUID = 1L;
	private CardLayout m_layout;
	private JPanel m_panel;
	private JButton m_showJapaneseButton;
}
