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
		//????????????
		Document document = new Document();
		//?????????????????????
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
		//????????????
		document.open();
		
		try{
			//????????????
			document.add(PDFUtils.setParagraph("???????????????", 15, FontStyle.BOLD.getValue(),PDFUtils.ALIGN_CENTER));
			
			//??????(10???)
			PdfPTable table = PDFUtils.createTable(12,100,10,0); 

			//?????????????????????
			List<PdfPRow> listRow = table.getRows();
			//????????????
			float[] columnWidths = { 3f, 3f, 3f, 3f ,3f ,3f ,3f ,3f ,3f ,3f,3f ,3f };
			table.setWidths(columnWidths);
	
			//?????????
			PdfPCell cells[] = new PdfPCell[12];
			PdfPRow row = new PdfPRow(cells);
	
			//??????????????????
			cells[0] = new PdfPCell(PDFUtils.setParagraph("???    ???", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));//???????????????
			cells[0].setColspan(2);
			cells[2] = new PdfPCell(PDFUtils.setParagraph(CommonUtils.converToString(main.getCnName()), FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[2].setColspan(2);
			
			cells[4] = new PdfPCell(PDFUtils.setParagraph("??????/??????", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[4].setColspan(2);
			cells[6] = new PdfPCell(PDFUtils.setParagraph(CommonUtils.converToString(main.getDepartName()), FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[6].setColspan(2);
			cells[8] = new PdfPCell(PDFUtils.setParagraph("???    ???", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[8].setColspan(2);
			cells[10] = new PdfPCell(PDFUtils.setParagraph(CommonUtils.converToString(main.getPositionName()), FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[10].setColspan(2);
			listRow.add(row);
	
			//?????????
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
			
			//??????????????????
			cells[0] = new PdfPCell(PDFUtils.setParagraph("????????????", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[0].setColspan(2);
			cells[2] = new PdfPCell(PDFUtils.setParagraph(DateUtils.format(main.getStartTime(),DateUtils.FORMAT_SHORT_CN), FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[2].setColspan(2);
		    cells[4] = new PdfPCell(PDFUtils.setParagraph("????????????", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_RIGHT));
			cells[4].setColspan(2);
			cells[6] = new PdfPCell(PDFUtils.setParagraph(CommonUtils.converToString(substring), FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[6].setColspan(2);
			cells[8] = new PdfPCell(PDFUtils.setParagraph("????????????", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[8].setColspan(2);
			cells[10] = new PdfPCell(PDFUtils.setParagraph(CommonUtils.converToString(getVehicleName(main.getVehicle())), FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cells[10].setColspan(2);
			listRow.add(row);
			
			//?????????
			cells = new PdfPCell[12];
			cells[0] = new PdfPCell();
			cells[0].setColspan(12);
			row = new PdfPRow(cells);
			PdfPTable tItemTable= PDFUtils.createTable(1,100,0,0);
			cells[0].addElement(tItemTable);//??????????????????????????????
			
			//???????????????????????????
			PdfPCell cell = new PdfPCell();
            
			String rowIcon = "1??????????????????"+getCheckedIcon(main.getBusinessType(),100)+"????????????(???????????????";/**/
			if(main.getBusinessType().intValue() == 100){
				rowIcon += getProjectName(main.getReason());
			}else{
				rowIcon += "______";
			}
			rowIcon += ")"+getCheckedIcon(main.getBusinessType(),200)+"????????????(???????????????";/**/
			if(main.getBusinessType().intValue() == 200){
				rowIcon += getProjectName(main.getReason());
			}else{
				rowIcon += "______";
			}
			rowIcon +=")";
			
			cell = new PdfPCell(PDFUtils.setParagraph(rowIcon, FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cell.setBorder(0);
			tItemTable.addCell(cell);
			
			//???????????????????????????
			cell = new PdfPCell();
			cell = new PdfPCell(PDFUtils.setParagraph("2??????????????????", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cell.setBorder(0);
			tItemTable.addCell(cell);
			
			//???????????????????????????
			Double duration = main.getDuration();
			cell = new PdfPCell();
			cell = new PdfPCell(PDFUtils.setParagraph("       ???"+DateUtils.format(main.getStartTime(),DateUtils.FORMAT_SHORT_CN)+"???"+
			    DateUtils.format(main.getEndTime(),DateUtils.FORMAT_SHORT_CN)+"??????????????? "+duration+" ???", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cell.setBorder(0);
			tItemTable.addCell(cell);
			
			//???????????????????????????
			String rowIconFore = "3??????????????????"+CommonUtils.converToString(main.getAddress())+"(?????????????????????????????????)";/**/
		
			cell = new PdfPCell(PDFUtils.setParagraph(rowIconFore, FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cell.setBorder(0);
			tItemTable.addCell(cell);
			
			//???????????????????????????
			cell = new PdfPCell();
			cell = new PdfPCell(PDFUtils.setParagraph("4????????????????????????????????????????????????????????????", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cell.setBorder(0);
			tItemTable.addCell(cell);
			listRow.add(row);
			
			//?????????
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
	
			//??????????????????
			cells[0] = new PdfPCell(PDFUtils.setParagraph("??????", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//???????????????
			cells[0].setColspan(4);
			cells[4] = new PdfPCell(PDFUtils.setParagraph("????????????", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//???????????????
			cells[4].setColspan(4);
			cells[8] = new PdfPCell(PDFUtils.setParagraph("????????????", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//???????????????
			cells[8].setColspan(4);
			listRow.add(row);
			
			//?????????
			//while(duration > 0){
			for(EmpApplicationBusinessDetail detail:detailList)
			{
				cells = new PdfPCell[12];
				row = new PdfPRow(cells);
		
				//??????????????????
				cells[0] = new PdfPCell(PDFUtils.setParagraph(DateUtils.format(detail.getWorkStartDate(),DateUtils.FORMAT_SHORT_CN)+"-"+DateUtils.format(detail.getWorkEndDate(),DateUtils.FORMAT_SHORT_CN), FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//???????????????
				cells[0].setColspan(4);
				cells[4] = new PdfPCell(PDFUtils.setParagraph(CommonUtils.converToString(detail.getWorkPlan()), FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//???????????????
				cells[4].setColspan(4);
				cells[8] = new PdfPCell(PDFUtils.setParagraph(CommonUtils.converToString(detail.getWorkObjective()), FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//???????????????
				cells[8].setColspan(4);
				
				listRow.add(row);
			}
			//?????????
			int whiteCount = detailList.size();
			if(whiteCount < 3){
				for(int j = 0 ; j < 3 - whiteCount ; j++){
					cells = new PdfPCell[12];
					row = new PdfPRow(cells);
					
					cells[0] = new PdfPCell(PDFUtils.setParagraph(" ", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//???????????????
					cells[0].setColspan(4);
					cells[4] = new PdfPCell(PDFUtils.setParagraph(" ", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//???????????????
					cells[4].setColspan(4);
					cells[8] = new PdfPCell(PDFUtils.setParagraph(" ", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//???????????????
					cells[8].setColspan(4);
					
					listRow.add(row);
				}
			}
			
			//?????????
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//??????????????????
			//?????????1
			cells[0] = new PdfPCell();
			cells[0].setColspan(4);//??????????????????????????????3???
			tItemTable= PDFUtils.createTable(1,100,0,0);
			cells[0].addElement(tItemTable);//??????????????????????????????
			cell = new PdfPCell();
			cell = new PdfPCell(PDFUtils.setParagraph("??????????????????:", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
			cell.setBorder(0);
			tItemTable.addCell(cell);
			//????????????
			whiteCount = 3;
			while(whiteCount > 0){
				cell = new PdfPCell(PDFUtils.setParagraph(" ", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
				cell.setBorder(0);
				whiteCount --;
				tItemTable.addCell(cell);
			}
			
			//?????????2
			cells[4] = new PdfPCell(PDFUtils.setParagraph("???????????????????????????", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//???????????????
			cells[4].setColspan(4);//??????????????????????????????3???
			//?????????3
			cells[8] = new PdfPCell(PDFUtils.setParagraph("", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_CENTER));//???????????????
			cells[8].setColspan(4);//??????????????????????????????3???
			listRow.add(row);
			
			//?????????
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//??????????????????
			cells[0] = new PdfPCell(PDFUtils.setParagraph("??? ??? ???", FONT_SIZE, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_LEFT));//???????????????
			cells[0].setColspan(2);
			cells[2] = new PdfPCell(PDFUtils.setParagraph("??????????????????", FONT_SIZE, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_LEFT));//???????????????
			cells[2].setColspan(2);
			cells[4] = new PdfPCell(PDFUtils.setParagraph("?????????????????????", FONT_SIZE, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_LEFT));//???????????????
			cells[4].setColspan(2);
			cells[6] = new PdfPCell(PDFUtils.setParagraph("??????????????????????????????", FONT_SIZE, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_LEFT));//???????????????
			cells[6].setColspan(2);
			cells[8] = new PdfPCell(PDFUtils.setParagraph("???????????????", FONT_SIZE, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_LEFT));//???????????????
			cells[8].setColspan(2);
			cells[10] = new PdfPCell(PDFUtils.setParagraph("CEO/COO??????", FONT_SIZE, FontStyle.BOLD.getValue(), PDFUtils.ALIGN_LEFT));//???????????????
			cells[10].setColspan(2);
			listRow.add(row);
			
			//?????????
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//??????????????????
			int count = 0;//??????????????????
			int index = 0;//???????????????

			Map<String, String> asignName = new HashMap<String, String>();
			for(ViewTaskInfoTbl flow:flows){
				
				if(StringUtils.equalsIgnoreCase(flow.getTaskId(), "proposer")){//?????????
					asignName.put("start", CommonUtils.converToString(flow.getAssigneeName()));
					asignName.put("start_time", DateUtils.format(flow.getFinishTime(), DateUtils.FORMAT_SHORT_CN));
				}else if(StringUtils.equalsIgnoreCase(flow.getTaskId(), "administration")){//????????????    ???????????????????????????
//					asignName.put("ADMINISTRATIVECONFIRM", CommonUtils.converToString(flow.getAssigneeName()));
//					asignName.put("ADMINISTRATIVECONFIRM_time", DateUtils.format(flow.getFinishTime(), DateUtils.FORMAT_SHORT_CN));
				}else if(StringUtils.equalsIgnoreCase(flow.getTaskId(), "personnelLeader")){//?????????????????????
					asignName.put("ADMINISTRATIVECONFIRM", CommonUtils.converToString(flow.getAssigneeName()));
					asignName.put("ADMINISTRATIVECONFIRM_time", DateUtils.format(flow.getFinishTime(), DateUtils.FORMAT_SHORT_CN));
				}else if(StringUtils.equalsIgnoreCase(flow.getTaskId(), "departmentHead")){//?????????????????????
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
				
				if(count == 0){/**?????????**/
					
					account = CommonUtils.converToString(asignName.get("start"));
					time = CommonUtils.converToString(asignName.get("start_time"));
				}else if(count == 1){/**????????????**/
					if(asignName!=null&&asignName.containsKey("RESIGN_DH")){
						account = CommonUtils.converToString(asignName.get("RESIGN_DH"));
						time = CommonUtils.converToString(asignName.get("RESIGN_DH_time"));
					}
				}else if(count == 2){/**???????????????**/
					//?????????????????????????????????VEP????????????????????????????????????????????????VEP
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
				}else if(count == 3){/**???????????????**/

					account = CommonUtils.converToString(asignName.get("ADMINISTRATIVECONFIRM"));
					time = CommonUtils.converToString(asignName.get("ADMINISTRATIVECONFIRM_time"));
				}else if(count == 4){/**?????????**/
                     /**??????????????????????????????**/
					/*account = asignName.get("start");
					time = asignName.get("start_time");*/
				}else if(count == 5){/**????????????**/

					account = CommonUtils.converToString(asignName.get("RESIGN_COO"));
					time = CommonUtils.converToString(asignName.get("RESIGN_COO_time"));
				}
				
				
				cells[index] = new PdfPCell();
				tItemTable= PDFUtils.createTable(1,100,0,0);
				cells[index].addElement(tItemTable);//??????????????????????????????
				cell = new PdfPCell(PDFUtils.setParagraph("??????:"+account, FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
				cell.setBorder(0);
				tItemTable.addCell(cell);
				//????????????
				whiteCount = 3;
				while(whiteCount > 0){
					cell = new PdfPCell(PDFUtils.setParagraph(" ", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
					cell.setBorder(0);
					whiteCount --;
					tItemTable.addCell(cell);
				}
				cell = new PdfPCell(PDFUtils.setParagraph("??????:\n" + time, FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
				cell.setBorder(0);
				tItemTable.addCell(cell);
				cells[index].setColspan(2);

				index += 2;
				count++;
			}
			listRow.add(row);
			
			
			//?????????
			cells = new PdfPCell[12];
			row = new PdfPRow(cells);
			
			//??????????????????
			count = 0;//??????????????????
			index = 0;//???????????????
			String startTime = DateUtils.format(main.getStartTime(), DateUtils.FORMAT_SHORT_CN);
			String endTime = DateUtils.format(main.getEndTime(), DateUtils.FORMAT_SHORT_CN);
			while(count < 2){
				cells[index] = new PdfPCell();
				tItemTable= PDFUtils.createTable(1,100,0,0);
				cells[index].addElement(tItemTable);//??????????????????????????????
				cell = new PdfPCell(PDFUtils.setParagraph(count == 0 ? "????????????:" : "????????????????????????:", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_LEFT));
				cell.setBorder(0);
				tItemTable.addCell(cell);
				
				//????????????
				int childCount = 3;
				String tmpStr = "";
				while(childCount > 0){
					if(childCount == 3){
						//tmpStr = "___???___???___???___???___???";
						if(count == 0){
							tmpStr = startTime + "9???";
						}else if(count == 1){
							tmpStr = endTime + "18???";
						}
					}else if(childCount == 2){
						tmpStr = "              ??????????????????????????????:  "+CommonUtils.converToString(asignName.get("ADMINISTRATIVECONFIRM"));
					}else if(childCount == 1){
						tmpStr = "   		            ????????????:  "+CommonUtils.converToString(asignName.get("ADMINISTRATIVECONFIRM_time"));
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
			
			//???????????????????????????
			document.add(table);
			
			try{
				//???????????????
				String OA_URL = configCacheManager.getConfigDisplayCode("MO_WEB_URL");
				String codePath = OA_URL+"/empApplicationBusiness/approval.htm?businessId="+main.getId()+"&flag=no"; 
						// + "/runTask/nonAuth/toView.htm?empId="+main.getEmployeeId()+"&ruTaskId="+ hiActinstList.get(0).getRuTaskId() 
						//+ "&sign=" + MD5Encoder.md5Hex(hiActinstList.get(0).getRuTaskId() + "");
				String imagePath = ConfigConstants.OA_RSAKEY  + SendMailUtil.formatPath(MD5Encoder.md5Hex(DateUtils.getNow())) + ".png";//?????????????????????
				QRCodeUtils.encode(codePath, imagePath, BarcodeFormat.QR_CODE, 20, 20);//???????????????
				Image image = Image.getInstance(imagePath);
				image.setAlignment(PDFUtils.ALIGN_RIGHT);
				document.add(image);//?????????????????????pdf???
				File delfile = new File(imagePath);//???????????????
		        if(!delfile.delete()) {
		        	logger.error("??????????????????");
		        }
				
		        //???????????????????????????
		        document.add(PDFUtils.setParagraph("????????????????????????????????????", FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_RIGHT));
		        document.add(PDFUtils.setParagraph("????????????????????????MO????????????-" + System.currentTimeMillis() + main.getId(), FONT_SIZE, FontStyle.NORMAL.getValue(), PDFUtils.ALIGN_RIGHT));
			}catch(Exception e){
				logger.error("???????????????????????????????????????,????????????={}",e.getMessage());
			}
		}catch(Exception e){
			
		}finally{
			//????????????
			document.close();
			//???????????????
			writer.close();
		}
	}
	
	private String getVehicleName(Integer vehicle){
		String vehicleName = "";
		if(null == vehicle){
			
		}else if(100 == vehicle.intValue()){
			vehicleName = "??????";
		}else if(200 == vehicle.intValue()){
			vehicleName = "??????";
		}else if(300 == vehicle.intValue()){
			vehicleName = "??????";
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
