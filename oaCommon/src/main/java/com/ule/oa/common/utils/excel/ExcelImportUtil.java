package com.ule.oa.common.utils.excel;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.ule.oa.common.utils.ConfigConstants;

/**
 * @author : richard
 * @description :excel 注解导入工具类
 * @date :create in 2018/11/1
 */
public class ExcelImportUtil {

    //转换中统一日期格式
    private static final String DATEFORMAT="yyyy-MM-dd";


    /***
     * @description: 转换的主入口方法
     * @param clazz  对象
     * @param inputStream 文件输入流
     * @param sheetNum sheet序号
     * @param headNum  标题头所在的行
     * @return : bean的集合
    */
    public static List<Object> excelToBean(Class clazz,InputStream inputStream,int sheetNum,int headNum){
        //得到excel中的数据
        List<Map<String, String>> maps = transformExcelToList(inputStream, sheetNum, headNum);
        //转换到bean
        return  listToBean(clazz, maps);
    }

    /***
     * @description: excel数据转到List<Map<>>
     * @param inputStream 文件输入流
     * @param sheetNum sheet序号
     * @param headNum  标题头所在的行
     * @return : <标题名,内容> --> <属性名,属性值>
     */
    private static List<Map<String,String>> transformExcelToList(InputStream inputStream,int sheetNum,int headNum){

        //返回数据的格式
        List<Map<String,String>> resultList=new ArrayList<>();
        try {
            Workbook workbook = WorkbookFactory.create(inputStream);
            //得到指定的工作表
            Sheet sheet = workbook.getSheetAt(sheetNum);
            //得到excel的行数
            int totalRows = sheet.getPhysicalNumberOfRows();
            //得到excel的列数
            int totalCols=0;
            //用标题行来进行判断总列数
            Row headRow=sheet.getRow(headNum);
            if (totalRows>=1&&headRow!=null){
                totalCols=headRow.getPhysicalNumberOfCells();
            }

            //循环第一列 将标题放入list中，之后拼接成对应的返回格式
            List<String> headList=new ArrayList<>();
            for (int i=0;i<totalCols;i++){
                Cell cell = headRow.getCell(i);
                String value = getCellStringValue(cell);
                headList.add(value);
            }

            //循环取出数据 标题行的下一行是数据行 组装<标题名称，内容> 一行是一个map
            for (int i=headNum+1;i<totalRows;i++){
                Map<String,String> map=new HashMap<>();
                Row row = sheet.getRow(i);
                for (int j=0;j<totalCols;j++){
                	if(row!=null){
                		  Cell cell = row.getCell(j);
                          //如果是空单元格
                          if (cell==null){
                              map.put(headList.get(j),"");
                          }else {
                              String value = getCellStringValue(cell);
                              map.put(headList.get(j),value);
                          }
                	}
                }
               //第一列数据为空，默认没有该条数据，不计入总数
                if(StringUtils.isNotBlank(map.get(headList.get(0)))){
                	resultList.add(map);
                }
            }
        } catch (IOException e) {

        } catch (InvalidFormatException e) {

        }
        return resultList;
    }


    /***
     * @description: excel的List<Map<>>数据转换到对应的bean集合中
     * @param clazz bean
     * @param nameAndObjectMap excel中得到的数据类型
     * @return : java.util.List<java.lang.Object>
     */
    private static List<Object> listToBean(Class clazz, List<Map<String,String>> nameAndObjectMap){

        //返回的beanList
        List<Object> list=new ArrayList<>();

        HashMap<String, List<Object>> fieldAndMethodMap = getMethodsByReflect(clazz);

        if (nameAndObjectMap==null){
            return null;
        }else {
            //对excel中得到的数据进行循环
            for (Map<String,String> map:nameAndObjectMap){
                try {
                    Object instance = clazz.newInstance();
                    //对excel中每一行数据进行循环
                    for (Map.Entry<String, String> entry : map.entrySet()){
                        //看传入对象的属性中是否存在 如果不存在则代表该属性不存在或者不需要转换 注解名称单词均用小写
                        List typeAndMethodList=fieldAndMethodMap.get(entry.getKey().toLowerCase());
                        if (typeAndMethodList!=null){
                            //取出对应的属性
                            Type type=(Type) typeAndMethodList.get(0);
                            //取出对应的方法
                            Method method=(Method)typeAndMethodList.get(1);
                            if (method!=null){
                                invokePlus(method,instance,type,entry.getValue());
                            }
                        }
                    }
                    list.add(instance);
                } catch (InstantiationException e) {

                } catch (IllegalAccessException e) {

                }
            }
        }
        return list;
    }


