package api;

import java.io.IOException;

import core.BuildAllMessIndex;
import core.OrgMetaData;

public class Usage {
	BuildAllMessIndex buildIndex;
	OrgMetaData orgMetaData;
	
	public static void main(String[] args) throws IOException, Exception {
		
	}
	void buildLuceneIndex(){
		if(buildIndex==null){
			buildIndex=new BuildAllMessIndex();
		}
		buildIndex.readSql();
	}
	void exportToMongo(){
		if(orgMetaData==null){
			orgMetaData=new OrgMetaData();
		}
		orgMetaData.exportData();
	}
}
