package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.document.Document;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import model.Publication;

public class ExcelService {
	static protected HashMap<String, Double> firstAuthorList = new HashMap<String, Double>();
	static protected HashMap<String, Double> secondAuthorList = new HashMap<String, Double>();
	static protected HashMap<String, Double> thirdAuthorList = new HashMap<String, Double>();
	static protected HashMap<String, Double> otherAuthorList = new HashMap<String, Double>();
	List<String> fList;

	public int getSheetNum(String filePath) {
		int num = 0;
		Workbook book = null;
		try {
			book = Workbook
					.getWorkbook(new FileInputStream(new File(filePath)));
			num = book.getNumberOfSheets();
			book.close();
		} catch (Exception e) {

		} finally {
			if (book != null)
				book.close();
		}
		return num;
	}

	public List<Publication> getPubsFromExcel(String filePath, int sheetNo) {
		Workbook book = null;
		List<Publication> pubs = new ArrayList<Publication>();
		try {
			WorkbookSettings workbookSettings = new WorkbookSettings();
			workbookSettings.setEncoding("utf-8");
			book = Workbook.getWorkbook(
					new FileInputStream(new File(filePath)), workbookSettings);
			Sheet sheet = book.getSheet(sheetNo);
			System.out.println(sheet.getName());
			for (int j = 1; j < sheet.getRows(); j++) {
				System.out.println(sheet.getRow(j).length);
				Publication pub = new Publication(sheet.getRow(j));
				pubs.add(pub);
				if (j == sheet.getRows() - 1)
					System.out.println(" 共" + (j + 1) + "条！！");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BiffException e) {
			e.printStackTrace();
		} finally {
			if (book != null)
				book.close();
		}
		return pubs;
	}

	public ArrayList<String> readAll(String filePath, int sheetNum) {
		ArrayList<String> list = new ArrayList<String>();
		Workbook book = null;
		try {
			System.out.println(filePath);
			WorkbookSettings workbookSettings = new WorkbookSettings();
			workbookSettings.setEncoding("utf-8");
			book = Workbook.getWorkbook(
					new FileInputStream(new File(filePath)), workbookSettings);
			Sheet sheet = book.getSheet(sheetNum);
			for (int j = 0; j < sheet.getRows(); j++) {
				Cell[] cells = sheet.getRow(j);
				StringBuilder content = new StringBuilder();
				for (Cell cell : cells)
					content.append(cell.getContents() + "\t");
				list.add(content.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (book != null)
				book.close();
		}
		return list;
	}

	/**
	 * 
	 * @Title: readDirs
	 * @Description: 读excel一列
	 * @param String
	 *            filepath ,colNo
	 * 
	 * @return List<String>
	 * @throws IOException
	 *             ,FileNotFoundException
	 */
	public ArrayList<String> readCol(String filePath, int sheetNo, int colNo) {
		ArrayList<String> list = new ArrayList<String>();
		Workbook book = null;
		try {
			// 打开文件
			System.out.println(filePath);
			WorkbookSettings workbookSettings = new WorkbookSettings();
			workbookSettings.setEncoding("utf-8");
			book = Workbook.getWorkbook(
					new FileInputStream(new File(filePath)), workbookSettings);
			Sheet sheet = book.getSheet(sheetNo);
			for (int j = 0; j < sheet.getRows(); j++) {
				Cell cell = sheet.getCell(colNo, j);
				list.add(cell.getContents());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (book != null)
				book.close();
		}
		return list;
	}

	public ArrayList<String> searchKeyWordReturnCell(int colNumSource,
			int colNumDest, String keyWord, boolean isRegex, String filePath) {
		ArrayList<String> list = searchKeyWordReturnEntireRow(colNumSource,
				keyWord, isRegex, filePath);
		for (int i = 0; i < list.size(); i++) {
			list.set(i, list.get(i).split("\t")[colNumDest]);// 从0开始
		}
		return list;
	}

	public ArrayList<String> searchKeyWordReturnEntireRow(int colNumSource,
			String keyWord, boolean isRegex, String filePath) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			WorkbookSettings workbookSettings = new WorkbookSettings();
			workbookSettings.setEncoding("utf-8");
			Workbook book = Workbook.getWorkbook(new FileInputStream(new File(
					filePath)), workbookSettings);
			Sheet sheet = book.getSheet(0);
			for (int j = 0; j < sheet.getRows(); j++) {
				Cell cell = sheet.getCell(colNumSource, j);
				StringBuilder stringBuilder = new StringBuilder();
				boolean isHit = false;
				if (cell.getContents() != "") {
					if (isRegex) {
						if (cell.getContents().matches(keyWord)) {
							isHit = true;
						}
					} else {
						if (cell.getContents().contains(keyWord)) {
							isHit = true;
						}
					}
				}
				if (isHit) {
					Cell[] row = sheet.getRow(j);
					for (int i = 0; i < row.length; i++) {
						stringBuilder.append(row[i].getContents() + "\t");
					}
					list.add(stringBuilder.toString());
				}
			}
			book.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BiffException e) {
			e.printStackTrace();
		}
		return list;
	}

	

	void writeAuthorList(String result, Double nCitation) {
		if (result != "") {
			String[] Arr = result.split(";");// Split(result, ";");
			double incFirst = 1;
			double incSecord = 0.5;
			double incThird = 0.3;
			double incOther = 0.2;
			switch (Arr.length) {
			case 1:
				incFirst = 2;
				break;
			case 2:
				incFirst = 1.3;
				incSecord = 0.7;
				break;
			case 3:
				incSecord = 0.6;
				incThird = 0.4;
				break;
			default:
				break;
			}
			// if(result.contains("Shandong University"))
			// System.err.println(result+" "+nCitation+" "+incFirst);
			for (int i = 0; i < Arr.length; i++) {
				String line = Arr[i].trim();
				if (!line.equals("")) {
					switch (i) {
					case 0:
						if (!firstAuthorList.containsKey(line)) {
							firstAuthorList.put(line, incFirst * nCitation);
						} else {
							firstAuthorList.put(line, firstAuthorList.get(line)
									+ incFirst * nCitation);
						}

						break;
					case 1:
						if (!secondAuthorList.containsKey(line)) {
							secondAuthorList.put(line, incSecord * nCitation);
						} else {
							secondAuthorList.put(line,
									secondAuthorList.get(line) + incSecord
											* nCitation);
						}
						break;
					case 2:
						if (!thirdAuthorList.containsKey(line)) {
							thirdAuthorList.put(line, incThird * nCitation);
						} else {
							thirdAuthorList.put(line, thirdAuthorList.get(line)
									+ incThird * nCitation);
						}
						break;
					default:
						if (Arr.length > 4)
							incOther = 0.2 / (Arr.length - 3);
						if (incOther < 0.01)
							incOther = 0.01;
						if (!otherAuthorList.containsKey(line)) {
							otherAuthorList.put(line, incOther * nCitation);
						} else {
							otherAuthorList.put(line, otherAuthorList.get(line)
									+ incOther * nCitation);
						}
						break;
					}
				}
			}
		}
	}

	void writeAuthorList(String result) {
		if (result != "") {
			String[] Arr = result.split(";");// Split(result, ";");
			double incFirst = 1;
			double incSecord = 0.5;
			double incThird = 0.3;
			double incOther = 0.2;
			switch (Arr.length) {
			case 1:
				incFirst = 2;
				break;
			case 2:
				incFirst = 1.3;
				incSecord = 0.7;
				break;
			case 3:
				incSecord = 0.6;
				incThird = 0.4;
				break;
			default:
				break;
			}
			for (int i = 0; i < Arr.length; i++) {
				String str = Arr[i];
				String[] subArr = str.split(":");
				if (subArr.length > 1) {
					String line = subArr[1];
					switch (i) {
					case 0:
						if (!firstAuthorList.containsKey(line)) {
							firstAuthorList.put(line, incFirst);
						} else {
							firstAuthorList.put(line, firstAuthorList.get(line)
									+ incFirst);
						}

						break;
					case 1:
						if (!secondAuthorList.containsKey(line)) {
							secondAuthorList.put(line, incSecord);
						} else {
							secondAuthorList.put(line,
									secondAuthorList.get(line) + incSecord);
						}
						break;
					case 2:
						if (!thirdAuthorList.containsKey(line)) {
							thirdAuthorList.put(line, incThird);
						} else {
							thirdAuthorList.put(line, thirdAuthorList.get(line)
									+ incThird);
						}
						break;
					default:
						if (Arr.length > 4)
							incOther = 0.2 / (Arr.length - 3);
						if (incOther < 0.01)
							incOther = 0.01;
						if (!otherAuthorList.containsKey(line)) {
							otherAuthorList.put(line, incOther);
						} else {
							otherAuthorList.put(line, otherAuthorList.get(line)
									+ incOther);
						}
						break;
					}
				}
			}
		}
	}

}
