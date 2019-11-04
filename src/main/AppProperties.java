package main;

import java.util.ResourceBundle;

public class AppProperties {

	public String inputPath;

	public String outputPath;

	public AppProperties() {
		inputPath = ResourceBundle.getBundle("app").getString("input-path");
		outputPath = ResourceBundle.getBundle("app").getString("output-path");
	}

}
