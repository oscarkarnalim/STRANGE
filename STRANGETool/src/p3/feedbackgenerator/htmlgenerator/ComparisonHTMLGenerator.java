package p3.feedbackgenerator.htmlgenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import p3.feedbackgenerator.comparison.ComparisonPairTuple;
import p3.feedbackgenerator.comparison.Comparer;
import p3.feedbackgenerator.language.java.JavaHtmlGenerator;
import p3.feedbackgenerator.language.python.PythonHtmlGenerator;
import p3.feedbackgenerator.message.FeedbackMessageGenerator;
import support.stringmatching.AdditionalKeywordsManager;

public class ComparisonHTMLGenerator {
	public static void generateHtml(String assignmentRootPath,
			String extension, double threshold, String coreTemplateHTMLPath, String pairTemplateHTMLPath,
			String outputDirPath, int minimumMatchLength, String languageCode,
			ArrayList<ArrayList<String>> additionalKeywords) throws Exception {
		// preparing the data
		int numSuspected = 0;
		ArrayList<ComparisonPairTuple> codePairs = Comparer
				.getRankedComparisonPairTuples(assignmentRootPath, extension,
						minimumMatchLength, languageCode, additionalKeywords);

		// remove pairs which similarity degree is lower than threshold
		for (int i = 0; i < codePairs.size(); i++) {
			if (codePairs.get(i).getAvgSyntax() < threshold) {
				codePairs.remove(i);
				i--;
			} else {
				numSuspected++;
			}
		}

		String tableContent = getTableContent(codePairs, languageCode);

		// create directory
		File dirRoot = new File(outputDirPath);
		if (dirRoot.exists() == false)
			dirRoot.mkdir();

		// generate the html page
		File templateFile = new File(coreTemplateHTMLPath);
		File outputFile = new File(outputDirPath + File.separator + "index.html");
		BufferedReader fr = new BufferedReader(new FileReader(templateFile));
		BufferedWriter fw = new BufferedWriter(new FileWriter(outputFile));
		String line;
		while ((line = fr.readLine()) != null) {
			if (line.contains("@filename")) {
				line = line.replace("@filename", assignmentRootPath
						.substring(assignmentRootPath.lastIndexOf(File.separator) + 1));
			}

			if (line.contains("@filepath")) {
				line = line.replace("@filepath", assignmentRootPath);
			}

			if (line.contains("@threshold")) {
				line = line.replace("@threshold",
						String.format("%.2f", threshold * 100));
			}

			if (line.contains("@numsuspected")) {
				line = line.replace("@numsuspected", numSuspected + "");
			}

			if (line.contains("@tablecontent")) {
				line = line.replace("@tablecontent", tableContent);
			}

			if (line.contains("@explanation")) {
				if (languageCode.equals("en"))
					line = line.replace("@explanation",
							getExplanationContentEn());
				else
					line = line.replace("@explanation",
							getExplanationContentId());
			}

			fw.write(line);
			fw.write(System.lineSeparator());
		}
		fr.close();
		fw.close();
		
		// generate pairwise files
		int numID = 0;
		for (ComparisonPairTuple ct : codePairs) {
			// generate the ID
			String entryID = "r" + numID;

			if (extension.equals("java")) {
				JavaHtmlGenerator.generateHtml(ct.getCodePath1(),
						ct.getCodePath2(), ct.getAssignmentName1(),
						ct.getAssignmentName2(), pairTemplateHTMLPath,
						outputDirPath + File.separator + entryID + ".html", null,
						minimumMatchLength, languageCode, additionalKeywords);
			} else if (extension.equals("py")) {
				PythonHtmlGenerator.generateHtml(ct.getCodePath1(),
						ct.getCodePath2(), ct.getAssignmentName1(),
						ct.getAssignmentName2(), pairTemplateHTMLPath,
						outputDirPath + File.separator + entryID + ".html", null,
						minimumMatchLength, languageCode, additionalKeywords);
			}

			// increment number for ID
			numID++;
		}
	}

	private static String getTableContent(
			ArrayList<ComparisonPairTuple> codePairs, String languageCode) {
		String textForObserve = "observe";
		if (languageCode.equals("id"))
			textForObserve = "amati";

		String s = "";
		int numID = 0;
		for (ComparisonPairTuple ct : codePairs) {
			// generate the ID
			String entryID = "r" + numID;

			// generate the string
			s += "<tr id=\"" + entryID + "\" onclick=\"selectRow('" + entryID
					+ "','sumtablecontent')\">";
			s += ("\n\t<td><a>" + ct.getAssignmentName1() + "-"
					+ ct.getAssignmentName2() + "</a></td>");
			s += ("\n\t<td>" + String.format("%.2f", ct.getAvgSyntax() * 100) + " %</td>");
			s += ("\n\t<td>" + String.format("%.2f", ct.getMaxSyntax() * 100) + " %</td>");
			s += ("\n\t<td>" + String.format("%.2f", ct.getAvgComment() * 100) + " %</td>");
			s += ("\n\t<td>" + String.format("%.2f", ct.getAvgComment() * 100) + " %</td>");
			s += ("\n\t<td><button onclick=\"window.open('" + entryID
					+ ".html', '_blank');\">" + textForObserve + "</button></td>");
			s += "\n</tr>\n";

			// increment number for ID
			numID++;
		}
		return s;
	}

