package com.xwtz.platform.dealing;

public interface ProgressIndicator {
	public void setProgress(long count, long iterations, String text);

	public void enableProgress();

	public void showProgress(String progressText);

	public void dispose();
}
