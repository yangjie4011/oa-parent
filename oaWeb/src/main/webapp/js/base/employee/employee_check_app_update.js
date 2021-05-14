
var backToMain = function() {//通用返回页面
	$("form").find(".edit-page-a").hide();
	$(".my-record").show();
}

var editEvent = function() {

	$("#baseInfoForm .quit,#baseJobForm .quit,#baseInfoForm .goback,#baseJobForm .goback,#remarkForm .goback,#remarkForm .quit")
	.click(function() {//点击取消
				backToMain();
	});

	$("#baseInfoForm .birthday").on('input',function(e){ //根据生日计算年龄
		var birthday = $("#baseInfoForm .birthday").val();
		var today = new Date().format("yyyy-mm-dd"); 
		var year = getAge(birthday);
		//console.info("今天"+today+",生日"+birthday+",年龄:"+year);
		if(year != ''){
			year = Math.floor(year);
		}
		$("#baseInfoForm .age").val(year);
	});

	$("#baseJobForm .firstEntryTime").on('input',function(e){ 
		//根据生日计算年龄
		var firstEntryTime = $("#baseJobForm .firstEntryTime").val();
		//不是离职状态，计算
		if($("#jobStatus").val()!=1){
			var year= countYear(firstEntryTime);			
			$("#baseJobForm .ourAge").val(year);
			$("#empJobTable #ourAge").text(year);
			
		}
	});
	
	$("#baseInfoForm .save").click(function() { //保存基础信息
		$("#baseId").val($("#id").val());
		
		var form = $("#baseInfoForm");
		
		var flag = checkForm(form);
		
		if(flag){
			$.ajax({
				async : true,
				type : "post",
				dataType : "json",
				data : form.serialize(),
				url : contextPath + "/employeeApp/updateById.htm",
				success : function(data) {
					OA.titlePup(data.result, 'win');
					initEmployee();
					form.find(".goback").click();
				}
			})
		}
	});

	$("#baseJobForm .save").click(function() { //保存在职信息
		$("#jobId").val($("#id").val());
		var form = $("#baseJobForm");
		var flag = true;
		var jobStatus = form.find(".jobStatus").val();
		var beforeWorkAge = form.find(".beforeWorkAge").val();
		form.find(".beforeWorkAge").val(beforeWorkAge);
		if(jobStatus == '1'){//离职，不验证其他输入项
			console.log("离职！！！");
		}else{
			flag = checkForm(form);
		}
		if(flag){
			
			/**针对指纹ID特殊处理，如果未修改，则置为空，以免后台调修改打卡机的接口**/
			var oldFingerprintId = $("#empJobTable #fingerprintId").text();
			var newFingerprintId = form.find("#fingerprintId").val();
			console.log("指纹ID 旧："+oldFingerprintId +",新："+newFingerprintId);
			if(oldFingerprintId == newFingerprintId){
				form.find("#fingerprintId").val("");//置为空
			}
			
			OA.pageLoading(true);
			$.ajax({
				async : true,
				type : "post",
				dataType : "json",
				data : form.serialize(),
				url : contextPath + "/empPosition/updateEmpPositionInfo.htm",
				success : function(data) {
					OA.pageLoading(false);
					OA.titlePup(data.result, 'win');
					console.info(JSON.stringify(data));
					if(data.success == 'true'){
						initEmployee();
						form.find(".goback").click();
					}else{
						
					}
				}
			})
		}
	});

	$("#remarkForm .save").click(function() { //保存备注
		
		var form = $("#remarkForm");
		$.ajax({
			async : true,
			type : "post",
			dataType : "json",
			data : form.serialize(),
			url : contextPath + "/employeeApp/updateById.htm",
			success : function(data) {
				OA.titlePup(data.result, 'win');
				initEmployee();
				form.find(".goback").click();
			}
		})
	});

	addEvent("schoolForm");
	addEvent("tranningForm");
	addEvent("workForm");
	addEvent("urgentContactForm");
	addEvent("spouseForm");
	addEvent("childForm");
	addEvent("achievementForm");
	addEvent("companyTrainForm");
	addEvent("contractForm");
	addEvent("empAppraiseForm");
	addEvent("empPositionForm");

	function getUrl(formId, type) {
		var url = contextPath;
		if (type == "add") {
			if (formId == 'schoolForm') {
				url = url + "/empSchool/save.htm";
			} else if (formId == 'tranningForm') {
				url = url + "/empTraining/save.htm";
			} else if (formId == 'workForm') {
				url = url + "/empWorkRecord/save.htm";
			} else if (formId == 'urgentContactForm') {
				url = url + "/empUrgentContact/save.htm";
			} else if (formId == 'spouseForm') {
				url = url + "/empFamilyMember/save.htm";
			} else if (formId == 'childForm') {
				url = url + "/empFamilyMember/save.htm";
			} else if (formId == 'achievementForm') {
				url = url + "/empAchievement/save.htm";
			} else if (formId == 'companyTrainForm') {
				url = url + "/empTraining/save.htm";
			} else if (formId == 'contractForm') {
				url = url + "/empContract/save.htm";
			} else if (formId == 'empAppraiseForm') {
				url = url + "/empAppraise/save.htm";
			} else if (formId == 'empPositionForm') {
				url = url + "/empPostRecord/save.htm";
			}
		} else if (type == "update") {
			if (formId == 'schoolForm') {
				url = url + "/empSchool/updateById.htm"
			} else if (formId == 'tranningForm') {
				url = url + "/empTraining/updateById.htm"
			} else if (formId == 'workForm') {
				url = url + "/empWorkRecord/updateById.htm"
			} else if (formId == 'urgentContactForm') {
				url = url + "/empUrgentContact/updateById.htm"
			} else if (formId == 'spouseForm') {
				url = url + "/empFamilyMember/updateById.htm"
			} else if (formId == 'childForm') {
				url = url + "/empFamilyMember/updateById.htm"
			} else if (formId == 'achievementForm') {
				url = url + "/empAchievement/updateById.htm"
			} else if (formId == 'companyTrainForm') {
				url = url + "/empTraining/updateById.htm"
			} else if (formId == 'contractForm') {
				url = url + "/empContract/updateById.htm";
			} else if (formId == 'empAppraiseForm') {
				url = url + "/empAppraise/updateById.htm";
			} else if (formId == 'empPositionForm') {
				url = url + "/empPostRecord/updateById.htm";
			}
		}
		return url;
	}

	function initByFormId(formId) {
		if (formId == 'schoolForm') {
			initEmpSchool();
		} else if (formId == 'tranningForm') {
			initEmpTranning();
		} else if (formId == 'workForm') {
			initWork();
		} else if (formId == 'urgentContactForm') {
			initUrgentContact();
		} else if (formId == 'spouseForm') {
			initFamilyMember();
		} else if (formId == 'childForm') {
			initFamilyMember();
		} else if (formId == 'achievementForm') {
			initAchieverment();
		} else if (formId == 'companyTrainForm') {
			initCompanyTrain();
		} else if (formId == 'contractForm') {
			initContract();
		} else if (formId == 'empAppraiseForm') {
			initEmpAppraise();
		} else if (formId == 'empPositionForm') {
			initEmpPosition();
		}
	}

	function addEvent(formId) {
		var form = $("#" + formId);
		form.find(".add-type").click(function() {//添加教育经历
			$(this).hide();
			form.find("li").find("input,textarea").val("");
			form.find(".tip").text("新增");
			form.find(".delete").hide();
			form.find("li").not(":first").hide();
			form.find("li:first").show();
			form.find(".double-btn").show();
			form.attr("action", getUrl(formId, "add"));//暂存保存url
		});

		$(document).on(
				'click',
				'#' + formId + ' .edit-page-a .main ul li:nth-child(n+2)',
				function() {//动态详情编辑事件，仅仅hr可编辑
					var authority = $("#authority").val();
					if(authority != 'hr'){
						return false;
					}

					//赋值到第一个li,在第一个li编辑
					var valueArea = $(this).find(".r-input");
					var inputArea = form.find("li:first")
							.find("input,textarea");
					$(valueArea).each(function(i1, item1) {
						var id1 = $(item1).attr("id");
						$(inputArea).each(function(i2, item2) {
							var id2 = $(item2).attr("class");
							//不适用id，是因为前端时间控件，每个时间事件的id必须唯一
							if (id1 == id2) {
								$(item2).val($(item1).text());
							}
						});
					});

					form.find(".add-type").hide();
					form.find(".tip").text("详情编辑");
					form.find(".delete").show();
					form.find("li").not(":first").hide();
					form.find("li:first").show();
					form.find(".double-btn").show();
				})

		form.find(".save").click(function() { //保存事件（更新或者增加）
			var url = form.attr("action");
			form.find("li").not(":first").remove();//移除多余元素

			if (typeof url != 'undefined' && '' != url) {//手动添加了url，新增事件

			} else {//编辑事件
				url = getUrl(formId, "update");
			}
			
			var flag = true;
			if(form.find("#delFlag").val()=='0'){//更新或者保存时才验证
				flag = checkForm(form);
			}

	        if(flag){
				$.ajax({
					async : true,
					type : "post",
					dataType : "json",
					data : form.serialize(),
					url : url,
					success : function(data) {
						OA.titlePup(data.result, 'win');
						initByFormId(formId);
						form.find("#delFlag").val('0');//操作过后必须重置为0
						form.attr("action", "");//移除url
						form.find(".goback").click();//返回上一页
					}
				});
	        }
		});

		form.find(".delete").click(function() { //删除事件，逻辑删除	
			form.find("#delFlag").val('1');
			form.find(".save").click();
		});

		form.find(".quit,.goback").click(function() { //取消事件，不作任何操作
			form.find(".tip").text("详情列表");
			form.find(".delete").hide();
			form.find(".double-btn").hide();
			if(formId!='spouseForm'){//配偶不能添加
				form.find(".add-type").show();
			}

			var firstLi = form.find("li:first");
			if (firstLi.is(":hidden")) {//没有打开编辑和更新页面,返回首页
				backToMain();
			} else {//打开了编辑页面，返回明细
				form.attr("action", "");//移除新增url
				form.find("li:first").hide();
				form.find("li").not(":first").show();
			}
		});

	}
}

