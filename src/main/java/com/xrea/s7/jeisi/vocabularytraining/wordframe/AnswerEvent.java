package com.xrea.s7.jeisi.vocabularytraining.wordframe;

public class AnswerEvent {
	
	public AnswerEvent(Answer result) {
		this.result = result;
	}
	
	public AnswerEvent(Answer result, boolean isWronged, boolean isDabbling) {
		this.result = result;
		this.isWronged = isWronged;
		this.isDabbling = isDabbling;
	}
	
	
	public final Answer getResult() {
		return result;
	}
	
//	public int getHintCount() {
//		return hintCount;
//	}
	
	public boolean isWronged() {
		return isWronged;
	}
	
	public boolean isDabbling() {
		return isDabbling;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof AnswerEvent)) {
			return false;
		}
		
		AnswerEvent other = (AnswerEvent) obj;
		return (result == other.result &&
//				hintCount == other.hintCount &&
				isWronged == other.isWronged);
	}
	
	private Answer result;
	private boolean isWronged;
	private boolean isDabbling = false;		// いくつかの単語は非入力時。
}
