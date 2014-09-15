package model;

public class Person {
	int id;
	String name;
	String org;
	public Person(){
		
	}
	public Person(int id,String name,String org){
		this.id=id;
		this.name=name;
		this.org=org;
	}
	public int getId(){
		return this.id;
	}
	public void setId(int id){
		this.id=id;
	}
	public String getorg(){
		return this.org;
	}
	public void setorg(String org){
		this.org=org;
	}
	public String getname(){
		return this.name;
	}
	public void setname(String name){
		this.name=name;
	}
	public String toString(){
		return this.id+this.name+this.org;
	}
}
