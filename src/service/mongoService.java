package service;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import model.OrgJson;
import model.TypeScoreJson;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

public class MongoService {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MongoService ms = new MongoService();
//		ms.doSomeTask();
	}
	public void insertOrgJsonIntoMongo(List<String> jsons){
		insert(geneDBOList(jsons));
	}
	public List<DBObject> geneDBOList(List<String> jsons) {
		List<DBObject> list=new ArrayList<DBObject>();
		for (String json : jsons) {
			DBObject dbObject = (DBObject)JSON.parse(json);
			list.add(dbObject);
		}
		return list;
	}

	public void insert(List<DBObject> list) {
		String collectionName = "orgranktest";
		DBCollection dbc = getCollection(collectionName);
		dbc.insert(list);

	}

	DBCollection getCollection(String collectionName) {
		try {
			Mongo mongo = new Mongo("166.111.7.105", 30017);
			char[] pwd_char = "datiantian123!@#".toCharArray();
			DB db = mongo.getDB("bigsci");
			boolean auth = db.authenticate("kegger_bigsci", pwd_char);
			if (auth) {
				System.out.println("auth successed!");
				return db.getCollection(collectionName);
			} else {
				System.err.println("auth failed");
				return null;
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
