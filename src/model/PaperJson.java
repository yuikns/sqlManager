package model;

import java.util.List;

public class PaperJson {
	Id _id;
	String hash;
	String title;
	int n_citation;
	String oid;
	List<String> keywords;
	List<Author> authors;
	String volume;
	String issue;
	Venue venue;
	String doi;
	List<String> url;
	// List<String> reference;
	String _abstract;
	String date;
	int year;
	String orgRankId;
//	int page_start;
//	int page_end;

	public Id getId() {
		return _id;
	}

	public void setId(Id id) {
		this._id = id;
	}

	public String getOrgRankId() {
		return (orgRankId == null) ? "-1" : orgRankId;
	}

	public void setorgRankId(Id id) {
		this._id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	public int getNCitation() {
		return this.n_citation;
	}

	public void setNCitation(int NCitation) {
		this.n_citation = NCitation;
	}
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

//	public int getPage_start() {
//		return page_start;
//	}
//
//	public void setPage_start(int page_start) {
//		this.page_start = page_start;
//	}
//
//	public int getPage_end() {
//		return page_end;
//	}
//
//	public void setpage_end(int page_end) {
//		this.page_end = page_end;
//	}
//
//	public String getPage() {
//		String page = "-1";
//		if (page_end > 0 && page_start > 0) {
//			page = String.valueOf(page_start) + "--" + String.valueOf(page_end);
//		}
//		return page;
//	}

	public String getAus() {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		if(authors!=null)
		for (Author au : authors) {
			String name = au.getName();
			if (name!=null&&!name.equals("")) {
				sb.append(name);
			} else {
				sb.append("-");
			}
			if (i == 0) {
				sb.append(";");
				i++;
			}
		}
		return sb.toString();
	}

	public String getOrgs() {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		if(authors!=null)
		for (Author au : authors) {
			if (i != 0) {
				sb.append(";");
			}
			i++;
			String org = au.getOrg();
			if (org!=null&&!org.trim().equals("")) {
				sb.append(org.trim());
			} else {
				sb.append("-");
			}
			
		}
		return sb.toString();
	}
}

class Id {
	String oid;

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}
}

class Venue {
	String raw;
	Id _id;

	public Id getId() {
		return _id;
	}

	public void setId(Id id) {
		this._id = id;
	}
}

class Author {
	String org;
	String name;

	public String getOrg() {
		return org;
	}

	public void setId(String org) {
		this.org = org;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}