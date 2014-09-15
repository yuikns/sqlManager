package service;

import java.util.List;

public class AminerPaperService extends SqlService{
	String dbName = "arnet_db";
	String tableName = "publication";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public AminerPaperService(){
		fillMetaData(dbName, tableName);
	}
	public int queryNciteByTitle(String title,String year){
		int nCite=-1;
		String where=String.format("where title like \"%%%s%%\" and `year`=%s", title,year);
		String[] cols={"ncitation"};
		List<String> res=query(dbName, tableName, where, cols);
		if(res.size()>0){
			nCite=Integer.parseInt(res.get(0));
		}
		return nCite;
	}
}
