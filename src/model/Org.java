package model;

import jxl.*;

public class Org {
	String id;
	String name;
	String[] names;
	Double scoreCount;
	Double scoreCountReg;
	Double scoreCite;
	Double scoreCiteReg;

	public Org() {

	}

	public Org(int id,Cell[] row) {
		setId(id);
		setNames(row);
		setName();
	}
	public Org(int id,String row) {
		setId(id);
		setNames(row);
		setName();
	}
	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}
	public void setId(String id){
		this.id=id;
	}
	public void setId(Integer id){
		this.id=String.valueOf(id);
	}
	public void setName(String name){
		this.name=name;
	}
	public void setName(){
		if(null!=names&&names.length>0){
			this.name=names[0];
		}
	}
	public void setNames(String row){
		this.names=row.split("\t");
	}
	public void setNames(Cell[] row){
		this.names=new String[row.length];
		for(int i=0;i<row.length;i++){
			names[i]=row[i].getContents();
		}
				
	}
	public String[] getNames() {
		return this.names;
	}

	public String getClusterText() {
		StringBuilder sb = new StringBuilder();
		for (String str : names)
			sb.append(str+"\t");
		return sb.toString();
	}

	public Double getScoreCount() {
		return this.scoreCount;
	}

	public Double getscoreCountReg() {
		return this.scoreCountReg;
	}

	public Double getScoreCite() {
		return this.scoreCount;
	}

	public Double getscoreCiteReg() {
		return this.scoreCountReg;
	}
	public void setScoreCount(Double scoreCount) {
		this.scoreCount=scoreCount;
	}

	public void setscoreCountReg(Double scoreCountReg) {
		this.scoreCountReg=scoreCountReg;
	}

	public void setScoreCite(Double scoreCite) {
		this.scoreCite=scoreCite;
	}

	public void setscoreCiteReg(Double scoreCiteReg) {
		this.scoreCiteReg=scoreCiteReg;
	}
}