package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import model.Publication;

public class OrgrankPaperCopyService extends SqlService {
	String dbName = "orgranktest";
	String tableName = "paper_copy";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		OrgrankPaperCopyService os = new OrgrankPaperCopyService();
		List<String> a = os.queryAColByYC("title", "2005", "ESEC_FSE");
		for (String aa : a) {
			System.out.println(aa);
		}
	}

	public OrgrankPaperCopyService() {
		fillMetaData(dbName, tableName);
	}

	public int getMaxid() {
		int ret = -1;
		try {
			String sql = "SELECT max(idpaper) FROM `orgranktest`.`paper_copy`";
			List<String> res = queryDIY(dbName, sql, 1);
			ret = Integer.parseInt(res.get(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public int updateOrg(String id, String newOrg) {
		String where = String.format("Where idpaper=%s", id);
		List<String> value = new Vector<String>();
		value.add(newOrg);
		return update(dbName, tableName, value, where, "orgs");
	}

	public int updateNcite(String id, int nCite) {
		String where = String.format("Where idpaper=%s", id);
		List<String> value = new Vector<String>();
		value.add(String.valueOf(nCite));
		return update(dbName, tableName, value, where, "nCite");
	}

	public int updatePage(String id, String newPage) {
		String where = String.format("Where idpaper=%s", id);
		String[] cols = { "startPage", "endPage" };
		List<String> value = new Vector<String>();
		Publication pub = new Publication();
		pub.pages = newPage;
		String startPage = String.valueOf(pub.getStartPage());
		String endPage = String.valueOf(pub.getEndPage());
		value.add(startPage);
		value.add(endPage);
		return update(dbName, tableName, value, where, cols);
	}

	public List<String> checkGroupByCY() {
		String sql = "SELECT jconf,`year`,count(idpaper) FROM `orgranktest`.`paper_copy` where orgs=\"\" or orgs like \"%-;%\"  or orgs =\"-\" group by jconf,`year`";
		return queryDIY(dbName, sql, 3);
	}

	public List<String> checkOrgGroupByCY() {
		String sql = "select count(idpaper),jconf,`year` from`orgranktest`.`paper_copy` where orgs=\"\" or orgs like \"%-;%\"  or orgs =\"-\" group by  jconf,`year`";
		return queryDIY(dbName, sql, 3);
	}

	public List<String> checkOrg() {
		String sql = "select idpaper,title,authors,orgs,jconf,`year`,startpage,endpage,ncite from`orgranktest`.`paper_copy` where orgs=\"\" or orgs like \"%-;%\"  or orgs =\"-\" or orgs=null";
		return queryDIY(dbName, sql, 9);
	}

	public List<String> queryLackCite() {
		String sql = "select idpaper,title,`year` from`orgranktest`.`paper_copy` where ncite=-1 or ncite=\"\" or ncite=null";
		return queryDIY(dbName, sql, 3);
	}

	public List<Publication> writeBack() {
		String sql = "select idpaper,title,authors,orgs,jconf,`year`,startPage,endPage,nCite from`orgranktest`.`paper_copy`";
		List<String> res = queryDIY(dbName, sql, 9);
		List<Publication> pubs = new ArrayList<Publication>();
		for (String a : res) {
			Publication pub = new Publication(a);
			pubs.add(pub);
		}
		return pubs;
	}

	public void comparePapers(List<String> a, List<String> b) {
		Set<String> sa = new HashSet<String>(a);
		Set<String> sb = new HashSet<String>(b);
		sa.remove("");
		sb.remove("");
		findDifference(sa, sb);
	}

	public void findDifference(Set<String> sa, Set<String> sb) {
		System.out.println(sa.size() + " " + sb.size());
		TxtService ts = new TxtService();
		Set<String> tmp = new HashSet<String>(sa);
		sa.removeAll(sb);
		sb.removeAll(tmp);
		if (sa.size() == 0 && sb.size() == 0) {
			System.out.println("equal!");
		} else {
			Set<String> sc = new HashSet<String>();
			for (String str1 : sa) {
				for (String str2 : sb) {
					if (ts.getLCS_length(str1, str2) > 0.9) {
						sc.add(str1);
						sc.add(str2);
						// System.out.println(str1 + "<->" + str2);
					}
				}
			}
			sa.removeAll(sc);
			sb.removeAll(sc);
			for (String str1 : sa) {
				System.out.println("+ " + str1);
			}
			for (String str1 : sb) {
				System.out.println("- " + str1);
			}
		}

	}

	public void checkPage(String year, String conf) {
		List<String> a = queryAColByYC("startPage", year, conf);
		List<String> b = queryAColByYC("endPage", year, conf);
		List<Integer> res = findPage(a, b);
		if (res.size() == 2) {
			System.out.print("res.size=2:");
		}
		for (int i = 0; i < res.size(); i++) {
			if (i % 2 == 0)
				System.out.print(res.get(i) + "-");
			else
				System.out.println(res.get(i));

		}
		System.out.println("----------------");
	}

	public List<String> queryPubById00() {
		String[] col = { "idpaper" };
		String where = "where `idpaper` like \"%00\" and nCite=0 order by jconf,year ";
		return query(dbName, tableName, where, col);
	}

	public int deleteByTitle(String title) {
		int result = 0;
		if (title != "") {
			title = title.replace("'", "\'").trim();
			String where = String.format("where title like \"%s%%\"", title);
			result = delete(dbName, tableName, where);
		}
		return result;
	}

	public List<String> queryPubByTitle(String title) {
		String[] col = { "idpaper" };
		String where = String.format("where `title` like'%%%s%%' ", title);
		return query(dbName, tableName, where, col);
	}

	public List<String> queryPubByOrg(String org) {
		String[] col = { "idpaper", "orgs" };
		String where = String.format("where `orgs` like'%%%s%%' ", org);
		return query(dbName, tableName, where, col);
	}

	public List<String> queryPubByOrg(String[] orgs) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < orgs.length; i++) {
			orgs[i].replaceAll(",", "%");
			if (i == 0) {
				sb.append(String.format("where (`orgs` like'%%%s%%' ) ", orgs[i]));
			} else {
				sb.append(String.format("or (`orgs` like'%%%s%%') ", orgs[i]));
			}
		}
		String[] col = { "idpaper","title", "jconf","year", "orgs"};
		String where =sb.toString()+" order by `year`";
		return query(dbName, tableName, where, col);
	}

	public List<String> queryAColByYC(String col, String year, String conf) {
		String where = String.format("where jconf='%s' and `year`=%s", conf, year);
		return query(dbName, tableName, where, col);
	}

	public List<String> queryAColByYCP(String year, String conf, String person) {
		String[] col = { "title" };
		String where = String.format("where jconf='%s' and `year`=%s and `authors` like'%%%s%%' ", conf, year, person);
		return query(dbName, tableName, where, col);
	}

	List<Integer> findPage(List<String> start, List<String> end) {
		List<Integer> pocess = new ArrayList<Integer>();
		Collections.sort(start, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				Integer a1 = Integer.parseInt(o1);
				Integer a2 = Integer.parseInt(o2);
				return a1.compareTo(a2);
			}
		});
		Collections.sort(end, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				Integer a1 = Integer.parseInt(o1);
				Integer a2 = Integer.parseInt(o2);
				return a1.compareTo(a2);
			}
		});
		int en = 0;
		for (int i = 0; i < start.size(); i++) {
			int cur_st = Integer.parseInt(start.get(i));
			int cur_en = Integer.parseInt(end.get(i));
			if (i == 0) {
				pocess.add(cur_st);
				pocess.add(cur_en);
			}
			if (cur_st == en + 1) {
				pocess.set(pocess.size() - 1, cur_en);
			} else {
				pocess.add(cur_st);
				pocess.add(cur_en);
			}
			en = cur_en;
		}
		return pocess;
	}

	public int insertIntoPaperCopy(Publication obj) {
		List<String> values = new ArrayList<String>();
		String[] cols = new String[11];
		for (int i = 0; i < 11; i++) {
			cols[i] = tableMetaData.get(i).get(1);
		}
		values.add(String.valueOf(obj.getId()));
		values.add(obj.title);
		values.add(obj.authors);
		values.add(obj.year);
		values.add(obj.jconf);
		values.add(obj.keywords);
		values.add(obj.abstracts);
		values.add(String.valueOf(obj.getStartPage()));
		values.add(String.valueOf(obj.getEndPage()));
		values.add(String.valueOf(obj.getNcite()));
		values.add(obj.getOrgs());
		int result = insert(dbName, tableName, values, cols);
		return result;
	}

}