var readyEdit = function(companyId) {

	dateSel();
	downBox(companyId);

	$(".get-more").click(function() {//加载更多
		getMore();
	});

	$("#editBase").click(function() {//基础信息编辑
		$(".my-record").hide();
		$("#baseInfoForm > div").show();
	});

	$("#editJob").click(function() {//工作信息编辑
		$(".my-record").hide();
		$("#baseJobForm > div").show();
	});

	$("#editSchoool").click(function() {//学历编辑
		$(".my-record").hide();
		$("#schoolForm > div").show();
	});

	$("#editTrainning").click(function() {//培训编辑
		$(".my-record").hide();
		$("#tranningForm > div").show();
	});

	$("#editWork").click(function() {//工作记录编辑
		$(".my-record").hide();
		$("#workForm > div").show();
	});

	$("#editUrgentContact").click(function() {//基金联系人编辑
		$(".my-record").hide();
		$("#urgentContactForm > div").show();
	});

	$("#editSpouse").click(function() {//夫妻编辑
		$(".my-record").hide();
		$("#spouseForm > div").show();
	});

	$("#editChild").click(function() {//子女编辑
		$(".my-record").hide();
		$("#childForm > div").show();
	});

	$("#editAchievement").click(function() {//绩效编辑
		$(".my-record").hide();
		$("#achievementForm > div").show();
	});

	$("#editCompanyTrain").click(function() {//公司培训编辑
		$(".my-record").hide();
		$("#companyTrainForm > div").show();
	});

	$("#editContract").click(function() {//合同编辑
		$(".my-record").hide();
		$("#contractForm > div").show();
	});

	$("#editAppraise").click(function() {//奖惩编辑
		$(".my-record").hide();
		$("#empAppraiseForm > div").show();
	});

	$("#editPosition").click(function() {//岗位编辑
		$(".my-record").hide();
		$("#empPositionForm > div").show();
	});
	$("#editRemark").click(function() {//备注编辑
		$(".my-record").hide();
		$("#remarkForm > div").show();
	});

}

