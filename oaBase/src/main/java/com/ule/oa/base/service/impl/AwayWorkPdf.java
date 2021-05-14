package com.ule.oa.base.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.zxing.BarcodeFormat;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font.FontStyle;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.ule.oa.base.cache.ConfigCacheManager;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpApplicationBusiness;
import com.ule.oa.base.po.EmpApplicationBusinessDetail;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.util.SendMailUtil;
import com.ule.oa.common.utils.CommonUtils;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.MD5Encoder;
import com.ule.oa.common.utils.PDFUtils;
import com.ule.oa.common.utils.QRCodeUtils;

@Component
public class AwayWorkPdf {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private ConfigCacheManager configCacheManager;
	@Resource
	private DepartService departService;
	@Resource
	private ConfigService configService;
	@Resource
	private EmployeeService employeeService;
	
	final Integer FONT_SIZE = 12;
	final String checkedIcon = "("+(char)8730+")";
	final String unCheckedIcon = "(  )";
	final String rightIcon = (char)8730+"";
	
	public void buildAwayWorkPdf(EmpApplicationBusiness main,List<EmpApplicationBusinessDetail> detailList, List<ViewTaskInfoTbl> flows, String filePath) throws DocumentException, IOException{
		//创建文件
		Document document = new Document();
		//创建一个书写器
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
		//打开文件
		document.open();
		
		try{
			//添加表头
			document.add(PDFUtils.setParagraph("出差申请表", 15, FontStyle.BOLD.getValue(),PDFUtils.ALIGN_CENTER));
			
			//建表(10列)
			PdfPTable table = PDFUtils.createTable(12,100,10,0); 

			//获得表格中的列
			List<PdfPRow> listRow = table.getRows();
			//设置列宽
			float[] columnWidths = { 3f, 3f, 3f, 3f ,3f ,3f ,3f ,3f ,3f ,3f,3f ,3f };
			table.setWidths(columnWidths);
	
			//第一行
			PdfPCell cells[] = new PdfPCell[12];
			PdfPRow row = new PdfPRow(cells);
	
			//第一行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph("姓    名", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));//单元格内容
			cells[0].setColspan(2);
			cells[2] = new PdfPCell(PDFUtils.setParagraph(CommonUtils.converToString(main.getCnName()), FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[2].setColspan(2);
			
			cells[4] = new PdfPCell(PDFUtils.setParagraph("公司/部门", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[4].setColspan(2);
			cells[6] = new PdfPCell(PDFUtils.setParagraph(CommonUtils.converToString(main.getDepartName()), FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[6].setColspan(2);
			cells[8] = new PdfPCell(PDFUtils.setParagraph("职    务", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[8].setColspan(2);
			cells[10] = new PdfPCell(PDFUtils.setParagraph(CommonUtils.converToString(main.getPositionName()), FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[10].setColspan(2);
			listRow.add(row);
	
			//第二行
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			String substring = "";
			if(StringUtils.isNotBlank(main.getTravelProvince1())){
				substring = main.getTravelProvince1();
			}
			if(StringUtils.isNotBlank(main.getTravelCity1())){
				substring +="/"+main.getTravelCity1()+",";
			}
			if(StringUtils.isNotBlank(main.getTravelProvince2())){
				substring += main.getTravelProvince2();
			}
			if(StringUtils.isNotBlank(main.getTravelCity2())){
				substring +="/"+main.getTravelCity2()+",";
			}
			if(StringUtils.isNotBlank(main.getTravelProvince3())){
				substring += main.getTravelProvince3();
			}
			if(StringUtils.isNotBlank(main.getTravelCity3())){
				substring +="/"+main.getTravelCity3()+",";
			}
			if(StringUtils.isNotBlank(main.getTravelProvince4())){
				substring += main.getTravelProvince4();
			}
			if(StringUtils.isNotBlank(main.getTravelCity4())){
				substring +="/"+main.getTravelCity4()+",";
			}
			if(StringUtils.isNotBlank(main.getTravelProvince5())){
				substring += main.getTravelProvince5();
			}
			if(StringUtils.isNotBlank(main.getTravelCity5())){
				substring +="/"+main.getTravelCity5()+",";
			}
			substring = substring.substring(0,substring.length()-1);
			
			//第二行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph("出发时间", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[0].setColspan(2);
			cells[2] = new PdfPCell(PDFUtils.setParagraph(DateUtils.format(main.getStartTime(),DateUtils.FORMAT_SHORT_CN), FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[2].setColspan(2);
		    cells[4] = new PdfPCell(PDFUtils.setParagraph("出差地点", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_RIGHT));
			cells[4].setColspan(2);
			cells[6] = new PdfPCell(PDFUtils.setParagraph(CommonUtils.converToString(substring), FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[6].setColspan(2);
			cells[8] = new PdfPCell(PDFUtils.setParagraph("交通工具", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[8].setColspan(2);
			cells[10] = new PdfPCell(PDFUtils.setParagraph(CommonUtils.converToString(getVehicleName(main.getVehicle())), FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[10].setColspan(2);
			listRow.add(row);
			
			//第三行
			cells = new PdfPCell[12];
			cells[0] = new PdfPCell();
			cells[0].setColspan(12);
			row = new PdfPRow(cells);
			PdfPTable tItemTable= PDFUtils.createTable(1,100,0,0);
			cells[0].addElement(tItemTable);//将表格嵌套到单元格中
			
			//第三行第一个单元格
			PdfPCell cell = new PdfPCell();
            
			String rowIcon = "1、出差事由："+getCheckedIcon(main.getBusinessType(),100)+"项目出差(项目名称：";/**/
			if(main.getBusinessType().intValue() == 100){
				rowIcon += getProjectName(main.getReason());
			}else{
				rowIcon += "______";
			}
			rowIcon += ")"+getCheckedIcon(main.getBusinessType(),200)+"业务出差(业务类型：";/**/
			if(main.getBusinessType().intValue() == 200){
				rowIcon += getProjectName(main.getReason());
			}else{
				rowIcon += "______";
			}
			rowIcon +=")";
			
			cell = new PdfPCell(PDFUtils.setParagraph(rowIcon, FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cell.setBorder(0);
			tItemTable.addCell(cell);
			
			//第三行第二个单元格
			cell = new PdfPCell();
			cell = new PdfPCell(PDFUtils.setParagraph("2、出差周期：", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cell.setBorder(0);
			tItemTable.addCell(cell);
			
			//第三行第三个单元格
			Double duration = main.getDuration();
			cell = new PdfPCell();
			cell = new PdfPCell(PDFUtils.setParagraph("       从"+DateUtils.format(main.getStartTime(),DateUtils.FORMAT_SHORT_CN)+"至"+
			    DateUtils.format(main.getEndTime(),DateUtils.FORMAT_SHORT_CN)+"；大致共计 "+duration+" 天", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cell.setBorder(0);
			tItemTable.addCell(cell);
			
			//第三行第四个单元格
			String rowIconFore = "3、出差路线："+CommonUtils.converToString(main.getAddress())+"(按此路线报销往返交通费)";/**/
		
			cell = new PdfPCell(PDFUtils.setParagraph(rowIconFore, FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cell.setBorder(0);
			tItemTable.addCell(cell);
			
			//第三行第四个单元格
			cell = new PdfPCell();
			cell = new PdfPCell(PDFUtils.setParagraph("4、出差每日行程及工作计划安排（可附页）：", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cell.setBorder(0);
			tItemTable.addCell(cell);
			listRow.add(row);
			
			//第四行
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
	
			//第四行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph("日期", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[0].setColspan(4);
			cells[4] = new PdfPCell(PDFUtils.setParagraph("工作计划", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[4].setColspan(4);
			cells[8] = new PdfPCell(PDFUtils.setParagraph("工作目标", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[8].setColspan(4);
			listRow.add(row);
			
			//第五行
			//while(duration > 0){
			for(EmpApplicationBusinessDetail detail:detailList)
			{
				cells = new PdfPCell[12];
				row = new PdfPRow(cells);
		
				//第五行单元格
				cells[0] = new PdfPCell(PDFUtils.setParagraph(DateUtils.format(detail.getWorkStartDate(),DateUtils.FORMAT_SHORT_CN)+"-"+DateUtils.format(detail.getWorkEndDate(),DateUtils.FORMAT_SHORT_CN), FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
				cells[0].setColspan(4);
				cells[4] = new PdfPCell(PDFUtils.setParagraph(CommonUtils.converToString(detail.getWorkPlan()), FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
				cells[4].setColspan(4);
				cells[8] = new PdfPCell(PDFUtils.setParagraph(CommonUtils.converToString(detail.getWorkObjective()), FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
				cells[8].setColspan(4);
				
				listRow.add(row);
			}
			//空白行
			int whiteCount = detailList.size();
			if(whiteCount < 3){
				for(int j = 0 ; j < 3 - whiteCount ; j++){
					cells = new PdfPCell[12];
					row = new PdfPRow(cells);
					
					cells[0] = new PdfPCell(PDFUtils.setParagraph(" ", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
					cells[0].setColspan(4);
					cells[4] = new PdfPCell(PDFUtils.setParagraph(" ", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
					cells[4].setColspan(4);
					cells[8] = new PdfPCell(PDFUtils.setParagraph(" ", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
					cells[8].setColspan(4);
					
					listRow.add(row);
				}
			}
			
			//第六行
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//第六行单元格
			//单元格1
			cells[0] = new PdfPCell();
			cells[0].setColspan(4);//第六行第一个单元格跨3行
			tItemTable= PDFUtils.createTable(1,100,0,0);
			cells[0].addElement(tItemTable);//将表格嵌套到单元格中
			cell = new PdfPCell();
			cell = new PdfPCell(PDFUtils.setParagraph("费用预算项目:", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cell.setBorder(0);
			tItemTable.addCell(cell);
			//增加空行
			whiteCount = 3;
			while(whiteCount > 0){
				cell = new PdfPCell(PDFUtils.setParagraph(" ", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
				cell.setBorder(0);
				whiteCount --;
				tItemTable.addCell(cell);
			}
			
			//单元格2
			cells[4] = new PdfPCell(PDFUtils.setParagraph("借款金额：（大写）", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[4].setColspan(4);//第六行第二个单元格跨3行
			//单元格3
			cells[8] = new PdfPCell(PDFUtils.setParagraph("", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//单元格内容
			cells[8].setColspan(4);//第六行第三个单元格跨3行
			listRow.add(row);
			
			//第七行
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//第七行单元格
			cells[0] = new PdfPCell(PDFUtils.setParagraph("申 请 人", FONT_SIZE, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_LEFT));//单元格内容
			cells[0].setColspan(2);
			cells[2] = new PdfPCell(PDFUtils.setParagraph("部门主管审批", FONT_SIZE, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_LEFT));//单元格内容
			cells[2].setColspan(2);
			cells[4] = new PdfPCell(PDFUtils.setParagraph("分管副总裁审批", FONT_SIZE, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_LEFT));//单元格内容
			cells[4].setColspan(2);
			cells[6] = new PdfPCell(PDFUtils.setParagraph("人力资源及行政部审批", FONT_SIZE, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_LEFT));//单元格内容
			cells[6].setColspan(2);
			cells[8] = new PdfPCell(PDFUtils.setParagraph("财务部审批", FONT_SIZE, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_LEFT));//单元格内容
			cells[8].setColspan(2);
			cells[10] = new PdfPCell(PDFUtils.setParagraph("CEO/COO审批", FONT_SIZE, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_LEFT));//单元格内容
			cells[10].setColspan(2);
			listRow.add(row);
			
			//第八行
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//第八行单元格
			int count = 0;//第几个单元格
			int index = 0;//单元格下标

			Map<String, String> asignName = new HashMap<String, String>();
			for(ViewTaskInfoTbl flow:flows){
				
				if(StringUtils.equalsIgnoreCase(flow.getTaskId(), "proposer")){//申请人
					asignName.put("start", CommonUtils.converToString(flow.getAssigneeName()));
					asignName.put("start_time", DateUtils.format(flow.getFinishTime(), DateUtils.FORMAT_SHORT_CN));
				}else if(StringUtils.equalsIgnoreCase(flow.getTaskId(), "administration")){//行政审批    目前没有行政审批框
//					asignName.put("ADMINISTRATIVECONFIRM", CommonUtils.converToString(flow.getAssigneeName()));
//					asignName.put("ADMINISTRATIVECONFIRM_time", DateUtils.format(flow.getFinishTime(), DateUtils.FORMAT_SHORT_CN));
				}else if(StringUtils.equalsIgnoreCase(flow.getTaskId(), "personnelLeader")){//人事部门负责人
					asignName.put("ADMINISTRATIVECONFIRM", CommonUtils.converToString(flow.getAssigneeName()));
					asignName.put("ADMINISTRATIVECONFIRM_time", DateUtils.format(flow.getFinishTime(), DateUtils.FORMAT_SHORT_CN));
				}else if(StringUtils.equalsIgnoreCase(flow.getTaskId(), "departmentHead")){//部门负责人审批
					asignName.put("RESIGN_DH", CommonUtils.converToString(flow.getAssigneeName()));
					asignName.put("RESIGN_DH_time", DateUtils.format(flow.getFinishTime(), DateUtils.FORMAT_SHORT_CN));
				}else if(StringUtils.containsIgnoreCase(flow.getTaskId(), "coo")){//coo
					asignName.put("RESIGN_COO", CommonUtils.converToString(flow.getAssigneeName()));
					asignName.put("RESIGN_COO_time", DateUtils.format(flow.getFinishTime(), DateUtils.FORMAT_SHORT_CN));
				}else if(StringUtils.equalsIgnoreCase(flow.getTaskId(), "VEP_1")){//VEP
					asignName.put("VEP_1", CommonUtils.converToString(flow.getAssigneeName()));
					asignName.put("VEP_1_time", DateUtils.format(flow.getFinishTime(), DateUtils.FORMAT_SHORT_CN));
				}else if(StringUtils.equalsIgnoreCase(flow.getTaskId(), "VEP_2")){//VEP
					asignName.put("VEP_2", CommonUtils.converToString(flow.getAssigneeName()));
					asignName.put("VEP_2_time", DateUtils.format(flow.getFinishTime(), DateUtils.FORMAT_SHORT_CN));
				}
			}
			
			while(count <= 5){
				String account = "";
				String time = "";
				
				if(count == 0){/**申请人**/
					
					account = CommonUtils.converToString(asignName.get("start"));
					time = CommonUtils.converToString(asignName.get("start_time"));
				}else if(count == 1){/**部门主管**/
					if(asignName!=null&&asignName.containsKey("RESIGN_DH")){
						account = CommonUtils.converToString(asignName.get("RESIGN_DH"));
						time = CommonUtils.converToString(asignName.get("RESIGN_DH_time"));
					}
				}else if(count == 2){/**分管副总裁**/
					//查询员工所在部门是否是VEP负责的部门与所在部门负责人是否是VEP
					Depart depart = departService.getInfoByEmpId(main.getEmployeeId());
					boolean headerIsVEP = false;
					List<Config> vepList = configService.getListByCode("VEP");
					for(Config data:vepList){
						Employee param = new Employee();
						param.setCode(data.getDisplayCode());
						Employee vep = employeeService.getByCondition(param);
						if(vep.getId()!=null&&vep.getId().toString().equals(depart.getLeaderId())){
							headerIsVEP = true;
							break;
						}
					}
					if(headerIsVEP){
						if(asignName!=null&&asignName.containsKey("RESIGN_DH")){
							account = CommonUtils.converToString(asignName.get("RESIGN_DH"));
							time = CommonUtils.converToString(asignName.get("RESIGN_DH_time"));
						}
					}
				    if(asignName!=null&&asignName.containsKey("VEP_1")){
				    	account = CommonUtils.converToString(asignName.get("VEP_1"));
				    	time = CommonUtils.converToString(asignName.get("VEP_1_time"));
				    }
				    if(asignName!=null&&asignName.containsKey("VEP_2")){
				    	account = CommonUtils.converToString(asignName.get("VEP_2"));
				    	time = CommonUtils.converToString(asignName.get("VEP_2_time"));
				    }
				}else if(count == 3){/**人力资源部**/

					account = CommonUtils.converToString(asignName.get("ADMINISTRATIVECONFIRM"));
					time = CommonUtils.converToString(asignName.get("ADMINISTRATIVECONFIRM_time"));
				}else if(count == 4){/**财务部**/
                     /**暂时审批中没有财务部**/
					/*account = asignName.get("start");
					time = asignName.get("start_time");*/
				}else if(count == 5){/**运营总裁**/

					account = CommonUtils.converToString(asignName.get("RESIGN_COO"));
					time = CommonUtils.converToString(asignName.get("RESIGN_COO_time"));
				}
				
				
				cells[index] = new PdfPCell();
				tItemTable= PDFUtils.createTable(1,100,0,0);
				cells[index].addElement(tItemTable);//将表格嵌套到单元格中
				cell = new PdfPCell(PDFUtils.setParagraph("签字:"+account, FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
				cell.setBorder(0);
				tItemTable.addCell(cell);
				//增加空行
				whiteCount = 3;
				while(whiteCount > 0){
					cell = new PdfPCell(PDFUtils.setParagraph(" ", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
					cell.setBorder(0);
					whiteCount --;
					tItemTable.addCell(cell);
				}
				cell = new PdfPCell(PDFUtils.setParagraph("日期:\n" + time, FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
				cell.setBorder(0);
				tItemTable.addCell(cell);
				cells[index].setColspan(2);

				index += 2;
				count++;
			}
			listRow.add(row);
			
			
			//第九行
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//第九行单元格
			count = 0;//第几个单元格
			index = 0;//单元格下标
			String startTime = DateUtils.format(main.getStartTime(), DateUtils.FORMAT_SHORT_CN);
			String endTime = DateUtils.format(main.getEndTime(), DateUtils.FORMAT_SHORT_CN);
			while(count < 2){
				cells[index] = new PdfPCell();
				tItemTable= PDFUtils.createTable(1,100,0,0);
				cells[index].addElement(tItemTable);//将表格嵌套到单元格中
				cell = new PdfPCell(PDFUtils.setParagraph(count == 0 ? "出行时间:" : "出差后回公司时间:", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
				cell.setBorder(0);
				tItemTable.addCell(cell);
				
				//增加空行
				int childCount = 3;
				String tmpStr = "";
				while(childCount > 0){
					if(childCount == 3){
						//tmpStr = "___年___月___日___点___分";
						if(count == 0){
							tmpStr = startTime + "9点";
						}else if(count == 1){
							tmpStr = endTime + "18点";
						}
					}else if(childCount == 2){
						tmpStr = "              人力资源及行政部审批:  "+CommonUtils.converToString(asignName.get("ADMINISTRATIVECONFIRM"));
					}else if(childCount == 1){
						tmpStr = "   		            记录时间:  "+CommonUtils.converToString(asignName.get("ADMINISTRATIVECONFIRM_time"));
					}
					cell = new PdfPCell(PDFUtils.setParagraph(tmpStr, FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
					cell.setBorder(0);
					childCount --;
					tItemTable.addCell(cell);
				}
				cells[index].setColspan(6);
				count++;
				index = 6;
			}
			listRow.add(row);
			
			//把表格添加到文件中
			document.add(table);
			
			try{
				//生成二维码
				String OA_URL = configCacheManager.getConfigDisplayCode("MO_WEB_URL");
				String codePath = OA_URL+"/empApplicationBusiness/approval.htm?businessId="+main.getId()+"&flag=no"; 
						// + "/runTask/nonAuth/toView.htm?empId="+main.getEmployeeId()+"&ruTaskId="+ hiActinstList.get(0).getRuTaskId() 
						//+ "&sign=" + MD5Encoder.md5Hex(hiActinstList.get(0).getRuTaskId() + "");
				String imagePath = ConfigConstants.OA_RSAKEY  + SendMailUtil.formatPath(MD5Encoder.md5Hex(DateUtils.getNow())) + ".png";//二维码临时地址
				QRCodeUtils.encode(codePath, imagePath, BarcodeFormat.QR_CODE, 20, 20);//生成二维码
				Image image = Image.getInstance(imagePath);
				image.setAlignment(PDFUtils.ALIGN_RIGHT);
				document.add(image);//将二维码添加到pdf中
				File delfile = new File(imagePath);//删除二维码
		        if(!delfile.delete()) {
		        	logger.error("删除文件失败");
		        }
				
		        //生成二维码备注信息
		        document.add(PDFUtils.setParagraph("扫一扫，查看电子单据内容", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_RIGHT));
		        document.add(PDFUtils.setParagraph("此单据内容由邮乐MO系统出具-" + System.currentTimeMillis() + main.getId(), FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_RIGHT));
			}catch(Exception e){
				logger.error("生成出差总结报告二维码失败,失败原因={}",e.getMessage());
			}
		}catch(Exception e){
			
		}finally{
			//关闭文档
			document.close();
			//关闭书写器
			writer.close();
		}
	}
	
	private String getVehicleName(Integer vehicle){
		String vehicleName = "";
		if(null == vehicle){
			
		}else if(100 == vehicle.intValue()){
			vehicleName = "火车";
		}else if(200 == vehicle.intValue()){
			vehicleName = "飞机";
		}else if(300 == vehicle.intValue()){
			vehicleName = "汽车";
		}
		
		return vehicleName;	
	}
	
	private String getCheckedIcon(Integer businessType,Integer iconType) throws UnsupportedEncodingException{
		
		if(null == businessType){
			return unCheckedIcon;
		}else{
			if(businessType.equals(iconType)){
				return checkedIcon;	
			}else{
				return unCheckedIcon;	
			}
		}
	}
	
	private String getProjectName(String projectName){
		if(null == projectName || "".equals(projectName)){
			return "______";
		}else{
			return projectName;
		}
	}
}
