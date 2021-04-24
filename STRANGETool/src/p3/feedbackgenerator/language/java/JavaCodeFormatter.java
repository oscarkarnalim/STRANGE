package p3.feedbackgenerator.language.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.google.googlejavaformat.java.Formatter;

public class JavaCodeFormatter {
	public static boolean formatCode(String filepath) throws Exception {
		/*
		 * Convert the code to its beautiful format with the help of Google Java
		 * Format VERSION 1.0. The method returns true if the formatting is successful
		 */

		String content = "";
		BufferedReader br = new BufferedReader(new FileReader(
				new File(filepath)));
		String line;
		while ((line = br.readLine()) != null) {
			// only consider lines with non-whitespaces
			if (line.trim().length() > 0) {
				content = content + line + "\n";
			}
		}
		br.close();
		
		
		try {
			content = new Formatter().formatSource(content);
			// write it as a file
			FileWriter fw = new FileWriter(new File(filepath + ".formatted"));
			fw.write(content.replaceAll("\n\n", "\n"));
			fw.close();
			return true;
		} catch (Exception e) {
			FileWriter fw = new FileWriter(new File(filepath + ".formatted"));
			fw.write(content);
			fw.close();
			return false;
		}
	}
}
