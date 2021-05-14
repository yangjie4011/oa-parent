package com.ule.oa.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

public class ExcelUtil {
	//每个sheet最大行数
	public static final int avg = 50000;
	
	/**
	 * 对外提供读取excel 的方法
	 * 
	 * @param @param file
	 * @param @param columnNum excel列数
	 * */
	public static List<List<Object>> readExcel(File file,String fileName,int columnNum) throws IOException {
		return readExcel(new FileInputStream(file), fileName, columnNum);
	}

	public static List<List<Object>> readExcel(MultipartFile file,String fileName,int columnNum) throws IOException {
		return readExcel(file.getInputStream(), fileName, columnNum);
	}
	
	public static List<List<Object>> readExcel(InputStream in,String fileName,int columnNum) throws IOException {
		String extension = fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);
		if ("xls".equals(extension)) {
			return read2003Excel(in, columnNum);
		} else if ("xlsx".equals(extension)) {
			return read2007Excel(in, columnNum);
		} else {
			throw new IOException("不支持的文件类型");
		}
	}

	/**
	 * @Description: 读取Office 2003 excel
	 * @param @param file
	 * @param @param columnNum excel列数
	 * @param @throws IOException
	 * @return List<List<Object>> 返回类型
	 */
	private static List<List<Object>> read2003Excel(InputStream in, int columnNum) throws IOException {
		List<List<Object>> list = new LinkedList<List<Object>>();
		HSSFWorkbook hwb = new HSSFWorkbook(in);
		HSSFSheet sheet = hwb.getSheetAt(0);
		Object value = null;
		HSSFRow row = null;
		HSSFCell cell = null;
		for (int i = 0; i <= sheet
				.getPhysicalNumberOfRows(); i++) {
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			List<Object> linked = new LinkedList<Object>();
			for (int j = 0; j <= columnNum; j++) {
				cell = row.getCell(j);
				if (cell == null) {
					linked.add("");
					continue;
				}
				switch (cell.getCellType()) {
				case XSSFCell.CELL_TYPE_STRING:
					value = cell.getStringCellValue();
					break;
				case XSSFCell.CELL_TYPE_NUMERIC:
					if (HSSFDateUtil.isCellDateFormatted(cell)) {
						value = cell.getDateCellValue();
					} else {
						// 如果是数字当成字符串解析
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						value = String.valueOf(cell.getStringCellValue());
					}
					break;
				case XSSFCell.CELL_TYPE_BOOLEAN:
					value = cell.getBooleanCellValue();
					break;
				case XSSFCell.CELL_TYPE_BLANK:
					value = "";
					break;
				default:
					value = cell.toString();
				}
				if (value == null || "".equals(value)) {
					value = "";
				}
				linked.add(value);
			}
			list.add(linked);
		}
		return list;
	}

	/**
	 * @Description: 读取Office 2007 excel
	 * @param @param file
	 * @param @param columnNum excel列数
	 * @param @throws IOException
	 * @return List<List<Object>> 返回类型
	 */
	private static List<List<Object>> read2007Excel(InputStream in, int columnNum) throws IOException {
		List<List<Object>> list = new LinkedList<List<Object>>();
		// 构造 XSSFWorkbook 对象，strPath 传入文件路径
		XSSFWorkbook xwb = new XSSFWorkbook(in);
		// 读取第一章表格内容
		XSSFSheet sheet = xwb.getSheetAt(0);
		Object value = null;
		XSSFRow row = null;
		XSSFCell cell = null;
		//i从1开始 忽略读取表头行
		for (int i = 0; i <= sheet
				.getPhysicalNumberOfRows(); i++) {
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			List<Object> linked = new LinkedList<Object>();
			for (int j = 0; j <= columnNum; j++) {
				cell = row.getCell(j);
				if (cell == null) {
					linked.add("");
					continue;
				}
				switch (cell.getCellType()) {
				case XSSFCell.CELL_TYPE_STRING:
					value = cell.getStringCellValue();
					break;
				case XSSFCell.CELL_TYPE_NUMERIC:
					if (HSSFDateUtil.isCellDateFormatted(cell)) {
						// 把Date转换成本地格式的字符串
//						value = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cell.getDateCellValue());
						value = cell.getDateCellValue();
					} else {
						// 如果是数字当成字符串解析
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						value = String.valueOf(cell.getStringCellValue());
					}
//					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
//					value = String.valueOf(cell.getStringCellValue());
					break;
				case XSSFCell.CELL_TYPE_BOOLEAN:
					value = cell.getBooleanCellValue();
					break;
				case XSSFCell.CELL_TYPE_BLANK:
					value = "";
					break;
				default:
					value = cell.toString();
				}
				if (value == null || "".equals(value)) {
					linked.add("");
					continue;
				}
				linked.add(value);
			}
			list.add(linked);
		}
		return list;
	}
	
	public static HSSFWorkbook exportExcel(List<Object[]> data, String[] titles, String sheetName){
		HSSFWorkbook workbook = new HSSFWorkbook();
		if (null != sheetName) {
			workbook.createSheet(sheetName);
		}
		fillWorkBook(data, titles, workbook);
		return workbook;
	}
	
	public static void fillWorkBook(List<Object[]> data, String[] titles, HSSFWorkbook workbook){
		if (0 == workbook.getNumberOfSheets()) {
			workbook.createSheet("sheet1");
		}
		HSSFSheet sheet = workbook.getSheetAt(0);
		HSSFCellStyle style = workbook.createCellStyle();
		if (data != null && data.size() > 0) {
			HSSFRow headRow = sheet.createRow(0);// 建立表头
			for (int colIndex = 0; colIndex < titles.length; colIndex++) {
				Object obj = titles[colIndex];
				
				if(obj instanceof Long || obj instanceof Double || obj instanceof Integer || obj instanceof Float || obj instanceof BigDecimal){
					createCol(workbook, headRow, colIndex, style, HSSFCell.CELL_TYPE_NUMERIC, obj);
				}else{
					createCol(workbook, headRow, colIndex, style, HSSFCell.CELL_TYPE_STRING, obj);
				}
			}
			for (int i = 0; i < data.size(); i++) {
				HSSFRow rowNumber = sheet.createRow(i + 1);// 建立新行
				Object[] contents = data.get(i);
				for (int colNumber = 0; colNumber < contents.length; colNumber++) {
					Object content = contents[colNumber];
					content = null == content?"":content;
					
					if(content instanceof Long || content instanceof Double || content instanceof Integer || content instanceof Float || content instanceof BigDecimal){
						createCol(workbook, rowNumber, colNumber, style, HSSFCell.CELL_TYPE_NUMERIC, content);
					}else{
						createCol(workbook, rowNumber, colNumber, style, HSSFCell.CELL_TYPE_STRING, content);
					}
				}
			}
		}
		else {
			HSSFRow headRow = sheet.createRow(0);// 建立表头
			for (int colIndex = 0; colIndex < titles.length; colIndex++) {
				createCol(workbook, headRow, colIndex, style, HSSFCell.CELL_TYPE_STRING, titles[colIndex]);
			}
			createCol(workbook, sheet.createRow(1), 0, style,HSSFCell.CELL_TYPE_STRING, "无数据");
		}
	}
	
	public static HSSFWorkbook exportExcel(List<Map<String, Object>> datas,String[] keys,String[] titles,String excelName){
		HSSFWorkbook workbook = null;
		workbook = new HSSFWorkbook();
		 
		if(datas != null && datas.size() > 0){
			double count = Math.ceil(datas.size()/50000.0f);
			Integer countSheet = (int) Math.ceil(datas.size()/50000.0f);
			for (int j = 1; j <= countSheet; j++) {
				HSSFSheet sheet = workbook.createSheet(excelName+j);
				if (datas != null && datas.size() > 0) {
					HSSFRow row = sheet.createRow(0);// 建立表头
					for(int colIndex=0;colIndex<titles.length;colIndex++){
						ExcelUtil.createRow(row,colIndex,null,HSSFCell.CELL_TYPE_STRING,titles[colIndex]);
					}
					if(datas!=null&&datas.size()>0){
						if(datas.size()<50000){
							count =datas.size();
						}else{
							if(countSheet==j && countSheet>1){//计算最后的余数   
								count=datas.size()%50000;
							}else{
								count=50000;
							}
						}
						for (int i = 0; i <count; i++) {
							HSSFRow rowContent = sheet.createRow(i + 1);// 建立新行
							Map<String,Object> content = (Map<String, Object>) datas.get((((j-1)*50000)+i));
							for(int contentColIndex=0;contentColIndex<keys.length;contentColIndex++){
								Object obj = content.get(keys[contentColIndex]);
								
								if(obj instanceof Long || obj instanceof Double || obj instanceof Integer || obj instanceof Float || obj instanceof BigDecimal){
									ExcelUtil.createRow(rowContent, contentColIndex, null, HSSFCell.CELL_TYPE_NUMERIC, obj);
								}else{
									ExcelUtil.createRow(rowContent, contentColIndex, null, HSSFCell.CELL_TYPE_STRING, obj);
								}
							};
						}
					} 
				} 
			}
		}else{
			HSSFSheet sheet = workbook.createSheet(excelName);
			ExcelUtil.createRow(sheet.createRow(1), 0, null,HSSFCell.CELL_TYPE_STRING, "无数据");
		}
		
		return workbook;
	}
	public static void createCol(HSSFWorkbook workbook, HSSFRow row, int column, HSSFCellStyle style,int cellType, Object value){
		HSSFCell cell = row.createCell(column);
		if (style != null) {
			cell.setCellStyle(style);
		}
		switch (cellType) {
			case HSSFCell.CELL_TYPE_BLANK: {
			}
			break;
			case HSSFCell.CELL_TYPE_STRING: {
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				if(null != value){
					cell.setCellValue(value.toString());
				}else{
					cell.setCellValue("");
				}
			}
			break;
			case HSSFCell.CELL_TYPE_NUMERIC: {
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				if(null!=value){
					cell.setCellValue(Double.parseDouble(value.toString()));
				}else{
					cell.setCellValue(0);
				}
			}
			break;
		}
	}
	
	public static void createRow(HSSFRow row, int column, HSSFCellStyle style,int cellType, Object value){
		HSSFCell cell = row.createCell(column);
		if (style != null) {
			cell.setCellStyle(style);
		}
		switch (cellType) {
			case HSSFCell.CELL_TYPE_BLANK: {
			}
			break;
			case HSSFCell.CELL_TYPE_STRING: {
				if(null!=value){
					cell.setCellValue(value.toString());
				}
			}
			break;
			case HSSFCell.CELL_TYPE_NUMERIC: {
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(Double.parseDouble(value.toString()));
			}
			break;
		}
	}
	
	public static void createColXSSF(XSSFWorkbook workbook, XSSFRow row, int column, XSSFCellStyle style,int cellType, Object value){
		XSSFCell cell = row.createCell(column);
		if (style != null) {
			cell.setCellStyle(style);
		}
		switch (cellType) {
			case XSSFCell.CELL_TYPE_BLANK: {
			}
			break;
			case XSSFCell.CELL_TYPE_STRING: {
				cell.setCellType(XSSFCell.CELL_TYPE_STRING);
				if(null != value){
					cell.setCellValue(value.toString());
				}else{
					cell.setCellValue("");
				}
			}
			break;
			case XSSFCell.CELL_TYPE_NUMERIC: {
				cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
				if(null!=value){
					cell.setCellValue(Double.parseDouble(value.toString()));
				}else{
					cell.setCellValue(0);
				}
			}
			break;
		}
	}
	
	public static void createRowXSSF(XSSFRow row, int column, XSSFCellStyle style,int cellType, Object value){
		XSSFCell cell = row.createCell(column);
		if (style != null) {
			cell.setCellStyle(style);
		}
		switch (cellType) {
			case XSSFCell.CELL_TYPE_BLANK: {
			}
			break;
			case XSSFCell.CELL_TYPE_STRING: {
				if(null!=value){
					cell.setCellValue(value.toString());
				}
			}
			break;
			case XSSFCell.CELL_TYPE_NUMERIC: {
				cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(Double.parseDouble(value.toString()));
			}
			break;
		}
	}

	/**
	 * 
	 * @param datas
	 * @param keys
	 * @param titles0 第一列title
	 * @param title0Coordinate 第一列坐标
	 * @param titles1 第二列title
	 * @param monthTitles sheet的月份名称
	 * @param excelName
	 * @return
	 */
	public static XSSFWorkbook exportExcelForLeaveReports(
			Map<String, List<Map<String, Object>>> datas, String[] keys, String[] titles0,
			int[][] title0Coordinate, String[] titles1, String[] monthTitles, String excelName) {

		XSSFWorkbook workbook = null;
		workbook = new XSSFWorkbook();
		List<Map<String, Object>> dataList = null;
		if (datas != null && datas.size() > 0) {
			XSSFSheet sheet = null;
			for(int j=0;j<datas.size()/avg+1;j++){
				for (String month : monthTitles) {
					if(j == 0){
						sheet = workbook.createSheet(month);
					}else{
						sheet = workbook.createSheet(month + "_" + j);
					}
					sheet.createFreezePane(2, 0);
					// 表头标题样式
					XSSFCellStyle headstyle = workbook.createCellStyle();
					Font headFont = workbook.createFont();
					headFont.setFontName("宋体");
					headFont.setFontHeightInPoints((short)12);
					headFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
					headstyle.setFont(headFont);
			        headstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
			        headstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
			        headstyle.setBorderLeft(XSSFCellStyle.BORDER_THICK);//左边框
			        headstyle.setBorderRight(XSSFCellStyle.BORDER_THICK);//右边框
			        headstyle.setLocked(true);
			        
			    	// 表头标题样式
					XSSFCellStyle colstyle = workbook.createCellStyle();
					colstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
					colstyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //下边框
					colstyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框
					colstyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//上边框
					colstyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框
					colstyle.setLocked(true);
					
	            	// 建立表头第一行
	            	XSSFRow row0 = sheet.createRow((short) 0);
	            	for(int colIndex=0;colIndex<titles0.length;colIndex++){
	            		/*headstyle.setBorderColor(BorderSide.TOP, new XSSFColor(Color.blue));
				        headstyle.setBorderColor(BorderSide.LEFT, new XSSFColor(Color.blue));
				        headstyle.setBorderColor(BorderSide.RIGHT, new XSSFColor(Color.blue));
				        headstyle.setBorderColor(BorderSide.BOTTOM, new XSSFColor(Color.blue));*/
	            		ExcelUtil.createColXSSF(workbook, row0, title0Coordinate[colIndex][2], headstyle, XSSFCell.CELL_TYPE_STRING, titles0[colIndex]);
	            	}
	            	//动态合并第一行
	            	CellRangeAddress cra = null;
	            	for(int colIndex=0;colIndex<titles0.length;colIndex++){
	            		cra = new CellRangeAddress(title0Coordinate[colIndex][0], title0Coordinate[colIndex][1], title0Coordinate[colIndex][2], title0Coordinate[colIndex][3]);
	            		sheet.addMergedRegion(cra);
	            	}
	            	
	            	headstyle.setBorderTop(XSSFCellStyle.BORDER_THICK);//上边框
			        headstyle.setBorderBottom(XSSFCellStyle.BORDER_THICK); //下边框
	            	
	            	// 建立表头第二行
	            	XSSFRow row1 = sheet.createRow((short) 1);
	            	for(int colIndex=0; colIndex < titles1.length; colIndex++){
	            		//第三列开始缩小列宽度
        				if(colIndex >= 2){
        					sheet.setColumnWidth(colIndex, 1000);
        				}
	            		ExcelUtil.createColXSSF(workbook, row1, colIndex, headstyle, XSSFCell.CELL_TYPE_STRING, titles1[colIndex]);
	            	}
	            	dataList = datas.get(month);
	            	if(null != dataList){
	            		for (int i = avg*j; i <= (j+1)*avg-1; i++) {
	            			if(i>dataList.size()-1){
	            				continue;
	            			}
	            			XSSFRow rowContent = sheet.createRow((short) (i-j*avg + 2));// 建立新行
	            			Map<String,Object> content = (Map<String, Object>) dataList.get(i);
	            			Object value = null;
	            			for(int contentColIndex=0;contentColIndex<keys.length;contentColIndex++){
	            				value = content.get(keys[contentColIndex]);
	            				if(value != null){
	            					value = value.toString().replace(".0", "");
	            				}
	            				//第三列开始缩小列宽度
	            				if(contentColIndex >= 2){
	            					sheet.setColumnWidth(contentColIndex, 1000);
	            				}
	            				ExcelUtil.createColXSSF(workbook, rowContent, contentColIndex, colstyle, XSSFCell.CELL_TYPE_STRING,value);
	            			}
	            		}
	            	}
	        	
				}
			}
		}else {
			XSSFSheet sheet = workbook.createSheet(excelName);
			ExcelUtil.createColXSSF(workbook, sheet.createRow(1), 0, null,HSSFCell.CELL_TYPE_STRING, "无数据");
		}
		return workbook;
	}

	public static HSSFWorkbook exportExcelForMonths(Map<String, List<Map<String, Object>>> datas, String[] keys, String[] titles,
			String[] monthTitles, String excelName) {
		HSSFWorkbook workbook = null;
		HSSFSheet sheet = null;
		workbook = new HSSFWorkbook();
		List<Map<String, Object>> dataList = null;
		for (String month : monthTitles) {
			sheet = workbook.createSheet(month);
			if (datas != null && datas.size() > 0) {
				HSSFRow row = sheet.createRow((short) 0);// 建立表头
				for(int colIndex=0;colIndex<titles.length;colIndex++){
					ExcelUtil.createRow(row,colIndex,null,HSSFCell.CELL_TYPE_STRING,titles[colIndex]);
				}
				if(datas!=null){
					dataList = datas.get(month);
					if(dataList != null && !dataList.isEmpty()){
						for (int i = 0; i < dataList.size(); i++) {
							HSSFRow rowContent = sheet.createRow((short) (i + 1));// 建立新行
							Map<String, Object> content = (Map<String, Object>) dataList.get(i);
							for(int contentColIndex=0;contentColIndex<keys.length;contentColIndex++){
								ExcelUtil.createRow(rowContent, contentColIndex, null, HSSFCell.CELL_TYPE_STRING,content.get(keys[contentColIndex]));
							};
						}
					}
				} 
			} else {
				ExcelUtil.createRow(sheet.createRow(1), 0, null,HSSFCell.CELL_TYPE_STRING, "无数据");
			}
		}
		return workbook;
	}
	
	/**
	 * excel生成下拉框
	 * @param sheet				sheet页
	 * @param startRow			起始行
	 * @param endRow			终止行
	 * @param startColumn		起始列
	 * @param endColumn			终止列
	 * @param dateList			下拉框内容
	 */
	public static void creatComoboxRow(HSSFSheet sheet,int startRow, int endRow, int startColumn,int endColumn, String[] dateArray) {
		CellRangeAddressList comoboxRow = new CellRangeAddressList(startRow, endRow, startColumn, endColumn);//参数：起始行，终止行，起始列，终止列
        // 创建下拉列表数据
        DVConstraint comobox = DVConstraint.createExplicitListConstraint(dateArray);
        // 绑定下拉框
        HSSFDataValidation dataValidation = new HSSFDataValidation(comoboxRow, comobox);
        sheet.addValidationData(dataValidation);
	}
}