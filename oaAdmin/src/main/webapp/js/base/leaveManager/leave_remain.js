$(function(){
	
	signExcel = new SignExcel();  
	signExcel.init();
	
	holidayTable.init();
	//默认查询所有
	gotoPage(1);
	
	//查询按钮点击事件
	$("#query").click(function(){
		gotoPage(1);
	});
	
	//导出点击事件
	$("#export").click(function(){
		exportReport();
	});
	
	//导出点击事件
	$("#exportNew").click(function(){
		exportReportNew();
	});
	
	var currentCompanyId = $("#currentCompanyId").val();
	//初始化工时类型
	initWorkType(currentCompanyId);
	//初始化单据状态
	initApprovalStatus(currentCompanyId);
	//初始化部门
	getFirstDepart();
	//根据一级部门选择二级部门
	$("#firstDepart").change(function(){
		getSecondDepart(this.value);
	});
	calCurLegalYear();
	calCurWelfareYear();
	calLastLegalYear();
	calLastWelfareYear();
});

//导出查询报表
function exportReport(){
	var data = encodeURI(encodeURI(decodeURIComponent($("#queryform").serialize(),true)));
	
	window.location.href = basePath + "/empLeave/exportLeaveRemainReport.htm?" + data;
}

//导出查询报表
function exportReportNew(){
	var data = encodeURI(encodeURI(decodeURIComponent($("#queryform").serialize(),true)));
	
	window.location.href = basePath + "/empLeave/exportLeaveRemainReportNew.htm?" + data;
}

//分页查询外出报表
function gotoPage(page){
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	$("#pageNo").val(page);
	var data = $("#queryform").serialize() + "&page=" + $("#pageNo").val() + "&rows=10";
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "/empLeave/getReportPageList.htm",
		success:function(response){
			
			holidayTable.renderList(response.rows);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			initPage("commonPage",response,page);
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	});
}

function quitCount(employee){
	$(".spanClass").html("");
	$("#counter").show();
	if(employee!=null && employee.id!=""){
		$("#countemployeeId").val(employee.id);
		$("#countQuitTime").val("");
		if(employee.quitTime!=null){
			$("#countQuitTime").val(employee.quitTime);
		}
	}
	var quitTime=$("#countQuitTime").val();
	var id=$("#countemployeeId").val();
	var data = {id:id,quitTime:quitTime};
	countLeave(data);
	
}

function confirmYearLeave(){
	var leave2= $("#leave2").val();//当年假期天数总数-法定年假
	var leave2_old= $("#leave2_old").val();
	var leave14 = $("#leave14").val();//当年已使用假期天数-法定年假
	var leave14_old = $("#leave14_old").val();
	var leave3 =  $("#leave3").val();//当年假期天数总数-福利年假
	var leave3_old =  $("#leave3_old").val();
	var leave15 = $("#leave15").val();//当年已使用假期天数-福利年假
	var leave15_old = $("#leave15_old").val();
	var leave32 = $("#leave32").val();//去年年假总数-法定
	var leave32_old = $("#leave32_old").val();
	var leave34 = $("#leave34").val();//去年已用年假天数-法定
	var leave34_old = $("#leave34_old").val();
	var leave33 = $("#leave33").val();//去年年假总数-福利
	var leave33_old = $("#leave33_old").val();
	var leave35 = $("#leave35").val();//去年已用年假天数-福利
	var leave35_old = $("#leave35_old").val();
	//确认消息 \r\n
	var confirmMsg = "";
	if(leave2!=leave2_old){
		confirmMsg = confirmMsg + "当年年假法定总数从"+leave2_old+"天修改为"+leave2+"天\r\n";
	}
	if(leave14!=leave14_old){
		confirmMsg = confirmMsg + "当年年假法定已用从"+leave14_old+"天修改为"+leave14+"天\r\n";
	}
	if(leave3!=leave3_old){
		confirmMsg = confirmMsg + "当年年假福利总数从"+leave3_old+"天修改为"+leave3+"天\r\n";
	}
	if(leave15!=leave15_old){
		confirmMsg = confirmMsg + "当年年假福利已用从"+leave15_old+"天修改为"+leave15+"天\r\n";
	}
	if(leave32!=leave32_old){
		confirmMsg = confirmMsg + "去年年假法定总数从"+leave32_old+"天修改为"+leave32+"天\r\n";
	}
	if(leave34!=leave34_old){
		confirmMsg = confirmMsg + "去年年假法定已用从"+leave34_old+"天修改为"+leave34+"天\r\n";
	}
	if(leave33!=leave33_old){
		confirmMsg = confirmMsg + "去年年假福利总数从"+leave33_old+"天修改为"+leave33+"天\r\n";
	}
	if(leave35!=leave35_old){
		confirmMsg = confirmMsg + "去年年假福利已用从"+leave35_old+"天修改为"+leave35+"天。";
	}
	if(confirmMsg!=""){
		$("#confirmMsg1").val(confirmMsg);
		$("#confirmDiv1").show();
	}
}

