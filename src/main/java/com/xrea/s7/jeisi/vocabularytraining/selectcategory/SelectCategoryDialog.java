package com.xrea.s7.jeisi.vocabularytraining.selectcategory;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.xrea.s7.jeisi.vocabularytraining.main.ConfigurableFrame;
import com.xrea.s7.jeisi.vocabularytraining.main.VocabularyTraining;

public class SelectCategoryDialog extends JFrame implements ConfigurableFrame {

	public SelectCategoryDialog(VocabularyTraining parent) {
		m_parent = parent;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
	}
	
	public void open() {
		buildCenter();
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				saveLocation();
			}
		});
		
		setTitle(VocabularyTraining.getApplicationTitle() + " - ジャンル選択");
		pack();
		m_parent.getConfig().restoreLocation(this);
		setVisible(true);
	}
	
	public String getActionCommand() {
		return actionCommand;
	}
	
	@Override
	public String getIdentifer() {
		return "select category";
	}
	
	private void buildCenter() {
		JPanel panel = new JPanel();
		
		JButton phraseButton = new JButton("Phrase");
		panel.add(phraseButton);
		phraseButton.setActionCommand("phrase");
		phraseButton.addActionListener(actionListener);
		
		JButton sentenceButton = new JButton("Sentence");
		panel.add(sentenceButton);
		sentenceButton.setActionCommand("sentence");
		sentenceButton.addActionListener(actionListener);
		
		JButton listeningPhraseButton = new JButton("Listening Phrase");
		panel.add(listeningPhraseButton);
		listeningPhraseButton.setActionCommand("listening phrase");
		listeningPhraseButton.addActionListener(actionListener);
		
		JButton listeningSentenceButton = new JButton("Listening Sentence");
		panel.add(listeningSentenceButton);
		listeningSentenceButton.setActionCommand("listening sentence");
		listeningSentenceButton.addActionListener(actionListener);
		
		add(panel, BorderLayout.CENTER);
	}
	
	private void saveLocation() {
		m_parent.getConfig().saveLocation(this);
	}
	
	public class MyActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			actionCommand = e.getActionCommand();
			dispose();
		}
	}
	
	private static final long serialVersionUID = 1L;
	private VocabularyTraining m_parent;
	private String actionCommand = "no select";
	private ActionListener actionListener = new MyActionListener();

}