	private static String getExplanationContentEn() {
		String s = "";

		s += "<b>Syntax similarity</b> considers all tokens except comments and "
				+ "whitespaces, after generalising identifiers, constants, and "
				+ "some data types. <br/> <br />";
		s += "<b>Comment similarity</b> considers comments exclusively"
				+ "; all adjacent comments are merged and "
				+ "preprocessed prior comparison. <br/> <br />";

		// average similarity
		s += "The characteristics of <b>average similarity (avg sim)</b>:<ul>";
		s += "<li>It considers all differences in its calculation.</li>";
		s += "<li>It only leads to 100% similarity if both code files are similar, even if some fragments have been swapped.</li>";
		s += "<li>It is best used when all differences are equally important.</li>";
		s += "<li>It is calculated as <b>2M&nbsp;/&nbsp;(&nbsp;A&nbsp;+&nbsp;B&nbsp;)</b> where <b>M</b> is the number of matches, ";
		s += "<b>A</b> is the number of tokens on the left code, and <b>B</b> is the number of tokens on the right code.</li>";
		s = s + "</ul>\n";

		// maximum similarity
		s += "The characteristics of <b>maximum similarity (max sim)</b>:<ul>";
		s += "<li>It can lead to 100% similarity either if both code files are similar, "
				+ "or if one code file is a part of another.</li>";
		s += "<li>It is best used when many unnecessary tokens can be added to the code without changing its meaning.</li>";
		s += "<li>It is calculated as <b>M&nbsp;/&nbsp;Min(&nbsp;A&nbsp;,&nbsp;B&nbsp;)&nbsp;</b> where <b>M</b> is the number of matches, ";
		s += "<b>A</b> is the number of tokens on the left code, and <b>B</b> is the number of tokens on the right code.</li>";
		s += "</ul>\n";

		return s;
	}

	private static String getExplanationContentId() {
		String s = "";

		s += "<b>Kesamaan sintaks</b> memperhitungkan semua token kecual komentar dan whitespace,"
				+ "setelah identifier, konstanta, dan beberapa tipe data digeneralisir. <br/> <br />";
		s += "<b>Kesamaan komentar</b> hanya memperhitungkan komentar dalam prosesnya; "
				+ "semua komentar berdekatan akan digabungkan menjadi sebuah komentar dan diproses sebelum dibandingkan."
				+ "<br/> <br />";

		// average similarity
		s += "Karakteristik <b>kesamaan rerata (avg sim)</b>:<ul>";
		s += "<li>Memperhitungkan semua aspek perbedaan dalam prosesnya.</li>";
		s += "<li>Hanya menghasilkan kesamaan 100% jika konten kedua file serupa, meskipun sebagian fragmennya bertukar posisi.</li>";
		s += "<li>Cocok digunakan jika semua perbedaan kode dianggap penting.</li>";
		s += "<li>Dihitung dengan persamaan: <b>2M&nbsp;/&nbsp;(&nbsp;A&nbsp;+&nbsp;B&nbsp;)</b>; <b>M</b> adalah jumlah token sama, ";
		s += "<b>A</b> adalah jumlah token pada kode kiri, dan <b>B</b> adalah jumlah token pada kode kanan.</li>";
		s = s + "</ul>\n";

		// maximum similarity
		s += "Karakteristik <b>kesamaan maksimum (max sim)</b>:<ul>";
		s += "<li>Dapat menghasilkan kesamaan 100% jika konten kedua file serupa, "
				+ "atau salah satu kode merupakan bagian dari kode satunya.</li>";
		s += "<li>Cocok digunakan jika banyak token tambahan dapat disertakan pada kode tanpa mengubah makna.</li>";
		s += "<li>Dihitung dengan persamaan: <b>M&nbsp;/&nbsp;Min(&nbsp;A&nbsp;,&nbsp;B&nbsp;)&nbsp;</b>; <b>M</b> adalah jumlah token sama, ";
		s += "<b>A</b> adalah jumlah token pada kode kiri, dan <b>B</b> adalah jumlah token pada kode kanan.</li>";
		s += "</ul>\n";

		return s;
	}
}
