package com.xrea.s7.jeisi.vocabularytraining.wordframe;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.LayerUI;

public class ResultLayerUI extends LayerUI<JComponent> {
	
	public void setVisible(boolean isVisible) {
		m_isVisible = isVisible;
	}
	
	 @Override
	  public void paint(Graphics g, JComponent c) {
	    super.paint(g, c);

	    if(!m_isVisible) {
	    	return;
	    }
	    
	    Graphics2D g2 = (Graphics2D) g.create();

	    Font font = new Font(Font.DIALOG_INPUT, Font.BOLD, 48);
	    g2.setFont(font);
	    g2.setColor(Color.RED);
	    
	    String message = "正解";
	    FontMetrics fm = g2.getFontMetrics();
	    Rectangle rect = fm.getStringBounds(message, g2).getBounds();
	    
	    int w = c.getWidth();
	    int h = c.getHeight();
	    g2.drawString("正解", (w - rect.width) / 2, (h - rect.height) / 2 + fm.getMaxAscent());
	    g2.dispose();
	 }
	
	private static final long serialVersionUID = 1L;
	
	private boolean m_isVisible = false;
}