function saveYearLeave(){
	pageLoading(true);//加载动画
	var leave2= $("#leave2").val();//当年假期天数总数-法定年假
	var leave14 = $("#leave14").val();//当年已使用假期天数-法定年假
	var leave3 =  $("#leave3").val();//当年假期天数总数-福利年假
	var leave15 = $("#leave15").val();//当年已使用假期天数-福利年假
	var leave32 = $("#leave32").val();//去年年假总数-法定
	var leave34 = $("#leave34").val();//去年已用年假天数-法定
	var leave33 = $("#leave33").val();//去年年假总数-福利
	var leave35 = $("#leave35").val();//去年已用年假天数-福利
	var remark	= $("#remarkYear").val();//年假 -备注
	var data = {leaveType:1,employeeId:$("#employeeId").val(),leave2:leave2,leave14:leave14,
			leave3:leave3,leave15:leave15,leave32:leave32,leave34:leave34,leave33:leave33,leave35:leave35,remark:remark};
	updateLeave(1,data);
}

function confirmSickLeave(){
	var leave17 = $("#leave17").val();//当年已使用假期天数-带薪病假
	var leave17_old = $("#leave17_old").val();
	var leave18 = $("#leave18").val();//当年已使用假期天数-非带薪病假
	var leave18_old = $("#leave18_old").val();
	//确认消息 \r\n
	var confirmMsg = "";
	if(leave17!=leave17_old){
		confirmMsg = confirmMsg + "当年已用带薪病假从"+leave17_old+"天修改为"+leave17+"天\r\n";
	}
	if(leave18!=leave18_old){
		confirmMsg = confirmMsg + "当年已用非带薪病假从"+leave18_old+"天修改为"+leave18+"天\r\n";
	}
	if(confirmMsg!=""){
		$("#confirmMsg2").val(confirmMsg);
		$("#confirmDiv2").show();
	}
}

function saveSickLeave(){
	pageLoading(true);//加载动画
	var leave17 = $("#leave17").val();//当年已使用假期天数-带薪病假
	var leave18 = $("#leave18").val();//当年已使用假期天数-非带薪病假
	var remark	= $("#remarkSick").val();//病假 -备注
	var data = {leaveType:2,employeeId:$("#employeeId").val(),leave17:leave17,leave18:leave18,remark:remark};
	updateLeave(2,data);
}

function confirmOffLeave(){
	var leave11 = $("#leave11").val();//剩余假期天数-调休小时数
	var leave11_old = $("#leave11_old").val();
	var leave20 = $("#leave20").val();//当年已使用假期天数-调休小时数
	var leave20_old = $("#leave20_old").val();
	var leave36 = $("#leave36").val();//去年剩余调休小时数
	var leave36_old = $("#leave36_old").val();
	var leave37 = $("#leave37").val();//去年已用调休小时数
	var leave37_old = $("#leave37_old").val();
	//确认消息 \r\n
	var confirmMsg = "";
	if(leave11!=leave11_old){
		confirmMsg = confirmMsg + "当年剩余调休小时数从"+leave11_old+"小时修改为"+leave11+"小时\r\n";
	}
	if(leave20!=leave20_old){
		confirmMsg = confirmMsg + "当年已用调休小时数从"+leave20_old+"小时修改为"+leave20+"小时\r\n";
	}
	if(leave36!=leave36_old){
		confirmMsg = confirmMsg + "去年剩余调休小时数从"+leave36_old+"小时修改为"+leave36+"小时\r\n";
	}
	if(leave37!=leave37_old){
		confirmMsg = confirmMsg + "去年已用调休小时数从"+leave37_old+"小时修改为"+leave37+"小时\r\n";
	}
	if(confirmMsg!=""){
		$("#confirmMsg3").val(confirmMsg);
		$("#confirmDiv3").show();
	}
}

