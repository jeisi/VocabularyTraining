package com.xrea.s7.jeisi.vocabularytraining.main;

import java.util.List;
import java.util.Map;

public class WordInfo {
	public WordInfo(Map<String, Object> map) {
		this.map = map;
	}
	
	public String getEnglish() {
		return (String) map.get("english");
	}
	public void setEnglish(String english) {
		map.put("english", english);
	}
	
	public String getPhrase() {
		String phrase = (String) map.get("phrase");
		if(phrase != null) {
			return phrase;
		} else {
			return getEnglish();
		}
	}
	public void setPhrase(String phrase) {
		map.put("phrase", phrase);
	}
	
	public String getJapanese() {
		Object obj = map.get("japanese");
		if(obj instanceof Map) {
			StringBuilder builder = new StringBuilder();
			expandPartSpeech(builder, (Map<String, ?>) obj);
			return builder.toString();
		} else if(obj instanceof List) {
			StringBuilder builder = new StringBuilder();
			expandLevel = 0;
			expandArray(builder, (List<?>) obj);
			return builder.toString();
		} else {
			return (String) map.get("japanese");
		}
	}
	public void setJapanese(String japanese) {
		map.put("japanese", japanese);
	}

	public String getMp3() {
		return (String) map.get("mp3");
	}
	public void setMp3(String mp3) {
		map.put("mp3", mp3);
	}

	public String getSynosym() {
		return (String) map.get("synonym");
	}
	public void setSynosym(String synosym) {
		map.put("synonym", synosym);
	}
	
	private void expandPartSpeech(StringBuilder builder, Map<String, ?> map) {
		boolean isFirst = true;
//		Set<String> keys = map.keySet(); 
		for(String key : map.keySet()) {
			if(!isFirst) {
				builder.append("\n");
			}
			isFirst = false;
			if(key.equals("noun")) {
				builder.append("【名】 ");
			} else if(key.equals("transitive")) {
				builder.append("【他】 ");
			} else if(key.equals("intransitive")) {
				builder.append("【自】 ");
			} else if(key.equals("adjective")) {
				builder.append("【形】 ");
			} else if(key.equals("adverb")) {
				builder.append("【副】 ");
			} else if(key.equals("preposition")) {
				builder.append("【前】 ");
			} else if(key.equals("conjunction")) {
				builder.append("【接】");
			} else {
				throw new AssertionError(key + "は未定義です。");
			}
			expandLevel = 0;
			expandArray(builder, (List<?>) map.get(key));
		}
	}
	
	private void expandArray(StringBuilder builder, List<?> lists) {
		++expandLevel;
		boolean isFirst = true;
		for(Object obj : lists) {
			if(!isFirst) {
				if(expandLevel == 1) {
					builder.append("; ");
				} else if(expandLevel == 2) {
					builder.append(", ");
				} else {
					throw new AssertionError("expandLevel = " + expandLevel + " には対応していません");
				}
			}
			isFirst = false;
			if(obj instanceof List) {
				expandArray(builder, (List<?>) obj);
			} else {
				builder.append(obj);
			}
		}
		--expandLevel;
	}

	private Map<String, Object> map;
	private int expandLevel;
}
