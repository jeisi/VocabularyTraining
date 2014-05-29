package com.xrea.s7.jeisi.vocabularytraining.resultdialog;

public class ResultInfo {
	
	public int getQuestionCount() {
		return questionCount;
	}
	public void setQuestionCount(int questionCount) {
		this.questionCount = questionCount;
	}

	public float getTotalScore() {
		return totalScore;
	}
	public void setTotalScore(float totalScore) {
		this.totalScore = totalScore;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		int sc = Math.round(totalScore * 10.0f);
		int integral = sc / 10;
		int decimal = sc % 10;
		if(decimal == 0) {
			builder.append(integral);
		} else {
			builder.append(integral);
			builder.append(".");
			builder.append(decimal);
		}
		builder.append("/");
		builder.append(questionCount);
		
		return builder.toString();
	}
	
	private float totalScore;
	private int questionCount;

}
