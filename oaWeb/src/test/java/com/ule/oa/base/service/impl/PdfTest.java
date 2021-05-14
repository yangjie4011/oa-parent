package com.ule.oa.base.service.impl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font.FontStyle;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.ule.oa.common.utils.PDFUtils;
import com.ule.oa.web.service.impl.BaseServiceTest;

public class PdfTest extends BaseServiceTest{
	/**
	 * @throws DocumentException 
	 * @throws FileNotFoundException 
	  * testWriteApplyResult(这里用一句话描述这个方法的作用)
	  * @Title: testWriteApplyResult
	  * @Description: 生成出差总结报告
	  * void    返回类型
	  * @throws
	 */
//	@Ignore
	@Test
	public void testWriteApplyResult() throws FileNotFoundException, DocumentException{
		//创建文件
		Document document = new Document();
		//创建一个书写器
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("C:/Users/minsheng/pdf/出差总结报告.pdf"));
		
		//打开文件
		document.open();
			
		try{
			//添加表头
			document.add(PDFUtils.setParagraph("出差总结报告", 15, FontStyle.BOLD.getValue(),PDFUtils.ALIGN_CENTER));
			
			//建表(6列)
			PdfPTable table = PDFUtils.createTable(6,100,10,0);
			
			//获得表格中的列
			List<PdfPRow> listRow = table.getRows();
			//设置列宽
			float[] columnWidths = { 3f, 3f, 3f, 3f ,3f ,3f};
			table.setWidths(columnWidths);
	
			//第一行
			PdfPCell cell = null;
			PdfPCell cells[] = new PdfPCell[6];
			PdfPRow row = new PdfPRow(cells);
			
			//第一行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph("姓    名", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));//单元格内容
			cells[1] = new PdfPCell(PDFUtils.setParagraph("闵胜", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[2] = new PdfPCell(PDFUtils.setParagraph("职务", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[3] = new PdfPCell(PDFUtils.setParagraph("软件工程师", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[4] = new PdfPCell(PDFUtils.setParagraph("出差地点", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[5] = new PdfPCell(PDFUtils.setParagraph("非洲", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			listRow.add(row);
			
			//第二行
			cells = new PdfPCell[6];
			row = new PdfPRow(cells);
			
			//第二行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph("出差时长", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));//单元格内容
			cells[1] = new PdfPCell(PDFUtils.setParagraph("3天", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[1].setColspan(2);
			cells[3] = new PdfPCell(PDFUtils.setParagraph("总结日期", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[4] = new PdfPCell(PDFUtils.setParagraph("2017-10-20", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[4].setColspan(2);
			listRow.add(row);
			
			//第三行
			cells = new PdfPCell[6];
			row = new PdfPRow(cells);
			
			//第三行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph("出差事由", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));//单元格内容
			cells[1] = new PdfPCell();
			cells[1].setColspan(5);
			listRow.add(row);
			
			//第四行
			cells = new PdfPCell[6];
			row = new PdfPRow(cells);
			
			//第四行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph("出差每日行程及任务完成情况:(可附页)", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));//单元格内容
			cells[0].setColspan(6);
			listRow.add(row);
			
			//第五行
			cells = new PdfPCell[6];
			row = new PdfPRow(cells);
			
			//第五行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph("时间段", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[1] = new PdfPCell(PDFUtils.setParagraph("工作内容及任务完成情况", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[1].setColspan(4);
			cells[5] = new PdfPCell(PDFUtils.setParagraph("备注", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			listRow.add(row);
			
			//第六行
			cells = new PdfPCell[6];
			row = new PdfPRow(cells);
			
			//第六行单元格
			int count = 0;
			int index = 0;
			int whiteCount = 5;//空白行
			while(count < 3){
				cells[index] = new PdfPCell();
				PdfPTable tItemTable= PDFUtils.createTable(1,100,0,0);
				cells[index].addElement(tItemTable);//将表格嵌套到单元格中
				
				String str = "";
				if(count == 0){str = "aaa";}else if(count == 1){str="bbb";}else{str = "ccc";}
				while(whiteCount > 0){
					cell = new PdfPCell(PDFUtils.setParagraph(str, 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));
					cell.setBorder(0);
					tItemTable.addCell(cell);
					whiteCount--;
				}
				whiteCount = 5;
				
				if(count == 0){//第一个单元格跨1行
					cells[index].setColspan(1);
					index++;
				}else if(count == 1){//第二个单元格跨4行
					cells[index].setColspan(4);
					index += 4;
				}else if(count == 2){//第三个单元格跨1行
					cells[index].setColspan(1);
				}
				count++;
			}
			listRow.add(row);
			
			//第七行
			cells = new PdfPCell[6];
			row = new PdfPRow(cells);
			
			//第七行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph("实际总费用", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[1] = new PdfPCell(PDFUtils.setParagraph(" ", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[1].setColspan(2);
			cells[3] = new PdfPCell(PDFUtils.setParagraph("超预算金额", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[3].setColspan(2);
			cells[5] = new PdfPCell(PDFUtils.setParagraph(" ", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			listRow.add(row);
			
			//第八行
			cells = new PdfPCell[6];
			row = new PdfPRow(cells);
			
			//第八行单元格
			whiteCount = 3;//空白行
			cells[0] = new PdfPCell();
			PdfPTable tItemTable= PDFUtils.createTable(1,100,0,0);
			cells[0].addElement(tItemTable);//将表格嵌套到单元格中
			cells[0].setColspan(6);
			//空白行
			String tmpStr = "";
			while(whiteCount > 0){
				if(whiteCount == 3){tmpStr = "超支说明:";}else{tmpStr = " ";}
				cell = new PdfPCell(PDFUtils.setParagraph(tmpStr, 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));
				cell.setBorder(0);
				tItemTable.addCell(cell);
				whiteCount--;
			}
			listRow.add(row);
			
			//第九行
			cells = new PdfPCell[6];
			row = new PdfPRow(cells);
			
			//第九行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph("报 告 人", 8, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[0].setColspan(2);
			cells[2] = new PdfPCell(PDFUtils.setParagraph("部门主管审批", 8, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[2].setColspan(2);
			cells[4] = new PdfPCell(PDFUtils.setParagraph("运营总裁审批", 8, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[4].setColspan(2);
			listRow.add(row);
			
			//第十行
			cells = new PdfPCell[6];
			row = new PdfPRow(cells);
			
			//第十行单元格
			count = 0;//第几个单元格
			index = 0;//单元格下标
			while(count <= 2){
				cells[index] = new PdfPCell();
				tItemTable= PDFUtils.createTable(1,100,0,0);
				cells[index].addElement(tItemTable);//将表格嵌套到单元格中
				cell = new PdfPCell(PDFUtils.setParagraph("签字:", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
				cell.setBorder(0);
				tItemTable.addCell(cell);
				//增加空行
				whiteCount = 3;
				while(whiteCount > 0){
					cell = new PdfPCell(PDFUtils.setParagraph(" ", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
					cell.setBorder(0);
					whiteCount --;
					tItemTable.addCell(cell);
				}
				cell = new PdfPCell(PDFUtils.setParagraph("日期:", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
				cell.setBorder(0);
				tItemTable.addCell(cell);
				cells[index].setColspan(2);

				index += 2;
				count++;
			}
			listRow.add(row);
			
			//第十一行
			cells = new PdfPCell[6];
			row = new PdfPRow(cells);
			
			//第十一行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph(" ", 8, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[0].setColspan(6);
			listRow.add(row);
			
			//把表格添加到文件中
			document.add(table);
			//生成二维码
			document.add(Image.getInstance("C:\\test.png"));
		}catch(Exception e){

		}finally{
			//关闭文档
			document.close();
			//关闭书写器
			writer.close();
		}
	}
	
	
	
	/**
	 * @throws IOException 
	 * @throws DocumentException 
	 * @throws FileNotFoundException 
	  * testWriteApplyPdf(生成出差申请pdf文件)
	  * @Title: testWriteApplyPdf
	  * @Description: 生成pdf文件
	  * void    返回类型
	  * @throws
	 */
	@Ignore
	@Test
	public void testWriteApplyPdf() throws DocumentException, IOException{
		//创建文件
		Document document = new Document();
		//创建一个书写器
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("C:/Users/minsheng/pdf/出差申请表.pdf"));
		//打开文件
		document.open();
		
		try{
			//添加表头
			document.add(PDFUtils.setParagraph("出差申请表", 15, FontStyle.BOLD.getValue(),PDFUtils.ALIGN_CENTER));
			
			//建表(10列)
			PdfPTable table = PDFUtils.createTable(10,100,10,0); 

			//获得表格中的列
			List<PdfPRow> listRow = table.getRows();
			//设置列宽
			float[] columnWidths = { 3f, 3f, 3f, 3f ,3f ,3f ,3f ,3f ,3f ,3f };
			table.setWidths(columnWidths);
	
			//第一行
			PdfPCell cells[] = new PdfPCell[10];
			PdfPRow row = new PdfPRow(cells);
	
			//第一行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph("姓    名", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));//单元格内容
			cells[0].setColspan(2);
			cells[2] = new PdfPCell(PDFUtils.setParagraph("闵胜", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[3] = new PdfPCell(PDFUtils.setParagraph("公司/部门", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[3].setColspan(2);
			cells[5] = new PdfPCell(PDFUtils.setParagraph("邮乐/技术中心", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[5].setColspan(2);
			cells[7] = new PdfPCell(PDFUtils.setParagraph("职    务", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[7].setColspan(2);
			cells[9] = new PdfPCell(PDFUtils.setParagraph("软件工程师", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			listRow.add(row);
	
			//第二行
			cells = new PdfPCell[10];
			row = new PdfPRow(cells);
			
			//第二行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph("出发时间", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[0].setColspan(2);
			cells[2] = new PdfPCell(PDFUtils.setParagraph("2017-10-01", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[3] = new PdfPCell(PDFUtils.setParagraph("出差地点", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_RIGHT));
			cells[3].setColspan(2);
			cells[5] = new PdfPCell(PDFUtils.setParagraph("非洲", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[5].setColspan(2);
			cells[7] = new PdfPCell(PDFUtils.setParagraph("交通工具", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[7].setColspan(2);
			cells[9] = new PdfPCell(PDFUtils.setParagraph("飞机", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			listRow.add(row);
			
			//第三行
			cells = new PdfPCell[10];
			cells[0] = new PdfPCell();
			cells[0].setColspan(10);
			row = new PdfPRow(cells);
			PdfPTable tItemTable= PDFUtils.createTable(1,100,0,0);
			cells[0].addElement(tItemTable);//将表格嵌套到单元格中
			
			//第三行第一个单元格
			PdfPCell cell = new PdfPCell();
			cell = new PdfPCell(PDFUtils.setParagraph("1、出差事由：□项目出差(项目名称： ______)□业务出差(业务类型：______)", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cell.setBorder(0);
			tItemTable.addCell(cell);
			
			//第三行第二个单元格
			cell = new PdfPCell();
			cell = new PdfPCell(PDFUtils.setParagraph("2、出差周期：", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cell.setBorder(0);
			tItemTable.addCell(cell);
			
			//第三行第三个单元格
			int restDay = 3;//假设休息天数为3天
			cell = new PdfPCell();
			cell = new PdfPCell(PDFUtils.setParagraph("       从 ______年____月____日至______ 年____月____日____时____分；大致共计___"+restDay+"__天", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cell.setBorder(0);
			tItemTable.addCell(cell);
			
			//第三行第四个单元格
			cell = new PdfPCell();
			cell = new PdfPCell(PDFUtils.setParagraph("3、出差每日行程及工作计划安排（可附页）：", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cell.setBorder(0);
			tItemTable.addCell(cell);
			listRow.add(row);
			
			//第四行
			cells = new PdfPCell[10];
			row = new PdfPRow(cells);
	
			//第四行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph("日期", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[0].setColspan(2);
			cells[2] = new PdfPCell(PDFUtils.setParagraph("工作计划", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[2].setColspan(5);
			cells[7] = new PdfPCell(PDFUtils.setParagraph("工作目标", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[7].setColspan(3);
			listRow.add(row);
			
			//第五行
			while(restDay > 0){
				cells = new PdfPCell[10];
				row = new PdfPRow(cells);
		
				//第五行单元格
				cells[0] = new PdfPCell(PDFUtils.setParagraph("2017-10-0"+restDay+"", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
				cells[0].setColspan(2);
				cells[2] = new PdfPCell(PDFUtils.setParagraph("挖石油", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
				cells[2].setColspan(5);
				cells[7] = new PdfPCell(PDFUtils.setParagraph("挖了"+restDay+"吨", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
				cells[7].setColspan(3);
				listRow.add(row);
				restDay -- ;
			}
			
			//第六行
			cells = new PdfPCell[10];
			row = new PdfPRow(cells);
			
			//第六行单元格
			//单元格1
			cells[0] = new PdfPCell();
			cells[0].setColspan(4);//第六行第一个单元格跨3行
			tItemTable= PDFUtils.createTable(1,100,0,0);
			cells[0].addElement(tItemTable);//将表格嵌套到单元格中
			cell = new PdfPCell();
			cell = new PdfPCell(PDFUtils.setParagraph("费用预算项目:", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cell.setBorder(0);
			tItemTable.addCell(cell);
			//增加空行
			int whiteCount = 3;
			while(whiteCount > 0){
				cell = new PdfPCell(PDFUtils.setParagraph(" ", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
				cell.setBorder(0);
				whiteCount --;
				tItemTable.addCell(cell);
			}
			
			//单元格2
			cells[4] = new PdfPCell(PDFUtils.setParagraph("借款金额：（大写）", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[4].setColspan(2);//第六行第二个单元格跨3行
			//单元格3
			cells[6] = new PdfPCell(PDFUtils.setParagraph("", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[6].setColspan(4);//第六行第三个单元格跨3行
			listRow.add(row);
			
			//第七行
			cells = new PdfPCell[10];
			row = new PdfPRow(cells);
			
			//第七行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph("申 请 人", 8, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_LEFT));//单元格内容
			cells[0].setColspan(2);
			cells[2] = new PdfPCell(PDFUtils.setParagraph("部门主管审批", 8, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_LEFT));//单元格内容
			cells[2].setColspan(2);
			cells[4] = new PdfPCell(PDFUtils.setParagraph("人力资源及行政部审批", 8, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_LEFT));//单元格内容
			cells[4].setColspan(2);
			cells[6] = new PdfPCell(PDFUtils.setParagraph("财务部审批", 8, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_LEFT));//单元格内容
			cells[6].setColspan(2);
			cells[8] = new PdfPCell(PDFUtils.setParagraph("运营总裁审批", 8, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_LEFT));//单元格内容
			cells[8].setColspan(2);
			listRow.add(row);
			
			//第八行
			cells = new PdfPCell[10];
			row = new PdfPRow(cells);
			
			//第八行单元格
			int count = 0;//第几个单元格
			int index = 0;//单元格下标
			while(count <= 4){
				cells[index] = new PdfPCell();
				tItemTable= PDFUtils.createTable(1,100,0,0);
				cells[index].addElement(tItemTable);//将表格嵌套到单元格中
				cell = new PdfPCell(PDFUtils.setParagraph("签字:", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
				cell.setBorder(0);
				tItemTable.addCell(cell);
				//增加空行
				whiteCount = 3;
				while(whiteCount > 0){
					cell = new PdfPCell(PDFUtils.setParagraph(" ", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
					cell.setBorder(0);
					whiteCount --;
					tItemTable.addCell(cell);
				}
				cell = new PdfPCell(PDFUtils.setParagraph("日期:", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
				cell.setBorder(0);
				tItemTable.addCell(cell);
				cells[index].setColspan(2);

				index += 2;
				count++;
			}
			listRow.add(row);
			
			
			//第九行
			cells = new PdfPCell[10];
			row = new PdfPRow(cells);
			
			//第九行单元格
			count = 0;//第几个单元格
			index = 0;//单元格下标
			while(count < 2){
				cells[index] = new PdfPCell();
				tItemTable= PDFUtils.createTable(1,100,0,0);
				cells[index].addElement(tItemTable);//将表格嵌套到单元格中
				cell = new PdfPCell(PDFUtils.setParagraph(count == 0 ? "出行时间:" : "出差后回公司时间:", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
				cell.setBorder(0);
				tItemTable.addCell(cell);
				
				//增加空行
				int childCount = 3;
				String tmpStr = "";
				while(childCount > 0){
					if(childCount == 3){
						tmpStr = "___年___月___日___点___分";
					}else if(childCount == 2){
						tmpStr = "              人力资源及行政部审批:";
					}else if(childCount == 1){
						tmpStr = "   		            记录时间:";
					}
					cell = new PdfPCell(PDFUtils.setParagraph(tmpStr, 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
					cell.setBorder(0);
					childCount --;
					tItemTable.addCell(cell);
				}
				cells[index].setColspan(5);
				count++;
				index = 5;
			}
			listRow.add(row);
			
			//第十行
			cells = new PdfPCell[10];
			row = new PdfPRow(cells);
	
			//第一行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph("□ 《差旅总结报告》已收取。                                                                                    签字:                          日期:", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));//单元格内容
			cells[0].setColspan(10);
			listRow.add(row);
			
			//把表格添加到文件中
			document.add(table);
			//备注
			document.add(PDFUtils.setParagraph("备注：1、出差事由中，由于项目原因出差的员工需在“项目出差”前的“□”中打“√”，并填写具体项目名称；非项目原因出差的员工需在“业务出差”前的“□”中打“√”，并注明业务类型，如供应商洽谈、团购洽谈等。", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			document.add(PDFUtils.setParagraph("2、人员必须在返回公司的1个工作日内到人力资源部考勤管理人员处登记返回时间，3个工作日内提交《出差总结报告》。", 8, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
		}catch(Exception e){

		}finally{
			//关闭文档
			document.close();
			//关闭书写器
			writer.close();
		}
	}
}
