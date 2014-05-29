package com.xrea.s7.jeisi.vocabularytraining.wordsorder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.TreeSet;

import com.xrea.s7.jeisi.vocabularytraining.main.WordInfo;

public class WordsOrder {

	public WordsOrder() {
	
	}
	
	public void update(List<WordInfo> words, Date lastModified) {
		update(words, lastModified, new AllAcceptFilter());
	}
	
	public void update(List<WordInfo> words, Date lastModified, WordsOrderFilter filter) {
		for(WordInfo word : words) {
			ScoreInfo info = getContain(word.getEnglish());
			if(info == null) {
				if(filter.accept(word)) {
					addScoreInfo(word.getEnglish());
				}
			}
		}
		setLastModified(lastModified);
	}
	
	public void updateScore(String english, int score) {
		Iterator<ScoreInfo> it = sortedScore.iterator();
		while(it.hasNext()) {
			ScoreInfo scoreInfo = it.next();
			if(scoreInfo.getEnglish().equals(english)) {
				it.remove();
				scoreInfo.addScore(score);
				addScoreInfo(scoreInfo);
				return;
			}
		}
		throw new NoSuchElementException(english + "は登録されていません。");
	}
	
	public ScoreInfo getScoreInfo(String english) {
		for(ScoreInfo info : sortedScore) {
			if(info.getEnglish().equals(english)) {
				return info;
			}
		}
		return null;
	}
	
	public String[] getQuestions() {
		int size = Math.min(10, sortedScore.size());
		//String[] englishWords = new String[size];
		ArrayList<String> englishWords = new ArrayList<>();
		Iterator<ScoreInfo> it = sortedScore.iterator();
		//int nQuestionNo = 0;
		int nWordNo = 0;
		while(englishWords.size() < Math.min(size, 5)) {
			englishWords.add(it.next().getEnglish());
			++nWordNo;
		}
		
		try {
			Random random = m_random;
			while(englishWords.size() < Math.min(size, 9) && it.hasNext()) {
				while(!(random.nextFloat() < 0.2f) && it.hasNext()) {
					it.next();
					++nWordNo;
				}
				if(!it.hasNext()) {
					throw new NoQuestionExcepton();
				}
				englishWords.add(it.next().getEnglish());
				++nWordNo;
			}
			
			if(englishWords.size() < size) {
				if(!it.hasNext()) {
					throw new NoQuestionExcepton();
				}
				int nFastForward = (int)((sortedScore.size() - nWordNo) * random.nextFloat());
				while(nFastForward-- > 0) {
					it.next();
				}
				englishWords.add(it.next().getEnglish());
			}
		} catch(NoQuestionExcepton e) {
			it = sortedScore.iterator();
			while(it.hasNext() && englishWords.size() < size) {
				String word = it.next().getEnglish();
				if(englishWords.indexOf(word) == -1) {
					englishWords.add(word);
				}
			}
		}
		
		return englishWords.toArray(new String[englishWords.size()]);
	}
	
	public void remove(String english) {
		Iterator<ScoreInfo> it = sortedScore.iterator();
		while(it.hasNext()) {
			ScoreInfo scoreInfo = it.next();
			if(scoreInfo.getEnglish().equals(english)) {
				it.remove();
				break;
			}
		}
	}
	
	public TreeSet<ScoreInfo> getSortedScore() {
		return sortedScore;
	}
	
	public void setSortedScore(TreeSet<ScoreInfo> sortedScore) {
		this.sortedScore = sortedScore;
	}
	
	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public void addScoreInfo(String english) {
		int uniqueNo = getUniqueNo();
		sortedScore.add(new ScoreInfo(english, uniqueNo));
	}
	
	private void addScoreInfo(ScoreInfo info) {
		info.setUniqueNo(getUniqueNo());
		sortedScore.add(info);
	}
	
	int getUniqueNo() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int rnd = m_random.nextInt(100);
		int uniqueNo = (year * 365 + month * 31 + day) * 100 + rnd/*ランダム性を持たせるためにあえてそのまま足している*/;
		return uniqueNo;
	}
	
	Random newRandom() {
		return new Random();
	}
	
	private ScoreInfo getContain(String english) {
		for(ScoreInfo info : sortedScore) {
			if(info.getEnglish().equals(english)) {
				return info;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append(lastModified);
		builder.append(",");
		builder.append(sortedScore);
		builder.append("}");
		return builder.toString();
	}
	
	public static class ScoreInfo implements Comparable<ScoreInfo> {
		public ScoreInfo() {
		}
		
		public ScoreInfo(String english, int uniqueNo) {
			this.english = english;
			this.uniqueNo = uniqueNo;
		}
		
		public String getEnglish() {
			return english;
		}
		public void setEnglish(String english) {
			this.english = english;
		}
		
		public int getTotalScore() {
			return totalScore;
		}
		public void setTotalScore(int totalScore) {
			this.totalScore = totalScore;
		}
		
		public int getUniqueNo() {
			return uniqueNo;
		}
		public void setUniqueNo(int uniqueNo) {
			this.uniqueNo = uniqueNo;
		}
		
		public LinkedList<Integer> getScores() {
			return scores;
		}
		public void setScores(LinkedList<Integer> scores) {
			this.scores = scores;
		}
		
		public void addScore(int score) {
			scores.add(score);
			if(scores.size() > 10) {
				scores.poll();
			}
			
			/*
			totalScore = 0;
			for(Integer i : scores) {
				totalScore += i;
			}
			*/
			
			totalScore = 0;
			int rate = 10;
			ListIterator<Integer> it = scores.listIterator(scores.size());
			while(it.hasPrevious()) {
				int value = it.previous();
				totalScore += value * rate;
				--rate;
			}
		}
		
		public void clearScores() {
			scores.clear();
		}
		
		@Override
		public int compareTo(ScoreInfo o2) {
			ScoreInfo o1 = this;
			if(o1.getTotalScore() < o2.getTotalScore()) {
				return -1;
			} else if(o1.getTotalScore() > o2.getTotalScore()) {
				return 1;
			}
			if(o1.getUniqueNo() < o2.getUniqueNo()) {
				return -1;
			} else if(o1.getUniqueNo() > o2.getUniqueNo()) {
				return 1;
			}
			return o1.getEnglish().compareTo(o2.getEnglish());
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("{");
			builder.append(english);
			builder.append(",");
			builder.append(scores);
			builder.append(",");
			builder.append(totalScore);
			builder.append(",");
			builder.append(uniqueNo);
			builder.append("}");
			return builder.toString();
		}
		
		private LinkedList<Integer> scores = new LinkedList<>();
		private int totalScore;
		private int uniqueNo;
		private String english;
	}
	
//	private static class WordComparator implements Comparator<Score> {
//		@Override
//		public int compare(Score o1, Score o2) {
//			
//		}
//	}
	
	private static class NoQuestionExcepton extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	private Date lastModified;
	private TreeSet<ScoreInfo> sortedScore = new TreeSet<>();
	private Random m_random = newRandom();
}
