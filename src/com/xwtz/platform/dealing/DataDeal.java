package com.xwtz.platform.dealing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.xwtz.platform.ui.dialog.MainFrameDialog;

public class DataDeal implements IDataDealRunner {
	// 读取excel
	private static int minpts = 4;
	private static double radius = 0.02;
	private static List<List<double[]>> clusters;
	private static List<double[]> cores;
	// private static ArrayList<double[]> columnList = new
	// ArrayList<double[]>();
	private static double[][] points = {};
	private static List<String> dayList = new ArrayList<String>();
	private final MainFrameDialog mainFrameDialog;
	private boolean cancelled;
	private final String fileName;
	private final String gidText;

	public DataDeal(String fileName, String gidText, MainFrameDialog mainFrameDialog) {
		this.mainFrameDialog = mainFrameDialog;
		this.fileName = fileName;
		this.gidText = gidText;
		// init(fileName, gidText);
	}

	private void init(String fileName, String gidText) {
		dayList.add("201604");
		dayList.add("201605");
		dayList.add("201606");
		dayList.add("201607");
		dayList.add("201608");
		dayList.add("201609");
		dayList.add("201610");
		dayList.add("201611");
		dayList.add("201612");
		dayList.add("201701");
		dayList.add("201702");
		dayList.add("201703");
		dayList.add("201704");
		dayList.add("201705");
		dayList.add("201706");
		dayList.add("201707");
		dayList.add("201708");
		long starttime = System.currentTimeMillis();

		// 取出excel表中的数据
		for (int w = 0; w < dayList.size(); w++) {
			ArrayList<double[]> columnList = new ArrayList<double[]>();
			List<double[]> aList = rout(fileName, gidText, columnList, dayList.get(w));
			double[][] points = new double[aList.size()][2];
			Kmeans k = new Kmeans(1);
			// ArrayList<ArrayList<float[]>> clusterdataSet = new ArrayList<>();
			List listGeohash = new ArrayList<>();
			for (int i = 0; i < aList.size(); i++) { // 如何double[] 转为double[][]
				for (int n = 0; n < 2; n++) {
					points[i][n] = aList.get(i)[n];
				}
			}
			System.out.println(points);
			// DBSCAN算法
			cores = findCores(points, minpts, radius);
			putCoreToCluster(cores, points);
			for (List<double[]> cluster : clusters) {
				// System.out.println("cluster " + i++ + ":");
				ArrayList<float[]> dataSet = new ArrayList<float[]>();
				// ArrayList<float[]> dataSet = null;
				for (double[] point : cluster) {
					// System.out.println("[" + point[0] + "," + point[1] +
					// "]");
					dataSet.add(new float[] { (float) point[0], (float) point[1] });

				}
				// 设置原始数据集
				k.setDataSet(dataSet);
				// 执行kmeans算法
				k.execute();
				// 得到中心结果
				ArrayList<float[]> initCenters = k.initCenters();
				Float[] f = new Float[initCenters.size()];
				// List<GeoHash> listGeo = new ArrayList<>();
				for (int i1 = 0; i1 < initCenters.size(); i1++) {
					System.out.println("jieguoji " + ":" + initCenters.get(i1)[0] + "," + initCenters.get(i1)[1]);
					// 输出geohash
					GeoHash geoHash = new GeoHash(initCenters.get(i1)[0], initCenters.get(i1)[1]);
					// System.out.println("geoHash" + n++ + ":" +
					// geoHash.getGeoHashBase32());
					listGeohash.add(geoHash.getGeoHashBase32());

				}
				// 经纬度转换为geohash算法

			}
			// cluster 12:
			// 导入到excel
			// 第一步，创建一个webbook，对应一个Excel文件
			HSSFWorkbook wb = new HSSFWorkbook();
			// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
			HSSFSheet sheet = wb.createSheet("学生表一");
			// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
			HSSFRow row = sheet.createRow((int) 0);
			// 第四步，创建单元格，并设置值表头 设置表头居中
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

			HSSFCell cell = row.createCell(0);
			cell.setCellValue("gid");
			cell.setCellStyle(style);
			cell = row.createCell(1);
			cell.setCellValue("geohash");
			cell.setCellStyle(style);
			cell = row.createCell(2);
			cell.setCellValue("时间");
			cell.setCellStyle(style);

			for (int l = 0; l < listGeohash.size(); l++) {
				row = sheet.createRow((int) l + 1);
				// Student stu = (Student) listGeohash.get(l);
				// 第四步，创建单元格，并设置值
				row.createCell((short) 0).setCellValue(gidText);
				row.createCell((short) 1).setCellValue(listGeohash.get(l).toString());
				row.createCell((short) 2).setCellValue(dayList.get(w));
			}
			// 第六步，将文件存到指定位置
			try {
				String fName = "D:" + File.separator + "deal_data";
				File file = new File(fName);
				file.mkdir();
				File newfile = new File(fName + File.separator + dayList.get(w) + gidText + ".xls");
				if (!newfile.exists()) {
					newfile.createNewFile();
				}
				FileOutputStream fout = new FileOutputStream(newfile);
				wb.write(fout);
				fout.close();
				mainFrameDialog.showProgress("完成");
				System.out.println(w + "w:" + (System.currentTimeMillis() - starttime));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private List<double[]> rout(String rount, String gidText, List<double[]> columnList, String day) {
		try {
			System.gc();
			mainFrameDialog.enableProgress();
			mainFrameDialog.showProgress("Scanning historical datafile...");
			long t = System.currentTimeMillis();
			System.out.println("耗时操作：" + t);
			File file = new File(rount);
			FileInputStream in = new FileInputStream(file);
			XSSFWorkbook wb = new XSSFWorkbook(in);
			System.out.println("耗时操作结束耗时：" + (System.currentTimeMillis() - t));
			// 取得工作表
			Sheet sheet = wb.getSheetAt(0);
			int firstRowNum = sheet.getFirstRowNum();
			int lastRowNum = sheet.getLastRowNum();

			Row row = null;
			Cell cell_gid = null;
			Cell cell_day = null;
			Cell cell_lnglat = null;

			long marketDepthCounter = 0;
			long size = lastRowNum;

			// // marketDepthCounter = dataDeal.getLineNumber();
			// // if (!cancelled) {
			mainFrameDialog.showProgress("Running back test...");
			for (int i = firstRowNum; i <= lastRowNum; i++) {
				row = sheet.getRow(i); // 取得第i行
				cell_gid = row.getCell(0); // 取得i行的第一列
				cell_day = row.getCell(2);
				cell_lnglat = row.getCell(5);
				row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
				if (marketDepthCounter % 1000 == 0) {
					// System.out.println(marketDepthCounter++);
					mainFrameDialog.setProgress(marketDepthCounter, size, "Running back test");
					// Thread.sleep(200);
				}
				if (gidText.equals(cell_gid.getStringCellValue())
						&& day.equals(cell_day.getStringCellValue().substring(0, 6))) {
					// System.out.println("cell_gid.getStringCellValue()"+cell_gid.getStringCellValue()+",cell_day.getStringCellValue().substring(0,
					// 6)"+
					// cell_day.getStringCellValue().substring(0, 6));
					String[] lnglat = cell_lnglat.getStringCellValue().split(",");
					double lng = Double.parseDouble(lnglat[0]);
					double lat = Double.parseDouble(lnglat[1]);
					columnList.add(new double[] { lng, lat });
					row = null;
					cell_gid = null;
					cell_day = null;
					cell_lnglat = null;
					marketDepthCounter++;

				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return columnList;
	}

	/**
	 * find the core points
	 * 
	 * @param points
	 * @param minpts
	 * @param radius
	 * @return
	 */
	private List<double[]> findCores(double[][] points, int minpts, double radius) {
		List<double[]> cores = new ArrayList<double[]>();
		for (int i = 0; i < points.length; i++) {
			int pts = 0;
			for (int j = 0; j < points.length; j++) {
				if (countEurDistance(points[i], points[j]) < radius) {
					pts++;
				}
			}
			if (pts >= minpts) {
				cores.add(points[i]);
			}
		}
		return cores;
	}

	/**
	 * 欧氏距离
	 * 
	 * @param point1
	 * @param point2
	 * @return
	 */
	private static double countEurDistance(double[] point1, double[] point2) {
		double eurDistance = 0.0;
		for (int i = 0; i < point1.length; i++) {
			eurDistance += (point1[i] - point2[i]) * (point1[i] - point2[i]);
		}
		return Math.sqrt(eurDistance);
	}

	/**
	 * put the core point to cluster and get the densityconnect
	 * 
	 * @param points
	 * @param cores
	 */
	private static void putCoreToCluster(List<double[]> cores, double[][] points) {
		clusters = new ArrayList<List<double[]>>();
		int clusterNum = 0;
		for (int i = 0; i < cores.size(); i++) {
			clusters.add(new ArrayList<double[]>());
			clusters.get(clusterNum).add(cores.get(i));
			densityConnected(points, cores.get(i), clusterNum);
			clusterNum++;
		}
	}

	/**
	 * 
	 * @param points
	 * @param core
	 * @param clusterNum
	 */
	private static void densityConnected(double[][] points, double[] core, int clusterNum) {
		boolean isputToCluster;// 是否已经归为某个类
		boolean isneighbour = false;// 是不是core的“邻居”
		cores.remove(core);// 对某个core点处理后就从core集中去掉
		for (int i = 0; i < points.length; i++) {
			isneighbour = false;
			isputToCluster = false;
			for (List<double[]> cluster : clusters) {
				if (cluster.contains(points[i])) {// 如果已经归为某个类
					isputToCluster = true;
					break;
				}
			}
			if (isputToCluster)
				continue;// 已在聚类中，跳过，不处理
			if (countEurDistance(points[i], core) < radius) {// 是目前加入的core点的“邻居”吗？，ture的话，就和这个core加入一个类
				clusters.get(clusterNum).add(points[i]);
				isneighbour = true;
			}
			if (isneighbour) {// 如果是邻居，才会接下来对邻居进行densityConnected处理，否则，结束这个core点的处理
				if (cores.contains(points[i])) {
					cores.remove(points[i]);
					densityConnected(points, points[i], clusterNum);
				}
			}
		}

	}

	@Override
	public void run() {
		init(fileName, gidText);
	}

	@Override
	public void cancel() {
		cancelled = true;
	}
}
