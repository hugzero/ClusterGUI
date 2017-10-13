package com.xwtz.platform.test;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

public final class JProcessBarTest extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final String STRING = "Completed:";

	private JProgressBar progressBar = new JProgressBar();
	private JTextField textField = new JTextField(10);
	private JButton start = new JButton("start");
	private JButton end = new JButton("end");

	private boolean state = false;
	private int count = 0;

	// 工作线程workThead
	private WorkThead workThead = null;
	private Runnable run = null;

	public JProcessBarTest() {

		this.setLayout(new FlowLayout());
		this.add(progressBar);
		textField.setEditable(false);
		this.add(textField);
		this.add(start);
		this.add(end);

		// 开始按钮的监听器，负责由事件触发后创建工作线程
		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				state = true;
				if (workThead == null) {
					workThead = new WorkThead(progressBar, textField, state);
					new Thread(workThead).start();
				}
			}
		});

		end.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				state = false;
			}
		});

	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(JProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	public JTextField getTextField() {
		return textField;
	}

	public void setTextField(JTextField textField) {
		this.textField = textField;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		JProcessBarTest jTest = new JProcessBarTest();
		jTest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jTest.setSize(400, 400);
		jTest.setVisible(true);

	}

}
