package core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import model.Paper;
import model.PaperForEva;
import model.Publication;
import service.AminerPaperService;
import service.ExcelService;
import service.JsonService;
import service.OrgrankPaperCopyService;
import service.TxtService;

public class BuildPaperCopy {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BuildPaperCopy bc = new BuildPaperCopy();
		// bc.compareTitle("2004","VIS",".\\res\\pubEva\\vis20042008.xls");
		// bc.evaTitle();
		// String dirPath = ".\\res\\pubEva\\txt1";
		// File dir = new File(dirPath);
		// for (String filePath : dir.list()) {
		// bc.buildTable(dirPath + File.separator + filePath);
		// }
		// bc.updateUnicode("technlology", "technology");
		// bc.buildTable(3);
		// bc.deleteData();

		// bc.queryPage();
		// bc.updateOrg();
		// bc.updatePage();
		// bc.evaOrgCompletity();
		// bc.printTxtLackOrg();
		// bc.updateNcite();
		// bc.doSomeInc();
	}

	public void doSomeInc() {
		OrgrankPaperCopyService os = new OrgrankPaperCopyService();
		List<String> util = new Vector<String>();
		try {
			TxtService.getStringList("E:/za.txt", util);
			for (int i = 0; i < util.size(); i++) {
				String[] strs = util.get(i).split("\t");
				// List<String> ids = os.queryPubByTitle(strs[0]);
				// if(ids.size()>0){
				int res = os.updateOrg(strs[0], strs[2]);
				System.out.println(strs[0] + "\t" + res);
				// }
				// else{
				// System.err.println(strs[0]);
				// }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void evaTitle() {
		String dirPath = ".\\res\\pubEva\\txt1";
		File dir = new File(dirPath);
		System.out.println(dir.getAbsolutePath());
		for (String filePath : dir.list()) {
			int i1 = filePath.lastIndexOf('_');
			int i2 = filePath.lastIndexOf('.');
			String conf = filePath.substring(0, i1);
			String year = filePath.substring(i1 + 1, i2);
			System.out.println("----" + conf + " " + year + "------");
			compareTitle(year, conf, dirPath + File.separator + filePath);
		}
	}

	void compareTitle(String year, String conf, String filePath) {
		OrgrankPaperCopyService ps = new OrgrankPaperCopyService();
		// ===>Excel
		// ExcelService es = new ExcelService();
		// List<String> aa = es.readCol(filePath, 0, 2);
		// List<String> a = new Vector<String>();
		// for (String fulltext : aa) {
		// PaperForEva paper = new PaperForEva();
		// paper.setFulltext(fulltext);
		// a.add(paper.getTitle());
		// }

		// ====>Txt
		TxtService ts = new TxtService();
		List<String> a = ts.readAcol(filePath, 0);

		// ====>common
		List<String> b = ps.queryAColByYC("title", year, conf);
		ps.comparePapers(a, b);
	}

	void updateid() {
		OrgrankPaperCopyService ps = new OrgrankPaperCopyService();
		ps.queryPubById00();
	}

	void evaOrgCompletity() {
		OrgrankPaperCopyService ps = new OrgrankPaperCopyService();
		List<String> confs = new Vector<String>();
		try {
			TxtService.getStringList(".\\res\\CCFA.txt", confs);
			List<String> res = ps.checkOrgGroupByCY();
			String[][] a = new String[10][confs.size()];
			for (String str : res) {
				String conf = str.split("\t")[1];
				int year = Integer.parseInt(str.split("\t")[2]);
				a[year - 2004][confs.indexOf(conf)] = str.split("\t")[0];
			}
			for (int i = 0; i < confs.size(); i++) {
				for (int j = 0; j < 10; j++) {
					System.out.print(a[j][i] + "\t");
				}
				System.out.println();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void evaCompletity() {
		OrgrankPaperCopyService ps = new OrgrankPaperCopyService();
		List<String> confs = new Vector<String>();
		try {
			TxtService.getStringList(".\\res\\CCFA.txt", confs);
			List<String> res = ps.checkGroupByCY();
			String[][] a = new String[10][confs.size()];
			for (String str : res) {
				String conf = str.split("\t")[0];
				int year = Integer.parseInt(str.split("\t")[1]);
				a[year - 2004][confs.indexOf(conf)] = str.split("\t")[2];
			}
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < confs.size(); j++) {
					System.out.print(a[i][j] + "\t");
				}
				System.out.println();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void updateUnicode(String olds, String news) {
		OrgrankPaperCopyService ps = new OrgrankPaperCopyService();
		List<String> res = ps.queryPubByOrg(olds);
		System.out.println(res.size());
		for (int i = 0; i < res.size(); i++) {
			String id = res.get(i).split("\t")[0];
			String oldOrg = res.get(i).split("\t")[1].trim().toLowerCase();
			String newOrg = oldOrg.replace(olds.toLowerCase(),
					news.toLowerCase());
			int re = ps.updateOrg(id, newOrg);
			if (re > 0) {
				System.out.println("ok " + i);
			}
		}

	}

	public void updateNcite() {
		OrgrankPaperCopyService ps = new OrgrankPaperCopyService();
		List<String> res = ps.queryLackCite();
		AminerPaperService as = new AminerPaperService();
		int total = res.size();
		int count = 0;
		for (String line : res) {
			String[] str = line.split("\t");
			String id = str[0];
			String title = str[1];
			String year = str[2];
			int nCite = as.queryNciteByTitle(title, year);
			if (nCite != -1) {
				ps = new OrgrankPaperCopyService();
				int an = ps.updateNcite(id, nCite);
				count++;
			}
			if (count % 10 == 0) {
				System.out.println(count + "/" + total);
			}
		}

	}

	void updateOrg() {
		OrgrankPaperCopyService ps = new OrgrankPaperCopyService();
		ExcelService es = new ExcelService();
		String filePath = ".\\res\\缺机构.xls";
		List<String> ids = es.readCol(filePath, 0, 0);
		List<String> newOrgs = es.readCol(filePath, 0, 1);
		for (int i = 0; i < ids.size(); i++) {
			String id = ids.get(i);
			String newOrg = newOrgs.get(i).replaceAll("[\r\n]", "").trim();
			int res = ps.updateOrg(id, newOrg);
			if (res == 1) {
				System.out.println("ok " + i);
			}
		}

	}

	void updatePage() {
		OrgrankPaperCopyService ps = new OrgrankPaperCopyService();
		ExcelService es = new ExcelService();
		String filePath = ".\\res\\缺页码.xls";
		List<String> ids = es.readCol(filePath, 0, 0);
		List<String> newPages = es.readCol(filePath, 0, 1);
		for (int i = 0; i < ids.size(); i++) {
			String id = ids.get(i);
			String newPage = newPages.get(i).replaceAll("[\r\n]", "").trim();
			int res = ps.updatePage(id, newPage);
			if (res == 1) {
				System.out.println("ok " + i);
			}
		}

	}

	void queryPage() {
		OrgrankPaperCopyService ps = new OrgrankPaperCopyService();
		for (int i = 2004; i < 2014; i++) {
			System.out.println(i);
			ps.checkPage(String.valueOf(i), "ASPLOS");
		}
	}

	void deleteData() {
		int sheetNo = 2;
		ExcelService es = new ExcelService();
		String filePath = ".\\res\\List720 - 副本.xls";
		List<String> titles = es.readCol(filePath, sheetNo, 0);
		OrgrankPaperCopyService ps = new OrgrankPaperCopyService();
		for (String title : titles) {
			if (!title.equals("")) {
				System.out.println(title + ps.deleteByTitle(title));
			}
		}
	}

	void buildTable(String filePath) {
		OrgrankPaperCopyService ps = new OrgrankPaperCopyService();
		TxtService ts = new TxtService();
		List<Publication> pubs = ts.constructPubs(filePath);
		System.out.println(pubs.size());
		for (int i = 0; i < pubs.size(); i++) {
			Publication pub = pubs.get(i);
			int id = ps.getMaxid() + 1;
			pub.setId(id);
			if (ps.insertIntoPaperCopy(pub) != 1)
				System.out.println(i + "\t" + pub.toString());
			if (i % 10 == 0)
				System.out.println(i);
			// System.out.println();
		}
	}

	void buildTable(int sheetNo) {
		OrgrankPaperCopyService ps = new OrgrankPaperCopyService();
		ExcelService es = new ExcelService();
		List<Publication> pubs = es.getPubsFromExcel(
				".\\res\\List720 - 副本.xls", sheetNo);
		System.out.println(pubs.size());
		for (int i = 0; i < pubs.size(); i++) {
			Publication pub = pubs.get(i);
			System.out.println(i + "\t" + pub.toString());
			System.out.println(ps.insertIntoPaperCopy(pub));
		}
	}

	void printTxtLackOrg() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					".\\res\\require_org.txt"));
			OrgrankPaperCopyService ps = new OrgrankPaperCopyService();
			List<String> res = ps.checkOrg();
			for (String a : res) {
				System.out.println(a);
				bw.write(a + "\r\n");
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public enum Occur {
		MUST, Should, MUST_NOT
	}

	boolean termParser(String[] TermsAns, String TermsQue, Occur occur) {
		boolean re = false;
		Set<String> a = new HashSet<String>(Arrays.asList(TermsAns));
		Set<String> q = new HashSet<String>(Arrays.asList(TermsQue));
		if (occur == Occur.MUST) {
			a.removeAll(q);
			re = a.size() > 0 ? false : true;
		} else if (occur == Occur.Should) {
			int oldSize = a.size();
			a.removeAll(q);
			re = oldSize > a.size() ? true : false;
		} else {
			int oldSize = a.size();
			a.removeAll(q);
			re = oldSize == a.size() ? true : false;
		}
		return re;
	}
	List<Integer> findIndex(List<String> q,String term){
		List<Integer> res=new Vector<Integer>();
		for(int i=0;i<q.size();i++){
			if(term.equals(q.get(i))){
				res.add(i);
			}
		}
		return res;
	}
	boolean phaseParser(String que, String ans, int step) {
		boolean re = false;
		String[] TermsAns = ans.split(" ");
		String[] TermsQue = que.split(" ");
		List<String> a = Arrays.asList(TermsAns);
		List<String> q = Arrays.asList(TermsQue);
		int start = q.indexOf(a.get(0));
		if (start == -1)
			return re;
		for (String termA : a) {
			int temp =q.indexOf(termA);
			if (temp == -1)
				return re;
			if (temp - start > step)
				return re;
			start = temp;
			q.set(temp, "#");
		}
		return true;
	}

	boolean selfMadeOrgParser(String orgQue, String orgAns) {
		boolean re = false;
		String[] strs = orgAns.split(",");
		for (String str : strs) {
			phaseParser(str, orgQue, 1);
		}
		return re;
	}

	void buildHashMap(List<String> orgTextList) {
		// id \t org1 \t ...
		HashMap<String, List<String>> a = new HashMap<String, List<String>>();
		for (String orgText : orgTextList) {
			String[] strs = orgText.split("\t");
			String id = strs[0];
			List<String> tmp = Arrays.asList(strs);
			tmp.remove(0);
			a.put(id, tmp);
		}
	}
}