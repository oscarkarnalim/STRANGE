package p3;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class CodeMerger {

	public static void mergeCode(String rootDirFilepath, String ext) {
		String outputRootDirFilepath = rootDirFilepath.substring(0,
				rootDirFilepath.lastIndexOf(File.separator) + 1)
				+ "[merged] "
				+ rootDirFilepath.substring(
						rootDirFilepath.lastIndexOf(File.separator) + 1,
						rootDirFilepath.length());

		// create output directory
		new File(outputRootDirFilepath).mkdir();

		// for each student dir, take the code files and merge them as a file
		File rootDir = new File(rootDirFilepath);
		File[] studentDir = rootDir.listFiles();
		for (File sdir : studentDir) {
			if (sdir.isDirectory()) {
				File outputDir = new File(outputRootDirFilepath + File.separator
						+ sdir.getName());
				outputDir.mkdir();
				File outputFile = new File(outputDir.getAbsolutePath() + File.separator
						+ "[merged] " + sdir.getName() + "." + ext);
				// System.out.println(outputFile.getAbsolutePath());
				try {
					FileWriter fw = new FileWriter(outputFile);
					mergeSourceCodeFiles(sdir, ext, fw, sdir.getAbsolutePath().length());
					fw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void mergeSourceCodeFiles(File sfile, String ext,
			FileWriter fw, int studentDirPathLength) {
		if (sfile.isDirectory()) {
			File[] schildren = sfile.listFiles();
			for (File sc : schildren) {
				mergeSourceCodeFiles(sc, ext, fw, studentDirPathLength);
			}
		} else {
			String name = sfile.getName();
			// if the file does not end with the extension, ignore
			if (name.toLowerCase().endsWith(ext) == false)
				return;

			// read the file and write it in filewriter
			try {
				// write the path of the file as a comment
				String path = "Filepath: '" + sfile.getAbsolutePath().substring(studentDirPathLength+1) + "'";
				
				// begin a comment
				if(ext.endsWith("java")){
					String pattern ="/* ";
					for(int i=0;i<path.length();i++)
						pattern += "=";
					pattern += " */" + System.lineSeparator();
					
					fw.write(pattern);
					fw.write("/* " + path + " */" + System.lineSeparator());
					fw.write(pattern);
				}else if(ext.endsWith("py")){
					String pattern ="# ";
					for(int i=0;i<path.length();i++)
						pattern += "=";
					pattern += " #" + System.lineSeparator();
					
					fw.write(pattern);
					fw.write("# " + path + " #" + System.lineSeparator());
					fw.write(pattern);
				}
				
				Scanner sc = new Scanner(sfile);
				while(sc.hasNextLine()){
					fw.write(sc.nextLine() + System.lineSeparator());
				}
				sc.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static boolean isGeneratedFromCodeMerging(String s, String extension) {
		if (extension.toLowerCase().endsWith("java")) {
			if (s.matches("/\\* =+ \\*/"))
				return true;
			else if (s.matches("/\\* Filepath: '.+' \\*/"))
				return true;
			else
				return false;
		} else {
			// python
			if (s.matches("# =+ #"))
				return true;
			else if (s.matches("# Filepath: '.+' #"))
				return true;
			else
				return false;
		}
	}
}
