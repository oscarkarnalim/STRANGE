package p3;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

import p3.feedbackgenerator.htmlgenerator.ComparisonHTMLGenerator;
import p3.feedbackgenerator.htmlgenerator.JplagObserverUpdater;
import p3.feedbackgenerator.language.java.JavaHtmlGenerator;
import p3.feedbackgenerator.language.python.PythonCodeFormatter;
import p3.feedbackgenerator.language.python.PythonHtmlGenerator;
import support.stringmatching.AdditionalKeywordsManager;

public class Main {
	public static void main(String[] args) throws Exception {
		execute(args);
	}

	private static String python_compiler_path_file = "pythoncompilerpath.txt";
	private static String pair_template_path = "pair_html_template.html";
	private static String pair_template_path_id = "pair_html_template_id.html";
	private static String comparison_template_path = "core_html_template.html";
	private static String comparison_template_path_id = "core_html_template_id.html";
	private static String additional_dir_path = "strange_html_layout_additional_files";

	public static void execute(String[] args) throws Exception {
		// get the path of Python compiler from python_compiler_path_file
		File f = new File(python_compiler_path_file);
		if (f.exists()) {
			try {
				Scanner sc = new Scanner(f);
				PythonCodeFormatter.pythonCompilerPath = sc.nextLine();
				sc.close();
			} catch (Exception e) {
				// do nothing
			}
		}

		if (args.length == 0) {
			System.err.println("[English] Run this software with 'help' as the argument to show help.");
			System.err.println(
					"[Indonesia] Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
		} else {
			// start checking
			String mode = args[0];
			if (mode.equalsIgnoreCase("pair")) {
				executeMode1(args);
			} else if (mode.equalsIgnoreCase("pairi")) {
				executeMode1Idn(args);
			} else if (mode.equalsIgnoreCase("comp")) {
				executeMode2(args);
			} else if (mode.equalsIgnoreCase("compi")) {
				executeMode2Idn(args);
			} else if (mode.equalsIgnoreCase("jplag")) {
				executeMode3(args);
			} else if (mode.equalsIgnoreCase("jplagi")) {
				executeMode3Idn(args);
			} else if (mode.equalsIgnoreCase("group")) {
				executeGrouper(args);
			} else if (mode.equalsIgnoreCase("groupi")) {
				executeGrouperIdn(args);
			} else if (mode.equalsIgnoreCase("merge")) {
				executeMerger(args);
			} else if (mode.equalsIgnoreCase("mergei")) {
				executeMergerIdn(args);
			} else if (mode.equalsIgnoreCase("template")) {
				executeTemplateRemoval(args);
			} else if (mode.equalsIgnoreCase("templatei")) {
				executeTemplateRemovalIdn(args);
			} else if (mode.equalsIgnoreCase("help")) {
				showHelp();
			} else if (mode.equalsIgnoreCase("bantuan")) {
				tampilkanBantuan();
			} else {
				System.err.println(
						"[English] <mode> should be either 'pair', 'comp', 'jplag', 'group', 'merge', or 'template'.");
				System.err.println("Run this software with 'help' as the argument to show help.");
				System.err.println(
						"[Indonesian] <mode> harus bernilai 'pairi', 'compi', 'jplagi', 'groupi', 'mergei', or 'templatei'. ");
				System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
			}
		}
	}

	private static boolean executeTemplateRemoval(String[] args) throws Exception {
		if (args.length != 4) {
			System.err.println("[mode-6] template code removal");
			System.err.println("The number of arguments should be equal to four.");
			System.err.println("Run this software with 'help' as the argument to show help.");
			return false;
		}

		String assignment_root_dirpath = preparePathOrRegex(args[1]);
		if (isPathValidAndExist(assignment_root_dirpath) == false) {
			System.err.println("[mode-6] template code removal");
			System.err.println("<assignment_root_dirpath> is not a valid path or refers to a ");
			System.err.println("  nonexistent directory.");
			System.err.println("Run this software with 'help' as the argument to show help.");
			return false;
		}

		boolean temp = isProgrammingLanguageValid(args[2]);
		if (temp == false) {
			System.err.println("[mode-6] template code removal");
			System.err.println("<programming_language> should be either 'java' (for Java) or 'py' (for ");
			System.err.println("  Python).");
			System.err.println("Run this software with 'help' as the argument to show help.");
			return false;
		}
		String programming_language = args[2];

		String template_path = preparePathOrRegex(args[3]);
		if (template_path != null) {
			if (template_path.equals("null"))
				template_path = null;
			else if (isPathValidAndExist(template_path) == false) {
				System.err.println("[mode-6] template code removal");
				System.err.println("<template_path> is not a valid path or refers to ");
				System.err.println("  a nonexistent file.");
				System.err.println("Run this software with 'help' as the argument to show help.");
				return false;
			}
		}

		TemplateCodeRemoval.removeTemplateCode(assignment_root_dirpath, programming_language, template_path);

		System.out.println("The command has been executed succesfully!");
		String outputDirpath = assignment_root_dirpath.substring(0,
				assignment_root_dirpath.lastIndexOf(File.separator) + 1) + "[template removed] "
				+ assignment_root_dirpath.substring(assignment_root_dirpath.lastIndexOf(File.separator) + 1,
						assignment_root_dirpath.length());

		System.out.println("The result can be seen in '" + outputDirpath + "'.");

		return true;
	}

	private static boolean executeTemplateRemovalIdn(String[] args) throws Exception {
		if (args.length != 4) {
			System.err.println("[mode-6] penghapusan kode template");
			System.err.println("Jumlah argumen yang diberikan harus sama dengan 4.");
			System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
			return false;
		}

		String assignment_root_dirpath = preparePathOrRegex(args[1]);
		if (isPathValidAndExist(assignment_root_dirpath) == false) {
			System.err.println("[mode-6] penghapusan kode template");
			System.err.println("<assignment_root_dirpath> bukan path valid atau tidak mengarah ke sebuah");
			System.err.println("  direktori.");
			System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
			return false;
		}

		boolean temp = isProgrammingLanguageValid(args[2]);
		if (temp == false) {
			System.err.println("[mode-6] penghapusan kode template");
			System.err.println("<programming_language> harus bernilai 'java' (untuk Java) atau 'py'");
			System.err.println("  (untuk Python).");
			System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
			return false;
		}
		String programming_language = args[2];

		String template_path = preparePathOrRegex(args[3]);
		if (template_path != null) {
			if (template_path.equals("null"))
				template_path = null;
			else if (isPathValidAndExist(template_path) == false) {
				System.err.println("[mode-6] penghapusan kode template");
				System.err.println("<template_path> bukan path valid atau tidak mengarah pada ");
				System.err.println("  sebuah file.");
				System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
				return false;
			}
		}

		TemplateCodeRemoval.removeTemplateCode(assignment_root_dirpath, programming_language, template_path);

		System.out.println("Perintah telah berhasil dieksekusi dengan sukses!");
		String outputDirpath = assignment_root_dirpath.substring(0,
				assignment_root_dirpath.lastIndexOf(File.separator) + 1) + "[template removed] "
				+ assignment_root_dirpath.substring(assignment_root_dirpath.lastIndexOf(File.separator) + 1,
						assignment_root_dirpath.length());

		System.out.println("Hasil dapat dilihat di '" + outputDirpath + "'.");

		return true;
	}

	private static boolean executeMerger(String[] args) throws Exception {
		if (args.length != 3) {
			System.err.println("[mode-5] code merging");
			System.err.println("The number of arguments should be equal to three.");
			System.err.println("Run this software with 'help' as the argument to show help.");
			return false;
		}

		String assignment_root_dirpath = preparePathOrRegex(args[1]);
		if (isPathValidAndExist(assignment_root_dirpath) == false) {
			System.err.println("[mode-5] code merging");
			System.err.println("<assignment_root_dirpath> is not a valid path or refers to a ");
			System.err.println("  nonexistent directory.");
			System.err.println("Run this software with 'help' as the argument to show help.");
			return false;
		}

		boolean temp = isProgrammingLanguageValid(args[2]);
		if (temp == false) {
			System.err.println("[mode-5] code merging");
			System.err.println("<programming_language> should be either 'java' (for Java) or 'py' (for ");
			System.err.println("  Python).");
			System.err.println("Run this software with 'help' as the argument to show help.");
			return false;
		}
		String programming_language = args[2];

		CodeMerger.mergeCode(assignment_root_dirpath, programming_language);

		System.out.println("The command has been executed succesfully!");
		String outputDirpath = assignment_root_dirpath.substring(0,
				assignment_root_dirpath.lastIndexOf(File.separator) + 1) + "[merged] "
				+ assignment_root_dirpath.substring(assignment_root_dirpath.lastIndexOf(File.separator) + 1,
						assignment_root_dirpath.length());

		System.out.println("The result can be seen in '" + outputDirpath + "'.");

		return true;
	}

	private static boolean executeMergerIdn(String[] args) throws Exception {
		if (args.length != 3) {
			System.err.println("[mode-5] penggabungan file kode tugas");
			System.err.println("Jumlah argumen yang diberikan harus sama dengan 3.");
			System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
			return false;
		}

		String assignment_root_dirpath = preparePathOrRegex(args[1]);
		if (isPathValidAndExist(assignment_root_dirpath) == false) {
			System.err.println("[mode-5] penggabungan file kode tugas");
			System.err.println("<assignment_root_dirpath> bukan path valid atau tidak mengarah ke sebuah");
			System.err.println("  direktori.");
			System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
			return false;
		}

		boolean temp = isProgrammingLanguageValid(args[2]);
		if (temp == false) {
			System.err.println("[mode-5] penggabungan file kode tugas");
			System.err.println("<programming_language> harus bernilai 'java' (untuk Java) atau 'py'");
			System.err.println("  (untuk Python).");
			System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
			return false;
		}
		String programming_language = args[2];

		CodeMerger.mergeCode(assignment_root_dirpath, programming_language);

		System.out.println("Perintah telah berhasil dieksekusi dengan sukses!");
		String outputDirpath = assignment_root_dirpath.substring(0,
				assignment_root_dirpath.lastIndexOf(File.separator) + 1) + "[merged] "
				+ assignment_root_dirpath.substring(assignment_root_dirpath.lastIndexOf(File.separator) + 1,
						assignment_root_dirpath.length());

		System.out.println("Hasil dapat dilihat di '" + outputDirpath + "'.");

		return true;
	}

	private static void generateAdditionalDir(String targetDirPath) throws Exception {
		// create the dir
		File dir = new File(targetDirPath + File.separator + "strange_html_layout_additional_files");
		if (dir.exists() == false)
			dir.mkdir();

		// copy icon
		File source = new File(additional_dir_path + File.separator + "icon.png");
		File target = new File(dir.getAbsolutePath() + File.separator + "icon.png");
		if (target.exists() == false)
			Files.copy(source.toPath(), target.toPath());

		// copy logo
		source = new File(additional_dir_path + File.separator + "logo.png");
		target = new File(dir.getAbsolutePath() + File.separator + "logo.png");
		if (target.exists() == false)
			Files.copy(source.toPath(), target.toPath());

		// copy sort icon
		source = new File(additional_dir_path + File.separator + "sort icon.png");
		target = new File(dir.getAbsolutePath() + File.separator + "sort icon.png");
		if (target.exists() == false)
			Files.copy(source.toPath(), target.toPath());

		// copy jquery
		source = new File(additional_dir_path + File.separator + "run_prettify.js");
		target = new File(dir.getAbsolutePath() + File.separator + "run_prettify.js");
		if (target.exists() == false)
			Files.copy(source.toPath(), target.toPath());
	}

	private static boolean executeGrouper(String[] args) throws Exception {
		if (args.length < 4) {
			System.err.println("[mode-4] code grouping");
			System.err.println("The number of arguments should be greater or equal to four.");
			System.err.println("Run this software with 'help' as the argument to show help.");
			return false;
		}

		String assignment_root_dirpath = preparePathOrRegex(args[1]);
		if (isPathValidAndExist(assignment_root_dirpath) == false) {
			System.err.println("[mode-4] code grouping");
			System.err.println("<assignment_root_dirpath> is not a valid path or refers to a ");
			System.err.println("  nonexistent directory.");
			System.err.println("Run this software with 'help' as the argument to show help.");
			return false;
		}

		boolean temp = isProgrammingLanguageValid(args[2]);
		if (temp == false) {
			System.err.println("[mode-4] code grouping");
			System.err.println("<programming_language> should be either 'java' (for Java) or 'py' (for ");
			System.err.println("  Python).");
			System.err.println("Run this software with 'help' as the argument to show help.");
			return false;
		}
		String programming_language = args[2];

		// get the regexes
		String[] regexes = new String[args.length - 3];
		for (int i = 3; i < args.length; i++) {
			regexes[i - 3] = preparePathOrRegex(args[i]);
		}

		AssignmentMapper.assignCode(assignment_root_dirpath, programming_language, regexes);

		System.out.println("The command has been executed succesfully!");
		String outputDirpath = assignment_root_dirpath.substring(0,
				assignment_root_dirpath.lastIndexOf(File.separator) + 1) + "[grouped] "
				+ assignment_root_dirpath.substring(assignment_root_dirpath.lastIndexOf(File.separator) + 1,
						assignment_root_dirpath.length());

		System.out.println("The result can be seen in '" + outputDirpath + "'.");

		return true;
	}

	private static boolean executeGrouperIdn(String[] args) throws Exception {
		if (args.length < 4) {
			System.err.println("[mode-4] pengelompokan kode tugas");
			System.err.println("Jumlah argumen yang diberikan harus lebih besar sama dengan 4.");
			System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
			return false;
		}

		String assignment_root_dirpath = preparePathOrRegex(args[1]);
		if (isPathValidAndExist(assignment_root_dirpath) == false) {
			System.err.println("[mode-4] pengelompokan kode tugas");
			System.err.println("<assignment_root_dirpath> bukan path valid atau tidak mengarah ke sebuah");
			System.err.println("  direktori.");
			System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
			return false;
		}

		boolean temp = isProgrammingLanguageValid(args[2]);
		if (temp == false) {
			System.err.println("[mode-4] pengelompokan kode tugas");
			System.err.println("<programming_language> harus bernilai 'java' (untuk Java) atau 'py'");
			System.err.println("  (untuk Python).");
			System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
			return false;
		}
		String programming_language = args[2];

		// get the regexes
		String[] regexes = new String[args.length - 3];
		for (int i = 3; i < args.length; i++) {
			regexes[i - 3] = preparePathOrRegex(args[i]);
		}

		AssignmentMapper.assignCode(assignment_root_dirpath, programming_language, regexes);

		System.out.println("Perintah telah berhasil dieksekusi dengan sukses!");
		String outputDirpath = assignment_root_dirpath.substring(0,
				assignment_root_dirpath.lastIndexOf(File.separator) + 1) + "[grouped] "
				+ assignment_root_dirpath.substring(assignment_root_dirpath.lastIndexOf(File.separator) + 1,
						assignment_root_dirpath.length());

		System.out.println("Hasil dapat dilihat di '" + outputDirpath + "'.");

		return true;
	}

	private static boolean executeMode1(String[] args) throws Exception {
		if (args.length != 5 && args.length != 10) {
			System.err.println("[mode-1] pair observation");
			System.err.println("The number of arguments should be either five (for quick execution ");
			System.err.println("  command) or ten (for complete execution command).");
			System.err.println("Run this software with 'help' as the argument to show help.");
			return false;
		}

		String codepath1 = preparePathOrRegex(args[1]);
		if (isPathValidAndExist(codepath1) == false) {
			System.err.println("[mode-1] pair observation");
			System.err.println("<codepath1> is not a valid path or refers to a nonexistent file.");
			System.err.println("Run this software with 'help' as the argument to show help.");
			return false;
		}

		String codepath2 = preparePathOrRegex(args[2]);
		if (isPathValidAndExist(codepath2) == false) {
			System.err.println("[mode-1] pair observation");
			System.err.println("<codepath2> is not a valid path or refers to a nonexistent file.");
			System.err.println("Run this software with 'help' as the argument to show help.");
			return false;
		}

		boolean temp = isProgrammingLanguageValid(args[3]);
		if (temp == false) {
			System.err.println("[mode-1] pair observation");
			System.err.println("<programming_language> should be either 'java' (for Java) or 'py' (for ");
			System.err.println("  Python).");
			System.err.println("Run this software with 'help' as the argument to show help.");
			return false;
		}
		String programming_language = args[3];

		String target_html_filepath = preparePathOrRegex(args[4]);
		if (isPathValid(target_html_filepath) == false) {
			System.err.println("[mode-1] pair observation");
			System.err.println("<target_html_filepath> is not a valid path.");
			System.err.println("Run this software with 'help' as the argument to show help.");
			return false;
		}

		String projectname1, projectname2, home_filepath, human_language, additional_keywords_path;
		int syntax_minimum_match_length;

		if (args.length == 10) {
			projectname1 = args[5];
			if (isNameValid(projectname1) == false) {
				System.err.println("[mode-1] pair observation");
				System.err.println("<projectname1> is either null or the empty string.");
				System.err.println("Run this software with 'help' as the argument to show help.");
				return false;
			}

			projectname2 = args[6];
			if (isNameValid(projectname2) == false) {
				System.err.println("[mode-1] pair observation");
				System.err.println("<projectname2> is either null or the empty string.");
				System.err.println("Run this software with 'help' as the argument to show help.");
				return false;
			}

			home_filepath = args[7];
			if (home_filepath != null) {
				// to deal if null is passed as a string.
				if (home_filepath.equals("null"))
					home_filepath = null;
				else if (isPathValidAndExist(home_filepath) == false) {
					System.err.println("[mode-1] pair observation");
					System.err.println("<home_filepath> is not a valid path or refers to a nonexistent file.");
					System.err.println("Run this software with 'help' as the argument to show help.");
					return false;
				}
			}

			Integer tempN = prepareMinimumMatchingLength(args[8]);
			if (tempN == null) {
				System.err.println("[mode-1] pair observation");
				System.err.println("<syntax_minimum_match_length> is not a valid positive");
				System.err.println("  integer.");
				System.err.println("Run this software with 'help' as the argument to show help.");
				return false;
			}
			syntax_minimum_match_length = tempN;

			additional_keywords_path = preparePathOrRegex(args[9]);
			if (additional_keywords_path != null) {
				if (additional_keywords_path.equals("null"))
					additional_keywords_path = null;
				else if (isPathValidAndExist(additional_keywords_path) == false) {
					System.err.println("[mode-1] pair observation");
					System.err.println("<additional_keywords_path> is not a valid path or refers to ");
					System.err.println("  a nonexistent file.");
					System.err.println("Run this software with 'help' as the argument to show help.");
					return false;
				}
			}

		} else {
			projectname1 = codepath1;
			projectname2 = codepath2;
			home_filepath = null;
			syntax_minimum_match_length = 2;
			additional_keywords_path = null;
		}

		// read the keywords
		ArrayList<ArrayList<String>> additional_keywords = AdditionalKeywordsManager
				.readAdditionalKeywords(additional_keywords_path);

		human_language = "en";
		if (programming_language.equals("java")) {
			JavaHtmlGenerator.generateHtml(codepath1, codepath2, projectname1, projectname2, pair_template_path,
					target_html_filepath, home_filepath, syntax_minimum_match_length, human_language,
					additional_keywords);
		} else if (programming_language.equals("py")) {
			PythonHtmlGenerator.generateHtml(codepath1, codepath2, projectname1, projectname2, pair_template_path,
					target_html_filepath, home_filepath, syntax_minimum_match_length, human_language,
					additional_keywords);
		}

		generateAdditionalDir(((new File(target_html_filepath).getAbsoluteFile()).getParentFile()).getAbsolutePath());

		System.out.println("The command has been executed succesfully!");
		System.out.println("The result can be seen in '" + target_html_filepath + "'.");

		return true;
	}

	private static boolean executeMode1Idn(String[] args) throws Exception {
		if (args.length != 5 && args.length != 10) {
			System.err.println("[mode-1] pengamatan pasangan kode sumber");
			System.err.println("Jumlah argumen yang diberikan harus lima (untuk eksekusi standar)");
			System.err.println("  atau sepuluh (untuk eksekusi komprehensif).");
			System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
			return false;
		}

		String codepath1 = preparePathOrRegex(args[1]);
		if (isPathValidAndExist(codepath1) == false) {
			System.err.println("[mode-1] pengamatan pasangan kode sumber");
			System.err.println("<codepath1> bukan path valid atau tidak mengarah pada sebuah file.");
			System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
			return false;
		}

		String codepath2 = preparePathOrRegex(args[2]);
		if (isPathValidAndExist(codepath2) == false) {
			System.err.println("[mode-1] pengamatan pasangan kode sumber");
			System.err.println("<codepath2> bukan path valid atau tidak mengarah pada sebuah file.");
			System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
			return false;
		}

		boolean temp = isProgrammingLanguageValid(args[3]);
		if (temp == false) {
			System.err.println("[mode-1] pengamatan pasangan kode sumber");
			System.err.println("<programming_language> harus bernilai 'java' (untuk Java) or 'py'");
			System.err.println("  (untuk Python).");
			System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
			return false;
		}
		String programming_language = args[3];

		String target_html_filepath = preparePathOrRegex(args[4]);
		if (isPathValid(target_html_filepath) == false) {
			System.err.println("[mode-1] pengamatan pasangan kode sumber");
			System.err.println("<target_html_filepath> bukan path yang valid.");
			System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
			return false;
		}

		String projectname1, projectname2, home_filepath, human_language, additional_keywords_path;
		int syntax_minimum_match_length;

		if (args.length == 10) {
			projectname1 = args[5];
			if (isNameValid(projectname1) == false) {
				System.err.println("[mode-1] pengamatan pasangan kode sumber");
				System.err.println("<projectname1> bernilai null atau string kosong.");
				System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
				return false;
			}

			projectname2 = args[6];
			if (isNameValid(projectname2) == false) {
				System.err.println("[mode-1] pengamatan pasangan kode sumber");
				System.err.println("<projectname2> bernilai null atau string kosong.");
				System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
				return false;
			}

			home_filepath = args[7];
			if (home_filepath != null) {
				// to deal if null is passed as a string.
				if (home_filepath.equals("null"))
					home_filepath = null;
				else if (isPathValidAndExist(home_filepath) == false) {
					System.err.println("[mode-1] pengamatan pasangan kode sumber");
					System.err.println("<home_filepath> bukan path valid atau tidak mengarah pada sebuah file.");
					System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
					return false;
				}
			}

			Integer tempN = prepareMinimumMatchingLength(args[8]);
			if (tempN == null) {
				System.err.println("[mode-1] pengamatan pasangan kode sumber");
				System.err.println("<syntax_minimum_match_length> bukan bilangan bulat positif valid.");
				System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
				return false;
			}
			syntax_minimum_match_length = tempN;

			additional_keywords_path = preparePathOrRegex(args[9]);
			if (additional_keywords_path != null) {
				if (additional_keywords_path.equals("null"))
					additional_keywords_path = null;
				else if (isPathValidAndExist(additional_keywords_path) == false) {
					System.err.println("[mode-1] pengamatan pasangan kode sumber");
					System.err.println("<additional_keywords_path> bukan path valid atau tidak mengarah pada ");
					System.err.println("  sebuah file.");
					System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
					return false;
				}
			}

		} else {
			projectname1 = codepath1;
			projectname2 = codepath2;
			home_filepath = null;
			syntax_minimum_match_length = 2;
			additional_keywords_path = null;
		}

		// read the keywords
		ArrayList<ArrayList<String>> additional_keywords = AdditionalKeywordsManager
				.readAdditionalKeywords(additional_keywords_path);

		human_language = "id";
		if (programming_language.equals("java")) {
			JavaHtmlGenerator.generateHtml(codepath1, codepath2, projectname1, projectname2, pair_template_path_id,
					target_html_filepath, home_filepath, syntax_minimum_match_length, human_language,
					additional_keywords);
		} else if (programming_language.equals("py")) {
			PythonHtmlGenerator.generateHtml(codepath1, codepath2, projectname1, projectname2, pair_template_path_id,
					target_html_filepath, home_filepath, syntax_minimum_match_length, human_language,
					additional_keywords);
		}

		generateAdditionalDir(((new File(target_html_filepath).getAbsoluteFile()).getParentFile()).getAbsolutePath());

		System.out.println("Perintah telah berhasil dieksekusi dengan sukses!");
		System.out.println("Hasil dapat dilihat di '" + target_html_filepath + "'.");

		return true;
	}

	private static boolean executeMode2(String[] args) throws Exception {
		if (args.length != 4 && args.length != 7) {
			System.err.println("[mode-2] assignment comparison");
			System.err.println("The number of arguments should be either four (for quick execution ");
			System.err.println("  command) or seven (for complete execution command).");
			System.err.println("Run this software with 'help' as the argument to show help.");
			return false;
		}

		String assignment_root_dirpath = preparePathOrRegex(args[1]);
		if (isPathValidAndExist(assignment_root_dirpath) == false) {
			System.err.println("[mode-2] assignment comparison");
			System.err.println("<assignment_root_dirpath> is not a valid path or refers to a ");
			System.err.println("  nonexistent directory.");
			System.err.println("Run this software with 'help' as the argument to show help.");
			return false;
		}

		boolean temp = isProgrammingLanguageValid(args[2]);
		if (temp == false) {
			System.err.println("[mode-2] assignment comparison");
			System.err.println("<programming_language> should be either 'java' (for Java) or 'py' (for ");
			System.err.println("  Python).");
			System.err.println("Run this software with 'help' as the argument to show help.");
			return false;
		}
		String programming_language = args[2];

		String target_html_dirpath = preparePathOrRegex(args[3]);
		if (isPathValid(target_html_dirpath) == false) {
			System.err.println("[mode-2] assignment comparison");
			System.err.println("<target_html_dirpath> is not a valid path.");
			System.err.println("Run this software with 'help' as the argument to show help.");
			return false;
		}

		double sim_threshold;
		String human_language;
		int syntax_minimum_match_length;
		String additional_keywords_path;

		if (args.length == 7) {
			Double tempM = prepareSimThreshold(args[4]);
			if (tempM == null) {
				System.err.println("[mode-2] assignment comparison");
				System.err.println("<sim_threshold> is not a valid floating number between 0 and 1 ");
				System.err.println("  (inclusive).");
				System.err.println("Run this software with 'help' as the argument to show help.");
				return false;
			}
			sim_threshold = tempM;

			Integer tempN = prepareMinimumMatchingLength(args[5]);
			if (tempN == null) {
				System.err.println("[mode-2] assignment comparison");
				System.err.println("<syntax_minimum_match_length> is not a valid positive ");
				System.err.println("  integer.");
				System.err.println("Run this software with 'help' as the argument to show help.");
				return false;
			}
			syntax_minimum_match_length = tempN;

			additional_keywords_path = preparePathOrRegex(args[6]);
			if (additional_keywords_path != null) {
				if (additional_keywords_path.equals("null"))
					additional_keywords_path = null;
				else if (isPathValidAndExist(additional_keywords_path) == false) {
					System.err.println("[mode-2] assignment comparison");
					System.err.println("<additional_keywords_path> is not a valid path or refers to ");
					System.err.println("  a nonexistent file.");
					System.err.println("Run this software with 'help' as the argument to show help.");
					return false;
				}
			}
		} else {
			sim_threshold = 0;
			syntax_minimum_match_length = 2;
			additional_keywords_path = null;
		}

		// read the keywords
		ArrayList<ArrayList<String>> additional_keywords = AdditionalKeywordsManager
				.readAdditionalKeywords(additional_keywords_path);

		human_language = "en";
		ComparisonHTMLGenerator.generateHtml(assignment_root_dirpath, programming_language, sim_threshold,
				comparison_template_path, pair_template_path, target_html_dirpath, syntax_minimum_match_length,
				human_language, additional_keywords);

		generateAdditionalDir(target_html_dirpath);

		System.out.println("The command has been executed succesfully!");
		System.out.println("The result can be seen in '" + target_html_dirpath + "'.");

		return true;
	}

	private static boolean executeMode2Idn(String[] args) throws Exception {
		if (args.length != 4 && args.length != 7) {
			System.err.println("[mode-2] perbandingan sekumpulan program");
			System.err.println("Jumlah argumen yang diberikan harus empat (untuk eksekusi standar) ");
			System.err.println("  atau tujuh (untuk eksekusi komprehensif).");
			System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
			return false;
		}

		String assignment_root_dirpath = preparePathOrRegex(args[1]);
		if (isPathValidAndExist(assignment_root_dirpath) == false) {
			System.err.println("[mode-2] perbandingan sekumpulan program");
			System.err.println("<assignment_root_dirpath> bukan path valid atau tidak mengarah ke sebuah");
			System.err.println("  direktori.");
			System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
			return false;
		}

		boolean temp = isProgrammingLanguageValid(args[2]);
		if (temp == false) {
			System.err.println("[mode-2] perbandingan sekumpulan program");
			System.err.println("<programming_language> harus bernilai 'java' (untuk Java) atau 'py'");
			System.err.println("  (untuk Python).");
			System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
			return false;
		}
		String programming_language = args[2];

		String target_html_dirpath = preparePathOrRegex(args[3]);
		if (isPathValid(target_html_dirpath) == false) {
			System.err.println("[mode-2] perbandingan sekumpulan program");
			System.err.println("<target_html_dirpath> bukan path valid.");
			System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
			return false;
		}

		double sim_threshold;
		String human_language;
		int syntax_minimum_match_length;
		String additional_keywords_path;

		if (args.length == 7) {
			Double tempM = prepareSimThreshold(args[4]);
			if (tempM == null) {
				System.err.println("[mode-2] perbandingan sekumpulan program");
				System.err.println("<sim_threshold> bukan bilangan riil diantara 0 dan 1 (inklusif).");
				System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
				return false;
			}
			sim_threshold = tempM;

			Integer tempN = prepareMinimumMatchingLength(args[5]);
			if (tempN == null) {
				System.err.println("[mode-2] perbandingan sekumpulan program");
				System.err.println("<syntax_minimum_match_length> bukan bilangan bulat positif valid.");
				System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
				return false;
			}
			syntax_minimum_match_length = tempN;

			additional_keywords_path = preparePathOrRegex(args[6]);
			if (additional_keywords_path != null) {
				if (additional_keywords_path.equals("null"))
					additional_keywords_path = null;
				else if (isPathValidAndExist(additional_keywords_path) == false) {
					System.err.println("[mode-2] perbandingan sekumpulan program");
					System.err.println("<additional_keywords_path> bukan path valid atau tidak mengarah pada ");
					System.err.println("  sebuah file.");
					System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
					return false;
				}
			}
		} else {
			sim_threshold = 0;
			syntax_minimum_match_length = 2;
			additional_keywords_path = null;
		}

		// read the keywords
		ArrayList<ArrayList<String>> additional_keywords = AdditionalKeywordsManager
				.readAdditionalKeywords(additional_keywords_path);

		human_language = "id";
		ComparisonHTMLGenerator.generateHtml(assignment_root_dirpath, programming_language, sim_threshold,
				comparison_template_path_id, pair_template_path_id, target_html_dirpath, syntax_minimum_match_length,
				human_language, additional_keywords);

		generateAdditionalDir(target_html_dirpath);

		System.out.println("Perintah telah berhasil dieksekusi dengan sukses!");
		System.out.println("Hasil dapat dilihat di '" + target_html_dirpath + "'.");

		return true;
	}

	private static boolean executeMode3(String[] args) throws Exception {
		if (args.length != 4 && args.length != 6) {
			System.err.println("[mode-3] JPlag result update");
			System.err.println("The number of arguments should be either four (for quick execution ");
			System.err.println("  command) or six (for complete execution command).");
			System.err.println("Run this software with 'help' as the argument to show help.");
			return false;
		}

		String jplag_root_dirpath = preparePathOrRegex(args[1]);
		if (isPathValidAndExist(jplag_root_dirpath) == false) {
			System.err.println("[mode-3] JPlag result update");
			System.err.println("<jplag_root_dirpath> is not a valid path or refers to a ");
			System.err.println("  nonexistent directory.");
			System.err.println("Run this software with 'help' as the argument to show help.");
			return false;
		}

		String assignment_root_dirpath = preparePathOrRegex(args[2]);
		if (isPathValidAndExist(assignment_root_dirpath) == false) {
			System.err.println("[mode-3] JPlag result update");
			System.err.println("<assignment_root_dirpath> is not a valid path or refers to a ");
			System.err.println("  nonexistent directory.");
			System.err.println("Run this software with 'help' as the argument to show help.");
			return false;
		}

		boolean temp = isProgrammingLanguageValid(args[3]);
		if (temp == false) {
			System.err.println("[mode-3] JPlag result update");
			System.err.println("<programming_language> should be either 'java' (for Java) or 'py' (for ");
			System.err.println("  Python).");
			System.err.println("Run this software with 'help' as the argument to show help.");
			return false;
		}
		String programming_language = args[3];

		int syntax_minimum_match_length;
		String human_language;
		String additional_keywords_path;

		if (args.length == 6) {
			Integer tempN = prepareMinimumMatchingLength(args[4]);
			if (tempN == null) {
				System.err.println("[mode-3] JPlag result update");
				System.err.println("<syntax_minimum_match_length> is not a valid positive ");
				System.err.println("  integer.");
				System.err.println("Run this software with 'help' as the argument to show help.");
				return false;
			}
			syntax_minimum_match_length = tempN;

			additional_keywords_path = preparePathOrRegex(args[5]);
			if (additional_keywords_path != null) {
				if (additional_keywords_path.equals("null"))
					additional_keywords_path = null;
				else if (isPathValidAndExist(additional_keywords_path) == false) {
					System.err.println("[mode-3] JPlag result update");
					System.err.println("<additional_keywords_path> is not a valid path or refers to ");
					System.err.println("  a nonexistent file.");
					System.err.println("Run this software with 'help' as the argument to show help.");
					return false;
				}
			}
		} else {
			syntax_minimum_match_length = 2;
			additional_keywords_path = null;
		}

		human_language = "en";
		JplagObserverUpdater.extract(jplag_root_dirpath, assignment_root_dirpath, programming_language,
				syntax_minimum_match_length, human_language, additional_keywords_path, pair_template_path);

		generateAdditionalDir(jplag_root_dirpath);

		System.out.println("The command has been executed succesfully!");
		System.out.println("The result can be seen in '" + jplag_root_dirpath + "'.");

		return true;
	}

	private static boolean executeMode3Idn(String[] args) throws Exception {
		if (args.length != 4 && args.length != 6) {
			System.err.println("[mode-3] modifikasi hasil JPlag");
			System.err.println("Jumlah argumen yang diberikan harus empat (untuk eksekusi standar) ");
			System.err.println("  atau enam (untuk eksekusi komprehensif).");
			System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
			return false;
		}

		String jplag_root_dirpath = preparePathOrRegex(args[1]);
		if (isPathValidAndExist(jplag_root_dirpath) == false) {
			System.err.println("[mode-3] modifikasi hasil JPlag");
			System.err.println("<jplag_root_dirpath> bukan path valid atau tidak mengarah pada sebuah ");
			System.err.println("  direktori.");
			System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
			return false;
		}

		String assignment_root_dirpath = preparePathOrRegex(args[2]);
		if (isPathValidAndExist(assignment_root_dirpath) == false) {
			System.err.println("[mode-3] modifikasi hasil JPlag");
			System.err.println("<assignment_root_dirpath> bukan path valid atau tidak mengarah pada sebuah");
			System.err.println("  direktori.");
			System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
			return false;
		}

		boolean temp = isProgrammingLanguageValid(args[3]);
		if (temp == false) {
			System.err.println("[mode-3] modifikasi hasil JPlag");
			System.err.println("<programming_language> harus bernilai 'java' (untuk Java) or 'py'");
			System.err.println("  (untuk Python).");
			System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
			return false;
		}
		String programming_language = args[3];

		int syntax_minimum_match_length;
		String human_language;
		String additional_keywords_path;

		if (args.length == 6) {
			Integer tempN = prepareMinimumMatchingLength(args[4]);
			if (tempN == null) {
				System.err.println("[mode-3] modifikasi hasil JPlag");
				System.err.println("<syntax_minimum_match_length> bukan bilangan desimal positif valid. ");
				System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
				return false;
			}
			syntax_minimum_match_length = tempN;

			additional_keywords_path = preparePathOrRegex(args[5]);
			if (additional_keywords_path != null) {
				if (additional_keywords_path.equals("null"))
					additional_keywords_path = null;
				else if (isPathValidAndExist(additional_keywords_path) == false) {
					System.err.println("[mode-3] modifikasi hasil JPlag");
					System.err.println("<additional_keywords_path> bukan path valid atau tidak mengarah pada");
					System.err.println("  sebuah file.");
					System.err.println("Jalankan aplikasi dengan 'bantuan' sebagai argumen untuk menampilkan bantuan.");
					return false;
				}
			}
		} else {
			syntax_minimum_match_length = 2;
			additional_keywords_path = null;
		}

		human_language = "id";
		JplagObserverUpdater.extract(jplag_root_dirpath, assignment_root_dirpath, programming_language,
				syntax_minimum_match_length, human_language, additional_keywords_path, pair_template_path_id);

		generateAdditionalDir(jplag_root_dirpath);

		System.out.println("Perintah telah berhasil dieksekusi dengan sukses!");
		System.out.println("Hasil dapat dilihat di '" + jplag_root_dirpath + "'.");

		return true;
	}

	private static String preparePathOrRegex(String path) {
		if (path != null && (path.startsWith("'") || path.startsWith("\"")))
			return path.substring(1, path.length() - 1);
		else
			return path;
	}

	private static Integer prepareMinimumMatchingLength(String s) {
		// check whether s is actually a positive decimal.
		try {
			Integer x = Integer.parseInt(s);
			if (x > 0)
				return x;
			else
				return null;
		} catch (Exception e) {
			return null;
		}
	}

	private static Double prepareSimThreshold(String s) {
		// check whether s is actually a floating number ranged from 0 to 1
		// inclusive.
		try {
			Double x = Double.parseDouble(s);
			if (x >= 0 && x <= 1)
				return x;
			else
				return null;
		} catch (Exception e) {
			return null;
		}
	}

	private static boolean isProgrammingLanguageValid(String prog) {
		if (prog != null && (prog.equals("java") || prog.equals("py")))
			return true;
		else
			return false;
	}

	private static boolean isPathValidAndExist(String path) {
		// check the validity of the string
		if (isPathValid(path) == false)
			return false;

		// check whether such file exists
		File f = new File(path);
		if (f.exists() == false)
			return false;

		return true;
	}

	private static boolean isPathValid(String path) {
		// check the validity of the string
		if (path == null || path.length() == 0)
			return false;
		else
			return true;
	}

	private static boolean isNameValid(String name) {
		// check the validity of the string
		if (name == null || name.length() == 0)
			return false;
		else
			return true;
	}

	private static void println(String s) {
		System.out.println(s);
	}

	private static void showHelp() {
		println("[English] Thank you for downloading STRANGE (Similarity TRacker in Academia with ");
		println("  Natural lanGuage Explanation), a program for observing similarities and ");
		println("  the surface differences among Java/Python source code files. For Python, ");
		println("  the compiler needs to be installed and the absolute path should be set in ");
		println("  'pythoncompilerpath.txt'.");

		println("\nSTRANGE has six modes with their corresponding parameters:");
		println("1. Pair observation: this mode captures the similarities shared between two given source");
		println("   code files and stores them as an interactive HTML page. This mode can be embedded when");
		println("   developing other similarity detection software.");
		println("   -> Quick execution command: pair <codepath1> <codepath2> ");
		println("        <programming_language> <target_html_filepath>");
		println("   -> Complete execution command: pair <codepath1> <codepath2> ");
		println("        <programming_language> <target_html_filepath> <projectname1> ");
		println("        <projectname2> <home_filepath> <syntax_minimum_match_length> ");
		println("        <additional_keywords_path>");
		println("2. Assignment comparison: this mode pairwise compares student code files for an assignment");
		println("   and shows the similarities in interactive HTML pages. It accepts an assignment directory");
		println("   with each sub-directory represents a student submission containing one code file. The ");
		println("   results are stored as a directory with 'index.html' as the entry page. Please preprocess");
		println("   the assignment directory with mode 4 if the assignment is broken down to sub-assignments,");
		println("   or with mode 5 if the assignment requires more than one code file per submission.");
		println("   -> Quick execution command: comp <assignment_root_dirpath> ");
		println("        <programming_language> <target_html_dirpath>");
		println("   -> Complete execution command: comp <assignment_root_dirpath> ");
		println("        <programming_language> <target_html_dirpath> <sim_threshold> ");
		println("        <syntax_minimum_match_length> <additional_keywords_path>");
		println("3. JPlag result update: this mode upgrades JPlag-style HTML pages for similarity observation");
		println("   with the ones from STRANGE. It accepts a JPlag-generated directory and an assignment");
		println("   directory with each sub-directory represents a student submission containing one file. ");
		println("   Each of the HTML pages will be replaced with the STRANGE-style ones.");
		println("   -> Quick execution command: jplag <jplag_root_dirpath> ");
		println("        <assignment_root_dirpath> <programming_language>");
		println("   -> Complete execution command: jplag <jplag_root_dirpath> ");
		println("        <assignment_root_dirpath> <programming_language>");
		println("        <syntax_minimum_match_length> <additional_keywords_path>");
		println("4. Assignment code grouping: this mode groups student submissions based on their");
		println("   sub-assignments' filename patterns. It accepts an assignment directory with each");
		println("   sub-directory represents a student submission containing several code files, and then group");
		println("   the code files based on the sub-assignments' filename patterns. The results will be stored");
		println("   in a directory named '[grouped]' + assignment directory name. Each sub-assignment will be");
		println("   represented as one sub-directory in which the name is based on the pattern but with only ");
		println("   alphanumerics. Those that do not share one of the given file name pattern will be grouped");
		println("   as 'uncategorised'.");
		println("   -> Execution command: group <assignment_root_dirpath> <programming_language>");
		println("        <name_pattern_1> <name_pattern_2> ... <name_pattern_N> ");
		println("5. Assignment code file merging: this mode merges all code files in each student submission.");
		println("   It accepts an assignment directory containing students projects as the sub-directories, and");
		println("   then merge each project's code files as one large code file. The results will be stored in a");
		println("   directory named '[merged]' + assignment directory name, where each sub-directory represents");
		println("   one student project.");
		println("   -> Execution command: merge <assignment_root_dirpath> <programming_language>");
		println("6. Template code removal: this mode removes template code from all code files in an assignment");
		println("   directory. It accepts an assignment directory and a template code file. The results will be");
		println("   stored in a directory named '[template removed]' + assignment directory name, where the");
		println("   directory structure is similar to the given assignment directory, except that only processed");
		println("   code files are copied. The removed template code will be informed per code file as comments.");
		println("   Template code is detected with a greedy algorithm and it is not guarantee to be completely");
		println("   accurate (despite it is still acceptable).");
		println("   -> Execution command: template <assignment_root_dirpath> <programming_language>");
		println("        <template_path>");

		println("\nMode-1 ('pair') exclusive parameters description (sorted alphabetically):");
		println("  <codepath1>: a string representing the file path for the first code. Please use ");
		println("      quotes if the path contains spaces.");
		println("  <codepath2>: a string representing the file path for the second code. Please use ");
		println("      quotes if the path contains spaces.");
		println("  <home_filepath>: a string representing the file path for home html page (i.e., ");
		println("      the HTML page displayed before accessing the resulted page). Enter null if no ");
		println("      such a page exists. Please use quotes if the path contains spaces. This ");
		println("      parameter is null by default.");
		println("  <projectname1>: a string representing the project name for the first code. This ");
		println("      can be used to distinguish given code files if they share the same name. This ");
		println("      is assigned to <codepath1> by default.");
		println("  <projectname2>: a string representing the project name for the second code. This ");
		println("      can be used to distinguish given code files if they share the same name. This ");
		println("      is assigned to <codepath2> by default.");
		println("  <target_html_filepath>: a string representing the file path for the generated HTML.");
		println("      Please use quotes if the path contains spaces.");

		println("\nMode-2 ('comp') exclusive parameters description (sorted alphabetically):");
		println("  <sim_threshold>: a floating number representing the threshold of suspicion. Only ");
		println("      source code pairs with similarity degree greater than or equal to the threshold ");
		println("      are displayed in the results. This is assigned with 0 by default.");
		println("      Value: a floating number between 0 to 1 (inclusive).");
		println("  <target_html_dirpath>: a string representing the directory path for the generated ");
		println("      HTML pages. Please use quotes if the path contains spaces.");

		println("\nMode-3 ('jplag') exclusive parameters description:");
		println("  <jplag_root_dirpath>: a string representing the directory path of JPlag result.");
		println("      Please use quotes if the path contains spaces.");

		println("\nMode-4 ('group') exclusive parameters description:");
		println("  <name_pattern>: a string representing the name pattern of a sub-assignment, written");
		println("      as Java regular expression.");
		println("      Please use quotes if the path contains spaces.");

		println("\nMode-6 ('template') exclusive parameters description:");
		println("  <template_path>: a string representing a file containing template code that will be");
		println("      removed. The template code should be written in compliance to the programming");
		println("      language's syntax.");

		println("\nShared parameters description (sorted alphabetically):");
		println("  <additional_keywords_path>: a string representing a file containing additional ");
		println("      keywords with newline as the delimiter. Keywords with more than one token ");
		println("      should be written by embedding spaces between the tokens. For example, ");
		println("      'System.out.print' should be written as 'System . out . print'. Set this");
		println("      parameter to null if not needed.");
		println("  <assignment_root_dirpath>: a string representing the assignment root directory. ");
		println("      That directory should contain directories in which each of them refers to one ");
		println("      student program and has one Java or Python code file. Please use quotes if the");
		println("      path contains spaces.");
		println("  <programming_language>: a constant depicting the programming language used on");
		println("      given source code files.");
		println("      Value: 'java' (for Java) or 'py' (for Python).");
		println("  <syntax_minimum_match_length>: a number depicting the lowest length of captured ");
		println("      similar syntax code fragments. This is assigned 2 by default.");
		println("      Value: a positive integer.");
	}

	private static void tampilkanBantuan() {
		println("[Indonesia] Terima kasih telah mengunduh STRANGE (Similarity TRacker in Academia with");
		println("  Natural lanGuage Explanation), sebuah kakas untuk mengamati kesamaan dan perbedaan");
		println("  level permukaan dari kode sumber Java/Python. Untuk Python, kompilernya perlu");
		println("  diinstall dan path absolutnya harus diset di 'pythoncompilerpath.txt'.");

		println("\nSTRANGE memiliki enam mode dengan parameternya masing-masing:");
		println("1. Pengamatan pasangan kode sumber: menangkap kesamaan dari dua kode sumber dan ");
		println("   menyimpannya dalam sebuah laman interaktif HTML. Mode ini dapat diintegrasikan dalam");
		println("   pengembangan kakas deteksi kesamaan lainnya.");
		println("   -> Eksekusi standar: pairi <codepath1> <codepath2> ");
		println("        <programming_language> <target_html_filepath>");
		println("   -> Eksekusi komprehensif: pairi <codepath1> <codepath2> ");
		println("        <programming_language> <target_html_filepath> <projectname1> ");
		println("        <projectname2> <home_filepath> <syntax_minimum_match_length> ");
		println("        <additional_keywords_path>");
		println("2. Perbandingan sekumpulan program: membandingkan sekumpulan kode siswa untuk sebuah tugas ");
		println("   dan menampilkan kesamaannya dalam laman HTML interaktif. Mode ini menerima sebuah direktori ");
		println("   tugas dengan setiap upa direktorinya merepresentasikan satu tugas mahasiswa dan berisi satu");
		println("   file kode. Hasil akan disimpan dalam sebuah direktori dengan 'index.htnl' sebagai laman awal.");
		println("   Mohon memproses direktori tugas dengan mode 4 terlebih dahulu jika tugas tersebut terdiri");
		println("   dari banyak upa tugas, atau mode 5 jika tugas tersebut mewajibkan lebih dari satu file kode");
		println("   solusi untuk setiap siswa.");
		println("   -> Eksekusi standar: compi <assignment_root_dirpath> ");
		println("        <programming_language> <target_html_dirpath>");
		println("   -> Eksekusi komprehensif: compi <assignment_root_dirpath> ");
		println("        <programming_language> <target_html_dirpath> <sim_threshold> ");
		println("        <syntax_minimum_match_length> <additional_keywords_path>");
		println("3. Modifikasi hasil JPlag: mengganti laman HTML untuk observasi kesamaan dari JPlag dengan");
		println("   laman HTML serupa dari STRANGE. Mode ini menerima direktori output JPlag dan sebuah");
		println("   direktori tugas. Setiap laman HTMLnya akan digantikan dengan HTML serupa dari STRANGE.");
		println("   -> Eksekusi standar: jplagi <jplag_root_dirpath> ");
		println("        <assignment_root_dirpath> <programming_language>");
		println("   -> Eksekusi komprehensif: jplagi <jplag_root_dirpath> ");
		println("        <assignment_root_dirpath> <programming_language>");
		println("        <syntax_minimum_match_length> <additional_keywords_path>");
		println("4. Pengelompokan kode tugas: mengelompokkan tugas siswa berdasarkan pola nama file upa tugas.");
		println("   Mode ini menerima sebuah direktori tugas dengan setiap upa direktori merepresentasikan satu");
		println("   tugas mahasiswa, dan mengelompokkan kode berdasarkan pola nama upa tugas dalam sebuah");
		println("   direktori upa tugas dalam sebuah direktori bernama '[grouped]' + nama direktori tugas.");
		println("   Setiap upa tugas akan direpresentasikan dengan sebuah upa direktori dengan nama seperti");
		println("   pola namanya namun hanya melibatkan alphanumerik. Kode yang tidak memenuhi pola akan");
		println("   dikategorikan sebagai 'uncategorised'.");
		println("   -> Perintah eksekusi: groupi <assignment_root_dirpath> <programming_language>");
		println("        <name_pattern_1> <name_pattern_2> ... <name_pattern_N> ");
		println("5. Penggabungan file kode tugas: menggabungkan semua file kode dari tiap tugas mahasiswa.");
		println("   Mode ini menerima direktori tugas dengan upa direktorinya merupakan proyek siswa, dan");
		println("   untuk setiap proyek, menggabungkan semua kode sumbernya menjadi sebuah file kode besar.");
		println("   Hasil diletakkan di direktori bernama '[merged]' + nama direktori tugas, dimana setiap");
		println("   upa direktori merepresentasikan satu proyek.");
		println("   -> Perintah eksekusi: mergei <assignment_root_dirpath> <programming_language>");
		println("6. Penghapusan kode template: menghapus kode template dari setiap file kode di direktori");
		println("   tugas. Mode ini menerima direktori tugas dan sebuah file kode template. Hasil akan");
		println("   diletakkan di direktori bernama '[template removed]' + nama direktori tugas, dimana");
		println("   struktur direktorinya akan sama seperti direktori tugas masukan kecuali hanya berisi file");
		println("   kode terproses. Kode template terhapus akan diinformasikan di setiap file kode dalam bentuk");
		println("   komentar. Deteksi kode template dilakukan dengan algoritma greedy dan tidak menjamin hasil");
		println("   sempurna (walaupun masih dapat diterima).");
		println("   -> Perintah eksekusi: templatei <assignment_root_dirpath> <programming_language>");
		println("        <template_path>");

		println("\nDeskripsi parameter ekslusif mode-1 ('pairi'), terurut berdasarkan alphabet:");
		println("  <codepath1>: string berisi path file kode pertama. Mohon gunakan kutip jika path ");
		println("      mengandung spasi.");
		println("  <codepath2>: string berisi path file kode kedua. Mohon gunakan kutip jika path ");
		println("      mengandung spasi.");
		println("  <home_filepath>: string berisi path file dari laman html utama (laman yang ");
		println("      ditampilkan sebelum mengakses laman hasil). Isi dengan null jika tidak ada ");
		println("      laman utama. Mohon gunakan kutip jika path mengandung spasi. Parameter ini ");
		println("      diisi null secara otomatis.");
		println("  <projectname1>: string berisi nama proyek untuk kode pertama. Ini dapat digunakan ");
		println("      untuk membedakan kode dengan nama file sama. Parameter ini diisi dengan ");
		println("      <codepath1> secara otomatis.");
		println("  <projectname2>: string berisi nama proyek untuk kode kedua. Ini dapat digunakan ");
		println("      untuk membedakan kode dengan nama file sama. Parameter ini diisi dengan ");
		println("      <codepath2> secara otomatis.");
		println("  <target_html_filepath>: string berisi path file untuk lokasi laman hasil. Mohon ");
		println("      gunakan kutip jika path mengandung spasi.");

		println("\nDeskripsi parameter eksklusif mode-2 ('compi'), terurut berdasarkan alphabet:");
		println("  <sim_threshold>: bilangan riil yang menyatakan batas minimum kesamaan dalam ");
		println("      mencurigai pasangan kode sumber. Pasangan dengan kesamaan melebihi atau ");
		println("      sama dengan batas akan ditampilkan di laman luaran. Parameter ini diisi ");
		println("      0 secara otomatis.");
		println("      nilai: bilangan riil diantara 0 hingga 1 secara inclusif.");
		println("  <target_html_dirpath>: string berisi path direktori untuk lokasi direktori ");
		println("      hasil. Mohon gunakan kutip jika path mengandung spasi.");

		println("\nDeskripsi parameter eksklusif mode-3 ('jplagi'):");
		println("  <jplag_root_dirpath>: string berisi path direktori hasil luaran JPlag.");
		println("      Mohon gunakan kutip jika path mengandung spasi.");

		println("\nDeskripsi parameter eksklusif mode-4 ('groupi'):");
		println("  <name_pattern>: string berisi pola nama dari satu upa tugas, ditulis");
		println("      mengikuti kaidah regular expression Java.");
		println("      Mohon gunakan kutip jika pola mengandung spasi.");

		println("\nDeskripsi parameter eksklusif mode-6 ('templatei'):");
		println("  <template_path>: string berisi path file untuk kode template yang akan dibuang.");
		println("      Kode template dituliskan sesuai dengan sintaks bahasa pemrograman yang");
		println("      digunakan.");

		println("\nDeskripsi parameter lainnya:");
		println("  <additional_keywords_path>: string berisi path file untuk kata kunci pemrograman ");
		println("      tambahan dengan baris baru sebagai pemisah kata kunci. Kata kunci yang ");
		println("      melibatkan lebih dari satu token harus ditulis dalam satu baris dengan ");
		println("      spasi sebagai pemisah antar token. Sebagai contoh, 'System.out.print'");
		println("      dapat ditulis sebagai 'System . out . print'. Isi parameter ini dengan null");
		println("      jika tidak diperlukan.");
		println("  <assignment_root_dirpath>: string berisi path direktori dari kumpulan program. ");
		println("      Direktori harus berisi beberapa sub-direktori; setiap sub-direktori berisi ");
		println("      satu kode sumber dalam bahasa Java atau Python. Mohon gunakan kutip jika ");
		println("      path mengandung spasi.");
		println("  <programming_language>: konstanta menyatakan bahasa pemrograman kode sumber ");
		println("      yang akan diproses.");
		println("      nilai: java (untuk Java) atau py (untuk Python).");
		println("  <syntax_minimum_match_length>: bilangan bulat menyatakan panjang minimum dari ");
		println("      sekuens token sama yang akan dikenali oleh sistem. Parameter ini diisi ");
		println("      dengan dua secara otomatis.");
		println("      nilai: bilangan bulat positif.");
	}

}