function saveOffLeave(){
	pageLoading(true);//加载动画
	var leave11 = $("#leave11").val();//剩余假期天数-调休小时数
	var leave20 = $("#leave20").val();//当年已使用假期天数-调休小时数
	var leave36 = $("#leave36").val();//去年剩余调休小时数
	var leave37 = $("#leave37").val();//去年已用调休小时数
	var remark	= $("#remarkOff").val();//调休 -备注
	
	var data = {leaveType:3,employeeId:$("#employeeId").val(),leave11:leave11,leave20:leave20,leave36:leave36,leave37:leave37,remark:remark};
	updateLeave(3,data);
}

function confirmOtherLeave(){
	var leave19 = $("#leave19").val();//当年已使用假期天数-事假
	var leave19_old = $("#leave19_old").val();
	var leave21 = $("#leave21").val();//当年已使用假期天数-婚假
	var leave21_old = $("#leave21_old").val();
	var leave22 = $("#leave22").val();//当年已使用假期天数-丧假
	var leave22_old = $("#leave22_old").val();
	var leave23 = $("#leave23").val();//当年已使用假期天数-陪产假
	var leave23_old = $("#leave23_old").val();
	var leave25 = $("#leave25").val();//当年已使用假期天数-产假
	var leave25_old = $("#leave25_old").val();
	var leave24 = $("#leave24").val();//当年已使用假期天数-产前假
	var leave24_old = $("#leave24_old").val();
	var leave26 = $("#leave26").val();//当年已使用假期天数-哺乳假
	var leave26_old = $("#leave26_old").val();
	var leave27 = $("#leave27").val();//当年已使用假期天数-流产假
	var leave27_old = $("#leave27_old").val();
	var leave28 = $("#leave28").val();//当年已使用假期天数-其他
	var leave28_old = $("#leave28_old").val();
	//确认消息 \r\n
	var confirmMsg = "";
	if(leave19!=leave19_old){
		confirmMsg = confirmMsg + "当年已用事假从"+leave19_old+"天修改为"+leave19+"天\r\n";
	}
	if(leave21!=leave21_old){
		confirmMsg = confirmMsg + "当年已用婚假从"+leave21_old+"天修改为"+leave21+"天\r\n";
	}
	if(leave22!=leave22_old){
		confirmMsg = confirmMsg + "当年已用丧假从"+leave22_old+"天修改为"+leave22+"天\r\n";
	}
	if(leave23!=leave23_old){
		confirmMsg = confirmMsg + "当年已用陪产假从"+leave23_old+"天修改为"+leave23+"天\r\n";
	}
	if(leave25!=leave25_old){
		confirmMsg = confirmMsg + "当年已用产假从"+leave25_old+"天修改为"+leave25+"天\r\n";
	}
	if(leave24!=leave24_old){
		confirmMsg = confirmMsg + "当年已用产前假从"+leave24_old+"天修改为"+leave24+"天\r\n";
	}
	if(leave26!=leave26_old){
		confirmMsg = confirmMsg + "当年已用哺乳假从"+leave26_old+"天修改为"+leave26+"天\r\n";
	}
	if(leave27!=leave27_old){
		confirmMsg = confirmMsg + "当年已用流产假从"+leave27_old+"天修改为"+leave27+"天\r\n";
	}
	if(leave28!=leave28_old){
		confirmMsg = confirmMsg + "当年已用其他假从"+leave28_old+"天修改为"+leave28+"天\r\n";
	}
	if(confirmMsg!=""){
		$("#confirmMsg4").val(confirmMsg);
		$("#confirmDiv4").show();
	}
}

function saveOtherLeave(){
	pageLoading(true);//加载动画
	var leave19 = $("#leave19").val();//当年已使用假期天数-事假
	var leave21 = $("#leave21").val();//当年已使用假期天数-婚假
	var leave22 = $("#leave22").val();//当年已使用假期天数-丧假
	var leave23 = $("#leave23").val();//当年已使用假期天数-陪产假
	var leave25 = $("#leave25").val();//当年已使用假期天数-产假
	var leave24 = $("#leave24").val();//当年已使用假期天数-产前假
	var leave26 = $("#leave26").val();//当年已使用假期天数-哺乳假
	var leave27 = $("#leave27").val();//当年已使用假期天数-流产假
	var leave28 = $("#leave28").val();//当年已使用假期天数-其他
	var remark	= $("#remarkOther").val();//其他 -备注
	var data = {leaveType:4,employeeId:$("#employeeId").val(),leave19:leave19,leave21:leave21,leave22:leave22,leave23:leave23,leave25:leave25
			,leave24:leave24,leave26:leave26,leave27:leave27,leave28:leave28,remark:remark};
	updateLeave(4,data);
}


