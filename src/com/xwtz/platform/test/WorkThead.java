package com.xwtz.platform.test;

import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class WorkThead implements Runnable {
	private int count = 0;
	private boolean state = false;
	private static final String STRING = "Completed:";
	private final JProgressBar progressBar;
	private final JTextField textField;

	public WorkThead(JProgressBar progressBar, JTextField textField, boolean state) {
		this.progressBar = progressBar;
		this.textField = textField;
		this.state = state;
	}

	@Override
	public void run() {
		while (count < 100) {

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (state) {
				count++;
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						// 更新操作通过事件派发线程完成（一般实现Runnable()接口）
						progressBar.setValue(count);
						textField.setText(STRING + String.valueOf(count) + "%");
					}
				});
			}
		}
	}

}
