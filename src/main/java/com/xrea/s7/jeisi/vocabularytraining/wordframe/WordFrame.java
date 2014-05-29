package com.xrea.s7.jeisi.vocabularytraining.wordframe;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.xrea.s7.jeisi.jlib.errordialog.ErrorDialog;
import com.xrea.s7.jeisi.vocabularytraining.main.Config;
import com.xrea.s7.jeisi.vocabularytraining.main.ConfigurableFrame;
import com.xrea.s7.jeisi.vocabularytraining.main.VocabularyTraining;
import com.xrea.s7.jeisi.vocabularytraining.main.WordInfo;
import com.xrea.s7.jeisi.vocabularytraining.phrasefield.PhraseField;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.model.WordFrameModel;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.model.WordFrameModelListener;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;


public abstract class WordFrame extends JFrame implements WordFrameModelListener, ConfigurableFrame {

	public WordFrame(VocabularyTraining parent, WordFrameModel model) {
		this.parent = parent;
		setModel(model);
	}
	
	public void setModel(WordFrameModel model) {
		this.model = model;
		controller = newController(this, model);
		this.model.addWordFrameModelListener(this);
	}
	
	protected WordFrameController newController(WordFrame wordFrame, WordFrameModel model) {
		return new WordFrameController(wordFrame, model);
	}

	public void setParent(VocabularyTraining parent) {
		this.parent = parent;
	}
	
	public void open() {
		wordInfo = model.getCurrentWordInfo();
		
		buildCenterPane();
		buildSouthPane();
				
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("VocablaryLists");
		changeWordInfoDirect();
		pack();
		restoreLocation();
		
		addWindowListener(controller.createWindowListener());
		
		setVisible(true);
	}
	
	public void saveLocation() {
		Config config = parent.getConfig();
		if(config == null) {
			return;
		}
		
		config.saveLocation(this);
	}
	
	private void restoreLocation() {
		Config config = parent.getConfig();
		if(config == null) {
			return;
		}
		
		config.restoreLocation(this);
	}
	
	public void changeWordInfo(WordInfo wordInfo_) {
		this.wordInfo = wordInfo_;
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				changeWordInfoDirect();
				repaint();	
			}
		});
	}
	
	private void changeWordInfoDirect() {
		m_questionNoLabel.setText(model.getQuestionIndexMessage());
		japaneseLabel.setText(wordInfo.getJapanese());
		//englishField.setText("");
		englishField.setPhrase(wordInfo.getPhrase());
		englishField.setForeground(null);
		answerButton.setEnabled(false);
		boolean isVisibleMp3 = (wordInfo.getMp3() != null);
		playButton.setVisible(isVisibleMp3);
		clearResult();
		
		if(wordInfo.getSynosym() != null) {
			synosymHeaderLabel.setVisible(true);
			synosymLabel.setText(wordInfo.getSynosym());
			synosymLabel.setVisible(true);
		} else {
			synosymHeaderLabel.setVisible(false);
			synosymLabel.setVisible(false);
		}
		
		doChildChangeWordInfo();
		
		pack();
		englishField.requestFocusInWindow();
		
		onStartQuestion();
	}
	
	// 回答入力後のウエイト時間中に入力された文字列を先行入力文字列としてセット。
	public void setPrecedeInput(String precedeEnglishText) {
		this.precedeEnglishText = precedeEnglishText;
	}
	
	public void retryCorrect() {	
		englishField.clearCorrect();
		reflectPrecedeEnglishText();
		clearResult();
		englishField.requestFocusInWindow();
		
		repaint();
	}
	
	public void retry() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				englishField.checkCorrect();
				reflectPrecedeEnglishText();
				clearResult();
				englishField.requestFocusInWindow();
			}
		});
	}
	
	public void showSkip() {
		answerButton.setEnabled(false);
		
		m_answerLayout.show(m_answerPanel, "skip");

		m_skipButton.requestFocusInWindow();
	}
	
	private void reflectPrecedeEnglishText() {
		if(precedeEnglishText == null) {
			englishField.setText("");
		} else {
			englishField.setText("");
			englishField.setPrecedeText(precedeEnglishText);
		}
		precedeEnglishText = null;
	}
	
	private void clearResult() {
		resultLabel.setText("");
		m_layerUI.setVisible(false);
		
		m_answerLayout.show(m_answerPanel, "answer");
		
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				repaint();
//			}
//		});
	}
	
	public void showHint() {
		englishField.showHint();
		answerButton.setEnabled(false);
	}
	
	public WordFrameModel getModel() {
		return model;
	}

	public WordFrameController getController() {
		return controller;
	}
	
	public void setController(WordFrameController controller) {
		this.controller = controller;
	}
	
	@Override
	public void wordChanged(WordInfo wordInfo) {
		changeWordInfo(wordInfo);
	}
	
