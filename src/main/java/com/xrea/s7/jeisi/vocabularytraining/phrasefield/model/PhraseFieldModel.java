package com.xrea.s7.jeisi.vocabularytraining.phrasefield.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xrea.s7.jeisi.vocabularytraining.phrasefield.model.PhraseInfo.Kind;

public class PhraseFieldModel {
	public PhraseFieldModel() {
	}
	
	public PhraseFieldModel(String phraseText) {
		setPhrase(phraseText);
	}
	
	public ArrayList<PhraseInfo> getPhraseInfos() {
		return phraseInfos;
	}
	
	public void setPhrase(String phrase) {
		phraseInfos = analyze(phrase);
	}
	
	/**
	 * 入力された文字列が合っているかどうかを判定する。
	 * @param inputtedWords
	 */
	public void checkCorrect(String[] inputtedWords) {
		int wordIndex = 0;
		for(PhraseInfo info : phraseInfos) {
			if(info.getKind() == Kind.WORD) {
				String word = inputtedWords[wordIndex++];
				boolean isCorrect = (info.getWord().equals(word)/* && !info.isUseHint()*/);
				info.setCorrect(isCorrect);
			}
		}
	}
	
	public void clearCorrect() {
		for(PhraseInfo info : phraseInfos) {
			if(info.getKind() == Kind.WORD) {
				info.setCorrect(false);
			}
		}
	}
	
	public boolean setText(String phraseText, String[] inputtedWords) {
		List<PhraseInfo> inputtedPhraseInfos = getWordInfos(phraseText);
		List<PhraseInfo> wordInfos = getWordInfos();
		
		currentPhraseInfo = null;
		boolean isChanged = false;
		for(int i = 0; i < inputtedWords.length; ++i) {
			String inputtedWord = inputtedWords[i];
			String text = inputtedPhraseInfos.get(i).getWord();
			PhraseInfo phraseInfo = wordInfos.get(i);
			phraseInfo.setViewText(text);
			if(!inputtedWord.equals(text)) {
				isChanged = true;
			}
//			boolean isUseHint = (0 < text.length());
//			phraseInfo.setUseHint(isUseHint);
			if(!phraseInfo.isCorrect()) {
				if(currentPhraseInfo == null || 0 < text.length()) {
					currentPhraseInfo = phraseInfo;
				}
			}
		}
		
		if(currentPhraseInfo != null) {
			currentPhraseInfo.setCaretPosition(currentPhraseInfo.getViewText().length());
		}
		
		return isChanged;
	}
	
	public String getText() {
		StringBuilder builder = new StringBuilder();
		boolean isFirst = true;
		for(PhraseInfo info : phraseInfos) {
			if(info.getKind() == Kind.PERIOD) {
				builder.append(info.getWord());
				continue;
			}
			if(!isFirst) {
				builder.append(" ");
			}
			isFirst = false;
			if(info.getKind() == Kind.WORD) {
				builder.append(info.getWord());
			} else if(info.getKind() == Kind.SUB) {
				builder.append(info.getWord());
			} else {
				throw new AssertionError(info.getKind() + "は対応していません。");
			}
		}
		return builder.toString();
	}
	
	public String getText(String[] words) {
		StringBuilder builder = new StringBuilder();
		int index = 0;
		boolean isFirst = true;
		for(PhraseInfo info : phraseInfos) {
			if(info.getKind() == Kind.PERIOD) {
				builder.append(info.getWord());
				continue;
			}
			if(!isFirst) {
				builder.append(" ");
			}
			if(info.getKind() == Kind.WORD) {
				String word = words[index++];
				if(word.length() > 0) {
					builder.append(word);
					isFirst = false;
				}
			} else if(info.getKind() == Kind.SUB) {
				builder.append(info.getWord());
				isFirst = false;
			} else {
				throw new AssertionError(info.getKind() + "は対応していません。");
			}
		}
		return builder.toString();
	}
	
	public ArrayList<String> getWords(String text) {
		ArrayList<PhraseInfo> newPhraseInfos = getWordInfos(text);
		ArrayList<String> words = new ArrayList<String>();
		for(PhraseInfo info : newPhraseInfos) {
			words.add(info.getWord());
		}
		return words;
	}
	
	public ArrayList<PhraseInfo> getWordInfos() {
		ArrayList<PhraseInfo> words = new ArrayList<>();
		for(int index = 0; index < phraseInfos.size(); ++index) {
			PhraseInfo info0 = phraseInfos.get(index);
			if(info0.getKind() == Kind.SUB || info0.getKind() == Kind.PERIOD) {
				continue;
			}
			words.add(info0);
		}
		return words;
	}
	
	public ArrayList<PhraseInfo> getWordInfos(String text) {
		ArrayList<PhraseInfo> newPhraseInfos = analyze(text);
		
		ArrayList<PhraseInfo> words = new ArrayList<>();
		for(int index = 0; index < phraseInfos.size(); ++index) {
			PhraseInfo info0 = phraseInfos.get(index);
			if(info0.getKind() == Kind.SUB || info0.getKind() == Kind.PERIOD) {
				continue;
			}
			
			PhraseInfo word;
			if(index < newPhraseInfos.size()) {
				word = newPhraseInfos.get(index);
			} else if(phraseInfos.get(index).isCorrect()/* || phraseInfos.get(index).isUseHint()*/) {
				word = phraseInfos.get(index);
			} else {
				word = PhraseInfo.EMPTY_WORD;
			}
			words.add(word);
		}
		return words;
	}
	
	public PhraseInfo getCurrentPhraseInfo() {
		return currentPhraseInfo;
	}
	
	private ArrayList<PhraseInfo> analyze(String phraseText) {
		objectPattern = Pattern.compile("^(<[^>]*>?)$");
		Pattern periodPattern = Pattern.compile("(.+)([\\.,])");
		
		ArrayList<PhraseInfo> phraseInfos = new ArrayList<>();
		String[] words = phraseText.split(" ");
		for(String word : words) {	
			Matcher matcher = periodPattern.matcher(word);
			if(matcher.matches()) {
				phraseInfos.add(getAddPhrase(matcher.group(1)));
				phraseInfos.add(new PhraseInfo(Kind.PERIOD, matcher.group(2)));
				continue;
			}
			
			if(word.length() > 0) {
				phraseInfos.add(getAddPhrase(word));
			}
		}
		return phraseInfos;
	}
	
	private PhraseInfo getAddPhrase(String word) {
		Matcher matcher = objectPattern.matcher(word);
		if(matcher.matches()) {
			return new PhraseInfo(Kind.SUB, matcher.group(1));
		} else {
			return new PhraseInfo(Kind.WORD, word);
		}
	}
	
	private ArrayList<PhraseInfo> phraseInfos = new ArrayList<PhraseInfo>();
	//private String[] inputtedWords;
	private Pattern objectPattern;
	private PhraseInfo currentPhraseInfo;
}