var downBox = function(companyId) {//下拉总体样式
	var list = [ {
		id : 'sexSelect',
		values : [ '男', '女' ],
		keys : [ '0', '1' ]
	}];
	list.push(countrySelect());//国家
	list.push(nationSelect());//民族
	list.push(politicalStatusSelect());//政治面貌
	list.push(maritalStatusSelect());//婚姻状况
	list.push(industryRelevanceSelect());//从业相关性
	list.push(educationSelect());//学历，文化程度
	list.push(degreeSelect());//学位
	list.push(workTypeSelect());//工时种类
	list.push(positionSelect());//职位
	list.push(positionLevelSelect());//职级
	list.push(positionSeqSelect());//职位序列
	list.push(companySelect());//公司下拉列表
	list.push(empTypeSelect(companyId));//员工类型下拉列表
	list.push(whetherSchedulSelect());//员工是否排班

	//返写下拉列表事件
	//点击赋值事件
	for (var a = 0; a < list.length; a++) {
		$('.edit-page-a #' + list[a].id + '').find('input').attr('readonly',
				'readonly');
		$('.edit-page-a #' + list[a].id + '').click(function() {
			var m = $(this);
			var id = m.attr('id');
			for (var b = 0; b < list.length; b++) {
				if (id == list[b].id) {
					var values = list[b].values;
					var keys = list[b].keys;
					var index;
					OA.bPup(values, function(t) {
						m.find('input[type="text"]').val(t);

						//获取选中值的下标，由于不想改动前端js，故在这里处理
						for (var row in values) {
							if (values[row] == t) {
								index = row;
								break;
							}
						}
						m.find('input[type="hidden"]').val(keys[index]);
						
						//当选择“其他”时，需要手动填写..........I hava no words.
						console.info("id:"+id+","+"值："+ t)
						if(id == 'countrySelect'){
							if(t == '其他国家'){
								$(".countryOtherDiv").show();
							}else{
								$(".countryOtherDiv").hide();
								$(".countryOtherDiv .countryOther").val("");
							}
						}else if(id == "industryRelevanceSelect"){
							if(t == '其他'){
								$(".industryRelevanceOtherDiv").show();
							}else{
								$(".industryRelevanceOtherDiv").hide();
								$(".industryRelevanceOtherDiv .industryRelevanceOther").val("");
							}
						}else if(id == "educationSelect"){
							if(t == '其他'){
								$(".degreeOfEducationOtherDiv").show();
							}else{
								$(".degreeOfEducationOtherDiv").hide();
								$(".degreeOfEducationOtherDiv .degreeOfEducationOther").val("");
							}
						}else if(id == "politicalStatusSelect"){
							if(t == '其他民主党派'){
								$(".politicalStatusOtherDiv").show();
							}else{
								$(".politicalStatusOtherDiv").hide();
								$(".politicalStatusOtherDiv .politicalStatusOther").val("");
							}
						}
					})
				}
			}
		})
	};
	

	//部门职位，职级联动...
	$(".selCompanyPartment").click(function(){
		OA.selPart({
		    cb:function(departId){//再次查询部门信息
		    	$.ajax({
		       		async : false,
		       		type : "post",
		     		dataType:"json",
		     		data : {'departId':departId.partId},
		       		url : contextPath + "/depart/getInfoById.htm",
		       		success : function(data) {
		       			if('' != data){
		       				$('#baseJobForm .positionSelect').unbind();
		       				$('#baseJobForm .positionSelect input').val('');
//		       				$("#baseJobForm .positionLevelName").val('');
//		       				$("#baseJobForm .positionSeqName").val('');
//		       				
			       			$("#baseJobForm #departId").val(departId.partId);
			       			$("#baseJobForm #departName").val(data.name);
			       			$("#baseJobForm #leader").val(data.leader);
			       			$("#baseJobForm #reportToLeader").val(data.leader);
			       			$("#baseJobForm #leaderName").val(data.leaderName);
			       			$("#baseJobForm #reportToLeaderName").val(data.leaderName);
                            
			       			rebuildPositionSelect();
		       			}
		       		}
		     });
		   }
	    });
	});
	
	var rebuildPositionSelect = function() {//职位和汇报对象
			$('#baseJobForm .positionSelect,#baseJobForm .reporterSelect').click(function() {
			    //添加职位,汇报对象下拉
		        var m = $(this);
				var departId = $("#baseJobForm #departId").val();
				var employeeId = $("#jobId").val();
				var jsonObject;
				
				if(m.attr("class")=="r-input positionSelect"){
					jsonObject = positionSelect(departId);//职位;
				}else if(m.attr("class")=="r-input reporterSelect"){
					jsonObject = reporterSelect(employeeId);//负责人,排除自己以外的其他所有M级人员;
				}
				
				if(jsonObject!=null&&typeof(jsonObject.values) != 'undefined'){
					var values = jsonObject.values;
					var keys = jsonObject.keys;
					var index;
					OA.bPup(values, function(t) {
						m.find('input[type="text"]').val(t);

						//获取选中值的下标，由于不想改动前端js，故在这里处理
						for (row in values) {
							if (values[row] == t) {
								index = row;
								break;
							}
						}
						m.find('input[type="hidden"]').val(keys[index]);
						rebuildPosSeqAndLv(m.find('input[type="hidden"]').val());
					});
				}
			});
    }
	rebuildPositionSelect();
	
	var rebuildPosSeqAndLv = function(posId){//选择职位后，重新赋值职级和职位序列
    	$.ajax({
       		async : false,
       		type : "post",
     		dataType:"json",
     		data : {'id':posId},
       		url : contextPath + "/position/getPosSeqAndLv.htm",
       		success : function(data) {
       			if('' != data){
       				$('#baseJobForm .positionLevelSelect').unbind();
   					$('#baseJobForm .positionSeqSelect').unbind();
       				$('#baseJobForm .positionLevelSelect').click(function() {
       				    var m = $(this);
       					OA.bPup(data.levelList, function(t) {
       						m.find('input').val(t);
       					});
       				})
       				$('#baseJobForm .positionSeqSelect').click(function() {
       				    var m = $(this);
       					OA.bPup(data.seqList, function(t) {
       						m.find('input').val(t);
       					});
       				})
       				
       			}
       		}
     });
		
	} 
	rebuildPosSeqAndLv($("#baseJobForm .positionId").val());
}

