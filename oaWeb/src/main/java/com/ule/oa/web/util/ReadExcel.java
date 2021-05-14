package com.ule.oa.web.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ReadExcel {
    private String filePath;
    private String sheetName;
    private Workbook workBook;    
    private Sheet sheet;
    private List<String> columnHeaderList;
    private static List<List<String>> listData;
    private List<Map<String,String>> mapData;
    private boolean flag;

    public ReadExcel(String filePath, String sheetName) {
        this.filePath = filePath;
        this.sheetName = sheetName;
        this.flag = false;
        this.load();
    }    

    private void load() {
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(new File(filePath));
            workBook = WorkbookFactory.create(inStream);
            sheet = workBook.getSheet(sheetName);            
        } catch (Exception e) {
        }finally{
            try {
                if(inStream!=null){
                    inStream.close();
                }                
            } catch (IOException e) {                
            }
        }
    }

    private String getCellValue(Cell cell) {
        String cellValue = "";
        DataFormatter formatter = new DataFormatter();
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        cellValue = formatter.formatCellValue(cell);
                    } else {
                        double value = cell.getNumericCellValue();
                        int intValue = (int) value;
                        cellValue = value - intValue == 0 ? String.valueOf(intValue) : String.valueOf(value);
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    cellValue = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    cellValue = String.valueOf(cell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    cellValue = String.valueOf(cell.getCellFormula());
                    break;
                case Cell.CELL_TYPE_BLANK:
                    cellValue = "";
                    break;
                case Cell.CELL_TYPE_ERROR:
                    cellValue = "";
                    break;
                default:
                    cellValue = cell.toString().trim();
                    break;
            }
        }
        return cellValue.trim();
    }

    private void getSheetData() {
        listData = new ArrayList<List<String>>();
        mapData = new ArrayList<Map<String, String>>();    
        columnHeaderList = new ArrayList<String>();
        int numOfRows = sheet.getLastRowNum() + 1;
        for (int i = 0; i < numOfRows; i++) {
            Row row = sheet.getRow(i);
            Map<String, String> map = new HashMap<String, String>();
            List<String> list = new ArrayList<String>();
            if (row != null) {
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    if (i == 0){
                        columnHeaderList.add(getCellValue(cell));
                    }
                    else{                        
                        map.put(columnHeaderList.get(j), this.getCellValue(cell));
                    }
                    list.add(this.getCellValue(cell));
                }
            }
            if (i > 0){
                mapData.add(map);
            }
            listData.add(list);
        }
        flag = true;
    }
    
    public String getCellData(int row, int col){
        if(row<=0 || col<=0){
            return null;
        }
        if(!flag){
            this.getSheetData();
        }        
        if(listData.size()>=row && listData.get(row-1).size()>=col){
            return listData.get(row-1).get(col-1);
        }else{
            return null;
        }
    }
    
    public String getCellData(int row, String headerName){
        if(row<=0){
            return null;
        }
        if(!flag){
            this.getSheetData();
        }        
        if(mapData.size()>=row && mapData.get(row-1).containsKey(headerName)){
            return mapData.get(row-1).get(headerName);
        }else{
            return null;
        }
    }

    public static void clearn(String fileName) {
    	FileWriter fw = null;
		try {
			// 如果文件存在，则追加内容；如果文件不存在，则创建文件
			File f = new File("C:\\"+fileName+".txt");
			fw = new FileWriter(f);
			fw.write("");
			fw.close();
		} catch (IOException e) {

		}finally {
			if(fw!=null) {
				try {
					fw.close();
				} catch (IOException e) {
				}
			}
		}
    }
	public static void writeTxtFile(String content,String fileName) {
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			// 如果文件存在，则追加内容；如果文件不存在，则创建文件
			File f = new File("C:\\"+fileName+".txt");
			fw = new FileWriter(f, true);
			pw = new PrintWriter(fw);
		} catch (IOException e) {

		}finally {
			if(fw!=null) {
				try {
					fw.close();
				} catch (IOException e) {
				}
			}
			if(pw!=null) {
				pw.close();
			}
		}
	}
    //初始化员工信息
	public void getEmployeeSQL() {
    	ReadExcel eh = new ReadExcel("D:\\MO项目相关文件\\导入数据excel\\初始化员工信息 20180501.xlsx","Sheet2");
    	eh.getSheetData();//获得数据
    	String fileName = "导入员工";
    	clearn(fileName);
    	writeTxtFile("INSERT INTO base_employee (company_id,emp_type_id,code,cn_name,eng_name,first_Entry_time,sex,work_type,whether_scheduling,email,del_flag,create_time,create_user) values",fileName);
    	String sql = "";
    	String sex = "0";
    	String workType = "170";
    	for(int i = 1;i<listData.size();i++){
    		if("女".equals(listData.get(i).get(13))) {
    			sex = "1";
    		}
    		if("标准工时".equals(listData.get(i).get(16))) {
    			workType="169";
    		}
    		sql="( 1,13,'"+listData.get(i).get(2)+"','"+listData.get(i).get(3)+"','"+listData.get(i).get(4)+"',DATE_FORMAT('"+listData.get(i).get(5)+"','%Y-%m-%d'),"+sex+","+workType+",488,'"+listData.get(i).get(18)+"',0,SYSDATE(),'system'), ";
			System.out.println(sql);
    		writeTxtFile(sql,fileName);
    	}
    }
    //修改员工汇报对象
    public static void updateEmpLeader(){
    	ReadExcel eh = new ReadExcel("D:\\MO项目相关文件\\导入数据excel\\初始化员工信息 20180501.xlsx","Sheet2");
    	eh.getSheetData();//获得数据
    	String sql = "";
    	String fileName = "修改上级";
    	clearn(fileName);
    	for(int i = 1;i<listData.size();i++){
    		sql="update base_employee be set be.report_to_leader =(  select tmp.id from (select * from base_employee) tmp where tmp.cn_name = '"+listData.get(i).get(10)+"'  ) where be.code = '"+listData.get(i).get(2)+"' ;";
			System.out.println(sql);
    		writeTxtFile(sql,fileName);
    	}
    }
    //创建员工部门信息
    public static void insertEmp_depart() {
    	//insert into base_emp_depart(employee_id,depart_id,create_time,create_user,update_time,update_user,del_flag,version) select (select id from base_employee where cn_name = '�����'),(select id from base_depart where name = '�����칫��'),NOW(),'system',NOW(),'system',0,0;
    	ReadExcel eh = new ReadExcel("D:\\MO项目相关文件\\导入数据excel\\初始化员工信息 20180501.xlsx","Sheet2");
    	eh.getSheetData();//获得数据
    	String sql = "";
    	String fileName = "初始化员工部门信息";
    	clearn(fileName);
    	writeTxtFile("insert into base_emp_depart(employee_id,depart_id,create_time,create_user,update_time,update_user,del_flag,version) values ", fileName);
    	for(int i = 1;i<listData.size();i++){
    		sql="( (select id from base_employee where code = '"+listData.get(i).get(2)+"'),(select id from base_depart where name = '"+listData.get(i).get(8)+"'),NOW(),'system',NOW(),'system',0,0),";
			System.out.println(sql);
    		writeTxtFile(sql,fileName);
    	}
    }
    //创建员工职位信息
    public static void insertEmp_position() {
    	//insert into base_emp_position(employee_id,position_id,create_time,create_user,update_time,update_user,del_flag,version) select (select id from base_employee where cn_name = '�����'),(select id from base_position where position_name = '��Ӫ�ܲ�'),NOW(),'system',NOW(),'system',0,0;
    	ReadExcel eh = new ReadExcel("D:\\MO项目相关文件\\导入数据excel\\初始化员工信息 20180501.xlsx","Sheet2");
    	eh.getSheetData();//获得数据
    	String fileName = "初始化员工职位信息";
    	clearn(fileName);
    	writeTxtFile("insert into base_emp_position(employee_id,position_id,create_time,create_user,update_time,update_user,del_flag,version) values ", fileName);
    	String sql = "";
    	for(int i = 1;i<listData.size();i++){
    		//insert into base_emp_leave(company_id,employee_id,year,type,allow_days,used_days,allow_remain_days,parend_id,start_time,end_time,is_active,del_flag,create_time,update_time,create_user,update_user)  select (select id from base_company where code = 'ULE'),(select id from base_employee where code ='"++"'),'2017',2,'5','0','5',0,'2017-01-01 00:00:00','2017-12-31 23:59:59',0,0,now(),now(),'system','system';
    		sql=  "( (select id from base_employee where code = '"+listData.get(i).get(2)+"'),(select id from base_position where position_name = '"+listData.get(i).get(12)+"'),NOW(),'system',NOW(),'system',0,0 ) ,";
			System.out.println(sql);
    		writeTxtFile(sql,fileName);
    	}
    }
    public static void main(String[] args) {
    	
	}
}
