package com.xwtz.platform.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import com.xwtz.platform.dealing.DataDeal;
import com.xwtz.platform.dealing.IDataDealRunner;
import com.xwtz.platform.dealing.ProgressIndicator;
import com.xwtz.platform.util.FileChooser;
import com.xwtz.platform.util.SpringUtilities;

public class MainFrameDialog extends JFrame implements ProgressIndicator {
	// 得到显示器屏幕的宽高
	private int width = Toolkit.getDefaultToolkit().getScreenSize().width;
	private int height = Toolkit.getDefaultToolkit().getScreenSize().height;
	// 定义窗体的宽高
	private int windowsWedth = 600;
	private int windowsHeight = 200;
	private JTextField fileNameText, outPutfileNameText, gidText;
	private JButton outPutFileButton, confirmButton, selectFileButton, cancelButton;
	private JProgressBar progressBar;
	private IDataDealRunner iddr;

	public MainFrameDialog() {
		init();
		assignListeners();
	}

	private void init() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("聚类计算 ");
		setLayout(new BorderLayout());
		// data file choosing panel
		JPanel filePanel = new JPanel(new SpringLayout());
		JLabel fileNameLabel = new JLabel("选择导入文件:", JLabel.TRAILING);
		fileNameText = new JTextField();
		// fileNameText.setText(prefs.get(BackTesterFileName));
		selectFileButton = new JButton("...");
		// fileNameLabel.setLabelFor(fileNameText);
		filePanel.add(fileNameLabel);
		filePanel.add(fileNameText);
		filePanel.add(selectFileButton);
		SpringUtilities.makeTopOneLineGrid(filePanel);

		// dataout file choosing panel
		JPanel outfilePanel = new JPanel(new SpringLayout());
		// JLabel outfileNameLabel = new JLabel("选择要导出到:", JLabel.TRAILING);
		outPutfileNameText = new JTextField();
		outPutfileNameText.setText("默认导出到D盘根目录,文件名为deal_out");
		// 不可编辑
		outPutfileNameText.setEditable(false);
		// 居中
		outPutfileNameText.setHorizontalAlignment(JTextField.CENTER);
		// outPutFileButton = new JButton("...");
		// fileNameLabel.setLabelFor(fileNameText);
		// outfilePanel.add(outfileNameLabel);
		gidText = new JTextField();
		// gidText.setText("请填写gid,暂时只支持一个gid");
		outfilePanel.add(gidText);
		outfilePanel.add(outPutfileNameText);
		// outfilePanel.add(outPutFileButton);

		SpringUtilities.makeTopOneLineGrid(outfilePanel);

		// putting file panel and date filter panel into one panel called
		// northPanel
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(filePanel, BorderLayout.NORTH);
		northPanel.add(outfilePanel, BorderLayout.SOUTH);

		JPanel centerPanel = new JPanel(new SpringLayout());
		progressBar = new JProgressBar();
		progressBar.setVisible(false);
		progressBar.setStringPainted(true);
		centerPanel.add(progressBar);
		SpringUtilities.makeOneLineGrid(centerPanel);

		JPanel southPanel = new JPanel();
		confirmButton = new JButton("确定处理操作");
		confirmButton.setMnemonic('B');
		// cancelButton = new JButton("Cancel");
		// cancelButton.setMnemonic('C');
		// southPanel.add(cancelButton);
		southPanel.add(confirmButton);

		add(northPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
		// 设置窗体可见
		this.setVisible(true);
		// 设置窗体位置和大小
		this.setBounds((width - windowsWedth) / 2, (height - windowsHeight) / 2, windowsWedth, windowsHeight);
	}

	private void assignListeners() {
		selectFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileChooser.fillInTextField(fileNameText, "Select  Data File", getFileName());
				System.out.println(getGidText());
			}
		});
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getFileName() != null && !getFileName().isEmpty() && getGidText() != null
						&& !getGidText().isEmpty()) {
					DataDeal dataDeal = new DataDeal(getFileName(), getGidText(), MainFrameDialog.this);
					new Thread(dataDeal).start();
				} else {
					JOptionPane.showMessageDialog(getContentPane(), "请检查是否选择文件或填写gid！");
				}
			}
		});
		/*
		 * cancelButton.addActionListener(new ActionListener() { public void
		 * actionPerformed(ActionEvent e) { if (iddr != null) { iddr.cancel(); }
		 * dispose(); } });
		 */
	}

	public String getFileName() {
		return fileNameText.getText();
	}

	public String getOutFileName() {
		return outPutfileNameText.getText();
	}

	public String getGidText() {
		return gidText.getText();
	}

	@Override
	public void setProgress(long count, long iterations, String text) {
		int percent = (int) (100 * (count / (double) iterations));
		progressBar.setValue(percent);
		progressBar.setString(text);
		// + ": " + percent + "%");
	}

	@Override
	public void enableProgress() {
		progressBar.setValue(0);
		progressBar.setVisible(true);
		progressBar.setString("Starting back test...");
		// confirmButton.setEnabled(false);
		// cancelButton.setEnabled(true);
		// getRootPane().setDefaultButton(cancelButton);
	}

	@Override
	public void showProgress(String progressText) {
		progressBar.setValue(0);
		progressBar.setString(progressText);
	}
}