function closeCounterDiv(){
	$("#counter").hide();
}

function countLeave(data){
	$.ajax({
 		async : false,
 		type : "post",
		dataType:"json",
		data :data,
 		url : contextPath + "/empLeave/queryEmpLeaveInfoByDate.htm",
 		success : function(response) {
 			if(response.sucess){
 				
 				
 				$("#countCnName").html(response.cnName);
 				$("#countCode").html(response.code); 
 				$("#firstEntryTime").html(response.firstEntryTime); 
 				
 				$("#totalDays").html(response.totalDays);
 				$("#totalSickDays").html(response.totalSickDays);

 				$("#toQuitTotalLeaveDays").html(response.toQuitTotalLeaveDays);
 				$("#toQuitTotalSickDays").html(response.toQuitTotalSickDays);
 				
 				$("#totalUsedLeaveDays").html(response.totalUsedLeaveDays);
 				$("#uesdPaidSickDays").html(response.uesdPaidSickDays);
 				
 				$("#totalAllowRemainDays").html(response.totalAllowRemainDays);
 				$("#allowPaidSickDays").html(response.allowPaidSickDays);
 				
 				$("#legalLeaveDays").html(response.legalLeaveDays);
 				$("#uesdUnPaidSickDays").html(response.uesdUnPaidSickDays);
 				
 				$("#welfareLeaveDays").html(response.welfareLeaveDays);
 				$("#sickBlockDays").html(response.sickBlockDays);
 				$("#unSickBlockDays").html(response.unSickBlockDays);
 				$("#lastYearTotalAllowRemainDays").html(response.lastYearTotalAllowRemainDays);
 				$("#lastYearLegalLeaveDays").html(response.lastYearLegalLeaveDays);
 				$("#lastYearWelfareLeaveDays").html(response.lastYearWelfareLeaveDays);
 				$("#totalBlockedDays").html(response.totalBlockedDays);
 			}else{
				JEND.page.showError(response.msg);
			}
 		}
	});
}

function updateLeave(leaveType,data){
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data :data,
 		url : contextPath + "/empLeave/updateEmpLeaveNew.htm",
 		success : function(response) {
 			if(response.sucess){
 				closeDiv();
 				gotoPage($("#pageNo").val());
 			}else{
 				JEND.page.showError(response.msg);
 			}
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
			closeDiv();
        }
 	});
}

/**
 * 剩余假期table 布局
 */
//对象原型扩展
String.prototype.substitute = function (data) {
    if (data && typeof (data) == 'object') {
        return this.replace(/\{([^{}]+)\}/g, function (match, key) {
            var value = data[key];
            return (value !== undefined) ? '' + value : '';
        });
    } else {
        return this.toString();
    }
}


