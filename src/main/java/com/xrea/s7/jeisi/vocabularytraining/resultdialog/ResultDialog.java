package com.xrea.s7.jeisi.vocabularytraining.resultdialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class ResultDialog extends JDialog {

	public ResultDialog(JFrame parent, ResultInfo resultInfo) {
		super(parent);
		this.resultInfo = resultInfo;

		setLocationRelativeTo(parent);
	}
	
	public void open() {
		buildCenterPane();
		buildSouthPane();
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Result Dialog");
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		pack();
		setVisible(true);
	}
	
	public ResultType getResultType() {
		return resultType;
	}
	
	private void buildCenterPane() {
		JPanel panel = new JPanel();
		
		JLabel label = new JLabel("Accuracy Rate: ");
		panel.add(label);
		
		JLabel resultLabel = new JLabel(resultInfo.toString());
		panel.add(resultLabel);
		resultLabel.setFont(new Font("メイリオ", Font.PLAIN, 20));
		
		add(panel, BorderLayout.CENTER);
	}
	
	private void buildSouthPane() {
		JPanel panel = new JPanel();
		
		m_retryButton = new JButton("Retry");
		panel.add(m_retryButton);
		m_retryButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resultType = ResultType.RETRY;
				dispose();
			}
		});
		
		m_quitButton = new JButton("Quit");
		panel.add(m_quitButton);
		m_quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resultType = ResultType.QUIT;
				dispose();
			}
		});
		
		add(panel, BorderLayout.SOUTH);
	}
	
	private static final long serialVersionUID = 1L;
	
	private ResultInfo resultInfo;
	private ResultType resultType = ResultType.NONE;
	private JButton m_retryButton;
	private JButton m_quitButton;
}
