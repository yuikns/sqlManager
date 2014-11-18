package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class SqlService {
	// jdbc
	protected String driver = "com.mysql.jdbc.Driver";
	protected String url = "jdbc:mysql://166.111.7.106:3306/";
	protected String user = "root";
	protected String password = "keg2012";
	// optional properties
	protected static List<Vector<String>> tableMetaData = new ArrayList<Vector<String>>();
	protected static HashMap<String, Integer> mapMetaData = new HashMap<String, Integer>();
	String sqlBuilderForJointQuery(String[] dbName, String[] tableName,
			String where, String... cols) {
		int num = cols.length;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < num; i++) {
			if (i == 0) {
				sb.append( cols[i]);
				
			} else {
				sb.append(" , "+ cols[i] );
			}
		}
		int tableNum = tableName.length;
		StringBuilder sb1 = new StringBuilder();
		for (int i = 0; i < tableNum; i++) {
			if (i == 0) {
				sb1.append("`" + dbName[i] + "`.`"+tableName[i]+"`");
			} else {
				sb1.append(",`" + dbName[i] + "`.`"+tableName[i]+"`");
			}
		}
		String sql = String.format("Select " + sb.toString() + "From %s %s",
				sb1.toString(), where);
		return sql;
	}

	String sqlBuilderForQuery(String dbName, String tableName, String where,
			String... cols) {
		int num = cols.length;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < num; i++) {
			if (i == 0) {
				if (cols[i].equals("*")) {
					sb.append(cols[i]);
				} else {
					sb.append("`" + cols[i] + "`");
				}
			} else {
				sb.append(",`" + cols[i] + "`");
			}
		}
		String sql = String.format("Select " + sb.toString() + "From %s.%s %s",
				dbName, tableName, where);
		return sql;
	}

	String sqlBuilderForDelete(String dbName, String tableName, String where) {
		String sql = String.format("Delete From %s.%s %s", dbName, tableName,
				where);
		return sql;
	}

	String sqlBuilderForInsert(String dbName, String tableName, String... cols) {
		int num = cols.length;
		StringBuilder sb = new StringBuilder();
		StringBuilder wenhao = new StringBuilder();
		for (int i = 0; i < num; i++) {
			if (i == 0) {
				sb.append("`" + cols[i] + "`");
				wenhao.append('?');
			} else {
				sb.append(",`" + cols[i] + "`");
				wenhao.append(",?");
			}
		}
		String sql = String.format("INSERT INTO %s.%s(" + sb.toString()
				+ ") VALUES (" + wenhao.toString() + ")", dbName, tableName);
		return sql;
	}

	String sqlBuilderForUpdate(String dbName, String tableName, String where,
			String... cols) {
		int num = cols.length;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < num; i++) {
			if (i == 0) {
				sb.append("`" + cols[i] + "`=?");
			} else {
				sb.append(",`" + cols[i] + "`=?");
			}
		}
		String sql = String.format(
				"UPDATE %s.%s SET " + sb.toString() + " %s ", dbName,
				tableName, where);
		return sql;
	}

	protected void fillMetaData(String dbName, String tableName) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Class.forName(driver);
			String sql = sqlBuilderForQuery(dbName, tableName, "Limit 1", "*");
			conn = DriverManager.getConnection(url + dbName, user, password);
			ps = conn.prepareStatement(sql);
//			System.out.println(ps.toString());
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int tableLength = rsmd.getColumnCount();
			if (tableLength > 0) {
				tableMetaData.clear();
				mapMetaData.clear();
				for (int i = 1; i <= tableLength; i++) {
					// 获取数据库类型与java相对于的类型
					// 获取列名
					Vector<String> tmp = new Vector<String>();
					tmp.add(rsmd.getColumnClassName(i));
					tmp.add(rsmd.getColumnName(i));
					tableMetaData.add(tmp);
					mapMetaData.put(rsmd.getColumnName(i), i - 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	protected List<String> query(String dbName, String tableName, String where,
			String... cols) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> result = new ArrayList<String>();
		try {
			Class.forName(driver);
			String sql = sqlBuilderForQuery(dbName, tableName, where, cols);
			conn = DriverManager.getConnection(url + dbName, user, password);
			ps = conn.prepareStatement(sql);
//			System.out.println(ps.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < cols.length; i++) {
					if (i == 0)
						sb.append(rs.getString(i+1));
					else
						sb.append("\t" + rs.getString(i+1));
				}
				result.add(sb.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	protected List<String> queryDIY(String dbName, String sql,
			int nCol) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> result = new ArrayList<String>();
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url + dbName, user, password);
			ps = conn.prepareStatement(sql);
//			System.out.println(ps.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < nCol; i++) {
					if (i == 0)
						sb.append(rs.getString(i+1));
					else
						sb.append("\t" + rs.getString(i+1));
				}
				result.add(sb.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	protected List<String> queryJoint(String[] dbName, String[] tableName, String where,
			String... cols) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> result = new ArrayList<String>();
		try {
			Class.forName(driver);
			String sql = sqlBuilderForJointQuery(dbName, tableName, where, cols);
			conn = DriverManager.getConnection(url + dbName[0], user, password);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < cols.length; i++) {
					if (i == 0)
						sb.append(rs.getString(i+1));
					else
						sb.append("\t" + rs.getString(i+1));
				}
				result.add(sb.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	int[] colName2ColNo(String... cols) {
		int[] result = new int[cols.length];
		for (int i = 0; i < cols.length; i++) {
			result[i] = mapMetaData.get(cols[i]);
		}
		return result;
	}

	protected int insert(String dbName, String tableName, List<String> values,
			String... cols) {
		Connection conn = null;
		PreparedStatement ps = null;
		int result = 0;
		try {
			Class.forName(driver);
			String sql = sqlBuilderForInsert(dbName, tableName, cols);
			conn = DriverManager.getConnection(url + dbName, user, password);
			ps = conn.prepareStatement(sql);
			_constructPS(ps, values, colName2ColNo(cols));
//			System.out.println(ps.toString());
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}

	protected int update(String dbName, String tableName, List<String> values,
			String where, String... cols) {
		Connection conn = null;
		PreparedStatement ps = null;
		int result = 0;
		
		try {
			Class.forName(driver);
			String sql = sqlBuilderForUpdate(dbName, tableName, where, cols);
			conn = DriverManager.getConnection(url + dbName, user, password);
			ps = conn.prepareStatement(sql);
			_constructPS(ps, values, colName2ColNo(cols));
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}

	protected int delete(String dbName, String tableName, String where) {
		Connection conn = null;
		PreparedStatement ps = null;
		int result = 0;
		try {
			Class.forName(driver);
			String sql = sqlBuilderForDelete(dbName, tableName, where);
			conn = DriverManager.getConnection(url + dbName, user, password);
			ps = conn.prepareStatement(sql);
//			System.out.println(ps.toString());
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}

	public int _constructPS(PreparedStatement ps, List<String> values,
			int... colNum) throws Exception {
		int i = 0;

		for (; i < colNum.length; i++) {
			// colNum:1,2,3...
			if (tableMetaData.get(colNum[i]).get(0).contains("Integer")) {
				String val = values.get(i);
				if (!val.equals(""))
					ps.setInt(i + 1, Integer.parseInt(val));
			} else {
				ps.setString(i + 1, values.get(i));
			}
		}
		return i;
	}

}
