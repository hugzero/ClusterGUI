package com.xwtz.platform.dealing;

import com.xwtz.platform.ui.dialog.MainFrameDialog;

public class DataDealRunner implements IDataDealRunner {
	private final MainFrameDialog mainFrameDialog;
	private final DataDeal dataDeal;
	private boolean cancelled;

	public DataDealRunner(MainFrameDialog mainFrameDialog, DataDeal dataDeal) {
		this.mainFrameDialog = mainFrameDialog;
		this.dataDeal = dataDeal;
	}

	@Override
	public void cancel() {
		// mainFrameDialog.cancel();
		mainFrameDialog.showProgress("Stopping back test...");
		cancelled = true;
	}

	@Override
	public void run() {
		try {
			mainFrameDialog.enableProgress();
			mainFrameDialog.showProgress("Scanning historical data file...");
			long marketDepthCounter = 0;
			// long size = dataDeal.getTotalSizes();
			// marketDepthCounter = dataDeal.getLineNumber();
			if (!cancelled) {
				mainFrameDialog.showProgress("Running back test...");
				// BackTester backTester = new BackTester(strategy,
				// backTestReader, backTestProgressIndicator);
				// backTester.execute();
				if (marketDepthCounter % 10000 == 0) {
					// mainFrameDialog.setProgress(marketDepthCounter, size,
					// "Running back test");
				}
			}
		} catch (Exception ex) {
			ex.getMessage();
		} finally {
			mainFrameDialog.dispose();
		}
	}

}