var holidayTable = {
	init: function () {

        this.bindEvent();
    },
    tpls: {
        baseInfoTpl: '<li class="workerItem">\
                        <div class="baseInfo clearfix">\
                            <span class="showHolidayInfoBtn" data-target="all"></span>\
                            <span>{code}</span>\
                            <span>{cnName}</span>\
                            <span>{firstEntryTime}</span>\
                            <span>{departName}</span>\
                            <span>{quitTime}</span>\
                            <span>{jobStatus}</span>\
                            <span class="calculator" data-index="{index}"></span>\
                        </div>\
                        <div class="holidayInfo hide">{holidayContent}</div>\
                    </li>',
        holidayYearTpl: '<div class="holiday_year holidayBox">\
                            <h3>\
                                <span class="showHolidayInfoBtn" data-target="single"></span>\
                                <em>年假</em>\
                                <a href="javascript:;" data-index="{index}" data-type="year">修改</a>\
                            </h3>\
                            <div class="holiday_table hide">\
                                <table>\
                                    <thead>\
                                        <tr>\
                                            <th colspan="2">当年年假总天数</th>\
                                            <th colspan="2">当年年假剩余天数</th>\
                                            <th colspan="2">当年已用年假天数</th>\
                                            <th colspan="2">截止目前年假剩余天数</th>\
                                            <th rowspan="2">透支年假天数</th>\
                                            <th colspan="2">去年年假总天数</th>\
                                            <th colspan="2">去年已用年假天数</th>\
                                            <th colspan="2">去年年假剩余天数</th>\
                                        </tr>\
                                        <tr>\
                                            <th>法定</th>\
                                            <th>福利</th>\
                                            <th>法定</th>\
                                            <th>福利</th>\
                                            <th>法定</th>\
                                            <th>福利</th>\
                                            <th>法定</th>\
                                            <th>福利</th>\
                                            <th>法定</th>\
                                            <th>福利</th>\
                                            <th>法定</th>\
                                            <th>福利</th>\
                                            <th>法定</th>\
                                            <th>福利</th>\
                                        </tr>\
                                    </thead>\
                                    <tbody>\
                                        <tr>\
                                            <td>{leave2}</td>\
                                            <td>{leave3}</td>\
                                            <td>{leave7}</td>\
                                            <td>{leave8}</td>\
                                            <td>{leave14}</td>\
                                            <td>{leave15}</td>\
                                            <td>{leave12}</td>\
                                            <td>{leave13}</td>\
                                            <td>{leave29}</td>\
                                            <td>{leave32}</td>\
                                            <td>{leave33}</td>\
                                            <td>{leave34}</td>\
                                            <td>{leave35}</td>\
                                            <td>{leave5}</td>\
                                            <td>{leave6}</td>\
                                        </tr>\
                                    </tbody>\
                                </table>\
                            </div>\
                        </div>',
        holidayIllTpl: '<div class="holiday_ill holidayBox">\
                            <h3>\
                                <span class="showHolidayInfoBtn" data-target="single"></span>\
                                <em>病假</em>\
                                <a href="javascript:;" data-index="{index}" data-type="ill">修改</a>\
                            </h3>\
                            <div class="holiday_table hide">\
                                <table>\
                                    <thead>\
                                        <tr>\
                                            <th>当年带薪病假总天数</th>\
                                            <th>当年已用带薪病假天数</th>\
                                            <th>当年剩余带薪病假天数</th>\
                                            <th>当年已用非带薪病假天数</th>\
                                            <th>截止目前剩余带薪病假天数</th>\
                                            <th>已透支带薪病假天数</th>\
                                        </tr>\
                                    </thead>\
                                    <tbody>\
                                        <tr>\
                                            <td>{leave4}</td>\
                                            <td>{leave17}</td>\
                                            <td>{leave10}</td>\
                                            <td>{leave18}</td>\
                                            <td>{leave31}</td>\
                                            <td>{leave30}</td>\
                                        </tr>\
                                    </tbody>\
                                </table>\
                            </div>\
                        </div>',
        holidayOffTpl: '<div class="holiday_off holidayBox">\
                            <h3>\
                                <span class="showHolidayInfoBtn" data-target="single"></span>\
                                <em>调休</em>\
                                <a href="javascript:;" data-index="{index}" data-type="off">修改</a>\
                            </h3>\
                            <div class="holiday_table hide">\
                                <table>\
                                    <thead>\
                                        <tr>\
                                            <th>当年剩余调休小时数</th>\
                                            <th>当年已用调休小时数</th>\
                                            <th>去年剩余调休小时数</th>\
                                            <th>去年已用调休小时数</th>\
                                        </tr>\
                                    </thead>\
                                    <tbody>\
                                        <tr>\
                                            <td>{leave11}</td>\
                                            <td>{leave20}</td>\
                                            <td>{leave36}</td>\
                                            <td>{leave37}</td>\
                                        </tr>\
                                    </tbody>\
                                </table>\
                            </div>\
                        </div>',
        holidayOtherTpl: '<div class="holiday_other holidayBox">\
                            <h3>\
                                <span class="showHolidayInfoBtn" data-target="single"></span>\
                                <em>其他假期</em>\
                                <a href="javascript:;" data-index="{index}" data-type="other">修改</a>\
                            </h3>\
                            <div class="holiday_table hide">\
                                <table>\
                                    <thead>\
                                        <tr>\
                                            <th colspan="9">当年已用假期天数</th>\
                                        </tr>\
                                        <tr>\
                                            <th>事假</th>\
                                            <th>丧假</th>\
                                            <th>婚假</th>\
                                            <th>陪产假</th>\
                                            <th>产假</th>\
                                            <th>产前假</th>\
                                            <th>哺乳假</th>\
                                            <th>流产假</th>\
                                            <th>其他</th>\
                                        </tr>\
                                    </thead>\
                                    <tbody>\
                                        <tr>\
                                            <td>{leave19}</td>\
                                            <td>{leave22}</td>\
                                            <td>{leave21}</td>\
                                            <td>{leave23}</td>\
                                            <td>{leave25}</td>\
                                            <td>{leave24}</td>\
                                            <td>{leave26}</td>\
                                            <td>{leave27}</td>\
                                            <td>{leave28}</td>\
                                        </tr>\
                                    </tbody>\
                                </table>\
                            </div>\
                        </div>'
    },
    bindEvent: function () {

        var self = this;

        $('#hgc_restHoliday').on('click', '.showHolidayInfoBtn', function () {
            var targetType = $(this).attr('data-target');
            if (targetType === 'all') {
            	$(this).toggleClass('icon_add');
                $(this).closest('.baseInfo').siblings('.holidayInfo').toggleClass('hide');
            } else if (targetType === 'single') {
            	$(this).toggleClass('icon_add');
                $(this).closest('h3').siblings('.holiday_table').toggleClass('hide');
            }
        })
        
        $('#hgc_restHoliday').on('click','h3 a',function(event){
        	event.preventDefault();
        	var type = $(this).attr('data-type');
        	var index = +$(this).attr('data-index');
        	var data = self.reportList[index];
        	$(".codeUpdate").val(data.employee.code);
            $(".nameUpdate").val(data.employee.cnName);
            $("#employeeId").val(data.employee.id);
        	switch(type){
	        	case 'year':
                    $("#updateYearDiv").show();
                	$("#leave2").val(data.leave2);//当年假期天数总数-法定年假
                	$("#leave2_old").val(data.leave2);
                    $("#leave14").val(data.leave2-data.leave7);//当年已使用假期天数-法定年假
                    $("#leave14_old").val(data.leave2-data.leave7);
                    $("#leave7").val(data.leave7);//剩余假期天数-当年法定年假
                    $("#leave3").val(data.leave3);//当年假期天数总数-福利年假
                    $("#leave3_old").val(data.leave3);
                    $("#leave15").val(data.leave3-data.leave8);//当年已使用假期天数-福利年假
                    $("#leave15_old").val(data.leave3-data.leave8);
                    $("#leave8").val(data.leave8);//剩余假期天数-当年福利年假
                    $("#leave32").val(data.leave32);//去年年假总数-法定
                    $("#leave32_old").val(data.leave32);
                    $("#leave34").val(data.leave32-data.leave5);//去年已用年假天数-法定
                    $("#leave34_old").val(data.leave32-data.leave5);
                    $("#leave5").val(data.leave5);//剩余假期天数-去年法定年假
                    $("#leave33").val(data.leave33);//去年年假总数-福利
                    $("#leave33_old").val(data.leave33);
                    $("#leave35").val(data.leave33-data.leave6);//去年已用年假天数-福利
                    $("#leave35_old").val(data.leave33-data.leave6);
                    $("#leave6").val(data.leave6);//剩余假期天数-去年福利年假
	        		break;
				case 'ill':
					$("#updateSickDiv").show();	
					$("#leave17").val(data.leave17);
					$("#leave17_old").val(data.leave17);
	                $("#leave18").val(data.leave18);
	                $("#leave18_old").val(data.leave18);
				    break;
				case 'off':
					$("#updateOffDiv").show();
					$("#leave11").val(data.leave11);
					$("#leave11_old").val(data.leave11);
	                $("#leave20").val(data.leave20);
	                $("#leave20_old").val(data.leave20);
	                $("#leave36").val(data.leave36);
	                $("#leave36_old").val(data.leave36);
	                $("#leave37").val(data.leave37);
	                $("#leave37_old").val(data.leave37);
					break;
				case 'other':
					$("#updateOtherDiv").show();
					$("#leave19").val(data.leave19);
					$("#leave19_old").val(data.leave19);
	                $("#leave21").val(data.leave21);
	                $("#leave21_old").val(data.leave21);
	                $("#leave22").val(data.leave22);
	                $("#leave22_old").val(data.leave22);
	                $("#leave23").val(data.leave23);
	                $("#leave23_old").val(data.leave23);
	                $("#leave24").val(data.leave24);
	                $("#leave24_old").val(data.leave24);
	                $("#leave25").val(data.leave25);
	                $("#leave25_old").val(data.leave25);
	                $("#leave26").val(data.leave26);
	                $("#leave26_old").val(data.leave26);
	                $("#leave27").val(data.leave27);
	                $("#leave27_old").val(data.leave27);
	                $("#leave28").val(data.leave28);
	                $("#leave28_old").val(data.leave28);
					break;
        	}
        })
        
        $('#hgc_restHoliday').on('click','.calculator',function(event){
        	event.preventDefault();
        	var index = +$(this).attr('data-index');
        	var data = self.reportList[index].employee;
        	quitCount(data);
        })
        
        

    },
    reportList:[],
    renderList: function (data) {
        var self = this;
        self.reportList = data;
        var workerList = $('#hgc_restHoliday ul');
        workerList.find('.workerItem').remove();
        var holidayTpl = self.tpls.holidayYearTpl+self.tpls.holidayIllTpl+self.tpls.holidayOffTpl+self.tpls.holidayOtherTpl;
        data.forEach(function(item,index){
        	var tableHtml = '';
        	item.index = index;
        	item.employee.index = index;
            item.employee.holidayContent = holidayTpl.substitute(item);
            if(item.employee.jobStatus==1){
            	item.employee.jobStatus='离职';
            }else if(item.employee.jobStatus==2){
            	item.employee.jobStatus='待离职';
            }else{
            	item.employee.jobStatus='在职';
            }
            tableHtml+=self.tpls.baseInfoTpl.substitute(item.employee);
            workerList.append(tableHtml);
        });
    }
}

