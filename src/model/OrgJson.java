package model;

import java.util.List;

public class OrgJson {
	String idorg;
	String org;
	String orgClusterText;
	int isAcademic;
	List<TypeScoreJson> typeScore;
	
	public void setIdorg(String idorg) {
		this.idorg = idorg;
	}
	public String getIdorg() {
		return this.idorg;
	}
	public void setIsAcademic(int isAcademic) {
		this.isAcademic = isAcademic;
	}
	public int getIsAcademic() {
		return this.isAcademic;
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
