package com.ule.oa.admin.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.google.common.collect.Maps;
import com.ule.oa.admin.util.DocumentUtil;
import com.ule.oa.admin.util.IsMenu;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.service.EmployeeRecordService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;

/**
 * @ClassName: 员工履历管理
 * @Description: 员工履历管理
 * @author yangjie
 * @date 2019年11月19日
 */

@Controller
@RequestMapping("employeeRecord")
public class EmployeeRecordController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private EmployeeRecordService employeeRecordService;
	@Autowired
	private EmployeeService employeeService;
	
	//员工信息管理-员工履历管理
	@IsMenu
	@RequestMapping("/index.htm")
	public ModelAndView index() {
		return new ModelAndView("base/emp/employee_record_index");
	}
	
	//员工信息管理-员工履历管理-查询
	@ResponseBody
	@RequestMapping("/getListByPage.htm")
	public PageModel<Employee> getListByPage(Employee employee) {
		
		PageModel<Employee> pm = new PageModel<Employee>();
		pm.setRows(new java.util.ArrayList<Employee>());
		pm.setTotal(0);
		pm.setPageNo(1);

		try {
			pm = employeeRecordService.getListByPage(employee);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return pm;
	}
	
	//员工信息管理-员工履历管理-查看
	@RequestMapping("/scan.htm")
	public ModelAndView scan(HttpServletRequest request,Long employeeId) {
		Map<String,Object> trackInfo = employeeRecordService.getScanInfo(employeeId);
		request.setAttribute("trackInfo",trackInfo);
		return new ModelAndView("base/emp/employee_record_scan");
	}
	
	//员工信息管理-员工履历管理-编辑
	@RequestMapping("/update.htm")
	public ModelAndView update(HttpServletRequest request,Long employeeId,Long type) {
		Map<String,Object> trackInfo = employeeRecordService.getUpdateInfo(employeeId);
		request.setAttribute("trackInfo",trackInfo);
		request.setAttribute("backType",type);//标记返回那个页面
		return new ModelAndView("base/emp/employee_record_update");
	}
	
	//员工信息管理-员工履历管理-保存基本信息
	@ResponseBody
	@RequestMapping("/getPositionLevelAndSeqList.htm")
	public Map<String,Object> getPositionLevelAndSeqList(Long positionId){
		Map<String,Object> result = Maps.newHashMap();
		try {
			result = employeeRecordService.getPositionLevelAndSeqList(positionId);
		} catch (Exception e) {
			result.put("sucess",false);
			result.put("msg",e.getMessage());
		}
		return result;
	}
	
	
	/**
	 * 员工信息管理-员工履历管理-上传员工照片
	 * @param file
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/uploadEmployeePhoto.htm",method = RequestMethod.POST)
	public Map<String, Object> importEmployee(@RequestParam(value="file",required = false)CommonsMultipartFile file) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = employeeRecordService.uploadEmployeePhoto(file);
		} catch (OaException e) {
			map.put("success", false);
			map.put("message", e.getMessage());
		} catch (Exception e) {
			map.put("success", false);
			map.put("message", e.getMessage());
		}
        return map;
	}
	
	
	//员工信息管理-员工履历管理-保存基本信息
	@ResponseBody
	@RequestMapping("/saveBaseInfo.htm")
	public Map<String,Object> saveBaseInfo(Employee employee){
		Map<String,Object> result = Maps.newHashMap();
		try {
			result = employeeRecordService.saveBaseInfo(employee);
		} catch (Exception e) {
			result.put("sucess",false);
			result.put("msg",e.getMessage());
		}
		return result;
	}
	
	//员工信息管理-员工履历管理-保存在职信息
	@ResponseBody
	@RequestMapping("/savePayrollInfo.htm")
	public Map<String,Object> savePayrollInfo(Employee employee){
		Map<String,Object> result = Maps.newHashMap();
		try {
			result = employeeRecordService.savePayrollInfo(employee);
		} catch (Exception e) {
			result.put("sucess",false);
			result.put("msg",e.getMessage());
		}
		return result;
	}
	
	//员工信息管理-员工履历管理-保存教育经历
	@ResponseBody
	@RequestMapping("/saveEducationExperience.htm")
	public Map<String,Object> saveEducationExperience(@RequestParam(value = "empSchoolListStr") String empSchoolListStr,@RequestParam(value = "employeeId") Long employeeId){
		Map<String,Object> result = Maps.newHashMap();
		try {
			result = employeeRecordService.saveEducationExperience(empSchoolListStr,employeeId);
		} catch (Exception e) {
			result.put("sucess",false);
			result.put("msg",e.getMessage());
		}
		return result;
	}
	
	//员工信息管理-员工履历管理-保存培训证书
	@ResponseBody
	@RequestMapping("/saveTrainingCertificate.htm")
	public Map<String,Object> saveTrainingCertificate(@RequestParam(value = "empTrainingListStr") String empTrainingListStr,@RequestParam(value = "employeeId") Long employeeId){
		Map<String,Object> result = Maps.newHashMap();
		try {
			result = employeeRecordService.saveTrainingCertificate(empTrainingListStr,employeeId);
		} catch (Exception e) {
			result.put("sucess",false);
			result.put("msg",e.getMessage());
		}
		return result;
	}
	
	//员工信息管理-员工履历管理-保存工作经历
	@ResponseBody
	@RequestMapping("/saveWorkExperience.htm")
	public Map<String,Object> saveWorkExperience(@RequestParam(value = "empWorkRecordListStr") String empWorkRecordListStr,@RequestParam(value = "employeeId") Long employeeId){
		Map<String,Object> result = Maps.newHashMap();
		try {
			result = employeeRecordService.saveWorkExperience(empWorkRecordListStr,employeeId);
		} catch (Exception e) {
			result.put("sucess",false);
			result.put("msg",e.getMessage());
		}
		return result;
	}
	
	//员工信息管理-员工履历管理-保存紧急联系人
	@ResponseBody
	@RequestMapping("/saveEmergencyContact.htm")
	public Map<String,Object> saveEmergencyContact(@RequestParam(value = "empUrgentContactListStr") String empUrgentContactListStr,@RequestParam(value = "employeeId") Long employeeId){
		Map<String,Object> result = Maps.newHashMap();
		try {
			result = employeeRecordService.saveEmergencyContact(empUrgentContactListStr,employeeId);
		} catch (Exception e) {
			result.put("sucess",false);
			result.put("msg",e.getMessage());
		}
		return result;
	}
	
	//员工信息管理-员工履历管理-保存配偶信息
	@ResponseBody
	@RequestMapping("/saveSpouseInfo.htm")
	public Map<String,Object> saveSpouseInfo(@RequestParam(value = "spouseInfoListStr") String spouseInfoListStr,@RequestParam(value = "employeeId") Long employeeId){
		Map<String,Object> result = Maps.newHashMap();
		try {
			result = employeeRecordService.saveSpouseInfo(spouseInfoListStr,employeeId);
		} catch (Exception e) {
			result.put("sucess",false);
			result.put("msg",e.getMessage());
		}
		return result;
	}
	
	//员工信息管理-员工履历管理-保存子女信息
	@ResponseBody
	@RequestMapping("/saveChildrenInfo.htm")
	public Map<String,Object> saveChildrenInfo(@RequestParam(value = "childrenInfoListStr") String childrenInfoListStr,@RequestParam(value = "employeeId") Long employeeId){
		Map<String,Object> result = Maps.newHashMap();
		try {
			result = employeeRecordService.saveChildrenInfo(childrenInfoListStr,employeeId);
		} catch (Exception e) {
			result.put("sucess",false);
			result.put("msg",e.getMessage());
		}
		return result;
	}
	
	//员工信息管理-员工履历管理-保存业绩与奖惩
	@ResponseBody
	@RequestMapping("/saveAchievementAndRewardMerit.htm")
	public Map<String,Object> saveAchievementAndRewardMerit(@RequestParam(value = "empAchievementListStr") String empAchievementListStr,@RequestParam(value = "employeeId") Long employeeId){
		Map<String,Object> result = Maps.newHashMap();
		try {
			result = employeeRecordService.saveAchievementAndRewardMerit(empAchievementListStr,employeeId);
		} catch (Exception e) {
			result.put("sucess",false);
			result.put("msg",e.getMessage());
		}
		return result;
	}
	
	//员工信息管理-员工履历管理-保存业绩与奖惩
	@ResponseBody
	@RequestMapping("/saveTrainRecord.htm")
	public Map<String,Object> saveTrainRecord(@RequestParam(value = "trainRecordListStr") String trainRecordListStr,@RequestParam(value = "employeeId") Long employeeId){
		Map<String,Object> result = Maps.newHashMap();
		try {
			result = employeeRecordService.saveTrainRecord(trainRecordListStr,employeeId);
		} catch (Exception e) {
			result.put("sucess",false);
			result.put("msg",e.getMessage());
		}
		return result;
	}
	
	//员工信息管理-员工履历管理-合同签订记录
	@ResponseBody
	@RequestMapping("/saveContractSignRecord.htm")
	public Map<String,Object> saveContractSignRecord(@RequestParam(value = "empContractListStr") String empContractListStr,@RequestParam(value = "employeeId") Long employeeId){
		Map<String,Object> result = Maps.newHashMap();
		try {
			result = employeeRecordService.saveContractSignRecord(empContractListStr,employeeId);
		} catch (Exception e) {
			result.put("sucess",false);
			result.put("msg",e.getMessage());
		}
		return result;
	}
	
	//员工信息管理-员工履历管理-考核记录
	@ResponseBody
	@RequestMapping("/saveAssessRecord.htm")
	public Map<String,Object> saveAssessRecord(@RequestParam(value = "empAppraiseListStr") String empAppraiseListStr,@RequestParam(value = "employeeId") Long employeeId){
		Map<String,Object> result = Maps.newHashMap();
		try {
			result = employeeRecordService.saveAssessRecord(empAppraiseListStr,employeeId);
		} catch (Exception e) {
			result.put("sucess",false);
			result.put("msg",e.getMessage());
		}
		return result;
	}
	
	//员工信息管理-员工履历管理-岗位记录
	@ResponseBody
	@RequestMapping("/savePostRecord.htm")
	public Map<String,Object> savePostRecord(@RequestParam(value = "postRecordListStr") String postRecordListStr,@RequestParam(value = "employeeId") Long employeeId){
		Map<String,Object> result = Maps.newHashMap();
		try {
			result = employeeRecordService.savePostRecord(postRecordListStr,employeeId);
		} catch (Exception e) {
			result.put("sucess",false);
			result.put("msg",e.getMessage());
		}
		return result;
	}
	
	@RequestMapping("/downLoadEmployeeRecord.htm")
    public void exportMillCertificateWord(HttpServletRequest request,HttpServletResponse response,Long employeeId) throws IOException {   
         InputStream in = null;  
         ServletOutputStream out = null;  
         File file = null;
         try {  
            // 调用工具类的createDoc方法生成Word文档  
            Map<String,Object> data = employeeRecordService.getDownLoadInfo(employeeId);
            DocumentUtil documentUtil = new DocumentUtil();
            file = documentUtil.createDoc(data, "temp");
            in = new FileInputStream(file);  
  
            response.setCharacterEncoding("utf-8");  
            response.setContentType("application/msword");  
            
            // 设置浏览器以下载的方式处理该文件名  
            String agent = request.getHeader("User-Agent").toLowerCase();
            String fileName = "";
            String employeeName = (String) data.get("employeeName");
			if(agent.indexOf("firefox")>0){
				fileName = new String(employeeName.getBytes("UTF-8"), "ISO8859-1") + DateUtils.format(new Date(),DateUtils.FORMAT_SIMPLE) + ".doc";
			}else{
				fileName = URLEncoder.encode(employeeName + DateUtils.format(new Date(),DateUtils.FORMAT_SIMPLE) + ".doc","UTF-8");
			}
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition", "attachment; filename="+fileName);
           
            out = response.getOutputStream();  
            byte[] buffer = new byte[512];  // 缓冲区  
            int bytesToRead = -1;  
            // 通过循环将读入的Word文件的内容输出到浏览器中  
            while((bytesToRead = in.read(buffer)) != -1) {  
                 out.write(buffer, 0, bytesToRead);  
            }  
         } finally {  
             if(in != null){
            	 in.close();  
             } 
             if(out != null){
            	 out.close();  
             } 
             if(file != null) {
            	 if(!file.delete()) {
            		 logger.error("删除临时文件失败");
            	 }
             }
        }  
     }  
	
	
}
