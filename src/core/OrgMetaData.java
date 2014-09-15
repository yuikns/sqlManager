package core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import model.Org;
import model.Publication;

import service.ExcelService;
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
//		omd.updateScore(omd.getOrgRankList(3), "orgtype3");
		omd.queryPublicationById(297, 0, 1000);
	}

	public List<String> getOrgRankList(int n) {
		List<String> res;
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
			case 4:
				res = bami.countOrg(type4);
			case 5:
				res = bami.countOrg(type5);
			case 6:
				res = bami.countOrg(type6);
				break;
			case 7:
				res = bami.countOrg(type7);
			case 8:
				res = bami.countOrg(type8);
			case 9:
				res = bami.countOrg(type9);
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
			Org obj = new Org(++i, row);
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
		List<String> res = os.queryMetaById(String.valueOf(id));
		if (res.size() > 0) {
			return res.get(0);
		} else {
			System.out.println("res's size =0");
			return null;
		}
	}

	public List<Publication> queryPublicationByIdjdbc(int id, int cover, int limit) {
		List<Publication> res = null;
		// String[] col = { "idorg", "org", "orgClusterText" };
		String str = queryMetaById(id);
		if (str != null) {
			String[] strs = str.split("\t");
			String orgs = strs[2];
			BuildAllMessIndex bami = new BuildAllMessIndex();
			bami.getPubsByOrg(0, orgs);
			switch (cover) {
			case 1:
				res = bami.getPubsByOrg(0, orgs, type1);
				break;
			case 2:
				res = bami.getPubsByOrg(0, orgs, type2);
				break;
			case 3:
				res = bami.getPubsByOrg(0, orgs, type3);
				break;
			case 4:
				res = bami.getPubsByOrg(0, orgs, type4);
				break;
			case 5:
				res = bami.getPubsByOrg(0, orgs, type5);
				break;
			case 6:
				res = bami.getPubsByOrg(0, orgs, type6);
				break;
			case 7:
				res = bami.getPubsByOrg(0, orgs, type7);
				break;
			case 8:
				res = bami.getPubsByOrg(0, orgs, type8);
				break;
			case 9:
				res = bami.getPubsByOrg(0, orgs, type9);
				break;
			case 10:
				res = bami.getPubsByOrg(0, orgs, type10);
				break;
			default:
				res = bami.getPubsByOrg(0, orgs);
			}
		}
		System.out.println(res.size());
		if (limit > 0 && limit < res.size())
			return res.subList(0, limit);
		else
			return res;
	}

	public List<Publication> queryPublicationById(int id, int cover, int limit) {
		List<Publication> res = null;
		// String[] col = { "idorg", "org", "orgClusterText" };
		String str = queryMetaById(id);
		if (str != null) {
			String[] strs = str.split("\t");
			String orgs = strs[2];
			BuildAllMessIndex bami = new BuildAllMessIndex();
			bami.getPubsByOrg(0, orgs);
			switch (cover) {
			case 1:
				res = bami.getPubsByOrg(0, orgs, type1);
				break;
			case 2:
				res = bami.getPubsByOrg(0, orgs, type2);
				break;
			case 3:
				res = bami.getPubsByOrg(0, orgs, type3);
				break;
			case 4:
				res = bami.getPubsByOrg(0, orgs, type4);
				break;
			case 5:
				res = bami.getPubsByOrg(0, orgs, type5);
				break;
			case 6:
				res = bami.getPubsByOrg(0, orgs, type6);
				break;
			case 7:
				res = bami.getPubsByOrg(0, orgs, type7);
				break;
			case 8:
				res = bami.getPubsByOrg(0, orgs, type8);
				break;
			case 9:
				res = bami.getPubsByOrg(0, orgs, type9);
				break;
			case 10:
				res = bami.getPubsByOrg(0, orgs, type10);
				break;
			default:
				res = bami.getPubsByOrg(0, orgs);
			}
		}
		System.out.println(res.size());
		if (limit > 0 && limit < res.size())
			return res.subList(0, limit);
		else
			return res;
	}

	public List<String> queryOrg(int cover) {
		List<String> util;
		OrgrankOrgService os = new OrgrankOrgService();
		System.out.println("!!!cover=" + cover);
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
					Org obj = new Org(++i, row);
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
					Org obj = new Org(++i, row);
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
				String[] scores = row.split("\t");
				if (scores.length > 2) {
					int res = os.updateScore(scores[0], "scoreCount",
							scores[1], tableName);
					System.out.println(res);
					res = os.updateScore(scores[0], "scoreCountReg", scores[2],
							tableName);
				}
				if (scores.length > 4) {
					int res = os.updateScore(scores[0], "scoreCite", scores[3],
							tableName);
					System.out.println(res);
					res = os.updateScore(scores[0], "scoreCiteReg", scores[4],
							tableName);
					System.out.println(res);
				}
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
