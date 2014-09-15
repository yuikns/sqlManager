package model;

public class PaperForEva implements Paper {
	int id;
	String p_name;
	String title;
	String year;
	String conf;
	String authors;
	String fulltext;

	public PaperForEva() {
	}

	public PaperForEva(int id, String p_name, String conf, String year,
			String fulltext) {
		this.id = id;
		this.p_name = p_name;
		this.year = year;
		this.conf = conf;
		this.fulltext = fulltext;
		fillDetail();
	}

	void fillDetail() {
		try {
			if (!this.fulltext.equals("")) {
				int ii = this.fulltext.indexOf(':');
				if (ii > 0) {
					int jj = this.fulltext.lastIndexOf('.');
					if (ii + 1 < jj) {
						this.authors = this.fulltext.substring(0, ii).trim();
						this.title = this.fulltext.substring(ii + 1, jj).trim();
					} else if (this.fulltext.matches(".* \\d+-\\d+")) {
						jj = this.fulltext.lastIndexOf(' ');
						this.authors = this.fulltext.substring(0, ii).trim();
						this.title = this.fulltext.substring(ii + 1, jj).trim();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getP_name() {
		return this.p_name;
	}

	public void setP_id(String p_name) {
		this.p_name = p_name;
	}

	public int getId() {
		return this.id;
	}

	public void setFulltext(String fulltext) {
		this.fulltext = fulltext;
		fillDetail();
	}

	public String getFulltext() {
		return this.fulltext;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return this.title;
	}

	public void settitle(String title) {
		this.title = title;
	}

	public String getAuthors() {
		return this.authors;
	}

	public void setauthors(String authors) {
		this.authors = authors;
	}

	public int getYear() {
		int y = Integer.parseInt(this.year);
		return y;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getConf() {
		return this.conf;
	}

	public void setConf(String conf) {
		this.conf = conf;
	}

	public String toString() {
		return this.id + "\t" + this.year + "\t" + this.conf + "\t"
				+ this.title + "\t" + this.authors;
	}
}
