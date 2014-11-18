package model;

import java.util.List;

public class OrgJson {
	String idorg;
	String org;
	String orgClusterText;
	String type;
	List<TypeScoreJson> typeScore;
	
	public void setIdorg(String idorg) {
		this.idorg = idorg;
	}
	public String getIdorg() {
		return this.idorg;
	}
	public void setIsAcademic(String type) {
		this.type = type;
	}
	public String getIsAcademic() {
		return this.type;
	}
	public void setOrg(String org) {
		this.org = org;
	}
	public String getOrg() {
		return org;
	}
	public void setOrgClusterText(String orgClusterText) {
		this.orgClusterText = orgClusterText;
	}
	public String getOrgClusterText() {
		return orgClusterText;
	}
	public void setTypeScore(List<TypeScoreJson> typeScore) {
		this.typeScore =typeScore ;
	}
	public List<TypeScoreJson> getTypeScoreJsonScore() {
		return this.typeScore;
	}
	public TypeScoreJson getTypeScoreJsonScore(int typeScoreJsonNo) {
		return this.typeScore.get(typeScoreJsonNo);
	}
}
