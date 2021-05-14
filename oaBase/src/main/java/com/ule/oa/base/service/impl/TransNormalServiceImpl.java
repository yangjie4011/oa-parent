package com.ule.oa.base.service.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ule.arch.basehttpclient.BaseHttpClientFactory;
import com.ule.arch.basehttpclient.client.BaseHttpClient;
import com.ule.arch.basehttpclient.model.HttpCallBack;
import com.ule.arch.basehttpclient.model.HttpRequest;
import com.ule.arch.basehttpclient.model.HttpResponse;
import com.ule.arch.basehttpclient.standard.util.ContentCoverter;
import com.ule.oa.base.cache.CompanyConfigCacheManager;
import com.ule.oa.base.cache.ConfigCacheManager;
import com.ule.oa.base.po.Company;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.TransNormal;
import com.ule.oa.base.service.AttnSignRecordService;
import com.ule.oa.base.service.AttnTaskRecordService;
import com.ule.oa.base.service.AttnWorkHoursService;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.CompanyService;
import com.ule.oa.base.service.TransNormalDataService;
import com.ule.oa.base.service.TransNormalService;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.MsgUtils;
import com.ule.oa.common.utils.json.JSONUtils;

/**
  * @ClassName: TransNormalServiceImpl
  * @Description: 考勤第一步操作，将考勤机数据拷贝到oa数据库，并记录状态机（未完成）·
  * @author zhangjintao
  * @date 2017年6月29日 上午9:53:07
 */

@Service
public class TransNormalServiceImpl implements TransNormalService{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private AttnSignRecordService attnSignRecordService;
	
	@Resource
	private AttnWorkHoursService attnWorkHoursService;
	
	@Resource
	private CompanyService companyService;
	
	@Resource
	private TransNormalDataService transNormalDataService;
	
	@Resource
	private AttnTaskRecordService attnTaskRecordService;
	
	@Resource
	private CompanyConfigService companyConfigService;
	
	@Autowired
	private CompanyConfigCacheManager companyConfigCacheManager;
	
	@Autowired
	private ConfigCacheManager configCacheManager;
	
	private static BaseHttpClient client = BaseHttpClientFactory.getClient();
	
	
	private final Integer pageSize = 500;//每次查询500条数据
    private final String SIX = " 06:00:00";//6点整
    private final Integer CALCULATE_TIME = 8;/**统计考勤的时间，每天小时为8点**/
    
	private void setAttnSignRecordByDate(TransNormal transNormal) throws Exception {

		try {
			Date startTime = transNormal.getEvttime();// 统计指定日期的,继承自上层方法，一定不许为空

			Integer maxAutoId = attnSignRecordService.getMaxAttnId();// 得到统计过来的最大ID

			if(null == maxAutoId || 0 == maxAutoId){//首次导入数据，没有最大ID，则只用日期查询
				String startDateStr = DateUtils.format(startTime,DateUtils.FORMAT_SHORT);
				startDateStr = startDateStr + SIX;// 起始时间6点
				startTime = DateUtils.parse(startDateStr, DateUtils.FORMAT_LONG);
				transNormal.setStartTime(startTime);
				transNormal.setEndTime(null) ;
			}else{
				transNormal.setStartTime(null);
				transNormal.setEndTime(null);
			}

			//Date endTime = DateUtils.addDay(startTime, 1);
			//endTime = DateUtils.addMinute(endTime, 180);// 结束时间第二天9点
			//transNormal.setEndTime(endTime);// 由于有手动插入打卡记录，日期是以前的，但是autoID是最大的，不能再根据以前的日期和autoID一起查询
			transNormal.setAutoid(maxAutoId);

			Company condition = new Company();
			condition.setIsUle(1);// 邮乐
			Company uleCompany = companyService.getByCondition(condition);

			Integer total = transNormalDataService.getTotalRows(transNormal);
			List<TransNormal> list = null;

			Integer offset = 0;
			Integer limit = 0;

			Long uleId = uleCompany.getId();
			Date currentTime = DateUtils.parse(DateUtils.getNow(),DateUtils.FORMAT_LONG);
			String createUser = "api";// 保存时的创建人
			if (total > pageSize) {
				Integer totalPage = (int) Math.ceil((total*1.0)/ pageSize);
				for (int i = 0; i <= totalPage; i++) {
					offset = i * pageSize;
					limit = (i + 1) * pageSize;

					transNormal.setOffset(offset);
					transNormal.setLimit(limit);
					list = transNormalDataService.getTransNormalList(transNormal);

					// 批量插入考勤数据，日志数据
					if (!list.isEmpty()) {
						saveSignAndWork(list, uleId, currentTime, createUser);
					}
				}
			} else {

				transNormal.setOffset(0);
				transNormal.setLimit(pageSize);
				list = transNormalDataService.getTransNormalList(transNormal);

				// 批量插入考勤数据，日志数据
				if (!list.isEmpty()) {
					saveSignAndWork(list, uleId, currentTime, createUser);
				}

			}

		} catch (Exception e) {
			throw new Exception("复制单日考勤数据异常, "+ e.getMessage());
		}
	}
	
