package service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;

import model.Publication;

public class TxtService {
	public static void main(String[] args) {
		TxtService ts = new TxtService();
		ts.sortInfocom("E:/INFOCOM2008.txt");
	}

	String parseAuthorOrg(String str) {
		String res = null;
		int firstPar = str.indexOf('(');
		if (firstPar > 0) {
			String author = str.substring(0, firstPar).trim();
			String org = str.substring(firstPar + 1, str.length() - 1);
			res = author + "\t" + org;
		}
		return res;
	}

	public List<String> sortInfocom(String filePath) {
		File txt = new File(filePath);
		List<String> util = new Vector<String>();
		BufferedReader in = null;
		try {
			
			in = new BufferedReader(new InputStreamReader(new FileInputStream(
					txt), "utf-8"));
			if (null != in) {
				String line = null;
				String title = null;
				while ((line = in.readLine()) != null) {
					if (line.trim().equals("")||line.matches("\\d+:\\d+ \\w+ - \\d+:\\d+ \\w+"))
						continue;
					if (title == null)
						title = line;
					else {
						String[] strs = line.split(";");
						StringBuilder authors = new StringBuilder();
						StringBuilder orgs = new StringBuilder();
						int t = 0;
						String res = parseAuthorOrg(line);
						if(res==null){
							title=line;
						}else{
							for (String str : strs) {
								res = parseAuthorOrg(str);
//								System.out.println("<<<"+res);
								if (t == 0) {
									t++;
									if(res==null){
										authors.append(str);
										orgs.append("-");
									}else{
									authors.append(res.split("\t")[0]);
									orgs.append(res.split("\t")[1]);
									}
								} else {
									if(res==null){
										authors.append(str);
										orgs.append("-");
									}else{
										authors.append(";" + res.split("\t")[0]);
										orgs.append(";" + res.split("\t")[1]);
									}
									
								}
								
							}
							System.out.println(title + "\t"
									+ authors.toString() + "\t"
									+ orgs.toString());
							util.add(title + "\t" + authors.toString()
									+ "\t" + orgs.toString());
							title = null;
						}
					}

				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return util;
	}

	static public void getStringList(String filePath, List<String> util)
			throws IOException {
		File txt = new File(filePath);
		BufferedReader in = new BufferedReader(new FileReader(txt));
		if (null != in) {
			String line = null;
			while ((line = in.readLine()) != null) {
				util.add(line);
			}
		}
		if (null != in) {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public List<Publication> constructPubs(String filePath) {
		List<String> util = new ArrayList<String>();
		List<Publication> res = new ArrayList<Publication>();
		try {
			getStringList(filePath, util);
			for (String line : util) {
				String[] cols = line.split("\t");
				Publication pub = null;
				try {
					for (int i = 0; i < cols.length; i++) {
						pub = new Publication();
						pub.title = cols[0];
						pub.authors = cols[1];
						if (!cols[3].equals("")) {
							if (cols[3].split("-").length > 1) {
								int p1 = Integer
										.parseInt(cols[3].split("-")[0]);
								int p2 = Integer
										.parseInt(cols[3].split("-")[1]);
								pub.pages = String.valueOf(Math.min(p1, p2))
										+ "--"
										+ String.valueOf(Math.max(p1, p2));
							}
						}
						pub.year = cols[4];
						pub.jconf = cols[5];
					}
					res.add(pub);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	public List<String> readAcol(String filePath, int colNo) {
		File txt = new File(filePath);
		List<String> util = new Vector<String>();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(txt));
			if (null != in) {
				String line = null;
				while ((line = in.readLine()) != null) {
					if (!line.trim().equals("")) {
						String[] tmp = line.split("\t");
						if (tmp.length > colNo)
							util.add(tmp[colNo]);
						else
							util.add("");

					} else
						util.add("");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return util;
	}

	public double getLCS_length(String str1, String str2) {
		if (str1 == null || str2 == null)
			return 0;
		if (str1.trim().equals("") || str2.trim().equals(""))
			return 0;
		str1 = str1.toLowerCase();
		str2 = str2.toLowerCase();
		int m = str1.length();
		int n = str2.length();
		int c[][] = new int[m][n];
		for (int i = 0; i < m; i++)
			c[i][0] = 0;
		for (int j = 0; j < n; j++)
			c[0][j] = 0;
		for (int i = 1; i < m; i++) {
			for (int j = 1; j < n; j++) {
				if (str1.charAt(i) == str2.charAt(j)) {
					c[i][j] = c[i - 1][j - 1] + 1;
				} else if (c[i - 1][j] >= c[i][j - 1]) {
					c[i][j] = c[i - 1][j];
				} else {
					c[i][j] = c[i][j - 1];
				}
			}
		}
		double lcs = c[m - 1][n - 1] + 1;
		return lcs / (Math.min(m, n));
	}

	void writeLineInFile(ArrayList<String> lines, String fileName)
			throws IOException {
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(//
						new File(fileName + ".txt")), "utf-8"));
		for (String line : lines) {
			writer.printf("%s\r\n", line);
		}
		System.out.println(fileName + ".txt");
		writer.close();
	}

	void writeInFile(final HashMap<String, Double> orgNameList,
			String fileName, String str) throws IOException {
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(//
						new File(fileName + ".txt")), "utf-8"));
		ArrayList<String> List = printHashMap(orgNameList, str);
		for (String key : List) {
			writer.printf("%s\t%s\r\n", key, orgNameList.get(key));
		}
		System.out.println(fileName + ".txt");
		writer.close();
	}

	protected ArrayList<String> printHashMap(
			final HashMap<String, Double> orgNameList, String str) {
		ArrayList<String> List = new ArrayList<String>();
		List.addAll(orgNameList.keySet());
		if (str.equals("key")) {
			Collections.sort(List,
					Collections.reverseOrder(new Comparator<String>() {
						@Override
						public int compare(String o1, String o2) {
							return o1.toLowerCase().compareTo(o2.toLowerCase());
						}
					}));
		} else {// 按value排序
			Collections.sort(List,
					Collections.reverseOrder(new Comparator<String>() {
						@Override
						public int compare(String o1, String o2) {
							Double value1 = orgNameList.get(o1);
							Double value2 = orgNameList.get(o2);
							return value1.compareTo(value2);
						}
					}));
		}

		return List;
	}

	void sort(List<String> List, final Map<String, String> content) {
		Collections.sort(List,
				Collections.reverseOrder(new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						Integer value1 = Integer.parseInt(content.get(o1)
								.split("\t")[4]);
						Integer value2 = Integer.parseInt(content.get(o2)
								.split("\t")[4]);
						return value1.compareTo(value2);
					}
				}));
	}

	public Set<String> regulize(List<String> p1) {
		Set<String> res = new HashSet<String>();
		for (String str : p1) {
			str = str.toLowerCase().replaceAll("[^a-z -:]", "");
			res.add(str);
		}
		return res;
	}

	public Set<String> overLap(Set<String> p1, Set<String> p2) {
		Set<String> p1minusp2 = new HashSet<String>(p1);
		p1minusp2.removeAll(p2);
		p1.removeAll(p1minusp2);
		return p1;
	}

	/**
	 * 
	 * @Title: readDirs
	 * @Description: 读目录
	 * @param String
	 *            filepath sentence
	 * @return List<String>
	 * @throws IOException
	 *             ,FileNotFoundException
	 */
	protected static List<String> readDirs(String filepath)
			throws FileNotFoundException, IOException {
		List<String> fileList = new ArrayList<String>();
		try {
			File file = new File(filepath);
			if (!file.isDirectory()) {
				System.out.println("输入的参数应该为[文件夹名]");
				System.out.println("filepath: " + file.getAbsolutePath());
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File readfile = new File(filepath + File.separator
							+ filelist[i]);
					if (!readfile.isDirectory()) {
						fileList.add(readfile.getAbsolutePath());
					} else if (readfile.isDirectory()) {
						readDirs(filepath + File.separator + filelist[i]);
					}
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return fileList;
	}

	void modifyAnswer(String dir, String condition) {
		File fileDir = new File(dir);
		if (!fileDir.isDirectory()) {
			System.out.println("not a dir");
			return;
		}
		for (String file : fileDir.list()) {
			ArrayList<String> util = new ArrayList<String>();
			String path = dir + File.separator + file;
			BufferedReader in = null;
			BufferedWriter out = null;
			try {
				in = new BufferedReader(new FileReader(path));
				if (null != in) {
					String line = null;
					int count = 0;
					System.out.println(path);
					while ((line = in.readLine()) != null) {
						if (!line.contains(condition))
							util.add(line);
						else {
							count++;
						}
					}
					System.out.println("删除 " + count);
				}
				in.close();
				out = new BufferedWriter(new FileWriter(path));
				for (String s : util) {
					out.write(s + "\r\n");
				}
				out.flush();
				out.close();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					if (null != in)
						in.close();
					if (null != out)
						out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	void filter(String path, String regex) {
		ArrayList<String> util = new ArrayList<String>();
		BufferedReader in = null;
		BufferedWriter out = null;
		try {
			in = new BufferedReader(new FileReader(path));
			if (null != in) {
				String line = null;
				int count = 0;
				System.out.println(path);
				while ((line = in.readLine()) != null) {
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(line);
					if (matcher.find()) {
						System.out.println(matcher.group());
						util.add(matcher.group());
						count++;
					}

				}
				System.out.println(count);
			}
			in.close();
			out = new BufferedWriter(new FileWriter(path));
			for (String s : util) {
				out.write(s + "\r\n");
			}
			out.flush();
			out.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				if (null != in)
					in.close();
				if (null != out)
					out.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}
