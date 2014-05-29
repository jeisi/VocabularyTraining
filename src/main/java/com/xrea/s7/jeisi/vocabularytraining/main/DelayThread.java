package com.xrea.s7.jeisi.vocabularytraining.main;

import javax.swing.SwingUtilities;

public abstract class DelayThread extends Thread {

	public DelayThread(Object param, int sleepMilliSec) {
		this.param = param;
		this.sleepMilliSec = sleepMilliSec;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(sleepMilliSec);
		} catch (InterruptedException e) {
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				exec(param);
			}
		});
	}
	
	protected abstract void exec(Object param);
	
	private int sleepMilliSec;
	private Object param;
}