	@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
	public void saveSignAndWork(List<TransNormal> list, Long uleId, Date currentTime, String createUser){
		
		attnSignRecordService.saveTransToAttnSignBatch(list,uleId,currentTime,createUser);
		
	}
	
	//自动触发，获取最大已处理日期，对每一天处理
	@Override
	public void setAttnSignRecord(TransNormal transNormal) {
		logger.info("开始进入每日自动考勤数据统计[复制打卡记录].....");
		
		TransNormal transNormal1 = new TransNormal();
		Boolean isSendKafka = true;
        final Calendar c = Calendar.getInstance();
        Integer HOUR_OF_DAY = null;
        
        Date kafkaBegin,kafkaEnd;
		
	    //注意：要求每小时统计一次考勤，但是只有在 8:00 点的时候才发送kafka后续步骤
		c.setTime(new Date());
        HOUR_OF_DAY = c.get(Calendar.HOUR_OF_DAY);//现在是几点
        if(!HOUR_OF_DAY.equals(CALCULATE_TIME) ){
    		 logger.info("当前时间为：{}，不发送kafka。",HOUR_OF_DAY);
          	 isSendKafka = false;//不是8点不发送
        }else{
   		     logger.info("当前时间为：{}，需发送kafka。",HOUR_OF_DAY);
        }
		//排班会重算考勤,会导致task表里最大日期变为月底,所以这里的时间应取当前时间的前天
		Date eDate = DateUtils.getToday();//今天
//		Date sdate = attnTaskRecordService.getMaxDateOfTask();//获取已跑考勤的最大日期,
		Date sdate = DateUtils.addDay(eDate, -1);
		/*if(null == sdate){
			sdate = DateUtils.getToday();
		}else{
			sdate = DateUtils.addDay(sdate, 1);//最大日期已统计，不包含，所以加一天
		}*/

		logger.info("本次统计时间范围是{}---{}",sdate,eDate);
		try{	
			
			transNormal1.setEvttime(sdate);
		  	
		  	if("ULE".equals(getCompanyCode())){
		  		setAttnSignRecordByDate(transNormal1);//定时计算考勤，9点才发送kafka
		  	}
			//循环之前发一次消息，计算最大日期以前的未完成考勤
			sendKafka(isSendKafka,null,sdate,null);
	        while (sdate.getTime()<=eDate.getTime()) {
				  logger.info("开始处理日期为 {} 的考勤数据.",DateUtils.format(sdate));
	  			  
				  kafkaBegin = sdate;
				  if(sdate.equals(DateUtils.getToday())){//今天的考勤数据，明天再统计
					  isSendKafka = false; 
				  }
	        	  
	              c.setTime(sdate);
	              c.add(Calendar.DATE, 1); // 日期加1天
	              sdate = c.getTime();
	              kafkaEnd = sdate;
	              
	              sendKafka(isSendKafka,kafkaBegin,kafkaEnd,null);
	        }
		}catch(Exception e){
			logger.error("遍历日期过程中出错，处理的时间为 {}.错误信息：{}",DateUtils.format(c.getTime()),e.getMessage());
		}
  		logger.info("每日自动考勤数据统计[复制打卡记录]结束.....");
	}
	
	public String getCompanyCode(){
		//查询当前登录的MO归属公司
    	String code = "ULE";
    	List<CompanyConfig> companyConfigList = companyConfigCacheManager.get("companyCode");
		if(companyConfigList!=null&&companyConfigList.size()>0){
			if("TOM".equals(companyConfigList.get(0).getDisplayCode())){
				code = companyConfigList.get(0).getDisplayCode();
			}
		}
    	return code;
	}
	
