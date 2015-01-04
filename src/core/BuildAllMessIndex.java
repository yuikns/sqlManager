package core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Publication;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
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

import service.OrgrankOrgService;
import service.OrgrankPaperCopyService;
import service.TxtService;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

public class BuildAllMessIndex {
	Set<String> visited = new HashSet<String>();
	public static ArrayList<String> univList100 = new ArrayList<String>();
	Analyzer analyzer = null;
	protected static File indexFile = new File("./indexDirMess/");
	protected static File indexFileSql = new File("./indexDirSql/");

	public BuildAllMessIndex() {
		analyzer = new SimpleAnalyzer(Version.LUCENE_43);
	}

	public enum Arg {
		count, cite, citeLog, normCount
	};

	enum MyField {
		id, author, jconf, firstAuthorOrg, secordAuthorOrg, thirdAuthorOrg, otherAuthorOrg, nPage, nCite, content
	};

	public static void main(String[] args) throws IOException, Exception {
		BuildAllMessIndex bami = new BuildAllMessIndex();
		// bami.readExcel(".\\res\\List720 - 副本.xls");
		// bami.readSql();
//		 bami.openIndexFile("RTSS ");
		// bami.pubQuery(
		// "Public Key Encryption with Keyword Search",
		// "CVPR", "2007");
		// bami.orgQuery(0,
		// "Wake Forest University");
		// bami.countOrg();
		// bami.doSomeTask();
		// bami.orgQueryEva("0");
//		String[] type10 = { "SIGKDD" };
//		bami.orgQuery(0, "microsoft", type10);
		// bami.countOrgNew(type10);
		// List<Publication>res=bami.getPubsByOrg(0,
		// "microsoft");

	}

	// List<String> countOrg(String[]... type) throws IOException {
	// List<String> res = new Vector<String>();
	// OrgrankOrgService oos = new OrgrankOrgService();
	// List<String> metaList = oos.queryMeta();
	// for (String line : metaList) {
	// StringBuilder sb = new StringBuilder();
	// String orgs = line.split("\t")[2];
	// // String id = line.split("\t")[0];
	// // sb.append(id + "\t");
	// // sb.append(org + "\t");
	// Double[] sum = { 0.0, 0.0, 0.0, 0.0,0.0 };
	// System.out.println(orgs);
	// Double[] result1 = orgQuery(0, orgs, type);
	// sum[0] = result1[0];// paper num
	// sum[3] = result1[2];// sum paper cite
	// for (int i = 0; i < 4; i++) {
	// Double[] result = orgQuery(i + 1, orgs, type);
	// if(i==0){
	// sum[4]=result[0];
	// }
	// sum[1] += result[1];// reg paper num
	// sum[2] += result[2];// reg sum paper cite
	//
	// }
	// sb.append(sum[0] + "\t"+sum[4]+"\t" + sum[1] + "\t" + sum[3] + "\t" +
	// sum[2]);
	// res.add(sb.toString());
	// System.out.println(sb.toString());
	// }
	// return res;
	// }
	List<String> countOrg(String[]... type) throws IOException {
		List<String> res = new Vector<String>();
		OrgrankOrgService oos = new OrgrankOrgService();
		List<String> metaList = oos.queryMeta();
		for (String line : metaList) {
			StringBuilder sb = new StringBuilder();
			String orgs = line.split("\t")[2];
			String id = line.split("\t")[0];
			// sb.append(id + "\t");
			// sb.append(org + "\t");
			Double[] sum = { 0.0, 0.0, 0.0, 0.0, 0.0 };
			System.out.println(orgs);
			Double[] result1 = orgQuery(0, orgs, type);
			sum[0] = result1[0];// paper num
			sum[3] = result1[2];// sum paper cite
			for (int i = 0; i < 4; i++) {
				Double[] result = orgQuery(i + 1, orgs, type);
				if (i == 0) {
					sum[4] = result[0];
				}
				sum[1] += result[1];// reg paper num
				sum[2] += result[2];// reg sum paper cite

			}
			sb.append(sum[0] + "\t" + sum[4] + "\t" + sum[1] + "\t" + sum[3]
					+ "\t" + sum[2]);
			res.add(sb.toString());
			System.out.println("paperNum:" + sum[0] + "\tfirstAuthorNum:"
					+ sum[4] + "\tregPaperNum:" + sum[1] + "\tsumPaperCite:"
					+ sum[3] + "\tregPaperCite" + sum[2]);
		}
		return res;
	}

