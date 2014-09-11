package service;

import model.PaperJson;

import com.google.gson.Gson;

public class JsonService {
	public static void main(String args[]) {
		JsonService js = new JsonService();
		String json =
				"{ \"_id\" : { \"$oid\" : \"53e9aad9b7602d970345b00b\"} , \"hash\" : \"tiovonpoaedc\" , \"title\" : \"The impact of virtualization on network performance of amazon EC2 data center\" , \"n_citation\" : 153 , \"oid\" : 2801182 , \"keywords\" : [ \"networking performance\" , \"EC2 data center\" , \"large scale data center\" , \"data center\" , \"cloud service provider\" , \"machine virtualization\" , \"Amazon EC2\" , \"cloud computing service\" , \"network performance\" , \"cloud service\" , \"Amazon Elastic Cloud Computing\" , \"data center network\"] , \"isbn\" : \"\" , \"authors\" : [ { \"org\" : \"Dept. of Computer Science, Rice University\" , \"name\" : \"Guohui Wang\"} , { \"org\" : \"Dept. of Computer Science, Rice University\" , \"name\" : \"T. S. Eugene Ng\"}] , \"volume\" : \"\" , \"issue\" : \"\" , \"author_str\" : \"Guohui Wang;T. S. Eugene Ng\" , \"venue\" : { \"raw\" : \"INFOCOM'10 Proceedings of the 29th conference on Information communications\" , \"_id\" : { \"$oid\" : \"5390ab8520f70186a0eaf6f1\"}} , \"issn\" : \"\" , \"doi\" : \"\" , \"url\" : [ \"http://dx.doi.org/10.1109/INFCOM.2010.5461931\"] , \"reference\" : [ { \"raw\" : \"INFOCOM 2010. 29th IEEE International Conference on Computer Communications, Joint Conference of the IEEE Computer and Communications Societies, 15-19 March 2010, San Diego, CA, USA\" , \"sid\" : \"conf/infocom/2010\"}] , \"src\" : \"dblp\" , \"lang\" : \"en\" , \"copies\" : [ \"53a72b0a20f7420be8c17018\" , \"5390ab8820f70186a0eb1acf\"] , \"abstract\" : \"Cloud computing services allow users to lease computing resources from large scale data centers operated by service providers. Using cloud services, users can deploy a wide variety of applications dynamically and on-demand. Most cloud service providers use machine virtualization to provide flexible and costeffective resource sharing. However, few studies have investigated the impact of machine virtualization in the cloud on networking performance. In this paper, we present a measurement study to characterize the impact of virtualization on the networking performance of the Amazon Elastic Cloud Computing (EC2) data center. We measure the processor sharing, packet delay, TCP/UDP throughput and packet loss among Amazon EC2 virtual machines. Our results show that even though the data center network is lightly utilized, virtualization can still cause significant throughput instability and abnormal delay variations. We discuss the implications of our findings on several classes of applications.\" , \"page_start\" : \"1163\" , \"page_end\" : \"1171\" , \"date\" : \"2010-08-20\" , \"year\" : 2010}";
		System.out.println(js.formPublication(json));
	}
	public String formPublication(String json) {
		Gson gson = new Gson();
		PaperJson pj = gson.fromJson(json, PaperJson.class);
		StringBuilder sb = new StringBuilder();
			sb.append(pj.getOrgRankId()+"\t");
			sb.append(pj.getTitle()+"\t");
			sb.append(pj.getAus()+"\t");
			sb.append(pj.getOrgs()+"\t");
			sb.append("dont know\t");
			sb.append(pj.getYear()+"\t");
			sb.append("-1\t");//			sb.append(pj.getPage()+"\t");
			sb.append(pj.getNCitation());
		return sb.toString();
	}
}