	//手动输入日期进行行处理，循环日期，对每一天处理
	@Override
	public void startAttnByTime(TransNormal transNormal) {
		
		Date sdate = null,eDate = null;
        Date kafkaBegin,kafkaEnd;
        String employeeIds = null;
		if(null != transNormal){
			sdate = transNormal.getStartTime();
			eDate = transNormal.getEndTime();
			employeeIds = transNormal.getEmployeeIds();
		}else{
			logger.info("没有定义开始时间或者结束时间。");
		}
		
		if(null !=sdate && null != eDate){
	        Calendar c = Calendar.getInstance();
			try{
			    //long betweenDays=(long) ((eDate.getTime()-sdate.getTime())/(1000 * 60 * 60 *24)+0.5);//天数间隔
				//循环之前发一次消息，计算最大日期以前的未完成考勤
        	    transNormal.setEvttime(sdate);//根据这个日期一天天的统计
        	    if("ULE".equals(getCompanyCode())){
        	    	setAttnSignRecordByDate(transNormal);//手动计算考勤，一定要发送kafka
        	    }
        	    
				sendKafka(true,null,sdate,employeeIds);
		        while (sdate.getTime()<=eDate.getTime()) {
		        	
					  logger.info("开始处理日期为 {} 的考勤数据.",DateUtils.format(sdate));
					  kafkaBegin = sdate;
		        	  
		              c.setTime(sdate);
		              c.add(Calendar.DATE, 1); // 日期加1天
		              sdate = c.getTime();
		              kafkaEnd = sdate;
		              
			          sendKafka(true,kafkaBegin,kafkaEnd,employeeIds);
		        }
			}catch(Exception e){
				logger.error("遍历日期过程中出错，处理的时间为 {}.错误信息：{}",DateUtils.format(c.getTime()),e.getMessage());
			}
		}
	}
	

  	/**
    	 参数解释：
	  	 参数1：是否发送kafka；
	  	 参数2：统计的开始时间；
	  	 参数3：统计的结束时间；
	  	 参数4：需要统计的员工ID，以逗号隔开。
  	**/
	private void sendKafka(Boolean isSendKafka,Date begin,Date end,String employeeIds){
		if (isSendKafka) {
			try {
				String beginDate = null,endDate = null;
				if(null != begin){
					beginDate=DateUtils.format(begin, DateUtils.FORMAT_SHORT);
				}
				if(null != end){
					endDate=DateUtils.format(end, DateUtils.FORMAT_SHORT);
				}
				if(null != employeeIds){
					if(employeeIds.isEmpty()){
						employeeIds = null;
					}
				}
				HashMap<String, Object> map = new HashMap<String, Object>();
				// kafka消息头，必须有时间范围，否则后续会重复消费数据
				map.put("beginDate", beginDate);//可为null
				map.put("endDate", endDate);
				map.put("employeeIds", employeeIds);
				MsgUtils.syncSendKafkaMsgByMap(map,"com.ule.oa.service.attnTaskRecordProducer");
				logger.info("发送kafka成功  com.ule.oa.service.attnTaskRecordProducer");
			} catch (Exception e) {
				logger.error("发送kafka失败  com.ule.oa.service.attnTaskRecordProducer",e);
			}
		} else {
			logger.info("不发送kafka!");
		}
	}

	@Override
	public Map<String, String> recalculateAttnByCondition(TransNormal transNormal) {
		
		final Map<String, String> map = new HashMap<String, String>();
		
		try {
			final Map<String, String> paramMap = new HashMap<String,String>();
			
			paramMap.put("data", JSONUtils.write(transNormal));
			
			String TRANS_NORMAL_URL = "/transNormal/startAttnByTime.htm";
	    	String OA_SERVICE_URL = configCacheManager.getConfigDisplayCode("MO_SERVICE_URL");
	    	TRANS_NORMAL_URL = OA_SERVICE_URL + TRANS_NORMAL_URL;
			
			HttpRequest req = new HttpRequest.Builder().url(TRANS_NORMAL_URL).post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
    				ContentCoverter.formConvertAsString(paramMap)).build();
			
			map.put("response", "触发成功,请稍后查看数据!");
			client.sendRequestAsyn(req, new HttpCallBack() {
                
				public void onFailure(Exception e) {
					logger.error("手动触发考勤规则请求失败,失败原因={}",e.getMessage());
					map.put("response", "触发失败!!!");
                }

                public void onResponse(HttpResponse response) {
                	logger.info("手动触发考勤规则请求成功");
                }
            });
		} catch (Exception e) {
			logger.error("手动触发考勤规则请求失败,失败原因={}",e.getMessage());
			map.put("response", "触发失败!!!");
		}
		
		return map;
		
	}
}
