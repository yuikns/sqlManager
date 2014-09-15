package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.Publication;

public class OrgrankAcmpaperService extends SqlService {
	String dbName = "orgranktest";
	String tableName = "paper_copy";
	HashMap<String, List<String>> confMap = new HashMap<String, List<String>>();

	public static void main(String arg[]) throws IOException {
		OrgrankAcmpaperService oas = new OrgrankAcmpaperService();
		oas.test();
	}

	public void test() {
		Publication pub = new Publication();
		pub.id = "2";
		pub.title = "hello world2";
		pub.authors = "kikyo;alice";
		insertIntoOrgRankTestAcmpaper(pub);
		updateOrgRankTestAcmpaper();
		// deleteFromOrgRankTestAcmpaper("");
	}

	public OrgrankAcmpaperService() {
		fillMetaData(dbName, tableName);
	}

	public int queryOrgRankTestAcmpaper() {
		List<String> values = new ArrayList<String>();
		String cols=tableMetaData.get(4).get(1);
		String where="";
		values.add("2004");
		int result = update(dbName, tableName, values, where, cols);
		return result;
	}
	public int updateOrgRankTestAcmpaper() {
		List<String> values = new ArrayList<String>();
		String cols=tableMetaData.get(4).get(1);
		String where="";
		values.add("2004");
		int result = update(dbName, tableName, values, where, cols);
		return result;
	}
	
	public int insertIntoOrgRankTestAcmpaper(Publication obj) {
		List<String> values = new ArrayList<String>();
		String[] cols = new String[3];
		for (int i = 0; i < 3; i++) {
			cols[i] = tableMetaData.get(i).get(1);
		}
		values.add(obj.id);
		values.add(obj.getTitle());
		values.add(obj.getAuthors());
		int result = insert(dbName, tableName, values, cols);
		return result;
	}

	public int deleteFromOrgRankTestAcmpaper(String where) {
		int result = delete(dbName, tableName, where);
		return result;
	}

	public void getSreamByte() throws IOException {
		// 导入文件
		File txt = new File("E://Sample.txt");
		BufferedReader in = new BufferedReader(new FileReader(txt));
		if (null != in) {
			String line = null;
			while ((line = in.readLine()) != null) {
				if (line.split("\t").length > 1) {

					String abbr = line.split("\t")[0];
					String str = line.split("\t")[1];
					if (!confMap.containsKey(abbr)) {
						List<String> temp = new ArrayList<String>();
						temp.add(str);
						confMap.put(abbr, temp);
					} else {
						List<String> temp = confMap.get(abbr);
						temp.add(str);
						confMap.put(abbr, temp);
					}
				}
			}
		}
		if (null != in) {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