function closeDiv(){
	$("#updateYearDiv").hide();
	$("#updateSickDiv").hide();
	$("#updateOffDiv").hide();
	$("#updateOtherDiv").hide();
	$("#updateDiv").hide();
	$("#confirmDiv1").hide();
	$("#confirmDiv2").hide();
	$("#confirmDiv3").hide();
	$("#confirmDiv4").hide();
}
var SignExcel = function(){  
    
    this.init = function(){  
          
        //模拟上传excel  
         $("#uploadEventBtn").unbind("click").bind("click",function(){  
             $("#uploadEventFile").click();  
         });  
         $("#uploadEventFile").bind("change",function(){  
             $("#uploadEventPath").attr("value",$("#uploadEventFile").val());  
         });  
          
    };  
    //点击上传按钮  
    this.uploadBtn = function(){  
        var uploadEventFile = $("#uploadEventFile").val();  
        if(uploadEventFile == ''){  
        	JEND.load('util.dialog', function() {
				JEND.util.dialog.alert("请选择Excel文件");
			});  
        }else if(uploadEventFile.lastIndexOf(".xls")<0){//可判断以.xls和.xlsx结尾的excel  
        	JEND.load('util.dialog', function() {
				JEND.util.dialog.alert("只能上传Excel文件");
			});  
        }else{  
            var url =  basePath + 'empLeave/importEmpLeaveList.htm';  
            var formData = new FormData($('form')[1]);  
            signExcel.sendAjaxRequest(url,'POST',formData);  
        }  
    };  
      
    this.sendAjaxRequest = function(url,type,data){
    	pageLoading(true);//加载动画
    	
        $.ajax({  
            url : url,  
            type : type,  
            data : data,  
    		dataType:'json',
            success : function(result) {
            	var msg = result.response;
            	getSetForm(msg)
                pageLoading(false);//关闭动画
            },  
            error : function() {  
                pageLoading(false);//关闭动画
            },
            complete:function(XMLHttpRequest,textStatus){  
    			if(XMLHttpRequest.status=="403"){
    				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
    			}
    			pageLoading(false);//关闭动画
            },
            cache : false,  
            contentType : false,  
            processData : false  
        });  
    };  
}
//导入弹框
function getSetForm(obj){
	$("#importMsg").val(obj);
	$("#updateDiv").show();
}

