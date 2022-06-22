package p3;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class AssignmentMapper {
	
	public static void assignCode(String rootDirFilepath, String ext, String[] regexs){
		String outputRootDirFilepath = rootDirFilepath.substring(0,
				rootDirFilepath.lastIndexOf(File.separator) + 1)
				+ "[grouped] "
				+ rootDirFilepath.substring(
						rootDirFilepath.lastIndexOf(File.separator) + 1,
						rootDirFilepath.length());
		
		// create output directory
		new File(outputRootDirFilepath).mkdir();

		// create the taskfilepaths
		String[] taskFilepaths = new String[regexs.length + 1];
		// per regex
		for (int i = 0; i < regexs.length; i++) {
			taskFilepaths[i] = outputRootDirFilepath + File.separator
					+ regexs[i].replaceAll("[^a-zA-Z0-9]", "");
			// create task output directory
			new File(taskFilepaths[i]).mkdir();
		}
		// add another dir for others, those which do not match to any regexes
		taskFilepaths[regexs.length] = outputRootDirFilepath + File.separator + "uncategorised";
		

		// for each student dir, take the code files and map them according to
		// their defined task
		File rootDir = new File(rootDirFilepath);
		File[] studentDir = rootDir.listFiles();
		for (File sdir : studentDir) {
			if (sdir.isDirectory())
				listSourceCodeFiles(sdir, ext, regexs, taskFilepaths,
						sdir.getName());
		}
	}

	public static void listSourceCodeFiles(File sfile, String ext,
			String[] regexs, String[] taskFilepaths, String studentId) {
		if (sfile.isDirectory()) {
			File[] schildren = sfile.listFiles();
			for (File sc : schildren) {
				listSourceCodeFiles(sc, ext, regexs, taskFilepaths, studentId);
			}
		} else {
			String name = sfile.getName();
			// if the file does not end with the extension, ignore
			if (name.toLowerCase().endsWith(ext) == false)
				return;

			boolean isMapped = false;

			// check whether the filename match a regex
			for (int i = 0; i < regexs.length; i++) {
				if (name.matches(regexs[i])) {
					// make a directory stating the student
					File targetDir = new File(taskFilepaths[i] + File.separator
							+ studentId);
					targetDir.mkdir();

					try {
						Files.copy(sfile.toPath(),
								new File(targetDir.getAbsolutePath() + File.separator
										+ name).toPath(),
								StandardCopyOption.REPLACE_EXISTING);
						isMapped = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
			}

			// otherwise, put it on an 'other' section
			if (isMapped == false) {
				// create 'other' directory
				new File(taskFilepaths[regexs.length]).mkdir();
				
				// make a directory stating the student
				File targetDir = new File(
						taskFilepaths[taskFilepaths.length - 1] + File.separator
								+ studentId);
				targetDir.mkdir();

				try {
					Files.copy(sfile.toPath(),
							new File(targetDir + File.separator + name).toPath(),
							StandardCopyOption.REPLACE_EXISTING);
					isMapped = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
