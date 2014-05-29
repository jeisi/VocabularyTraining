package com.xrea.s7.jeisi.vocabularytraining.wordframe.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.yaml.snakeyaml.Yaml;

import com.xrea.s7.jeisi.jlib.errordialog.ErrorDialog;
import com.xrea.s7.jeisi.vocabularytraining.main.VocabularyTraining;
import com.xrea.s7.jeisi.vocabularytraining.main.WordInfo;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.AnswerEvent;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.AnswerResultType;
import com.xrea.s7.jeisi.vocabularytraining.wordsorder.WordsOrder;
import com.xrea.s7.jeisi.vocabularytraining.wordsorder.WordsOrderFilter;



public abstract class WordFrameModel {
	
	public WordFrameModel() {
		//load();
	}

	public WordFrameModel(ArrayList<WordInfo> loadedWords) {
		this.loadedWords = loadedWords;
	}
	
	public WordFrameModel(WordInfo wordInfo) { 
		this.wordInfo = wordInfo;
		loadedWords = new ArrayList<WordInfo>();
		loadedWords.add(this.wordInfo);
		wordsOrder.update(loadedWords, new Date());
	}

	public void setLoadedWords(ArrayList<WordInfo> loadedWords) {
		this.loadedWords = loadedWords;
	}
	
	public void addWordFrameModelListener(WordFrameModelListener listener) {
		listeners.add(listener);
	}
	
	public AnswerResultType answer(AnswerEvent answer) {
		switch(answer.getResult()) {
		case CORRECT:
			float score = 1.0f;
			if(!answer.isWronged()) {
				totalScore += score;
			}
			wordsOrder.updateScore(wordInfo.getEnglish(), 10);
			if(answer.isDabbling()) {
				return AnswerResultType.RETRY_CORRECT;
			}
			wordInfo = getNextWordInfo();
			if(wordInfo == null) {
				return AnswerResultType.COMPLETE;
			}
			return AnswerResultType.NEXT;
		case HINT:
			wordsOrder.updateScore(wordInfo.getEnglish(), -50);
			return AnswerResultType.RETRY_CORRECT;
		case VOICE_HINT:
			wordsOrder.updateScore(wordInfo.getEnglish(), -30);
			return AnswerResultType.CONTINUE;
		case WRONG:
			wordsOrder.updateScore(wordInfo.getEnglish(), -1);
			return AnswerResultType.RETRY_WRONG;
		case GIVEUP:
			wordsOrder.updateScore(wordInfo.getEnglish(), -100);
			wordInfo = getNextWordInfo();
			if(wordInfo == null) {
				return AnswerResultType.COMPLETE;
			}
			return AnswerResultType.NEXT;
		default:
			throw new AssertionError(answer.getResult() + "に対応する case が実装されていません。");
		}
	}
	
	public void hintUsed() {
		wordsOrder.updateScore(wordInfo.getEnglish(), -1);
	}
	
	public void makeQuestions() {
		boolean bRetry;
		do {
			bRetry = false;
			words.clear();
			for(String english : wordsOrder.getQuestions()) {
				WordInfo info = searchWordInfo(english, loadedWords);
				if(info == null) {
					wordsOrder.remove(english);
					bRetry = true;
					break;
				}
				words.add(info);
			}
		} while(bRetry);
		
		totalScore = 0.0f;
		questionIndex = 0;
		wordInfo = getNextWordInfo();
	}
	
