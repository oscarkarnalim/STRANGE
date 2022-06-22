package p3.feedbackgenerator.htmlgenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import p3.feedbackgenerator.comparison.Comparer;
import p3.feedbackgenerator.language.java.JavaHtmlGenerator;
import p3.feedbackgenerator.language.python.PythonHtmlGenerator;
import support.stringmatching.AdditionalKeywordsManager;

public class JplagObserverUpdater {

	public static void extract(String jplagDirPath, String assignmentPath,
			String extension, int minimumMatchLength, String languageCode, String additionalKeywordsPath, String pairTemplatePath) {
		// extract similarity results from jplag-output directory
		String filepath = jplagDirPath + File.separator + "index.html";
		try {
			// start parsing with jsoup
			Document doc = Jsoup.parse(new File(filepath), "UTF-8");
			// take all table tags as they are the containers of sim degree
			// results
			Elements tables = doc.select("TABLE");

			/*
			 * store all previously created html pages. Stored to avoid
			 * repetitive creation. This can fasten the execution process.
			 */
			HashSet<String> createdHTMLPages = new HashSet<>();

			// read the keywords
			ArrayList<ArrayList<String>> additionalKeywords = AdditionalKeywordsManager
					.readAdditionalKeywords(additionalKeywordsPath);

			// get average similarity pairs
			Element tableAvg = tables.get(2);
			generatePairHTMLPages(tableAvg, jplagDirPath, assignmentPath,
					extension, createdHTMLPages, minimumMatchLength,
					languageCode, additionalKeywords, pairTemplatePath);
			// table for maximum similarity
			Element tableMax = tables.get(3);
			generatePairHTMLPages(tableMax, jplagDirPath, assignmentPath,
					extension, createdHTMLPages, minimumMatchLength,
					languageCode, additionalKeywords, pairTemplatePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void generatePairHTMLPages(Element processedTable,
			String jplagDirPath, String assignmentPath, String extension,
			HashSet<String> createdHTMLPages, int minimumMatchLength,
			String languageCode, ArrayList<ArrayList<String>> additionalKeywords, String pairTemplatePath)
			throws Exception {
		// start extracting all rows
		Elements rows = processedTable.select("tr");
		// iterate per row
		for (int i = 0; i < rows.size(); i++) {
			Element row = rows.get(i);
			// take all the column
			Elements col = row.select("td");
			// first column is the first dir name
			String dirname1 = col.get(0).text();
			// get the first code path
			File code1 = Comparer.getCode(new File(assignmentPath + File.separator
					+ dirname1), extension);
			// start from the third column are the second filename and target
			// html for replacement
			for (int j = 2; j < col.size(); j++) {
				// get the link
				String targetFileName = col.get(j).select("a").get(0)
						.attr("href");
				// get the second dir name
				String dirname2 = col.get(j).text();
				dirname2 = dirname2.substring(0,dirname2.lastIndexOf("(")-1);
				// get the second code path
				File code2 = Comparer.getCode(new File(assignmentPath + File.separator
						+ dirname2), extension);

				// check if the target file exists
				if (createdHTMLPages.contains(targetFileName) == false) {
					// if not exist, generate the html based on extension
					if (extension.equals("java")) {
						JavaHtmlGenerator.generateHtml(code1.getAbsolutePath(),
								code2.getAbsolutePath(), dirname1, dirname2,
								pairTemplatePath, jplagDirPath + File.separator
										+ targetFileName, "index.html",
								minimumMatchLength, languageCode,
								additionalKeywords);
					} else if (extension.equals("py")) {
						PythonHtmlGenerator.generateHtml(
								code1.getAbsolutePath(),
								code2.getAbsolutePath(), dirname1, dirname2,
								pairTemplatePath, jplagDirPath + File.separator
										+ targetFileName, "index.html",
								minimumMatchLength, languageCode, additionalKeywords);
					}
				}
			}
		}
	}
}
