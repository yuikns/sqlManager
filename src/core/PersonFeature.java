package core;

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import service.*;
import model.*;

public class PersonFeature {

	public static void main(String[] args) throws IOException, Exception {
		PersonFeature pf = new PersonFeature();
		pf.eva("AAAI", "2005");
		pf.eva("AAAI", "2007");
//		List<String> confs = new ArrayList<String>();
//		TxtService.getStringList(".\\res\\CCFA.txt", confs);
//		String[][] a = new String[10][confs.size()];
//		for (int j = 0; j < confs.size(); j++) {
//			for (int i = 0; i < 10; i++) {
//				a[i][j] = pf.eva(confs.get(j), String.valueOf(i + 2004));
//			}
//		}
//		int[] totalColFind = new int[confs.size()];
//		int[] totalColTruth = new int[confs.size()];
//		for (int i = -1; i < 11; i++) {
//			int totalRowFind = 0;
//			int totalRowTruth = 0;
//			if (i == -1) {
//				System.out.print("\t");
//				for (int j = 0; j < confs.size(); j++)
//					System.out.print(confs.get(j) + "\t");
//				System.out.print("total");
//			} else if (i == 10) {
//				System.out.print("sum\t");
//				for (int j = 0; j < confs.size(); j++) {
//					System.out.print(totalColFind[j] + "/" + totalColTruth[j]
//							+ "\t");
//				}
//				System.out.println();
//				System.out.print("%\t");
//				for (int j = 0; j < confs.size(); j++) {
//					if (totalColTruth[j] > 0)
//						System.out.print(totalColFind[j] * 1.0
//								/ totalColTruth[j] + "\t");
//					else
//						System.out.print("\t");
//				}
//				System.out.println();
//			} else {
//				System.out.print(i + 2004 + "\t");
//				for (int j = 0; j < confs.size(); j++) {
//					totalRowFind += Integer.parseInt(a[i][j].split("/")[0]);
//					totalRowTruth += Integer.parseInt(a[i][j].split("/")[1]);
//					totalColFind[j] += Integer.parseInt(a[i][j].split("/")[0]);
//					totalColTruth[j] += Integer.parseInt(a[i][j].split("/")[1]);
//					System.out.print(a[i][j] + "\t");
//				}
//				System.out.print(totalRowFind + "/" + totalRowTruth + "\t"
//						+ totalRowFind * 1.0 / totalRowTruth);
//			}
//			System.out.println();
//		}
	}

	public String eva(String conf, String year) {
		// String conf = "CRYPTO";
		// String year = "2013";
		OrgrankPersonevaService personService = new OrgrankPersonevaService();
		OrgrankPaperCopyService paperService = new OrgrankPaperCopyService();
		LuceneService luceneService = new LuceneService();
		Map<String, List<String>> personPubMap = personService.queryCY(conf,
				year);
		int findAll = 0;
		int truthAll = 0;
		for (String person : personPubMap.keySet()) {
			if (personPubMap.get(person).size() > 0) {
				List<String> find = luceneService.personQuery(person, conf,
						year);
				findAll += find.size();
				truthAll += personPubMap.get(person).size();
				if (find.size() < personPubMap.get(person).size()) {
					TxtService ts = new TxtService();
					Set<String> set = ts.regulize(personPubMap.get(person));
					set.removeAll(ts.regulize(find));
					 System.out.println(person + " " + find.size() + "/"
					 + personPubMap.get(person).size());
					for (String title : set) {
						if (paperService.queryPubByTitle(title).size() > 0) {
							 System.out.println(title + " EXISTS");
							findAll++;
						} else {
							System.out.println(person + ":" + conf + year
									+ ":\t" + title);
						}
					}
				}
				if (find.size() > personPubMap.get(person).size()) {
					TxtService ts = new TxtService();
					Set<String> set = ts.regulize(personPubMap.get(person));
					set.removeAll(ts.regulize(find));
					 System.out.println(person + " " + find.size() + "/"
					 + personPubMap.get(person).size());
					for (String title : set) {
						if (paperService.queryPubByTitle(title).size() > 0) {
							 System.out.println(title + " EXISTS");
							findAll++;
						} else {
							System.out.println(person + ":" + conf + year
									+ ":\t" + title);
						}
					}
				}
			}
		}
		// System.out.println();
		return findAll + "/" + truthAll;
	}
}