var dateSel = function() {//选择日期
	var timeSelClick = $('.edit-page-a .time').parent().find('input');
	for (var a = 0; a < timeSelClick.length; a++) {
		$(timeSelClick[a]).attr('id', 'time_' + a + '')
		$(timeSelClick[a]).attr('readonly', 'readonly');
		$('#time_' + a + '').unbind()
		var calendar = new lCalendar();
		calendar.init({
			'trigger' : 'time_' + a + '',
			'type' : 'date'
		})
	}
}

var checkForm = function(form){
    var flag = true;
	form.find('input[type="text"],input[type="number"],textarea').each(function(i, obj) {
		if($(obj).parent().hasClass("null")){//不做验证
			
		}else{
	        if(obj.value == "") {
	            var title = $(obj).parent().parent().find(".l-title").text();
				OA.titlePup(title+" 不能为空!", 'win');
	            flag = false;
	            return false;
	        }
		}
    });
	return flag;
}

var getAge = function (strBirthday) {    
	
	if(typeof strBirthday == 'undefined'){
		return '';
	}
    var returnAge;  
    var strBirthdayArr=strBirthday.split("-");  
    var birthYear = strBirthdayArr[0];  
    var birthMonth = strBirthdayArr[1];  
    var birthDay = strBirthdayArr[2];  
      
    var d = new Date();  
    var nowYear = d.getFullYear();  
    var nowMonth = d.getMonth() + 1;  
    var nowDay = d.getDate();  
      
    if(nowYear == birthYear){  
        returnAge = 0;//同年 则为0岁  
    }  
    else{  
        var ageDiff = nowYear - birthYear ; //年之差  
        if(ageDiff > 0){  
            if(nowMonth == birthMonth) {  
                var dayDiff = nowDay - birthDay;//日之差  
                if(dayDiff < 0)  
                {  
                    returnAge = ageDiff - 1;  
                }  
                else  
                {  
                    returnAge = ageDiff ;  
                }  
            }  
            else  
            {  
                var monthDiff = nowMonth - birthMonth;//月之差  
                if(monthDiff < 0)  
                {  
                    returnAge = ageDiff - 1;  
                }  
                else  
                {  
                    returnAge = ageDiff ;  
                }  
            }  
        }  
        else  
        {  
            returnAge = -1;//返回-1 表示出生日期输入错误 晚于今天  
        }  
    }  
      
    return returnAge;//返回周岁年龄  
}

var getOurAge = function (strDate) {
    var a =new Date(); 
    var b = new Date(Date.parse(strDate.replace(/-/g,  "/"))); 
    var d = (a.getTime() - b.getTime()) / 1000 / 24 / 60 / 60;
    var year ;
    if(d>=0){
        year = d / 365;//不整除取最小的年数或者直接进位（Math.ceil），或者四舍五入Math.round，自己改这个罗
    }else{
    	year = 0;
    }
    year = Math.floor(year);//不要加，外部方法加
    
    return year+"年";
}

function countYear(strDate){
    var s1 = new Date(strDate.replace(/-/g, "/"));
    var s2 = new Date();//当前日期：2017-04-24
    var days = s2.getTime() - s1.getTime();
    var time = parseInt(days / (1000 * 60 * 60 * 24));
    if(time>=0){
        var year = time / 365;//不整除取最小的年数或者直接进位（Math.ceil），或者四舍五入Math.round，自己改这个罗
    }else{
    	var year = 0;
    }
    year = Math.floor(year * 10) / 10;//保存一位小数
    return year+"年";
}


var ourAgeShowFunction = function(ourAge){
	var year = Math.floor(ourAge * 10) / 10;
	return year;
}