	BooleanQuery parseKeyword(String field, String keyword) throws Exception {
		BooleanQuery query = new BooleanQuery();
		keyword = stemming(keyword);
		String[] keywords = keyword.split(",");
		for (int i = 0; i < keywords.length; i++) {
			PhraseQuery phraseQuery = new PhraseQuery();
			StringReader reader = new StringReader(keywords[i]);
			TokenStream ts = analyzer.tokenStream("", reader);
			CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
			ts.reset(); // Add this line removes NullPointerException
			// 设置查询关键词
			while (ts.incrementToken()) {
				// //调试用注释
				// System.out.println("<<<" + term.toString());
				phraseQuery.add(new Term(field, term.toString()));
			}
			query.add(new BooleanClause(phraseQuery, Occur.MUST));
		}
		return query;
	}

	Double calculateScore(int seq, int authorNum, double nCite) {
		Double score = calculateScore(seq, authorNum);
		if (nCite < 1) {
			nCite = 1;
		}
		score *= nCite;
		// System.out.println("<<<<seq:"+seq+"  score:"+score+"  cite:"+nCite+"   authorNum:"+authorNum);
		return score;
	}
	Double calculate_A_Score(int seq, int authorNum, double nCite) {
		Double score=0.0;
		if (seq == 0) {
			return 1.0;
		}
		for(int i=seq;i<=authorNum;i++)
			score+=1.0/i;
		score=score/authorNum;
		if (nCite < 1) {
			nCite = 1;
		}
		score *= nCite;
		// System.out.println("<<<<seq:"+seq+"  score:"+score+"  cite:"+nCite+"   authorNum:"+authorNum);
		return score;
	}
	Double calculateScore(int seq, int authorNum) {
		Double total = 0.0;
		if (seq == 0) {
			return 1.0;
		}
		for (int i = 0; i < authorNum; i++) {
			total += 1.0 / (i + 1);
		}
		Double score = (1.0 / seq) / total;
		return score;
	}

