package main;

import java.util.ResourceBundle;

public class AppProperties {
	
	public static String getInputPath() {
		return ResourceBundle.getBundle("app").getString("input-path");
	}

	public static String getOutputPath() {
		return ResourceBundle.getBundle("app").getString("output-path");
	}

	//path where temporary files are stored
	public static String getTmpPath() {
		return ResourceBundle.getBundle("app").getString("tmp-path");
	}

}