//实时变更当年年假法定总数，已用，剩余
function calCurLegalYear(){
	$("#leave2").bind("input propertychange change",function(event){
		$("#leave7").val($("#leave2").val()-$("#leave14").val());
	});
	$("#leave14").bind("input propertychange change",function(event){
		$("#leave7").val($("#leave2").val()-$("#leave14").val());
	});
}

//实时变更当年年假福利总数，已用，剩余
function calCurWelfareYear(){
	$("#leave3").bind("input propertychange change",function(event){
		$("#leave8").val($("#leave3").val()-$("#leave15").val());
	});
	$("#leave15").bind("input propertychange change",function(event){
		$("#leave8").val($("#leave3").val()-$("#leave15").val());
	});
}

//实时变更去年年假法定总数，已用，剩余
function calLastLegalYear(){
	$("#leave32").bind("input propertychange change",function(event){
		$("#leave5").val($("#leave32").val()-$("#leave34").val());
	});
	$("#leave34").bind("input propertychange change",function(event){
		$("#leave5").val($("#leave32").val()-$("#leave34").val());
	});
}

//实时变更去年年假福利总数，已用，剩余
function calLastWelfareYear(){
	$("#leave33").bind("input propertychange change",function(event){
		$("#leave6").val($("#leave33").val()-$("#leave35").val());
	});
	$("#leave35").bind("input propertychange change",function(event){
		$("#leave6").val($("#leave33").val()-$("#leave35").val());
	});
}

