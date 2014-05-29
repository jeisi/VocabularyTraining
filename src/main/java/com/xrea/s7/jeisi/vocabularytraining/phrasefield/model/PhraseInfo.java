package com.xrea.s7.jeisi.vocabularytraining.phrasefield.model;

public class PhraseInfo {
	static public enum Kind {
		WORD,
		SUB,
		PERIOD,
	}
	
	PhraseInfo(Kind kind, String word) {
		this.kind = kind;
		this.word = word;
	}
	
	public Kind getKind() {
		return kind;
	}
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	
	public void setViewText(String text) {
		viewText = text;
	}
	public String getViewText() {
		return viewText;
	}
	
	public void setCorrect(boolean isCorrect) {
		this.isCorrect = isCorrect;
	}
	public boolean isCorrect() {
		return isCorrect;
	}
	
//	public void setUseHint(boolean isUseHint) {
//		this.isUseHint = isUseHint;
//	}
//	public boolean isUseHint() {
//		return isUseHint;
//	}
	
	public void setCaretPosition(int position) {
		caretPosition = position;
	}
	public int getCaretPosition() {
		return caretPosition;
	}
	
	/**
	 * JTextField の enabled の設定。正解した単語は再入力する必要がないので unenabled。ヒントを使用した単語は再度入力するため enabled。
	 * @return JTextField.setEnabled() にセットする値。
	 */
	public boolean isViewEnabled() {
		boolean enabled = true;
//		if(isUseHint()) {
//			enabled = true; // ヒントを使用した場合は enabled。
//		} else 
		if(isCorrect()) {
			enabled = false; // 正解した単語は unenabled にする。
		}
		return enabled;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof PhraseInfo)) {
			return false;
		}
		PhraseInfo other = (PhraseInfo) obj;
		return kind == other.kind && word == other.word && viewText == other.viewText && caretPosition == other.caretPosition &&
				isCorrect == other.isCorrect /*&& isUseHint == other.isUseHint*/;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append(kind);
		builder.append(",");
		builder.append(word);
		builder.append(",");
		builder.append(viewText);
		builder.append(",");
		builder.append(isCorrect);
		builder.append(",");
//		builder.append(isUseHint);
//		builder.append(",");
		builder.append(caretPosition);
		builder.append("}");
		return builder.toString();
	}
	
	public static PhraseInfo EMPTY_WORD = new PhraseInfo(Kind.WORD, "");
	
	private Kind kind;
	private String word;
	
	private String viewText;
	private int caretPosition = 0;
	private boolean isCorrect = false;
//	private boolean isUseHint = false;
}
