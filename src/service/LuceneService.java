package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

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

import core.BuildPaperCopy;
import model.Publication;

public class LuceneService {
	Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_43);
	protected static File indexFile = new File("./indexDirMess/");
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LuceneService ls=new LuceneService();
//		ls.buildIndexFromExcel(".\\res\\List720 - 副本.xls");
		ls.personQuery("Jie Tang","SIGKDD","2011");
	}
	public List<String> personQuery(String name, String jconf,String year) {
		List<String> result = new ArrayList<String>();
		IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
		try {
			indexReader = DirectoryReader.open(FSDirectory.open(indexFile));
			Sort sort = new Sort(new SortField[] { new SortField("year",
					SortField.Type.STRING, true) });
			// 创建搜索类
			indexSearcher = new IndexSearcher(indexReader);
			BooleanQuery query = new BooleanQuery();
			BooleanQuery nameQuery = new BooleanQuery();
			StringReader reader = new StringReader(name);
			TokenStream ts = analyzer.tokenStream("", reader);
			CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
			ts.reset(); // Add this line removes NullPointerException
			// 设置查询关键词
			while (ts.incrementToken()) {
				TermQuery termQuery = new TermQuery(new Term("author",
						term.toString()));
				nameQuery.add(new BooleanClause(termQuery, Occur.MUST));
			}
			query.add(new BooleanClause(nameQuery, Occur.MUST));
			PhraseQuery yearQuery = new PhraseQuery();
			yearQuery.add(new Term("year", year));
			query.add(new BooleanClause(yearQuery, Occur.MUST));
			if (jconf != "") {
				PhraseQuery jconfQuery = new PhraseQuery();
				jconfQuery.add(new Term("jconf", jconf));
				query.add(new BooleanClause(jconfQuery, Occur.MUST));
			}
			// 搜索结果
			TopDocs topDocs = indexSearcher.search(query, 1000, sort);
			ScoreDoc[] scoreDoc = topDocs.scoreDocs;
//			 System.out.println(name + "\t" + scoreDoc.length);
			for (int i = 0; i < scoreDoc.length; i++) {
				int doc = scoreDoc[i].doc;
				Document mydoc = indexSearcher.doc(doc);
				String title = mydoc.get("content").split("\t")[2];
				result.add(title);
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

	List<String> personQuery(String name, String jconf) {
		List<String> result = new ArrayList<String>();
		IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
		try {
			indexReader = DirectoryReader.open(FSDirectory.open(indexFile));
			Sort sort = new Sort(new SortField[] { new SortField("year",
					SortField.Type.STRING, true) });
			// 创建搜索类
			indexSearcher = new IndexSearcher(indexReader);
			BooleanQuery query = new BooleanQuery();
			BooleanQuery nameQuery = new BooleanQuery();
			StringReader reader = new StringReader(name);
			TokenStream ts = analyzer.tokenStream("", reader);
			CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
			ts.reset(); // Add this line removes NullPointerException
			// 设置查询关键词
			while (ts.incrementToken()) {
				TermQuery termQuery = new TermQuery(new Term("author",
						term.toString()));
				nameQuery.add(new BooleanClause(termQuery, Occur.MUST));
			}
			query.add(new BooleanClause(nameQuery, Occur.MUST));
			if (jconf != "") {
				PhraseQuery jconfQuery = new PhraseQuery();
				jconfQuery.add(new Term("jconf", jconf));
				query.add(new BooleanClause(jconfQuery, Occur.MUST));
			}
			// 搜索结果
			TopDocs topDocs = indexSearcher.search(query, 1000, sort);
			ScoreDoc[] scoreDoc = topDocs.scoreDocs;
			// System.out.println(name + "\t" + scoreDoc.length);
			for (int i = 0; i < scoreDoc.length; i++) {
				int doc = scoreDoc[i].doc;
				Document mydoc = indexSearcher.doc(doc);
				String year = mydoc.get("year");
				String id = mydoc.get("id");
				String title = mydoc.get("content").split("\t")[2];
				String res = id + "\t" + year + "\t" + title;
				result.add(res);
				System.out.println(res);

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
	void deleteIndex(){
		
	}
	void updateIndex(){
		
	}
	void appendIndex(Publication pub) {
		Directory directory = null;
		IndexWriter indexWriter = null;
		try {
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
					Version.LUCENE_43, analyzer);
			// 创建磁盘目录对象
			directory = new SimpleFSDirectory(indexFile);
			indexWriter = new IndexWriter(directory, indexWriterConfig);
			// 为了避免重复插入数据，每次测试前 先删除之前的索
			indexWriter.deleteAll();
			// indexWriter添加索引
			Document doc = new Document();
			doc.add(new StringField("id", pub.id, Field.Store.YES));
			doc.add(new TextField("author", pub.getAuthors(), Field.Store.YES));
			doc.add(new StringField("jconf", pub.jconf, Field.Store.YES));
			doc.add(new StringField("year", pub.year, Field.Store.YES));
			doc.add(new StringField("firstAuthorOrg", pub.firstAuthorOrg,
					Field.Store.YES));
			doc.add(new StringField("secordAuthorOrg", pub.secordAuthorOrg,
					Field.Store.YES));
			doc.add(new StringField("thirdAuthorOrg", pub.thirdAuthorOrg,
					Field.Store.YES));
			doc.add(new StringField("otherAuthorOrg", pub.otherAuthorOrg,
					Field.Store.YES));
			doc.add(new StringField("nCite", pub.nCite, Field.Store.YES));
			doc.add(new StringField("content", pub.toString(), Field.Store.YES));
			indexWriter.addDocument(doc);
		} catch (IOException e) {
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
	}

	public void buildIndexFromExcel(String filePath) {
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
				if (authors != "") {
					// indexWriter添加索引
					Document doc = new Document();
					doc.add(new StringField("id", String.valueOf(j),
							Field.Store.YES));
					doc.add(new TextField("author", authors.trim(),
							Field.Store.YES));
					Publication pub = new Publication(sheet.getRow(j));
					doc.add(new StringField("jconf", pub.jconf, Field.Store.YES));
					doc.add(new StringField("year", pub.year, Field.Store.YES));
					doc.add(new StringField("firstAuthorOrg",
							pub.firstAuthorOrg, Field.Store.YES));
					doc.add(new StringField("secordAuthorOrg",
							pub.secordAuthorOrg, Field.Store.YES));
					doc.add(new StringField("thirdAuthorOrg",
							pub.thirdAuthorOrg, Field.Store.YES));
					doc.add(new StringField("otherAuthorOrg",
							pub.otherAuthorOrg, Field.Store.YES));
					doc.add(new StringField("nCite", pub.nCite, Field.Store.YES));
					doc.add(new StringField("content", pub.toString(),
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

}