function add(obj){
	var addObj = $(obj).parent().prev(".num");
	if($(addObj).attr("id")=="leave2"){
		$("#leave7").val(parseFloat($("#leave2").val())-parseFloat($("#leave14").val())+0.5);
	}
	if($(addObj).attr("id")=="leave14"){
		$("#leave7").val(parseFloat($("#leave2").val())-parseFloat($("#leave14").val())-0.5);
	}
	if($(addObj).attr("id")=="leave3"){
		$("#leave8").val(parseFloat($("#leave3").val())-parseFloat($("#leave15").val())+0.5);
	}
	if($(addObj).attr("id")=="leave15"){
		$("#leave8").val(parseFloat($("#leave3").val())-parseFloat($("#leave15").val())-0.5);
	}
	if($(addObj).attr("id")=="leave32"){
		$("#leave5").val(parseFloat($("#leave32").val())-parseFloat($("#leave34").val())+0.5);
	}
	if($(addObj).attr("id")=="leave34"){
		$("#leave5").val(parseFloat($("#leave32").val())-parseFloat($("#leave34").val())-0.5);
	}
	if($(addObj).attr("id")=="leave33"){
		$("#leave6").val(parseFloat($("#leave33").val())-parseFloat($("#leave35").val())+0.5);
	}
	if($(addObj).attr("id")=="leave35"){
		$("#leave6").val(parseFloat($("#leave33").val())-parseFloat($("#leave35").val())-0.5);
	}
	$(addObj).val(parseFloat($(addObj).val())+0.5);
}

function reduce(obj){
	var reduceObj = $(obj).parent().prev(".num");
	if(parseFloat($(reduceObj).val())>0){
		if($(reduceObj).attr("id")=="leave2"){
			$("#leave7").val(parseFloat($("#leave2").val())-parseFloat($("#leave14").val())-0.5);
		}
		if($(reduceObj).attr("id")=="leave14"){
			$("#leave7").val(parseFloat($("#leave2").val())-parseFloat($("#leave14").val())+0.5);
		}
		if($(reduceObj).attr("id")=="leave3"){
			$("#leave8").val(parseFloat($("#leave3").val())-parseFloat($("#leave15").val())-0.5);
		}
		if($(reduceObj).attr("id")=="leave15"){
			$("#leave8").val(parseFloat($("#leave3").val())-parseFloat($("#leave15").val())+0.5);
		}
		if($(reduceObj).attr("id")=="leave32"){
			$("#leave5").val(parseFloat($("#leave32").val())-parseFloat($("#leave34").val())-0.5);
		}
		if($(reduceObj).attr("id")=="leave34"){
			$("#leave5").val(parseFloat($("#leave32").val())-parseFloat($("#leave34").val())+0.5);
		}
		if($(reduceObj).attr("id")=="leave33"){
			$("#leave6").val(parseFloat($("#leave33").val())-parseFloat($("#leave35").val())-0.5);
		}
		if($(reduceObj).attr("id")=="leave35"){
			$("#leave6").val(parseFloat($("#leave33").val())-parseFloat($("#leave35").val())+0.5);
		}
		$(reduceObj).val(parseFloat($(reduceObj).val())-0.5);
	}
}
