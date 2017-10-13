package com.xwtz.platform.dealing;

import java.io.File;

public class Test {
	public static void main(String[] args) throws Exception {
		String fileName = "D:" + File.separator + "fewlkfe";
		File file = new File(fileName);
		file.mkdir();
		File newfile = new File(fileName + File.separator + "deal_out.xls");
		if (!newfile.exists()) {
			newfile.createNewFile();
		}
	}
}
