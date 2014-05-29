package com.xrea.s7.jeisi.vocabularytraining.main;

import java.awt.Point;
import java.util.HashMap;

public class Config {
	public void saveLocation(ConfigurableFrame frame) {
		String key = frame.getIdentifer();
		HashMap<String, Object> info = frameInfoMap.get(key);
		if(info == null) {
			info = new HashMap<String, Object>();
			frameInfoMap.put(key, info);
		}
		
		Point p = frame.getLocation();
		info.put("locationX", p.x);
		info.put("locationY", p.y);
	}
	
	public void restoreLocation(ConfigurableFrame frame) {
		String key = frame.getIdentifer();
		HashMap<String, Object> info = frameInfoMap.get(key);
		if(info == null) {
			return;
		}
		
		int x = (int) info.get("locationX");
		int y = (int) info.get("locationY");
		Point p = new Point(x, y);
		frame.setLocation(p);
	}
	
	public HashMap<String, HashMap<String, Object>> getFrameInfoMap() {
		return frameInfoMap;
	}

	public void setFrameInfoMap(HashMap<String, HashMap<String, Object>> frameInfoMap) {
		this.frameInfoMap = frameInfoMap;
	}

	private HashMap<String, HashMap<String, Object>> frameInfoMap = new HashMap<>();
}