//	@Override
//	public void pack() {
//		centerPanePanel.setPreferredSize(centerPaneMainPanel.getPreferredSize());
//		
//		super.pack();
//	}
	
	public void showCorrect(String resultMessage) {
		resultLabel.setText(resultMessage);
		resultLabel.setVisible(true);
		resultLabel.setForeground(null);
		//answerIconLabel.setVisible(true);
		m_layerUI.setVisible(true);
		repaint();
	}
	
	private void buildCenterPane() {
//		centerPanePanel = new JPanel();
//		SpringLayout layout = new SpringLayout();
//		centerPanePanel.setLayout(layout);
		
//		answerIconLabel = new JLabel(new ImageIcon("image/verygood.png"));
//		answerIconLabel.setVisible(false);
		
		centerPaneMainPanel = createMainPanel();
		m_layerUI = new ResultLayerUI();
		m_jlayer = new JLayer<>(centerPaneMainPanel, m_layerUI);

//		add(centerPaneMainPanel, BorderLayout.CENTER);
		add(m_jlayer, BorderLayout.CENTER);
	}
	
	private JPanel createMainPanel() {
		JPanel panel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		
		int y = 0;
		
		createHeaderLabel(y, "Question No:", panel);
		m_questionNoLabel = createValueLabel(y, "00 / 00", panel);
		++y;
		
		createHeaderLabel(y, "Japanese:", panel);
		JComponent japanesePanel = createJapaneseCardPanel();
		gbc.gridx = 1;
		gbc.gridy = y;
		gbc.anchor = GridBagConstraints.WEST;
		layout.setConstraints(japanesePanel, gbc);
		panel.add(japanesePanel);
		++y;
		
		createHeaderLabel(y, "English:", panel);		
		//englishField = new JTextField(20);
		//englishField.setName("englishField");
		englishField = new PhraseField();
		//Font currentFont = getFont();
		englishField.setFont(new Font("Courier New", Font.PLAIN, 12));
		gbc.gridx = 1;
		gbc.gridy = y;
		layout.setConstraints(englishField, gbc);
		englishField.addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				answerButton.setEnabled(true);
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
		//englishField.addKeyListener(controller.createEnglishFieldKeyListener());
		panel.add(englishField);
		++y;
		
		synosymHeaderLabel = createHeaderLabel(y, "Synosym:", panel);
		
		gbc.gridx = 1;
		gbc.gridy = y;
		synosymLabel = new JLabel();
		layout.setConstraints(synosymLabel, gbc);
		panel.add(synosymLabel);
		++y;
		
		createHeaderLabel(y, "Listening:", panel);
		
		playButton = new JButton("Play (english & japanese)");
		if(wordInfo.getMp3() == null) {
			playButton.setVisible(false);
		}
		gbc.gridx = 1;
		gbc.gridy = y;
		layout.setConstraints(playButton, gbc);
		panel.add(playButton);
		++y;
		ActionListener playButtonActionListener = controller.createVoiceButtonActionListener();
		if(playButtonActionListener != null) {
			playButton.addActionListener(playButtonActionListener);
		}
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				playMp3();
			}
		});
		
		createHeaderLabel(y, "State:", panel);
		
		resultLabel = new JLabel("That is correct!");
		resultLabel.setVisible(false);
		gbc.gridx = 1;
		gbc.gridy = y;
		gbc.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(resultLabel, gbc);
		panel.add(resultLabel);
		++y;
		
		return panel;
	}
	
	private void buildSouthPane() {
		JPanel panel = new JPanel();
		
		m_answerLayout = new CardLayout();
		m_answerPanel = new JPanel(m_answerLayout);
		panel.add(m_answerPanel);
		
		answerButton = new JButton("Answer");
		m_answerPanel.add(answerButton, "answer");
		answerButton.setEnabled(false);
		answerButton.addActionListener(controller.createAnswerButtonActionListener());
		getRootPane().setDefaultButton(answerButton);
		
		m_skipButton = new JButton("Skip");
		m_answerPanel.add(m_skipButton, "skip");
		m_skipButton.addActionListener(controller.createSkipButtonActionListener());
		m_skipButton.addKeyListener(controller.createSkipButtonKeyListener());
		
		m_answerLayout.show(m_answerPanel, "answer");
		
		hintButton = new JButton("Hint");
		panel.add(hintButton);
		hintButton.addActionListener(controller.createHintButtonActionListener());
		
		giveupButton = new JButton("Give up");
		panel.add(giveupButton);
		giveupButton.addActionListener(controller.createGiveUpActionListener());
		
		add(panel, BorderLayout.SOUTH);
	}
	
	/*
	protected void playMp3() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					FileInputStream inputStream = new FileInputStream(wordInfo.getMp3());
					Player player = new Player(inputStream);
					player.play();
				} catch(FileNotFoundException | JavaLayerException e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(runnable).start();
		englishField.requestFocusInWindow();
	}
	*/
	protected void playMp3() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				JarFile resource = parent.getResourceJar();
				try {
					ZipEntry ze = resource.getEntry(wordInfo.getMp3());
					InputStream is = resource.getInputStream(ze);
					Player player = new Player(is);
					player.play();
				} catch(JavaLayerException e) {
					new ErrorDialog(null, e, "MP3 再生時にエラーが出ました。").open();
					return;
				} catch (IOException e) {
					new ErrorDialog(null, e, "jar ファイル読込時にエラーが発生しました: " + resource.getName()).open();
					return;
				}
			}
		};
		new Thread(runnable).start();
		englishField.requestFocusInWindow();
	}

	private JLabel createHeaderLabel(int y, String text, JPanel panel) {
		GridBagLayout layout = (GridBagLayout) panel.getLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		
		JLabel label = new JLabel(text);
		gbc.gridx = 0;
		gbc.gridy = y;
		gbc.anchor = GridBagConstraints.SOUTHEAST;
		gbc.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(label, gbc);
		panel.add(label);
		
		return label;
	}
	
	private JLabel createValueLabel(int y, String text, JPanel panel) {
		GridBagLayout layout = (GridBagLayout) panel.getLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		
		JLabel label = new JLabel(text);
		gbc.gridx = 1;
		gbc.gridy = y;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		gbc.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(label, gbc);
		panel.add(label);
		
		return label;
	}
	
	protected JComponent createJapaneseCardPanel() {
		japaneseLabel = new JTextArea(wordInfo.getJapanese());
		japaneseLabel.setEditable(false);
		japaneseLabel.setBackground(null);
		japaneseLabel.setColumns(20);
		return japaneseLabel;
	}
	
	protected void doChildChangeWordInfo() {
	}
	
	protected void onStartQuestion() {
	}
	
	protected WordInfo getWordInfo() {
		return wordInfo;
	}
	
	protected void setJapaneseLabel(JTextArea textArea) {
		japaneseLabel = textArea;
	}
	
	public abstract String getIdentifer();
	
	JButton getPlayButton() {
		return playButton;
	}
	
	JButton getAnswerButton() {
		return answerButton;
	}
	
	JButton getSkipButton() {
		return m_skipButton;
	}
	
	JButton getHintButton() {
		return hintButton;
	}
	
	JButton getGiveUpButton() {
		return giveupButton;
	}
	
	JLabel getResultLabel() {
		return resultLabel;
	}
	
//	JLabel getHintLabel() {
//		return hintLabel;
//	}
	
	String getHintText() {
		return englishField.getText();
	}
	
	PhraseField getEnglishField() {
		return englishField;
	}
	
	String[] getPhraseTexts() {
		return englishField.getTexts();
	}
	
	private static final long serialVersionUID = 1L;
	
	private VocabularyTraining parent;
	private WordFrameModel model;
	protected WordFrameController controller;
	private WordInfo wordInfo;
	private String precedeEnglishText;

	//JTextField englishField;
	PhraseField englishField;
	JLabel resultLabel;
	private JLabel m_questionNoLabel;
	private JButton playButton;
	private JTextArea japaneseLabel;
	private JButton answerButton;
	private JButton giveupButton;
	private JButton hintButton;
//	private JLabel hintHeaderLabel;
//	private JLabel hintLabel;
	private JLabel synosymHeaderLabel;
	private JLabel synosymLabel;

	//private JLabel answerIconLabel;

	//private JPanel centerPanePanel;

	private JPanel centerPaneMainPanel;
	private ResultLayerUI m_layerUI;

	private JButton m_skipButton;

	private JPanel m_answerPanel;

	private CardLayout m_answerLayout;

	private JLayer<JComponent> m_jlayer;

}