    /***
     * @description: 反射获取属性名称、属性类型和set方法
     * @param clazz 需要转换的类
     * @return : java.util.HashMap<java.lang.String,java.util.List<java.lang.Object>>
     */
    private static HashMap<String,List<Object>> getMethodsByReflect(Class  clazz){
        //属性名字和对应的set方法
        HashMap<String,List<Object>> fieldAndMethodMap=new HashMap<>();

        //得到所有方法名称
        Field[] fields = clazz.getDeclaredFields();

        //得到方法注解名称
        for (Field field:fields){
            if (field.isAnnotationPresent(Excel.class)){
                //得到注解
                Excel excel = field.getAnnotation(Excel.class);
                //根据属性得到注解方法
                try {
                    PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                    //新建list 用于存放属性类型和属性set方法
                    Class<?> type = field.getType();
                    List<Object> list=new ArrayList<>();
                    list.add(type);
                    list.add(pd.getWriteMethod());
                    fieldAndMethodMap.put(excel.name(),list);
                } catch (IntrospectionException e) {
                }
            }
        }

        return fieldAndMethodMap;
    }


    /***
     * @description: excel 单元格值转换为 String
     * @param cell   单元格
     * @return : 所有单元格的数据都用String类型返回
     */
    private static String getCellStringValue(Cell cell) {
        String cellValue = "";
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_NUMERIC: // 数字
                //如果为时间格式的内容
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
                    cellValue=sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
                    break;
                } else {
                    Double dvalue=cell.getNumericCellValue();
                    if (dvalue%1==0){
                        cellValue=String.valueOf(dvalue.intValue());
                    }else {
                        cellValue = String.valueOf(dvalue);
                    }

                }
                break;
            case HSSFCell.CELL_TYPE_STRING: // 字符串
                cellValue = cell.getStringCellValue();
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case HSSFCell.CELL_TYPE_FORMULA: // 公式
                cellValue = cell.getCellFormula();
                break;
            case HSSFCell.CELL_TYPE_BLANK: // 空值
                break;
            case HSSFCell.CELL_TYPE_ERROR: // 故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        String resultValue = replaceBlank(cellValue);
        return resultValue;
    }



    /***
      * @description: 根据类型set值
      * @param method 方法名称
      * @param object 对象
      * @param type   待赋值属性的类型
      * @param value  待赋值属性的值
      * @return : void
    */
    private static void invokePlus(Method method,Object object,Type type,String value){
        try {
            Object result=null;
            String typeName=type.getTypeName();
            if (typeName.equals(String.class.getTypeName())){
                result=String.valueOf(value);
            }else if ("".equals(value)){
                //result默认为null 如果不是String类型并且value还为""，就认为值为null
            }
            else if (typeName.equals(int.class.getTypeName())){
                //cell为0时候被转换为0.0,所以需要反转换
                result=Integer.parseInt(value);
            }else if (typeName.equals(Integer.class.getTypeName())){
                result=Integer.parseInt(value);
            }else if (typeName.equals(Long.class.getTypeName())){
                result=Long.parseLong(value);
            }else if (typeName.equals(Double.class.getTypeName())){
                result=Double.parseDouble(value);
            }else if (typeName.equals(Boolean.class.getTypeName())){
                result=Boolean.parseBoolean(value);
            }else if (typeName.equals(Float.class.getTypeName())){
                result=Float.parseFloat(value);
            }else if (typeName.equals(Date.class.getTypeName())){
                //cell 时间 为空时候，需要转换 为空传null
                SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
                result = sdf.parse(value);

            }
            method.invoke(object,result);
        } catch (IllegalAccessException e) {

        } catch (InvocationTargetException e) {

        } catch (ParseException e) {

        }
    }



    /***
     * @description: 校验传入的excel 模板是否正确  校验行数和每一列的值
     * @param inputStream 文件输入流
     * @param sheetNum sheet序号
     * @param headNum  标题头所在的行
     * @param templateTitles  模板头字段集合 由平台定义
     * @return : boolean
     */
    public static boolean checkTemplate(InputStream inputStream,int sheetNum,int headNum,String[] templateTitles){

        try {
            Workbook workbook = WorkbookFactory.create(inputStream);
            //得到指定的工作表
            Sheet sheet = workbook.getSheetAt(sheetNum);
            //得到excel的行数
            int totalRows = sheet.getPhysicalNumberOfRows();
            //得到excel的列数
            int totalCols=0;
            //用标题行来进行判断总列数
            Row headRow=sheet.getRow(headNum);
            if (totalRows>=1&&headRow!=null){
                totalCols=headRow.getPhysicalNumberOfCells();
            }

            //循环第一列 将标题放入list中
            Set<String> uploadTitleSet=new HashSet<>();
            for (int i=0;i<totalCols;i++){
                Cell cell = headRow.getCell(i);
                String value = getCellStringValue(cell);
                //全用小写
                uploadTitleSet.add(value.toLowerCase());
            }

            Set<String> templateTitleSet=new HashSet<>(Arrays.asList(templateTitles));

            templateTitleSet.removeAll(uploadTitleSet);

            if (templateTitleSet.size()==0){
                return true;
            }else {
                return false;
            }

        } catch (IOException e) {

        } catch (InvalidFormatException e) {

        }
        return true;
    }

    public static List<Map<String,List<Map<String,String>>>> transformExcelsToList(InputStream inputStream){
    	try {
			Workbook workbook = WorkbookFactory.create(inputStream);
			// 获取工作表总数
			int activeSheetIndex = workbook.getNumberOfSheets();
			
			// 封装Excel数据
			List<Map<String,List<Map<String,String>>>> resultList=new ArrayList<>();
			for (int i = 0; i < activeSheetIndex; i++) {
				Map<String, List<Map<String,String>>> sheetMap = new HashMap<>();
				// 得到指定的工作表
	            Sheet sheet = workbook.getSheetAt(i);
	            String sheetName = sheet.getSheetName();
	            // 得到excel的行数
	            int totalRows = sheet.getPhysicalNumberOfRows();
	            // 得到excel的列数
	            int totalCols=0;
	            // 用标题行来进行判断总列数
	            Row headRow = sheet.getRow(0);
	            if (totalRows >=1 && headRow!=null){
	                totalCols=headRow.getPhysicalNumberOfCells();
	            }
	            
	            //循环第一列 将标题放入list中，之后拼接成对应的返回格式
	            List<String> headList=new ArrayList<>();
	            for (int j = 1; j < totalCols; j++){
	                Cell cell = headRow.getCell(j);
	                String value = getCellStringValue(cell);
	                headList.add(value);
	            }
	          
	            List<Map<String, String>> sheetList = new ArrayList<>();
	            //循环取出数据 标题行的下一行是数据行 组装<标题名称，内容> 一行是一个map
	            for (int k = 1; k < totalRows; k++){
	            	Map<String,String> map=new HashMap<>();
	                Row row = sheet.getRow(k);
	                if("".equals(getCellStringValue(row.getCell(1))) || "".equals(getCellStringValue(row.getCell(4)))) {
	                	continue;
	                }
	                for (int j = 1; j < totalCols; j++){
	                    Cell cell = row.getCell(j);
	                    if (cell==null){
	                        map.put(headList.get(j),"");
	                    }else {
	                        String value = getCellStringValue(cell);
	                        if(j == 1) {
	                        	map.put("QYTBDM",value);
	                        }else if(j == 2) {
	                        	map.put("YPNBBM",value);
	                        }else if(j == 3) {
	                        	map.put("YPMC",value);
	                        }else if(j == 4) {
	                        	map.put("JL",value);
	                        }
	                    }
	                }
	                sheetList.add(map);
	            }
	            
	            sheetMap.put(sheetName, sheetList);
	            resultList.add(sheetMap);
			}
			
//			System.out.println(resultList.toString()); 
            return resultList;
		} catch (Exception e) {
		}
    	return null;
    }
    
    /**
     * 去除空格
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            //空格\t、回车\n、换行符\r、制表符\t
            Matcher m = ConfigConstants.REPLACE_BLACK_PATTERN.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

}