	void pubQuery(String title, String jconf, String year) {
		IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
		try {
			indexReader = DirectoryReader.open(FSDirectory.open(indexFile));
			Sort sort = new Sort(new SortField[] { new SortField("id",
					SortField.Type.STRING, false) });
			// 创建搜索类
			indexSearcher = new IndexSearcher(indexReader);
			PhraseQuery phraseQuery = new PhraseQuery();
			StringReader reader = new StringReader(title);
			TokenStream ts = analyzer.tokenStream("", reader);
			CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
			ts.reset(); // Add this line removes NullPointerException
			while (ts.incrementToken()) {
				System.out.println(term.toString());
				phraseQuery.add(new Term("content", term.toString()));
			}
			TopDocs topDocs = indexSearcher.search(phraseQuery, 10000, sort);
			ScoreDoc[] scoreDoc = topDocs.scoreDocs;
			System.out.println(scoreDoc.length);
			for (int i = 0; i < scoreDoc.length; i++) {
				// 内部编号 ,和数据库表中的唯一标识列一样
				int doc = scoreDoc[i].doc;
				Document mydoc = indexSearcher.doc(doc);
				Publication pub = new Publication(mydoc.get("content"));
				System.out.println(pub.getConf() + "\t" + pub.getYear() + "\t"
						+ pub.getAuthors() + "\t" + pub.getTitle());
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (indexReader != null) {
				try {
					indexReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	void orgQueryEva(String isAcademic, String[]... type) {
		OrgrankOrgService oos = new OrgrankOrgService();
		List<String> ids = oos.queryScore(isAcademic, "scoreCount").subList(0,
				10);
		List<HashMap<Integer, Double[]>> ans = new ArrayList<HashMap<Integer, Double[]>>();
		// Tex
		System.out.println("\\begin{tabular}{|r|l|l|l|l|l|l|l|l|l|l|}\\hline");
		System.out.print("orgName");
		for (int i = 2004; i < 2014; i++) {
			System.out.print(" & " + i);
		}
		System.out.println("\\\\\\hline");
		for (String id : ids) {
			String metas = oos.queryMetaById(String.valueOf(id)).get(0);
			String orgs = metas.split("\t")[2];
			HashMap<Integer, Double[]> res = new HashMap<Integer, Double[]>();
			System.out.print(metas.split("\t")[1]);
			for (int y = 2004; y < 2014; y++) {
				Double[] sum = { 0.0, 0.0, 0.0, 0.0, 0.0 };
				Double[] result1 = orgQueryYear(0, orgs, y, type);
				sum[0] = result1[0];// paper num
				sum[3] = result1[2];// sum paper cite
				for (int i = 0; i < 4; i++) {
					Double[] result = orgQueryYear(i + 1, orgs, y, type);
					if (i == 0) {
						sum[1] = result[0];
					}
					sum[2] += result[1];// reg paper num
					sum[4] += result[2];// reg sum paper cite

				}
				System.out.print(" & " + sum[4]);
				if (y == 2013)
					System.out.println();
				// System.out.println(" \\\\\\hline ");
				res.put(y, sum);
			}
			ans.add(res);
		}
		System.out.println("\\end{tabular}");

	}

	Double[] orgQueryYear(int seq, String orgs, int year, String[]... type) {
		IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
		Double[] result = { 0.0, 0.0, 0.0, 0.0 };
		try {
			indexReader = DirectoryReader.open(FSDirectory.open(indexFileSql));
			Sort sort = new Sort(new SortField[] { new SortField("id",
					SortField.Type.STRING, false) });
			// 创建搜索类
			indexSearcher = new IndexSearcher(indexReader);
			BooleanQuery query = new BooleanQuery();

			PhraseQuery yearQuery = new PhraseQuery();
			yearQuery.add(new Term("year", String.valueOf(year)));
			query.add(new BooleanClause(yearQuery, Occur.MUST));

			String[] name = orgs.split(";");
			BooleanQuery subQuery = new BooleanQuery();
			for (int i = 0; i < name.length; i++) {
				BooleanQuery subsubQuery;
				switch (seq) {
				case 1:
					subsubQuery = parseKeyword("firstAuthorOrg", name[i]);
					subQuery.add(new BooleanClause(subsubQuery, Occur.SHOULD));
					break;
				case 2:
					subsubQuery = parseKeyword("secordAuthorOrg", name[i]);
					subQuery.add(new BooleanClause(subsubQuery, Occur.SHOULD));
					break;
				case 3:
					subsubQuery = parseKeyword("thirdAuthorOrg", name[i]);
					subQuery.add(new BooleanClause(subsubQuery, Occur.SHOULD));
					break;
				case 4:
					subsubQuery = parseKeyword("otherAuthorOrg", name[i]);
					subQuery.add(new BooleanClause(subsubQuery, Occur.SHOULD));
					break;
				default:
					subsubQuery = parseKeyword("firstAuthorOrg", name[i]);
					subQuery.add(new BooleanClause(subsubQuery, Occur.SHOULD));
					subsubQuery = parseKeyword("secordAuthorOrg", name[i]);
					subQuery.add(new BooleanClause(subsubQuery, Occur.SHOULD));
					subsubQuery = parseKeyword("thirdAuthorOrg", name[i]);
					subQuery.add(new BooleanClause(subsubQuery, Occur.SHOULD));
					subsubQuery = parseKeyword("otherAuthorOrg", name[i]);
					subQuery.add(new BooleanClause(subsubQuery, Occur.SHOULD));
					break;
				}
			}
			query.add(new BooleanClause(subQuery, Occur.MUST));
			if (type.length > 0) {
				BooleanQuery subtypeQuery = new BooleanQuery();
				for (String[] strs : type) {
					for (String str : strs) {
						TermQuery termQuery = new TermQuery(new Term("jconf",
								str));
						subtypeQuery.add(new BooleanClause(termQuery,
								Occur.SHOULD));
					}
				}
				query.add(new BooleanClause(subtypeQuery, Occur.MUST));
			}
			TopDocs topDocs = indexSearcher.search(query, 5000, sort);
			ScoreDoc[] scoreDoc = topDocs.scoreDocs;
			result[0] = scoreDoc.length * 1.0;
			for (int i = 0; i < scoreDoc.length; i++) {
				// 内部编号 ,和数据库表中的唯一标识列一样
				int doc = scoreDoc[i].doc;
				Document mydoc = indexSearcher.doc(doc);
				String author = mydoc.get("author");
				// System.out.println(mydoc.get("firstAuthorOrg")+"  "+mydoc.get("nCite"));
				int authorNum = author.split(";").length;
				double nCitation = 1;
				String tempCite = mydoc.get("nCite");
				result[1] += calculateScore(seq, authorNum, nCitation);
				nCitation = (Integer.parseInt(tempCite) > 1) ? Integer
						.parseInt(tempCite) : 1;
				// if(nCitation>10){
				// nCitation=10*Math.log10(nCitation);
				// }
				result[2] += calculateScore(seq, authorNum, nCitation);
				result[3] += calculate_A_Score(seq, authorNum, nCitation);
			}
			// // 调试用注释
			// System.out.println(query.toString());
			// //--------
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (indexReader != null) {
				try {
					indexReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	List<String> countOrgNew(String[]... type) {
		int startYear = 2004;
		int yearNum = 10;
		OrgrankOrgService oos = new OrgrankOrgService();
		List<String> ids = oos.queryScore("3", "scoreCount");
		List<String> ans = new ArrayList<String>();
		System.out.print("orgName");
		double[] avgNcite = new double[yearNum];
		for (int i = 0; i < yearNum; i++) {
			int year = i + startYear;
			System.out.print("\t" + year);
			avgNcite[i] = Double.parseDouble(oos.queryAvgNciteByYear(year).get(
					0));
		}
		System.out.println();
		for (String id : ids) {
			double[] sum = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,0.0 };
			String metas = oos.queryMetaById(String.valueOf(id)).get(0);
			String orgs = metas.split("\t")[2];
			String orgName = metas.split("\t")[1];
			System.out.print(orgName);
			for (int y = startYear; y < startYear + yearNum; y++) {
				Double[] result1 = orgQueryYear(0, orgs, y, type);
				Double am_score = 0.0;
				sum[0] += result1[0];// paper num
				sum[3] += result1[2];// sum paper cite
				for (int i = 0; i < 4; i++) {
					Double[] result = orgQueryYear(i + 1, orgs, y, type);
					if (i == 0) {
						sum[1] += result[0];
					}
					sum[2] += result[1];// reg paper num
					am_score += result[2];// reg sum paper cite
					sum[6] += result[3];//a_index
				}
				System.out.print("\t" + am_score);
				sum[4] += am_score;
				sum[5] += am_score / avgNcite[y - startYear];
				
				if (y == 2013)
					System.out.println();

			}
			StringBuilder sb = new StringBuilder();
			sb.append(metas + "\t");
			for (int i = 0; i < sum.length; i++) {
				sb.append(String.format("%.2f", sum[i]) + "\t");
			}
			ans.add(sb.toString());
		}
		TxtService txt = new TxtService();
		try {
			Collections.sort(ans, new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {
					// TODO Auto-generated method stub
					int index = o1.split("\t").length;
					Double v1 = Double.parseDouble(o1.split("\t")[index - 1]);
					Double v2 = Double.parseDouble(o2.split("\t")[index - 1]);
					return -v1.compareTo(v2);
				}

			});
			txt.writeLineInFile(ans, "score" + type.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ans;
	}

	Double[] orgQuery(int seq, String orgs, String[]... type) {
		IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
		Double[] result = { 0.0, 0.0, 0.0, 0.0 };
		try {
			indexReader = DirectoryReader.open(FSDirectory.open(indexFileSql));
			Sort sort = new Sort(new SortField[] { new SortField("id",
					SortField.Type.STRING, false) });
			// 创建搜索类
			indexSearcher = new IndexSearcher(indexReader);
			BooleanQuery query = new BooleanQuery();
			String[] name = orgs.split(";");
			BooleanQuery subQuery = new BooleanQuery();
			for (int i = 0; i < name.length; i++) {
				BooleanQuery subsubQuery;
				switch (seq) {
				case 1:
					subsubQuery = parseKeyword("firstAuthorOrg", name[i]);
					subQuery.add(new BooleanClause(subsubQuery, Occur.SHOULD));
					break;
				case 2:
					subsubQuery = parseKeyword("secordAuthorOrg", name[i]);
					subQuery.add(new BooleanClause(subsubQuery, Occur.SHOULD));
					break;
				case 3:
					subsubQuery = parseKeyword("thirdAuthorOrg", name[i]);
					subQuery.add(new BooleanClause(subsubQuery, Occur.SHOULD));
					break;
				case 4:
					subsubQuery = parseKeyword("otherAuthorOrg", name[i]);
					subQuery.add(new BooleanClause(subsubQuery, Occur.SHOULD));
					break;
				default:
					subsubQuery = parseKeyword("firstAuthorOrg", name[i]);
					subQuery.add(new BooleanClause(subsubQuery, Occur.SHOULD));
					subsubQuery = parseKeyword("secordAuthorOrg", name[i]);
					subQuery.add(new BooleanClause(subsubQuery, Occur.SHOULD));
					subsubQuery = parseKeyword("thirdAuthorOrg", name[i]);
					subQuery.add(new BooleanClause(subsubQuery, Occur.SHOULD));
					subsubQuery = parseKeyword("otherAuthorOrg", name[i]);
					subQuery.add(new BooleanClause(subsubQuery, Occur.SHOULD));
					break;
				}
			}
			query.add(new BooleanClause(subQuery, Occur.MUST));
			if (type.length > 0) {
				BooleanQuery subtypeQuery = new BooleanQuery();
				for (String[] strs : type) {
					for (String str : strs) {
						TermQuery termQuery = new TermQuery(new Term("jconf",
								str));
						subtypeQuery.add(new BooleanClause(termQuery,
								Occur.SHOULD));
					}
				}
				query.add(new BooleanClause(subtypeQuery, Occur.MUST));
			}
			System.out.println(query.toString());

			TopDocs topDocs = indexSearcher.search(query, 5000, sort);
			ScoreDoc[] scoreDoc = topDocs.scoreDocs;
			result[0] = scoreDoc.length * 1.0;
			for (int i = 0; i < scoreDoc.length; i++) {
				// 内部编号 ,和数据库表中的唯一标识列一样
				int doc = scoreDoc[i].doc;
				Document mydoc = indexSearcher.doc(doc);
				String author = mydoc.get("author");
				System.out.println(mydoc.get("firstAuthorOrg") + "  "
						+ mydoc.get("jconf"));
				int authorNum = author.split(";").length;
				double nCitation = 1;
				String tempCite = mydoc.get("nCite");
				result[1] += calculateScore(seq, authorNum, nCitation);
				nCitation = (Integer.parseInt(tempCite) > 1) ? Integer
						.parseInt(tempCite) : 1;
				result[2] += calculateScore(seq, authorNum, nCitation);

			}
			// // 调试用注释
			// System.out.println(result[0] + "  " + result[1] + "  " +
			// result[2]);
			// //--------
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (indexReader != null) {
				try {
					indexReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	List<Publication> getPubsByOrg(int seq, String orgs, String[]... type) {
		IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
		List<Publication> result = new ArrayList<Publication>();
		try {
			indexReader = DirectoryReader.open(FSDirectory.open(indexFileSql));
			Sort sort = new Sort(new SortField[] { new SortField("year",
					SortField.Type.STRING, false) });
			// 创建搜索类
			indexSearcher = new IndexSearcher(indexReader);
			BooleanQuery query = new BooleanQuery();
			String[] name = orgs.split(";");
			BooleanQuery subquery = new BooleanQuery();
			for (int i = 0; i < name.length; i++) {
				BooleanQuery subQuery;
				switch (seq) {
				case 1:
					subQuery = parseKeyword("firstAuthorOrg", name[i]);
					query.add(new BooleanClause(subQuery, Occur.MUST));
					break;
				case 2:
					subQuery = parseKeyword("secordAuthorOrg", name[i]);
					query.add(new BooleanClause(subQuery, Occur.MUST));
					break;
				case 3:
					subQuery = parseKeyword("thirdAuthorOrg", name[i]);
					query.add(new BooleanClause(subQuery, Occur.MUST));
					break;
				case 4:
					subQuery = parseKeyword("otherAuthorOrg", name[i]);
					query.add(new BooleanClause(subQuery, Occur.MUST));
					break;
				default:
					// BooleanQuery subquery = new BooleanQuery();
					query.add(new BooleanClause(parseKeyword("firstAuthorOrg",
							name[i]), Occur.SHOULD));
					query.add(new BooleanClause(parseKeyword("secordAuthorOrg",
							name[i]), Occur.SHOULD));
					query.add(new BooleanClause(parseKeyword("thirdAuthorOrg",
							name[i]), Occur.SHOULD));
					query.add(new BooleanClause(parseKeyword("otherAuthorOrg",
							name[i]), Occur.SHOULD));
					// query.add(new BooleanClause(subqu ery, Occur.MUST));
					break;
				}
			}
			// query.add(new BooleanClause(subQuery, Occur.MUST));
			if (type.length > 0) {
				BooleanQuery subtypeQuery = new BooleanQuery();
				for (String[] strs : type) {
					for (String str : strs) {
						TermQuery termQuery = new TermQuery(new Term("jconf",
								str));
						subtypeQuery.add(new BooleanClause(termQuery,
								Occur.SHOULD));
					}
				}
				query.add(new BooleanClause(subtypeQuery, Occur.MUST));
			}

			TopDocs topDocs = indexSearcher.search(query, 5000, sort);
			ScoreDoc[] scoreDoc = topDocs.scoreDocs;
			for (int i = 0; i < scoreDoc.length; i++) {
				// 内部编号 ,和数据库表中的唯一标识列一样
				int doc = scoreDoc[i].doc;
				Document mydoc = indexSearcher.doc(doc);
				String content = mydoc.get("content");
				Publication pub = new Publication();
				pub.id = content.split("\t")[0];
				pub.title = content.split("\t")[1];
				pub.authors = content.split("\t")[2];
				pub.year = mydoc.get("year");
				pub.jconf = mydoc.get("jonf");
				// pub.firstAuthorOrg=mydoc.get("firstAuthorOrg");

				pub.nCite = mydoc.get("nCite");
				// if(pub.year.equals("2013"))
				// System.out.println(pub.nCite);

				result.add(pub);
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (indexReader != null) {
				try {
					indexReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	int killShortPaper(String jconf, int startYear, int endYear, int arg) {
		int n = 0;
		System.out.print(jconf + ":\t");
		for (int i = startYear; i < endYear; i++) {
			pageQuery(jconf, String.valueOf(i), arg);
			System.out.print("\t");
		}
		System.out.println();
		return n;
	}

	List<Publication> pageQuery(String jconf, String year, int arg) {
		IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
		List<Publication> result = new ArrayList<Publication>();
		try {
			indexReader = DirectoryReader.open(FSDirectory.open(indexFile));
			Sort sort = new Sort(new SortField[] { new SortField(
					MyField.nPage.toString(), SortField.Type.INT, true) });
			// 创建搜索类
			indexSearcher = new IndexSearcher(indexReader);
			BooleanQuery query = new BooleanQuery();
			PhraseQuery confQuery = new PhraseQuery();
			confQuery.add(new Term("jconf", jconf));
			query.add(new BooleanClause(confQuery, Occur.MUST));
			if (year != "") {
				PhraseQuery yearQuery = new PhraseQuery();
				yearQuery.add(new Term("year", year));
				query.add(new BooleanClause(yearQuery, Occur.MUST));
			}
			// 搜索结果
			TopDocs topDocs = indexSearcher.search(query, 1000, sort);
			ScoreDoc[] scoreDoc = topDocs.scoreDocs;
			int deadline = 0;
			int kill = 0;
			int maxkillPage = 0;
			for (int i = 0; i < scoreDoc.length; i++) {
				// 内部编号 ,和数据库表中的唯一标识列一样
				int doc = scoreDoc[i].doc;
				Document mydoc = indexSearcher.doc(doc);
				int nPage = Integer.parseInt(mydoc.get("nPage"));
				if (i == 10) {
					deadline = nPage * 3 / 4;
				}
				if (nPage < deadline) {
					kill++;
					if (maxkillPage == 0)
						maxkillPage = nPage;
				}
				String content = mydoc.get("content");
				result.add(new Publication(content));
			}
			if (arg == 0) {
				System.out.print(scoreDoc.length + "\t" + kill + "\t"
						+ deadline);
			} else {
				System.out.print(scoreDoc.length + "\t"
						+ (scoreDoc.length - kill) + "\t" + maxkillPage);
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (indexReader != null) {
				try {
					indexReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public void readSql() {
		Directory directory = null;
		IndexWriter indexWriter = null;
		BufferedWriter bw = null;
		try {
			java.util.Date time = new Date();
			String logFile = ".\\res\\log" + time.getTime();
			bw = new BufferedWriter(new FileWriter(logFile));
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
					Version.LUCENE_43, analyzer);
			// 创建磁盘目录对象
			directory = new SimpleFSDirectory(indexFileSql);
			indexWriter = new IndexWriter(directory, indexWriterConfig);
			OrgrankPaperCopyService ps = new OrgrankPaperCopyService();
			List<Publication> a = ps.writeBack();
			// 为了避免重复插入数据，每次测试前 先删除之前的索引
			indexWriter.deleteAll();
			for (int j = 0; j < a.size(); j++) {
				// indexWriter添加索引
				Document doc = new Document();
				Publication pub = a.get(j);
				bw.write(pub.toString() + "\r\n");
				doc.add(new StringField("id", pub.id, Field.Store.YES));
				doc.add(new TextField("author", pub.getAuthors(),
						Field.Store.YES));
				doc.add(new StringField("jconf", pub.jconf, Field.Store.YES));
				doc.add(new StringField("year", pub.year, Field.Store.YES));
				doc.add(new TextField("firstAuthorOrg", pub.firstAuthorOrg,
						Field.Store.YES));
				doc.add(new TextField("secordAuthorOrg", pub.secordAuthorOrg,
						Field.Store.YES));
				doc.add(new TextField("thirdAuthorOrg", pub.thirdAuthorOrg,
						Field.Store.YES));
				doc.add(new TextField("otherAuthorOrg", pub.otherAuthorOrg,
						Field.Store.YES));
				doc.add(new StringField("nPage",
						String.valueOf(pub.getNpage()), Field.Store.YES));
				doc.add(new IntField("nCite", pub.getNcite(), Field.Store.YES));
				doc.add(new TextField("content", pub.toString(),
						Field.Store.YES));
				indexWriter.addDocument(doc);
				if (j == a.size() - 1)
					System.out.println("索引添加成功：第" + (j + 1) + "次！！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (indexWriter != null) {
				try {
					indexWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (directory != null) {
				try {
					directory.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("*****************创建索引结束**********************");

	}

	void readExcel(String filePath) {
		Directory directory = null;
		IndexWriter indexWriter = null;
		try {
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
					Version.LUCENE_43, analyzer);
			// 创建磁盘目录对象
			directory = new SimpleFSDirectory(indexFile);
			indexWriter = new IndexWriter(directory, indexWriterConfig);
			// 为了避免重复插入数据，每次测试前 先删除之前的索引
			indexWriter.deleteAll();
			WorkbookSettings workbookSettings = new WorkbookSettings();
			workbookSettings.setEncoding("utf-8");
			Workbook book = Workbook.getWorkbook(new FileInputStream(new File(
					filePath)), workbookSettings);
			Sheet sheet = book.getSheet(0);
			System.out.println(sheet.getName());
			for (int j = 0; j < sheet.getRows(); j++) {
				Cell cell = sheet.getCell(3, j);
				String authors = cell.getContents();
				// System.out.println(j + " " + authors);
				if (authors != "") {
					// indexWriter添加索引
					Document doc = new Document();
					Publication pub = new Publication(sheet.getRow(j));
					doc.add(new StringField("id", pub.id, Field.Store.YES));
					doc.add(new TextField("author", authors.trim(),
							Field.Store.YES));
					doc.add(new StringField("jconf", pub.jconf, Field.Store.YES));
					doc.add(new StringField("year", pub.year, Field.Store.YES));
					doc.add(new TextField("firstAuthorOrg", pub.firstAuthorOrg,
							Field.Store.YES));
					doc.add(new TextField("secordAuthorOrg",
							pub.secordAuthorOrg, Field.Store.YES));
					doc.add(new TextField("thirdAuthorOrg", pub.thirdAuthorOrg,
							Field.Store.YES));
					doc.add(new TextField("otherAuthorOrg", pub.otherAuthorOrg,
							Field.Store.YES));
					doc.add(new StringField("nPage", String.valueOf(pub
							.getNpage()), Field.Store.YES));
					doc.add(new StringField("nCite", pub.nCite, Field.Store.YES));
					doc.add(new TextField("content", pub.toString(),
							Field.Store.YES));
					indexWriter.addDocument(doc);
				}
				if (j == sheet.getRows() - 1)
					System.out.println("索引添加成功：第" + (j + 1) + "次！！");
			}
			book.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BiffException e) {
			e.printStackTrace();
		} finally {
			if (indexWriter != null) {
				try {
					indexWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (directory != null) {
				try {
					directory.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("*****************创建索引结束**********************");
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
		line = line.replaceAll(" ?(?:-|/|\\(|\\))+ ?", " ");

		line = line.replaceAll("univ\\S*\\b", "university");
		line = line.replaceAll("inst\\S*\\b", "institute");
		line = line.replaceAll("\\.", "");
		line = digital(line);
		return line;

	}

	public void openIndexFile(String conf) throws IOException {
		// 得到索引的目录
		Directory directory = null;
		IndexReader indexReader = null;
		BufferedWriter bw = null;
		try {
			directory = new SimpleFSDirectory(indexFileSql);
			// 根据目录打开一个indexReader
			indexReader = DirectoryReader.open(directory);
			Sort sort = new Sort(new SortField[] { new SortField("id",
					SortField.Type.STRING, false) });
			// 创建搜索类
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			BooleanQuery query = new BooleanQuery();
			BooleanQuery subtypeQuery = new BooleanQuery();
			TermQuery termQuery = new TermQuery(new Term("jconf", conf));
			subtypeQuery.add(new BooleanClause(termQuery, Occur.SHOULD));
			query.add(new BooleanClause(subtypeQuery, Occur.MUST));
			System.out.println(query.toString());
			TopDocs topDocs = indexSearcher.search(query, 5000, sort);
			ScoreDoc[] scoreDoc = topDocs.scoreDocs;
			System.out.println(scoreDoc.length);
//			for (int i = 0; i < scoreDoc.length; i++) {
//				// 内部编号 ,和数据库表中的唯一标识列一样
//				int doc = scoreDoc[i].doc;
//				Document mydoc = indexSearcher.doc(doc);
//				String author = mydoc.get("author");
//				String jconf=mydoc.get("jconf");
//				System.out.println(author+"\t"+jconf);
//			}
//			int docLength = indexReader.maxDoc();
//			for (int i = 0; i < docLength; i++) {
//				Document doc = indexReader.document(i);
//				if(doc.get("content").contains("RTSS")){
//					System.out.println(doc.get("jconf"));
//				}
//			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				bw.close();
			}
			if (indexReader != null) {
				try {
					indexReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (directory != null) {
				try {
					directory.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("*****************读取索引结束**********************");
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

	public void get100Univ() {
		try {
			TxtService.getStringList(".\\res\\univ100.txt", univList100);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
