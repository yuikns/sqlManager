package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Cell;

public class Publication implements Paper {
	public String id;
	public String title;

	public String authorsids;
	public String authors;

	public String orgids;
	public String orgs;

	public String type;// book,preceeding

	public String jconfFullText;
	public String jconf;

	public String year;
	public String nCite;
	public String pages;

	public String publisher;
	public String keywords;
	public String abstracts;

	public String firstAuthorOrg;
	public String secordAuthorOrg;
	public String thirdAuthorOrg;
	public String otherAuthorOrg;

	void init() {
		id = "-1";
		title = "";

		authorsids = "";
		authors = "";

		orgids = "";
		orgs = "";

		type = "";// book,preceeding

		jconfFullText = "";
		jconf = "";

		year = "-1";
		nCite = "-1";
		pages = "";

		publisher = "";
		keywords = "";
		abstracts = "";
	}

	public Publication() {
		init();
	}

	public Publication(Cell[] row) {
		if (row.length > 0 && row[0].getContents() != "") {
			this.id = row[0].getContents();
			this.jconf = row[1].getContents();
			this.title = row[2].getContents();
			this.authors = row[3].getContents();
			this.year = row[4].getContents();
			this.orgs = stemming(row[5].getContents());
			if (row.length > 11) {
				this.jconfFullText = row[11].getContents();
				this.pages = row[12].getContents().trim();
			}
			fillOrgList();
			String tempCite1 = row[6].getContents();
			int cite = 0;
			if (!tempCite1.equals("") && Integer.parseInt(tempCite1) > cite) {
				cite = Integer.parseInt(tempCite1);
			}
			String tempCite2 = row[7].getContents();
			if (!tempCite2.equals("") && Integer.parseInt(tempCite2) > cite) {
				cite = Integer.parseInt(tempCite2);
			}
			this.nCite = String.valueOf(cite);
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(id + "\t" + title + "\t" + authors
				+ "\t" + orgs + "\t" + jconf + "\t" + year + "\t" + pages
				+ "\t" + nCite);
		return sb.toString();
	}

	public Publication(String str) {
		init();
		String[] strs = str.split("\t");
		id = strs[0];
		title = strs[1];
		authors = strs[2];
		orgs = stemming(strs[3]);
		fillOrgList();
		jconf = strs[4];
		year = strs[5];
		String startPage = strs[6];
		String endPage = strs[7];
		if (!startPage.equals("") && !endPage.equals("")) {
			try {
				int start = Integer.parseInt(startPage);
				int end = Integer.parseInt(endPage);
				if (start == end) {
					pages = startPage;
				} else {
					pages = startPage + "--" + endPage;
				}
			} catch (Exception e) {
			}
		}
		if (strs.length > 8 && strs[8] != "") {
			nCite = strs[8];
		}
	}

	static public int[] calFeature(List<Publication> pubList) {
		List<Integer> citationNumbers = new ArrayList<Integer>();
		int citationCount = 0;
		for (int i = 0; i < pubList.size(); i++) {
			int citationNumber = pubList.get(i).getNcite();
			if (citationNumber < 0)
				citationNumber = 0;
			citationNumbers.add(citationNumber);
			citationCount += citationNumber;
		}
		Collections.sort(citationNumbers, Collections.reverseOrder());
		int[] temp = new int[3];
		temp[0] = hIndex(citationNumbers);
		temp[1] = gIndex(citationNumbers);
		temp[2] = citationCount;
		return temp;
	}

	static int gIndex(List<Integer> citationNumberList) {
		int maxGindex = citationNumberList.size();
		citationNumberList.add(-1);
		int currentCitation = 0;
		int gIndex = 0;
		int sumCitation = 0;
		for (int i = 0; i < citationNumberList.size(); i++) {
			currentCitation = citationNumberList.get(i);
			sumCitation += currentCitation;
			gIndex = (int) Math.floor(Math.sqrt(sumCitation));
			if (gIndex < i) {
				break;
			}
		}
		if (gIndex > maxGindex)
			gIndex = maxGindex;
		return gIndex;
	}

	static int hIndex(List<Integer> citationNumberList) {
		citationNumberList.add(-1);
		int currentCitation = 0;
		int hIndex = 0;
		for (int i = 0; i < citationNumberList.size(); i++) {
			currentCitation = citationNumberList.get(i);
			if (currentCitation <= i) {
				hIndex = i;
				break;
			}
		}
		return hIndex;
	}

	public String toFullText() {
		StringBuilder sb = new StringBuilder(id + "\t" + jconf + "\t" + title
				+ "\t" + authors + "\t" + year + "\t" + orgs + "\t" + nCite);
		sb.append("\t" + authorsids);
		sb.append("\t" + orgids);
		sb.append("\t" + type);// book,preceeding
		sb.append("\t" + jconfFullText);
		sb.append("\t" + pages);
		sb.append("\t" + publisher);
		sb.append("\t" + keywords);
		sb.append("\t" + abstracts);
		return sb.toString();
	}

	protected String stemming(String line) {

		line = line.toLowerCase();
		line = line.replaceAll("\\btu\\b|\\bt u\\b", "technical university of");
		line = line.replaceAll("\\buc\\b|\\bu c\\b",
				"university of california,");
		line = line.replaceAll("\\bu of\\b", "university of");
		line = line.replaceAll("\\band\\b", "&");
		line = line.replaceAll("\\bsci\\S* & tech\\S*\\b",
				"science & technology");
		line = line.replaceAll("\\bcole\\b", "ecole");
		line = line.replaceAll("\\bmnchen\\b", "munchen");
		line = line.replaceAll("\\bzrich\\b", "zurich");
		line = line.replaceAll(" ?, ?", " , ");
		line = line.replaceAll(" ?(?:/|\\(|\\))+ ?", " ");

		line = line.replaceAll("univ\\S*\\b", "university");
		line = line.replaceAll("inst\\S*\\b", "institute");
		line = line.replaceAll("\\.", "");
		line = digital(line);
		return line;

	}

	String digital(String line) {
		String regEx = "(?<=university paris )[0-9]+";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(line);
		if (m.find()) {
			int digital = Integer.parseInt(m.group(0));
			switch (digital) {
			case 1:
				line = m.replaceFirst("I");
				break;
			case 2:
				line = m.replaceFirst("II");
				break;
			case 3:
				line = m.replaceFirst("III");
				break;
			case 4:
				line = m.replaceFirst("IV");
				break;
			case 5:
				line = m.replaceFirst("V");
				break;
			case 6:
				line = m.replaceFirst("VI");
				break;
			case 7:
				line = m.replaceFirst("VII");
				break;
			case 8:
				line = m.replaceFirst("VIII");
				break;
			case 9:
				line = m.replaceFirst("XI");
				break;
			case 10:
				line = m.replaceFirst("X");
				break;
			case 11:
				line = m.replaceFirst("XI");
				break;
			case 12:
				line = m.replaceFirst("XII");
				break;
			case 13:
				line = m.replaceFirst("XIII");
				break;
			default:
				break;
			}
		}
		return line;
	}

	public String getAuthors() {
		return this.authors;
	}

	public String getOrgs() {
		return this.orgs;
	}

	public String getConf() {
		return this.jconf;
	}

	public String getTitle() {
		return this.title;
	}

	public void setId(int id) {
		if (id > 0)
			this.id = String.valueOf(id);
	}

	public int getId() {
		return Integer.parseInt(this.id);
	}

	public int getYear() {
		return Integer.parseInt(this.year);
	}

	public int getStartPage() {
		int startPage = -1;
		try {
			if (this.pages.matches("\\d+:\\d+--\\d+:\\d+")) {
				startPage = Integer.parseInt(this.pages.split(":")[0]);
			} else if (this.pages != "" && this.pages != "-1") {

				if (pages.split("--").length > 1) {
					startPage = Integer.parseInt(pages.split("--")[0]);
				} else {
					startPage = Integer.parseInt(this.pages);
				}
			}
		} catch (Exception e) {
			System.err.println(this.pages);
		}
		return startPage;
	}

	public int getEndPage() {
		int endPage = -1;
		try {
			if (this.pages.matches("\\d+:\\d+--\\d+:\\d+")) {
				endPage = Integer.parseInt(this.pages.split(":")[0]);
			} else if (this.pages != "" && this.pages != "-1") {
				if (pages.split("--").length > 1) {
					endPage = Integer.parseInt(pages.split("--")[1]);
				} else {
					endPage = Integer.parseInt(this.pages);
				}
			}
		} catch (Exception e) {
			System.err.println(this.pages);
		}
		return endPage;
	}

	public int getNpage() {
		int n = 0;
		try {
			if (this.pages.matches("\\d+:\\d+--\\d+:\\d+")) {
				String startPage = this.pages.split("--")[0];
				int start = Integer.parseInt(startPage.split(":")[1]);
				String endPage = this.pages.split("--")[1];
				int end = Integer.parseInt(endPage.split(":")[1]);
				n = end - start;
			} else if (this.pages != "" && this.pages != "-1") {
				n = 1;
				if (pages.split("--").length > 1) {
					int startPage = Integer.parseInt(pages.split("--")[0]);
					int endPage = Integer.parseInt(pages.split("--")[1]);
					n = startPage - endPage;
				}
			}
		} catch (Exception e) {
			System.err.println(this.pages);
		}
		return n;
	}

	public int getNcite() {
		if (!this.nCite.equals(""))
			return Integer.parseInt(this.nCite);
		else
			return 0;
	}

	public String getPageString() {
		return this.pages;
	}

	public String getAuthorIds() {
		return this.authorsids;
	}

	public String getOrgIds() {
		return this.orgids;
	}

	public String getKeyword() {
		return this.keywords;
	}

	public String getAbstract() {
		return this.abstracts;
	}

	void fillOrgList() {
		this.firstAuthorOrg = "";
		this.secordAuthorOrg = "";
		this.thirdAuthorOrg = "";
		this.otherAuthorOrg = "";
		if (this.orgs.length() != 0) {
			String[] subArr = orgs.split(";");
			for (int i = 0; i < subArr.length; i++) {
				String line = subArr[i];
				switch (i) {
				case 0:
					this.firstAuthorOrg = line;
					break;
				case 1:
					this.secordAuthorOrg = line;
					break;
				case 2:
					this.thirdAuthorOrg = line;
					break;
				default:
					this.otherAuthorOrg += line + ";";
					break;
				}
			}
		}
	}

}
