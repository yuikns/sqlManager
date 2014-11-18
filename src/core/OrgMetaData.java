package core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.util.Version;

import com.google.gson.Gson;

import model.Org;
import model.OrgJson;
import model.Publication;

import service.ExcelService;
import service.JsonService;
import service.MongoService;
import service.OrgrankOrgService;
import service.TxtService;

public class OrgMetaData {
	String[] type1 = { "ASPLOS", "FAST", "HPCA", "ISCA", "MICRO" };
	String[] type2 = { "MOBICOM", "SIGCOMM", "INFOCOM" };
	String[] type3 = { "CCS", "CRYPTO", "EUROCRYPT", "S&P", "USENIX Security" };
	String[] type4 = { "FSE_ESEC", "OOPSLA", "ICSE", "OSDI", "PLDI", "POPL",
			"SOSP" };
	String[] type5 = { "SIGMOD", "SIGKDD", "SIGIR", "VLDB", "ICDE" };
	String[] type6 = { "STOC", "FOCS", "LICS" };
	String[] type7 = { "ACM MM", "SIGGRAPH", "VIS" };
	String[] type8 = { "AAAI", "CVPR", "ICCV", "ICML", "IJCAI" };
	String[] type9 = { "CHI", "UbiComp" };
	String[] type10 = { "RTSS" };

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		OrgMetaData omd = new OrgMetaData();
		// omd.buildTable();
		// omd.updateScore();
		// omd.updateMeta();
		// omd.insertOneMeta(row);
		// omd.getOrgRankList(1);
//		omd.updateScore(omd.getOrgRankList(0), "org");
		// omd.queryPublicationById(297, 0, 1000);
		 omd.exportData();
		// omd.exportMeta();
//		 omd.exportMeta1();
	}

	public void exportMeta1() {
		List<String> ranks = new ArrayList<String>();
		try {
			TxtService.getStringList("./res/univ_rank.txt", ranks);
		} catch (IOException e) {
			e.printStackTrace();
		}
		HashMap<String, String> my_rank_map = new HashMap<String, String>();
		OrgrankOrgService oos = new OrgrankOrgService();
		List<String> res = oos.queryMeta();
		List<String> res1 = oos.queryScore("0", "scoreCount");
		List<String> res2 = oos.queryScore("0", "scoreCountReg");
		List<String> res3 = oos.queryScore("0", "scoreCite");
		List<String> res4 = oos.queryScore("0", "scoreCiteReg");
		List<String> res5 = oos.queryScore("0", "firstAuthorNum");
		for (String str : res) {
			String[] strs = str.split("\t");
			String id = strs[0];
			String name = strs[1];
			String type = strs[3];
			if (type.equals("0")) {
				StringBuilder var = new StringBuilder();
				String val1 = String.valueOf(res1.indexOf(id) + 1);
				String val2 = String.valueOf(res2.indexOf(id) + 1);
				String val3 = String.valueOf(res3.indexOf(id) + 1);
				String val4 = String.valueOf(res4.indexOf(id) + 1);
				String val5 = String.valueOf(res5.indexOf(id) + 1);
				var.append(val1 + "\t" +val5+"\t"+ val2 + "\t" + val3 + "\t" + val4);
				if (!my_rank_map.containsKey(name)) {
					my_rank_map.put(name, var.toString());
				}
			}
		}
		int i = 0;
		for (String str : ranks) {
			i++;
			String[] strs = str.split("\t");
			String name = strs[1].trim();
			if (my_rank_map.containsKey(name)) {
				System.out.println(i + "\t" + name + "\t"
						+ my_rank_map.get(name));
			} else {

				System.out.println(i + "\t" + name + "\tNot find!");
			}
		}
	}

	public void exportMeta() {
		List<String> ranks = new ArrayList<String>();
		try {
			TxtService.getStringList("./res/univ_rank.txt", ranks);
		} catch (IOException e) {
			e.printStackTrace();
		}
		OrgrankOrgService oos = new OrgrankOrgService();
		List<String> gsons = new ArrayList<String>();
		List<String> res = oos.queryMeta("0");
		List<List<String>> resi = new ArrayList<List<String>>();
		int tableNum = 1;
		for (int j = 0; j < tableNum; j++) {
			resi.add(getOrgRankList(j));
		}
		for (int i = 0; i < res.size(); i++) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < tableNum; j++)
				sb.append(resi.get(j).get(i) + "\t");
			String line = sb.toString();
			JsonService js = new JsonService();
			String output = js.toOrgJson(res.get(i), line);
			gsons.add(output);
		}
		Collections.sort(gsons, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				Gson gson = new Gson();
				String value1 = gson.fromJson(o1, OrgJson.class)
						.getTypeScoreJsonScore().get(0).getCiteLog();
				String value2 = gson.fromJson(o2, OrgJson.class)
						.getTypeScoreJsonScore().get(0).getCiteLog();

				Double d1 = Double.parseDouble(value1);
				Double d2 = Double.parseDouble(value2);
				return -1 * d1.compareTo(d2);
			}
		});

		HashMap<String, String> my_rank_map = new HashMap<String, String>();
		for (int i = 0; i < gsons.size(); i++) {
			String gson = gsons.get(i);
			Gson g = new Gson();
			String id = g.fromJson(gson, OrgJson.class).getIdorg();
			String name = g.fromJson(gson, OrgJson.class).getOrg();
			String val = id + "\t" + String.valueOf(i + 1);
			if (!my_rank_map.containsKey(name)) {
				my_rank_map.put(name, val);
			}
		}
		int i = 0;
		for (String str : ranks) {
			i++;
			String[] strs = str.split("\t");
			String name = strs[1].trim();
			if (my_rank_map.containsKey(name)) {
				System.out.println(i + "\t" + name + "\t"
						+ my_rank_map.get(name));
			} else {

				System.out.println(i + "\t" + name + "\tNot find!");
			}
		}
	}

	public void exportData() {
		OrgrankOrgService oos = new OrgrankOrgService();
		List<String> gsons = new ArrayList<String>();
		List<String> res = oos.queryMeta();
		List<List<String>> resi = new ArrayList<List<String>>();
		int tableNum =11;
		for (int j = 0; j < tableNum; j++) {
			resi.add(getOrgRankList(j));
		}
		for (int i = 0; i < res.size(); i++) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < tableNum; j++)
				sb.append(resi.get(j).get(i) + "\t");
			String line = sb.toString();
			
			JsonService js = new JsonService();
			String output = js.toOrgJson(res.get(i), line);
			if (i % 20 == 0) {
				System.out.println("res:" + res.get(i));
				System.out.println("line:"+line);
				System.out.println(i + ":" + output);
			}
			gsons.add(output);
		}
		MongoService ms = new MongoService();
		ms.insertOrgJsonIntoMongo(gsons);
	}

	public List<String> getOrgRankList(int n) {
		List<String> res = null;
		BuildAllMessIndex bami = new BuildAllMessIndex();
		try {
			switch (n) {
			case 1:
				res = bami.countOrg(type1);
				break;
			case 2:
				res = bami.countOrg(type2);
				break;
			case 3:
				res = bami.countOrg(type3);
				break;
			case 4:
				res = bami.countOrg(type4);
				break;
			case 5:
				res = bami.countOrg(type5);
				break;
			case 6:
				res = bami.countOrg(type6);
				break;
			case 7:
				res = bami.countOrg(type7);
				break;
			case 8:
				res = bami.countOrg(type8);
				break;
			case 9:
				res = bami.countOrg(type9);
				break;
			case 10:
				res = bami.countOrg(type10);
				break;
			default:
				res = bami.countOrg();
			}
			return res;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<String>();
		}

	}

	public void insertOneMeta(String row) {
		OrgrankOrgService os = new OrgrankOrgService();
		int i = os.getMaxid();
		row = row.trim();
		if (!"".equals(row)) {
			Org obj = new Org(row);
			int res = os.insertMetaIntoOrg(obj);
			System.out.println(row);
			System.out.println(res);
		}
	}

	public String queryExist(String orgRaw) {
		OrgrankOrgService os = new OrgrankOrgService();
		List<String> res = os.queryExist(orgRaw);
		if (res.size() > 0) {

			return res.get(0);
		} else {
			System.out.println("res's size =0");
			return null;
		}
	}

	public String queryMetaById(int id) {
		OrgrankOrgService os = new OrgrankOrgService();
		// String[] col = { "idorg", "org",
		// "orgClusterText" };
		List<String> res = os.queryMetaById(String.valueOf(id));
		if (res.size() > 0) {
			return res.get(0);
		} else {
			System.out.println("res's size =0");
			return null;
		}
	}

	public List<Publication> queryPublicationByIdYear(int id, int limit,
			int... year) {
		List<Publication> res = null;
		String str = queryMetaById(id);
		if (str != null) {
			str.replaceFirst("\t", ";");
			int start = str.indexOf("\t") + 1;
			String orgs = str.substring(start);
			BuildPaperCopy bc = new BuildPaperCopy();
			res = bc.getPubsByOrg(orgs, year);
		}
		System.out.println(res.size());
		if (limit > 0 && limit < res.size())
			return res.subList(0, limit);
		else
			return res;
	}

	public List<Publication> queryPublicationByIdConf(int id, int limit,
			String... conf) {
		List<Publication> res = null;
		String str = queryMetaById(id);
		if (str != null) {
			str.replaceFirst("\t", ";");
			int start = str.indexOf("\t") + 1;
			String orgs = str.substring(start);
			BuildPaperCopy bc = new BuildPaperCopy();
			res = bc.getPubsByOrgConf(orgs, conf);
		}
		if (limit > 0 && limit < res.size())
			return res.subList(0, limit);
		else
			return res;
	}

	// public List<Publication> queryPublicationById(int id, int cover, int
	// limit) {
	// List<Publication> res = null;
	// // String[] col = { "idorg", "org", "orgClusterText" };
	// String str = queryMetaById(id);
	// if (str != null) {
	// String[] strs = str.split("\t");
	// String orgs = strs[2];
	// BuildAllMessIndex bami = new BuildAllMessIndex();
	// bami.getPubsByOrg(0, orgs);
	// switch (cover) {
	// case 1:
	// res = bami.getPubsByOrg(0, orgs, type1);
	// break;
	// case 2:
	// res = bami.getPubsByOrg(0, orgs, type2);
	// break;
	// case 3:
	// res = bami.getPubsByOrg(0, orgs, type3);
	// break;
	// case 4:
	// res = bami.getPubsByOrg(0, orgs, type4);
	// break;
	// case 5:
	// res = bami.getPubsByOrg(0, orgs, type5);
	// break;
	// case 6:
	// res = bami.getPubsByOrg(0, orgs, type6);
	// break;
	// case 7:
	// res = bami.getPubsByOrg(0, orgs, type7);
	// break;
	// case 8:
	// res = bami.getPubsByOrg(0, orgs, type8);
	// break;
	// case 9:
	// res = bami.getPubsByOrg(0, orgs, type9);
	// break;
	// case 10:
	// res = bami.getPubsByOrg(0, orgs, type10);
	// break;
	// default:
	// res = bami.getPubsByOrg(0, orgs);
	// }
	// }
	// System.out.println(res.size());
	// if (limit > 0 && limit < res.size())
	// return res.subList(0, limit);
	// else
	// return res;
	// }

	public List<String> queryOrg(int cover) {
		List<String> util;
		OrgrankOrgService os = new OrgrankOrgService();
		switch (cover) {
		case 1:
			util = os.queryAll("orgType1");
			break;
		case 2:
			util = os.queryAll("orgType2");
			break;
		case 3:
			util = os.queryAll("orgType3");
			break;
		case 4:
			util = os.queryAll("orgType4");
			break;
		case 5:
			util = os.queryAll("orgType5");
			break;
		case 6:
			util = os.queryAll("orgType6");
			break;
		case 7:
			util = os.queryAll("orgType7");
			break;
		case 8:
			util = os.queryAll("orgType8");
			break;
		case 9:
			util = os.queryAll("orgType9");
			break;
		case 10:
			util = os.queryAll("orgType10");
			break;
		default:
			util = os.queryAll("org");
			break;
		}

		return util;
	}

	public void buildTable() {
		OrgrankOrgService os = new OrgrankOrgService();
		String filePath = "./res/univ100.txt";
		List<String> util = new Vector<String>();
		try {
			TxtService.getStringList(filePath, util);
			int i = 0;
			for (String row : util) {
				row = row.trim();
				if (!"".equals(row)) {
					Org obj = new Org(row);
					int res = os.insertMetaIntoOrg(obj);
					System.out.println(row);
					System.out.println(res);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateMeta() {
		OrgrankOrgService os = new OrgrankOrgService();
		String filePath = "./res/univ100.txt";
		List<String> util = new Vector<String>();
		try {
			TxtService.getStringList(filePath, util);
			int i = 0;
			for (String row : util) {
				row = row.trim();
				if (!"".equals(row)) {
					Org obj = new Org(row);
					int res = os.updateMetaIntoOrg(obj);
					System.out.println(row);
					System.out.println(res);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateScore(List<String> util, String tableName) {
		OrgrankOrgService os = new OrgrankOrgService();
		for (String row : util) {
			row = row.trim();
			if (!"".equals(row)) {
				System.out.println(row);
				String[] strs = row.split("\t");
				List<String> scores = new ArrayList<String>();
				for (int i = 1; i < strs.length; i++)
					scores.add(strs[i]);
				int res = os.updateScore(strs[0], scores);
				System.out.println(res);
			}
		}
	}

	public void updateScore(String tableName) {
		String filePath = "./res/output/sqlInputScore.txt";
		List<String> util = new Vector<String>();
		try {
			OrgrankOrgService os = new OrgrankOrgService();
			TxtService.getStringList(filePath, util);
			for (String row : util) {
				row = row.trim();
				if (!"".equals(row)) {
					System.out.println(row);
					String[] scores = row.split("\t");
					if (scores.length > 2) {
						int res = os.updateScore(scores[0], "scoreCount",
								scores[1], tableName);
						System.out.println(res);
						res = os.updateScore(scores[0], "scoreCountReg",
								scores[2], tableName);
						System.out.println(res);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