	/*
	private void load() {
		String dataDir = VocabularyLists.getDataDir();
		if(dataDir == null) {
			throw new AssertionError("dataDir がセットされていません。");
		}
		
		String fileName = getWordsFileName();
		File filename = new File(dataDir, fileName);
		Yaml yaml = new Yaml();
		loadedWords = new ArrayList<WordInfo>();
		try(FileReader reader = new FileReader(filename)) {
			List<?> yamlWords = (List<?>) yaml.load(reader);
			for(Object yamlWord : yamlWords) {
				@SuppressWarnings("unchecked")
				WordInfo wordInfo = new WordInfo((Map<String, Object>) yamlWord);
				loadedWords.add(wordInfo);
			}
		} catch (FileNotFoundException e) {
			new ErrorDialog(null, e, "ファイルがみつかりませんでした: " + filename).open();
			return;
		} catch (IOException e1) {
			new ErrorDialog(null, e1, "ファイル読込時にエラーが発生しました: " + filename).open();
			return;
		}
	}
	*/
	public void load(JarFile jarFile) {
		Yaml yaml = new Yaml();
		loadedWords = new ArrayList<WordInfo>();

		String fileName = getWordsFileName();
		ZipEntry ze = jarFile.getEntry(fileName);
		try(Reader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(ze)))) {
			List<?> yamlWords = (List<?>) yaml.load(reader);
			for(Object yamlWord : yamlWords) {
				@SuppressWarnings("unchecked")
				WordInfo wordInfo = new WordInfo((Map<String, Object>) yamlWord);
				loadedWords.add(wordInfo);
			}
		} catch (IOException e1) {
			new ErrorDialog(null, e1, "ファイル読込時にエラーが発生しました: " + fileName).open();
			return;
		}
	}
	
	public void saveWordsOrder() throws IOException {
		File filename = getOrderFile();
		Yaml yaml = new Yaml();
		try(FileWriter writer = new FileWriter(filename)) {
			yaml.dump(wordsOrder, writer);
		}
	}
	
	public void loadWordsOrder() throws IOException {
		File filename = getOrderFile();
		Yaml yaml = new Yaml();
		try(FileReader reader = new FileReader(filename)) {
			wordsOrder = (WordsOrder) yaml.load(reader);
		} catch (FileNotFoundException e) {
			wordsOrder = new WordsOrder();
		}
		
		wordsOrder.update(loadedWords, new Date(filename.lastModified()), getFilter());
	}
	
	private File getOrderFile() {
		String dataDir = VocabularyTraining.getUserDir();
		String fileName = getOrderFileName();
		return new File(dataDir, fileName);
	}
	
	public String getAnswer() {
		String answer = wordInfo.getPhrase();
		if(answer != null) {
			return answer;
		}
		answer = wordInfo.getEnglish();
		return answer;
	}
	
	public WordInfo getCurrentWordInfo() {
		return wordInfo;
	}
	
	public void setCurrentWordInfo(WordInfo wordInfo) {
		this.wordInfo = wordInfo;
		setWronged(false);
//		clearHint();
		fireWordChanged(wordInfo);
	}
	
	public int getQuestionCount() {
		return words.size();
	}
	
	public float getTotalScore() {
		return totalScore;
	}
	
	public WordsOrder getWordsOrder() {
		return wordsOrder;
	}
	
	public boolean isWronged() {
		return isWronged;
	}
	public void setWronged(boolean isWronged) {
		this.isWronged = isWronged;
	}
	
	public String getResultMessage(String inputText) {
		String correct = getAnswer();
		if(correct.equals(inputText)) {
//			if(hintCount == 0) {
				return "This is correct!";
//			} else if(hintCount >= correct.length()) {
//				return "Needs improvement.";
//			} else {
//				return "That is OK.";
//			}
		} else {
			return "Sorry. Wrong answer.";
		}
	}
	
	public String getQuestionIndexMessage() {
		return String.format("<html><font color=blue size=6>%2d</font> / %2d</html>", questionIndex, words.size());
	}
	
	static WordInfo searchWordInfo(String english, List<WordInfo> list) {
		for(WordInfo wordInfo : list) {
			if(wordInfo.getEnglish().equals(english)) {
				return wordInfo;
			}
		}
		//throw new NoSuchElementException(english + "は" + list + "の中に含まれていません。");
		return null;
	}
	
	private WordInfo getNextWordInfo() {
		if(questionIndex >= words.size()) {
			return null;
		}
		return words.get(questionIndex++);
	}
	
	private void fireWordChanged(WordInfo wordInfo) {
		for(WordFrameModelListener listener : listeners) {
			listener.wordChanged(wordInfo);
		}
	}
	
	protected abstract String getWordsFileName();
	protected abstract String getOrderFileName();
	protected abstract WordsOrderFilter getFilter();
	
	protected ArrayList<WordInfo> loadedWords;
	private WordInfo wordInfo;
	private ArrayList<WordInfo> words = new ArrayList<WordInfo>();
	protected WordsOrder wordsOrder = new WordsOrder();
	private float totalScore;
	private int questionIndex;

	//private int hintCount = 0;
	boolean isWronged = false;			// 一度でも間違えた？
	
//	private boolean m_bHintShowed = false; // 一度でもヒントを表示した？

	private ArrayList<WordFrameModelListener> listeners = new ArrayList<WordFrameModelListener>();

}
