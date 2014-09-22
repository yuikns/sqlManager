package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import model.Org;

public class OrgrankOrgService extends SqlService {
	String dbName = "orgranktest";
	String tableName = "org";

	public OrgrankOrgService() {
		fillMetaData(dbName, tableName);
		System.out.println(this.mapMetaData);
	}

	public List<String> queryMeta() {
		String[] col = { "idorg", "org", "orgClusterText" };
		String where = String.format("order by `idorg`");
		return query(dbName, tableName, where, col);
	}

	public List<String> queryAll(String tableName) {
		String[] col = {"idOrg", "org", "scoreCount", "scoreCountReg", "scoreCite",
				"scoreCiteReg" };
		String where = String.format("order by `scoreCountReg` desc");
		return query(dbName, tableName, where, col);
	}

	public List<String> queryExist(String orgRaw) {
		String[] col = { "org" };
		String where = String.format("where orgClusterText like \"%%%s%%\"",orgRaw);
		return query(dbName, tableName, where, col);
	}

	public List<String> queryScoreByOrg(String org) {
		String[] col = { "idorg", "org", "scoreCount", "scoreCountReg",
				"scoreCite", "scoreCiteReg" };
		String where = String.format("where `orgClusterText` like'%%%s%%' ",
				org);
		return query(dbName, tableName, where, col);
	}

	public List<String> queryScoreById(String Id) {
		String[] col = { "idorg", "org", "scoreCount", "scoreCountReg",
				"scoreCite", "scoreCiteReg" };
		String where = String.format("where `idorg` =%s ", Id);
		return query(dbName, tableName, where, col);
	}
	public List<String> queryMetaById(String Id) {
		
		String[] col = { "idorg", "org",
				 "orgClusterText" };
		String where = String.format("where `idorg` =%s ", Id);
		return query(dbName, tableName, where, col);
	}
	public int insertMetaIntoOrg(Org obj) {
		List<String> values = new ArrayList<String>();
		String[] cols = new String[3];
		for (int i = 0; i < 3; i++) {
			cols[i] = tableMetaData.get(i).get(1);
		}
		values.add(String.valueOf(obj.getId()));
		values.add(obj.getName());
		values.add(obj.getClusterText());
		int result = insert(dbName, tableName, values, cols);
		return result;
	}

	public int updateMetaIntoOrg(Org obj) {
		List<String> values = new ArrayList<String>();
		String[] cols = new String[2];
		cols[0] = tableMetaData.get(1).get(1);
		cols[1] = tableMetaData.get(2).get(1);
		values.add(obj.getName());
		values.add(obj.getClusterText());
		String where = String.format("where idorg=%s",
				String.valueOf(obj.getId()));
		int result = update(dbName, tableName, values, where, cols);
		return result;
	}

	public int updateScore(String id, String scoreName, String score,
			String tableName) {
		String where = String.format("Where idorg=%s", id);
		List<String> value = new Vector<String>();
		value.add(score);
		return update(dbName, tableName, value, where, scoreName);
	}

	public int updateScore(String id, List<String> scores) {
		String where = String.format("Where idorg=%s", id);
		String[] cols = { "scoreCount", "scoreCountReg", "scoreCite",
				"scoreCiteReg" };
		return update(dbName, tableName, scores, where, cols);
	}

	public int getMaxid() {
		int ret = -1;
		try {
			String sql = "SELECT max(idorg) FROM `orgranktest`.`org`";
			List<String> res = queryDIY(dbName, sql, 1);
			ret = Integer.parseInt(res.get(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

}
