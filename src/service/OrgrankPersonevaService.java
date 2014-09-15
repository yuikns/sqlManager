package service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import model.PaperForEva;
import model.Person;

public class OrgrankPersonevaService extends SqlService {
	String dbName = "orgranktest";
	String tableName = "personeva";
	static int i = 0;

	public OrgrankPersonevaService() {
		fillMetaData(dbName, tableName);
	}

	public OrgrankPersonevaService(String tableName) {
		this.tableName = tableName;
		fillMetaData(dbName, tableName);
	}

	public void queryPersonTruth(String name) {
		String[] cols = { "person_name", "title", "years", "conf", "fulltext" };
		String where = String.format(
				" where  `orgranktest`.`personeva`.`name` like '%s%%'", name);
		List<String> res = query(dbName, tableName, where, cols);
		for (String str : res) {
			System.out.println(str);
		}
		System.out.println("-----");
	}

	public void queryTitle(String title) {
		String[] cols = { "person_name", "title", "years", "conf", "fulltext" };
		String where = String.format(
				" where  `orgranktest`.`personeva`.`title` like '%s%%'", title);
		List<String> res = query(dbName, tableName, where, cols);
		for (String str : res) {
			System.out.println(str);
		}
		System.out.println("-----");
	}

	public Map<String,List<String>> queryCY(String conf, String year) {
		Map<String,List<String>> map =new HashMap<String, List<String>>();
		String[] cols = { "person_name", "title" };
		String where=String
				.format("where conf='%s' and `years`=%s",
				conf, year);
		List<String> res=query(dbName,tableName,where,cols);
		for (String str : res) {
			String name=str.split("\t")[0];
			String title=str.split("\t")[1];
			if(!map.containsKey(name)){
				List<String> a=new ArrayList<String>();
				a.add(title);
				map.put(name,a);
			}else{
				map.get(name).add(title);
			}
		}
		return map;
	}

	public void queryTruthDetail(String conf, String year) {
		String[] dbName1 = { "orgranktest", "orgranktest" };
		String[] tableName1 = { "personeva", "person" };
		String[] cols = { "`orgranktest`.`person`.`a_org`",
				"`personeva`.`person_name`", "`personeva`.`title`",
				"`personeva`.`fulltext`" };
		String where = String
				.format(" where  `orgranktest`.`personeva`.person_name=`orgranktest`.`person`.a_name and conf='%s' and `years`=%s",
						conf, year);
		List<String> res = queryJoint(dbName1, tableName1, where, cols);
		for (String str : res) {
			System.out.println(str);
		}
		System.out.println("-----");
	}

	void buildTable() {
		try {
			File file = new File(".\\res\\personEva");
			if (!file.isDirectory()) {
				System.out.println("输入的参数应该为[文件夹名]");
				System.out.println("filepath: " + file.getAbsolutePath());
				return;
			} else {
				List<String> confList = new ArrayList<String>();
				TxtService.getStringList(".\\res\\CCFA.txt", confList);
				delete(dbName, tableName, "where id>0");
				for (String path : file.list()) {
					List<String> mess = new ArrayList<String>();
					String filePath = file.getAbsolutePath() + File.separator
							+ path;
					TxtService.getStringList(filePath, mess);
					buildTableTruth(confList, mess);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void buildTableTruth(List<String> confList, List<String> truthList) {
		String name = "";
		for (String res : truthList) {
			if (!res.contains("@")) {
				name = res.substring((res.lastIndexOf("\\") + 1),
						res.indexOf("."));
				name = name.substring(name.indexOf(" ") + 1);
			} else {
				i++;
				String conf = res.split("@")[1].trim();
				if (conf.contains("ACM Trans. Graph."))
					conf = "SIGGRAPH";
				if (conf.contains("ACM Multimedia"))
					conf = "ACM MM";
				if (conf.contains("KDD"))
					conf = "SIGKDD";
				if (conf.contains("PVLDB"))
					conf = "VLDB";
				if (confList.contains(conf)) {
					String year = res.split("@")[0].trim();
					String fullText = res.split("@")[2].trim();
					if (Integer.parseInt(year) < 2014
							&& Integer.parseInt(year) > 2003) {
						int result = insertIntoPerosnEva(new PaperForEva(i,
								name, conf, year, fullText));
						if (result != 1) {
							System.err.println(new PaperForEva(i, name, conf,
									year, fullText).toString());
						}
					}
				}
			}
		}
	}

	void insertIntoPerson() {
		List<String> mess = new ArrayList<String>();
		try {
			TxtService
					.getStringList(
							"E:\\文档\\keg\\huaiyu\\University_Paper_List_0709 (1)\\test.txt",
							mess);
			String org = null;
			for (int i = 0; i < mess.size(); i++) {
				if (mess.get(i).contains(".txt")) {
					org = mess.get(i).replaceAll("-", "");
					org = org.split(".txt")[0];
				} else {
					String name = mess.get(i).split("\t")[0];
					insertIntoOrgRankTestperosn(new Person(i, name, org));
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int insertIntoOrgRankTestperosn(Person obj) {
		List<String> values = new ArrayList<String>();
		String[] cols = new String[3];
		for (int i = 0; i < 3; i++) {
			cols[i] = tableMetaData.get(i).get(1);
		}
		values.add(String.valueOf(obj.getId()));
		values.add(obj.getname());
		values.add(obj.getorg());
		int result = insert(dbName, tableName, values, cols);
		return result;
	}

	public int insertIntoPerosnEva(PaperForEva obj) {
		List<String> values = new ArrayList<String>();
		String[] cols = new String[7];
		for (int i = 0; i < 7; i++) {
			cols[i] = tableMetaData.get(i).get(1);
		}
		values.add(String.valueOf(obj.getId()));
		values.add(obj.getP_name());
		values.add(obj.getTitle());
		values.add(String.valueOf(obj.getYear()));// year
		values.add(obj.getConf());// conf
		values.add(obj.getAuthors());
		values.add(obj.getFulltext());
		int result = insert(dbName, tableName, values, cols);
		return result;
	}

}
