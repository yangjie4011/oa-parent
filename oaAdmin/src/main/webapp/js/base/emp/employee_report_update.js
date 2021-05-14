$(function(){
	
	//切换tab
	$(".jui-tabswitch").find("li").each(function(){
		var obj = $(this);
		var id = $(obj).attr("id");
		$(obj).click(function(){
			$(".tabDiv").hide();
			$("div[id='"+id+"']").show();
		})
	})
	
	//加载工作节点
	getProvinces();
	var i = 0;
	$("#workAddressProvince").find("option").each(function(index){
		if($("#workProvinceHidden").val()==$(this).text()){
			i = index-1;
			$("#workAddressProvince").val(i);
			return;
		}
	})
	if(i>0){
		getCitys(i);
		$("#workAddressCity").val($("#workCityHidden").val());
	}
	$("#workAddressProvince").change(function(){
		getCitys(this.value);
	});
	
	//上传头像
	signExcel = new SignExcel();  
	signExcel.init();
});

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
				JEND.util.dialog.alert("请选择图片文件！");
			});
        }else if(uploadEventFile.lastIndexOf(".jpg")<0&&uploadEventFile.lastIndexOf(".png")<0&&uploadEventFile.lastIndexOf(".jpeg")<0&&uploadEventFile.lastIndexOf(".bmp")<0){  
        	JEND.load('util.dialog', function() {
				JEND.util.dialog.alert("只能上传图片文件！");
			});  
        }else{  
            var url =  basePath + 'employeeRecord/uploadEmployeePhoto.htm';  
            var formData = new FormData(); 
            formData.append("file",$('#uploadEventFile')[0].files[0]);
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
            	JEND.page.alert({type:"success", message:result.message});
            	if(result.success){
            		$("#employeePhoto").val(result.picUlr);
            	}
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

function getDepartLeader(obj){
	if($(obj).val()!=""){
		$.ajax({
			async:true,
			type:'post',
			dataType:'json',
			data:{departId:$(obj).val()},
			url:contextPath + "/depart/getInfoById.htm",
			success:function(data){
				$("#departLeader").val(data.leaderName);
			}
		});
		
		var position = $("#positionId");
		position.empty();
		var options = "<option value=''>请选择</option>";
		position.append(options);
		$.ajax({
			async:true,
			type:'post',
			dataType:'json',
			data:{'departId':$(obj).val()},
			url:contextPath + "/position/getListByCondition.htm",
			success:function(data){
				$.each(data, function(index,item) {
					position.append("<option value= " + item.id + ">" + item.positionName + "</option>");
				});
			}
		});
	}
}


function getPositionLevelAndSeqList(obj){
	if($(obj).val()!=""){
		$.ajax({
			async:true,
			type:'post',
			dataType:'json',
			data:{positionId:$(obj).val()},
			url:contextPath + "/employeeRecord/getPositionLevelAndSeqList.htm",
			success:function(data){
				if(data.success){
					var positionLevel = $("#positionLevel");
					positionLevel.empty();
					var options = "<option value=''>请选择</option>";
					positionLevel.append(options);
					
					$.each(data.levelList, function(index) {
						positionLevel.append("<option value= " + data.levelList[index] + ">" + data.levelList[index] + "</option>");
					});
					
					var positionSeq = $("#positionSeq");
					positionSeq.empty();
					var options1 = "<option value=''>请选择</option>";
					positionSeq.append(options1);
					
					$.each(data.seqList, function(index) {
						positionSeq.append("<option value= " + data.seqList[index] + ">" + data.seqList[index] + "</option>");
					});
				}
			}
		});
	}
}

//保存基本信息
function saveBaseInfo(){
	secondConfirmation({
	   "msg":"是否确认保存基本信息？",
		sureFn:function(){
			pageLoading(true);//加载动画	
			var param = {
				id:$("#employeeId").val().trim(),cnName:$("#cnName").val().trim(),engName:$("#engName").val().trim(),
				sex:$("#sex").val().trim(),maritalStatus:$("#maritalStatus").val().trim(),country:$("#country").val().trim(),
				nation:$("#nation").val().trim(),birthday:$("#birthday").val().trim(),age:$("#age").val().trim(),
				politicalStatus:$("#politicalStatus").val().trim(),degreeOfEducation:$("#degreeOfEducation").val().trim(),
				householdRegister:$("#householdRegister").val().trim(),address:$("#address").val().trim(),
				industryRelevance:$("#industryRelevance").val().trim(),workingBackground:$("#workingBackground").val().trim(),
				mobile:$("#mobile").val().trim(),remark:$("#remark").val().trim(),photo:$("#employeePhoto").val()
			};
			$.ajax({
				async:true,
				type:'post',
				dataType:'json',
				data:param,
				url:contextPath + "/employeeRecord/saveBaseInfo.htm",
				success:function(data){
					JEND.page.alert({type:"success", message:data.msg});
				    pageLoading(false);
				},complete:function(XMLHttpRequest,textStatus){  
					if(XMLHttpRequest.status=="403"){
						JEND.page.alert({type:"error", message:"您没有该操作权限！"});
					}
					pageLoading(false);//关闭动画
		        }
			});
		}
	});
}

//保存在职信息
function savePayrollInfo(){
	secondConfirmation({
	   "msg":"是否确认保存在职信息？",
		sureFn:function(){
			pageLoading(true);//加载动画	
			var param = {
				id:$("#employeeId").val().trim(),code:$("#code").val().trim(),
				empTypeId:$("#empTypeId").val(),workType:$("#workType").val(),
				floorCode:$("#floorCode").val(),seatCode:$("#seatCode").val(),email:$("#email").val(),
				departId:$("#depart").val(),reportToLeader:$("#reportToLeader").val(),
				positionId:$("#positionId").val(),positionLevel:$("#positionLevel").val(),
				positionSeq:$("#positionSeq").val(),positionTitle:$("#positionTitle").val(),
				whetherScheduling:$("#whetherScheduling").val(),firstEntryTime:$("#firstEntryTime").val(),
				probationEndTime:$("#probationEndTime").val(),quitTime:$("#quitTime").val(),
				protocolEndTime:$("#protocolEndTime").val(),jobStatus:$("#jobStatus").val(),
				uleAccount:$("#uleAccount").val(),workAddressProvince:$("#workAddressProvince").find("option:selected").text(),
				workAddressCity:$("#workAddressCity").val(),beforeWorkAge:$("#beforeWorkAge").val()
			};
			$.ajax({
				async:true,
				type:'post',
				dataType:'json',
				data:param,
				url:contextPath + "/employeeRecord/savePayrollInfo.htm",
				success:function(data){
					JEND.page.alert({type:"success", message:data.msg});
				    pageLoading(false);
				},complete:function(XMLHttpRequest,textStatus){  
					if(XMLHttpRequest.status=="403"){
						JEND.page.alert({type:"error", message:"您没有该操作权限！"});
					}
					pageLoading(false);//关闭动画
		        }
			});
		}
	});
}

//添加教育经历
function addEducationExperience(){
	var tr = '<tr id=""><td><input value="" type="text" class="Wdate" onClick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})" readonly="readonly" onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd\'});" />-';
	tr += '<input value="" type="text" class="Wdate" onClick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})" readonly="readonly" onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd\'});" /></td>';
	tr += '<td><input value="" type="text" class="form-text" ></td><td><input value="" type="text" class="form-text" ></td>';
	tr += '<td>'+$("div #educationLevel").html()+'</td>';
	tr += '<td>'+$("div #degree").html()+'</td>';
	tr += '<td><a onclick="delTr(this);" style="color:blue;">删除</a></td></tr>';  
	$("div #educationExperience").find("table").append(tr);
}

//保存教育经历
function saveEducationExperience(){
	var list = new Array();
	$("div #educationExperience").find("tr").each(function(i){
		if(i>0){
			var data = {
			   "startTime":$(this).find("td:eq(0)").find("input:eq(0)").val(),
			   "endTime":$(this).find("td:eq(0)").find("input:eq(1)").val(),
			   "school":$(this).find("td:eq(1)").find("input").val(),
			   "major":$(this).find("td:eq(2)").find("input").val(),
			   "education":$(this).find("td:eq(3)").find("select").val(),
			   "degree":$(this).find("td:eq(4)").find("select").val()
			}
			list.push(data);
		}
	});
	secondConfirmation({
		"msg":"是否确认保存教育经历？",
		sureFn:function(){
			pageLoading(true);//加载动画	
			$.ajax({
				async:true,
				type:'post',
				dataType:'json',
				data:{"employeeId":$("#employeeId").val(),"empSchoolListStr":JSON.stringify(list)},
				url:contextPath + "/employeeRecord/saveEducationExperience.htm",
				success:function(data){
					JEND.page.alert({type:"success", message:data.msg});
				    pageLoading(false);
				},complete:function(XMLHttpRequest,textStatus){  
					if(XMLHttpRequest.status=="403"){
						JEND.page.alert({type:"error", message:"您没有该操作权限！"});
					}
					pageLoading(false);//关闭动画
		        }
			});
		}
	})
}

//添加培训证书
function addTrainingCertificate(){
	var tr = '<tr><td><input value="" type="text" class="Wdate" onClick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})" readonly="readonly" onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd\'});" />-';
	tr += '<input value="" type="text" class="Wdate" onClick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})" readonly="readonly" onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd\'});" /></td>';
	tr += '<td><input value="" type="text" class="form-text" ></td><td><input value="" type="text" class="form-text" ></td><td><input value="" type="text" class="form-text" ></td>';
	tr += '<td><a onclick="delTr(this);" style="color:blue;">删除</a></td></tr>';  
	$("div #trainingCertificate").find("table").append(tr);
	
}

//保存培训证书
function saveTrainingCertificate(){
	var list = new Array();
	$("div #trainingCertificate").find("tr").each(function(i){
		if(i>0){
			var data = {
			   "startTime":$(this).find("td:eq(0)").find("input:eq(0)").val(),
			   "endTime":$(this).find("td:eq(0)").find("input:eq(1)").val(),
			   "trainingInstitutions":$(this).find("td:eq(1)").find("input").val(),
			   "content":$(this).find("td:eq(2)").find("input").val(),
			   "obtainCertificate":$(this).find("td:eq(3)").find("input").val()
			}
			list.push(data);
		}
	});
	secondConfirmation({
		"msg":"是否确认保存培训证书？",
		sureFn:function(){
			pageLoading(true);//加载动画	
			$.ajax({
				async:true,
				type:'post',
				dataType:'json',
				data:{"employeeId":$("#employeeId").val(),"empTrainingListStr":JSON.stringify(list)},
				url:contextPath + "/employeeRecord/saveTrainingCertificate.htm",
				success:function(data){
					JEND.page.alert({type:"success", message:data.msg});
				    pageLoading(false);
				},complete:function(XMLHttpRequest,textStatus){  
					if(XMLHttpRequest.status=="403"){
						JEND.page.alert({type:"error", message:"您没有该操作权限！"});
					}
					pageLoading(false);//关闭动画
		        }
			});
		}
	})
}

//添加工作经历
function addWorkExperience(){
	var tr = '<tr><td><input value="" type="text" class="Wdate" onClick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})" readonly="readonly" onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd\'});" />-';
	tr += '<input value="" type="text" class="Wdate" onClick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})" readonly="readonly" onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd\'});" /></td>';
	tr += '<td><input value="" type="text" class="form-text" ></td><td><input value="" type="text" class="form-text" ></td><td><input value="" type="text" class="form-text" ></td>';
	tr += '<td><a onclick="delTr(this);" style="color:blue;">删除</a></td></tr>';  
	$("div #workExperience").find("table").append(tr);
}

//保存工作经历
function saveWorkExperience(){
	var list = new Array();
	$("div #workExperience").find("tr").each(function(i){
		if(i>0){
			var data = {
			   "startTime":$(this).find("td:eq(0)").find("input:eq(0)").val(),
			   "endTime":$(this).find("td:eq(0)").find("input:eq(1)").val(),
			   "companyName":$(this).find("td:eq(1)").find("input").val(),
			   "positionName":$(this).find("td:eq(2)").find("input").val(),
			   "positionTask":$(this).find("td:eq(3)").find("input").val()
			}
			list.push(data);
		}
	});
	secondConfirmation({
		"msg":"是否确认保存工作经历？",
		sureFn:function(){
			pageLoading(true);//加载动画	
			$.ajax({
				async:true,
				type:'post',
				dataType:'json',
				data:{"employeeId":$("#employeeId").val(),"empWorkRecordListStr":JSON.stringify(list)},
				url:contextPath + "/employeeRecord/saveWorkExperience.htm",
				success:function(data){
					JEND.page.alert({type:"success", message:data.msg});
				    pageLoading(false);
				},complete:function(XMLHttpRequest,textStatus){  
					if(XMLHttpRequest.status=="403"){
						JEND.page.alert({type:"error", message:"您没有该操作权限！"});
					}
					pageLoading(false);//关闭动画
		        }
			});
		}
	})
}

//添加紧急联系人
function addUrgentContact(){
	var tr = '<tr><td><select class="select_v1" style="float:none;"><option value="">请选择</option><option value="1">第一联系人</option><option value="2">第二联系人</option><option value="3">第三联系人</option><option value="4">第四联系人</option><option value="5">第五联系人</option></select></td>';
	tr += '<td><input value="" type="text" class="form-text" ></td><td><input value="" type="text" class="form-text" ></td><td><input value="" type="text" class="form-text" ></td>';
	tr += '<td><a onclick="delTr(this);" style="color:blue;">删除</a></td></tr>';  
	$("div #urgentContact").find("table").append(tr);
}

//保存紧急联系人
function saveEmergencyContact(){
	var list = new Array();
	$("div #urgentContact").find("tr").each(function(i){
		if(i>0){
			var data = {
			   "priority":$(this).find("td:eq(0)").find("select").val(),
			   "shortName":$(this).find("td:eq(1)").find("input").val(),
			   "name":$(this).find("td:eq(2)").find("input").val(),
			   "mobile":$(this).find("td:eq(3)").find("input").val()
			}
			list.push(data);
		}
	});
	secondConfirmation({
		"msg":"是否确认保存紧急联系人？",
		sureFn:function(){
			pageLoading(true);//加载动画	
			$.ajax({
				async:true,
				type:'post',
				dataType:'json',
				data:{"employeeId":$("#employeeId").val(),"empUrgentContactListStr":JSON.stringify(list)},
				url:contextPath + "/employeeRecord/saveEmergencyContact.htm",
				success:function(data){
					JEND.page.alert({type:"success", message:data.msg});
				    pageLoading(false);
				},complete:function(XMLHttpRequest,textStatus){  
					if(XMLHttpRequest.status=="403"){
						JEND.page.alert({type:"error", message:"您没有该操作权限！"});
					}
					pageLoading(false);//关闭动画
		        }
			});
		}
	})
}

//添加配偶信息
function addSpouseInfo(){
	var tr = '<tr><td><input value="" type="text" class="form-text" ></td><td><input value="" type="text" class="form-text" ></td><td><input value="" type="text" class="form-text" ></td>';
	tr += '<td><a onclick="delTr(this);" style="color:blue;">删除</a></td></tr>';  
	$("div #spouseInfo").find("table").append(tr);
}

//保存配偶信息
function saveSpouseInfo(){
	var list = new Array();
	$("div #spouseInfo").find("tr").each(function(i){
		if(i>0){
			var data = {
			   "memberName":$(this).find("td:eq(0)").find("input").val(),
			   "memberCompanyName":$(this).find("td:eq(1)").find("input").val(),
			   "memberMobile":$(this).find("td:eq(2)").find("input").val()
			}
			list.push(data);
		}
	});
	secondConfirmation({
		"msg":"是否确认保存配偶信息？",
		sureFn:function(){
			pageLoading(true);//加载动画	
			$.ajax({
				async:true,
				type:'post',
				dataType:'json',
				data:{"employeeId":$("#employeeId").val(),"spouseInfoListStr":JSON.stringify(list)},
				url:contextPath + "/employeeRecord/saveSpouseInfo.htm",
				success:function(data){
					JEND.page.alert({type:"success", message:data.msg});
				    pageLoading(false);
				},complete:function(XMLHttpRequest,textStatus){  
					if(XMLHttpRequest.status=="403"){
						JEND.page.alert({type:"error", message:"您没有该操作权限！"});
					}
					pageLoading(false);//关闭动画
		        }
			});
		}
	})
}

//添加子女信息
function addChildrenInfo(){
	var tr = '<tr><td><input value="" type="text" class="form-text" ></td>';
	tr += '<td><input value="" type="text" class="Wdate" onClick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})" readonly="readonly" onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd\'});" /></td>';
	tr += '<td><select class="select_v1" style="float:none;"><option value="">请选择</option><option value="0">男</option><option value="1">女</option></select></td>';
	tr += '<td><a onclick="delTr(this);" style="color:blue;">删除</a></td></tr>';  
	$("div #childrenInfo").find("table").append(tr);
}

//保存子女信息
function saveChildrenInfo(){
	var list = new Array();
	$("div #childrenInfo").find("tr").each(function(i){
		if(i>0){
			var data = {
			   "memberName":$(this).find("td:eq(0)").find("input").val(),
			   "birthday":$(this).find("td:eq(1)").find("input").val(),
			   "memberSex":$(this).find("td:eq(2)").find("select").val()
			}
			list.push(data);
		}
	});
	secondConfirmation({
		"msg":"是否确认保存子女信息？",
		sureFn:function(){
			pageLoading(true);//加载动画	
			$.ajax({
				async:true,
				type:'post',
				dataType:'json',
				data:{"employeeId":$("#employeeId").val(),"childrenInfoListStr":JSON.stringify(list)},
				url:contextPath + "/employeeRecord/saveChildrenInfo.htm",
				success:function(data){
					JEND.page.alert({type:"success", message:data.msg});
				    pageLoading(false);
				},complete:function(XMLHttpRequest,textStatus){  
					if(XMLHttpRequest.status=="403"){
						JEND.page.alert({type:"error", message:"您没有该操作权限！"});
					}
					pageLoading(false);//关闭动画
		        }
			});
		}
	})
}

//添加业绩与奖惩
function addAchievementAndRewardMerit(){
	var tr = '<tr>';
	tr += '<td><input value="" type="text" class="Wdate" onClick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})" readonly="readonly" onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd\'});" /></td>';
	tr += '<td><input value="" type="text" class="form-text" ></td>';
	tr += '<td><a onclick="delTr(this);" style="color:blue;">删除</a></td></tr>';  
	$("div #achievementAndRewardMerit").find("table").append(tr);
}

//保存业绩与奖惩
function saveAchievementAndRewardMerit(){
	var list = new Array();
	$("div #achievementAndRewardMerit").find("tr").each(function(i){
		if(i>0){
			var data = {
			   "processTime":$(this).find("td:eq(0)").find("input").val(),
			   "content":$(this).find("td:eq(1)").find("input").val()
			}
			list.push(data);
		}
	});
	secondConfirmation({
		"msg":"是否确认保存业绩与奖惩？",
		sureFn:function(){
			pageLoading(true);//加载动画	
			$.ajax({
				async:true,
				type:'post',
				dataType:'json',
				data:{"employeeId":$("#employeeId").val(),"empAchievementListStr":JSON.stringify(list)},
				url:contextPath + "/employeeRecord/saveAchievementAndRewardMerit.htm",
				success:function(data){
					JEND.page.alert({type:"success", message:data.msg});
				    pageLoading(false);
				},complete:function(XMLHttpRequest,textStatus){  
					if(XMLHttpRequest.status=="403"){
						JEND.page.alert({type:"error", message:"您没有该操作权限！"});
					}
					pageLoading(false);//关闭动画
		        }
			});
		}
	})
}

//添加培训记录
function addTrainRecord(){
	var tr = '<tr>';
	tr += '<td><input value="" type="text" class="Wdate" onClick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})" readonly="readonly" onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd\'});" /></td>';
	tr += '<td><input value="" type="text" class="form-text" ></td>';
	tr += '<td><a onclick="delTr(this);" style="color:blue;">删除</a></td></tr>';  
	$("div #trainRecord").find("table").append(tr);
}

//保存培训记录
function saveTrainRecord(){
	var list = new Array();
	$("div #trainRecord").find("tr").each(function(i){
		if(i>0){
			var data = {
			   "startTime":$(this).find("td:eq(0)").find("input").val(),
			   "trainingProName":$(this).find("td:eq(1)").find("input").val()
			}
			list.push(data);
		}
	});
	secondConfirmation({
		"msg":"是否确认保存培训记录？",
		sureFn:function(){
			pageLoading(true);//加载动画	
			$.ajax({
				async:true,
				type:'post',
				dataType:'json',
				data:{"employeeId":$("#employeeId").val(),"trainRecordListStr":JSON.stringify(list)},
				url:contextPath + "/employeeRecord/saveTrainRecord.htm",
				success:function(data){
					JEND.page.alert({type:"success", message:data.msg});
				    pageLoading(false);
				},complete:function(XMLHttpRequest,textStatus){  
					if(XMLHttpRequest.status=="403"){
						JEND.page.alert({type:"error", message:"您没有该操作权限！"});
					}
					pageLoading(false);//关闭动画
		        }
			});
		}
	})
}

//添加合同签订记录
function addContractSignRecord(){
	var tr = '<tr>';
	tr += '<td><input value="" type="text" class="form-text" ></td>';
	tr += '<td><input value="" type="text" class="Wdate" onClick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})" readonly="readonly" onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd\'});" /></td>';
	tr += '<td><input value="" type="text" class="Wdate" onClick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})" readonly="readonly" onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd\'});" /> - ';
	tr += '<input value="" type="text" class="Wdate" onClick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})" readonly="readonly" onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd\'});" /></td>';
	tr += '<td><input value="" type="text" class="form-text" ></td>';
	tr += '<td><input value="" type="text" class="form-text" ></td>';
	tr += '<td><a onclick="delTr(this);" style="color:blue;">删除</a></td></tr>';  
	$("div #contractSignRecord").find("table").append(tr);
}

//保存合同签订记录
function saveContractSignRecord(){
	var list = new Array();
	$("div #contractSignRecord").find("tr").each(function(i){
		if(i>0){
			var data = {
			   "contractCode":$(this).find("td:eq(0)").find("input").val(),
			   "contractSignedDate":$(this).find("td:eq(1)").find("input").val(),
			   "contractStartTime":$(this).find("td:eq(2)").find("input:eq(0)").val(),
			   "contractEndTime":$(this).find("td:eq(2)").find("input:eq(1)").val(),
			   "probationExpire":$(this).find("td:eq(3)").find("input").val(),
			   "contractPeriod":$(this).find("td:eq(4)").find("input").val()
			}
			list.push(data);
		}
	});
	secondConfirmation({
		"msg":"是否确认保存合同签订记录？",
		sureFn:function(){
			pageLoading(true);//加载动画	
			$.ajax({
				async:true,
				type:'post',
				dataType:'json',
				data:{"employeeId":$("#employeeId").val(),"empContractListStr":JSON.stringify(list)},
				url:contextPath + "/employeeRecord/saveContractSignRecord.htm",
				success:function(data){
					JEND.page.alert({type:"success", message:data.msg});
				    pageLoading(false);
				},complete:function(XMLHttpRequest,textStatus){  
					if(XMLHttpRequest.status=="403"){
						JEND.page.alert({type:"error", message:"您没有该操作权限！"});
					}
					pageLoading(false);//关闭动画
		        }
			});
		}
	})
}

//添加考核记录
function addAssessRecord(){
	var tr = '<tr>';
	tr += '<td><input value="" type="text" class="Wdate" onClick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})" readonly="readonly" onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd\'});" /></td>';
	tr += '<td><input value="" type="text" class="form-text" ></td>';
	tr += '<td><input value="" type="text" class="form-text" ></td>';
	tr += '<td><a onclick="delTr(this);" style="color:blue;">删除</a></td></tr>';  
	$("div #assessRecord").find("table").append(tr);
}

//保存考核记录
function saveAssessRecord(){
	var list = new Array();
	$("div #assessRecord").find("tr").each(function(i){
		if(i>0){
			var data = {
			   "annualExaminationTime":$(this).find("td:eq(0)").find("input").val(),
			   "score":$(this).find("td:eq(1)").find("input").val(),
			   "conclusion":$(this).find("td:eq(2)").find("input").val()
			}
			list.push(data);
		}
	});
	secondConfirmation({
		"msg":"是否确认保存考核记录？",
		sureFn:function(){
			pageLoading(true);//加载动画	
			$.ajax({
				async:true,
				type:'post',
				dataType:'json',
				data:{"employeeId":$("#employeeId").val(),"empAppraiseListStr":JSON.stringify(list)},
				url:contextPath + "/employeeRecord/saveAssessRecord.htm",
				success:function(data){
					JEND.page.alert({type:"success", message:data.msg});
				    pageLoading(false);
				},complete:function(XMLHttpRequest,textStatus){  
					if(XMLHttpRequest.status=="403"){
						JEND.page.alert({type:"error", message:"您没有该操作权限！"});
					}
					pageLoading(false);//关闭动画
		        }
			});
		}
	})
}

//岗位记录
function addPostRecord(){
	var tr = '<tr>';
	tr += '<td><input value="" type="text" class="Wdate" onClick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})" readonly="readonly" onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd\'});" /></td>';
	tr += '<td>'+$("div #departList").html()+'</td>';
	tr += '<td><select class="select_v1" style="float:none;"><option value="">请选择</option></select></td>';
	tr += '<td><input value="" type="text" class="form-text" ></td>';
	tr += '<td><a onclick="delTr(this);" style="color:blue;">删除</a></td></tr>';  
	$("div #postRecord").find("table").append(tr);
}

//保存岗位记录
function savePostRecord(){
	var list = new Array();
	$("div #postRecord").find("tr").each(function(i){
		if(i>0){
			var data = {
			   "effectiveDate":$(this).find("td:eq(0)").find("input").val(),
			   "departId":$(this).find("td:eq(1)").find("select").val(),
			   "positionId":$(this).find("td:eq(2)").find("select").val(),
			   "remark":$(this).find("td:eq(3)").find("input").val()
			}
			list.push(data);
		}
	});
	secondConfirmation({
		"msg":"是否确认保存岗位记录？",
		sureFn:function(){
			pageLoading(true);//加载动画	
			$.ajax({
				async:true,
				type:'post',
				dataType:'json',
				data:{"employeeId":$("#employeeId").val(),"postRecordListStr":JSON.stringify(list)},
				url:contextPath + "/employeeRecord/savePostRecord.htm",
				success:function(data){
					JEND.page.alert({type:"success", message:data.msg});
				    pageLoading(false);
				},complete:function(XMLHttpRequest,textStatus){  
					if(XMLHttpRequest.status=="403"){
						JEND.page.alert({type:"error", message:"您没有该操作权限！"});
					}
					pageLoading(false);//关闭动画
		        }
			});
		}
	})
}

//获取部门下的职位
function getPositionBydepart(obj){
	var position = $(obj).closest("td").next().find("select");
	position.empty();
	var options = "<option value=''>请选择</option>";
	position.append(options);
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{'departId':$(obj).val()},
		url:contextPath + "/position/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index,item) {
				position.append("<option value= " + item.id + ">" + item.positionName + "</option>");
			});
		}
	});
}

function delTr(obj){
	$(obj).closest("tr").remove();
}

function toIndexHtml(){
	pageLoading(true);//加载动画	
	if($("#backType").val()==2){
		window.location.href = basePath+"employeeRecord/scan.htm?employeeId="+$("#employeeId").val();
	}else{
		window.location.href = basePath+"employeeRecord/index.htm";
	}
	
}

var data = {
    provinces: ['北京', '上海', '天津', '重庆', '河北省', '山西省', '内蒙古自治区', '辽宁省', '吉林省', '黑龙江省', '江苏省', '浙江省', '安徽省', '福建省', '江西省', '山东省', '河南省', '湖北省', '湖南省', '广东省', '广西省', '海南省', '四川省', '贵州省', '云南省', '西藏自治区', '陕西省', '甘肃省', '宁夏省', '宁夏回族自治区', '新疆维吾尔自治区', '香港', '澳门', '台湾'],
    citys: [
        ["北京市"],
        ["上海市"],
        ["天津市"],
        ["重庆市"],
        // ["东城区", "西城区", "崇文区", "宣武区", "朝阳区", "丰台区", "石景山区", "海淀区", "门头沟区", "房山区", "通州区", "顺义区", "昌平区", "大兴区", "怀柔区", "平谷区", "密云县", "延庆县"],
        // ["黄浦区", "卢湾区", "徐汇区", "长宁区", "静安区", "普陀区", "虹口区", "杨浦区", "闵行区", "宝山区", "嘉定区", "浦东新区", "金山区", "松江区", "青浦区", "南汇区", "奉贤区", "崇明县"],
        // ["和平区", "河东区", "河西区", "南开区", "河北区", "红桥区", "塘沽区", "汉沽区", "大港区", "东丽区", "西青区", "津南区", "北辰区", "武清区", "宝坻区", "宁河县", "静海县", "蓟县"],
        // ["万州区", "涪陵区", "渝中区", "大渡口区", "江北区", "沙坪坝区", "九龙坡区", "南岸区", "北碚区", "万盛区", "双桥区", "渝北区", "巴南区", "黔江区", "长寿区", "綦江县", "潼南县", "铜梁县", "大足县", "荣昌县", "璧山县", "梁平县", "城口县", "丰都县", "垫江县", "武隆县", "忠县", "开县", "云阳县", "奉节县", "巫山县", "巫溪县", "石柱土家族自治县", "秀山土家族苗族自治县", "酉阳土家族苗族自治县", "彭水苗族土家族自治县", "江津市", "合川市", "永川市", "南川市"],
        ["石家庄市", "张家口市", "承德市", "秦皇岛市", "唐山市", "廊坊市", "保定市", "衡水市", "沧州市", "邢台市", "邯郸市"],
        ["太原市", "朔州市", "大同市", "阳泉市", "长治市", "晋城市", "忻州市", "晋中市", "临汾市", "吕梁市", "运城市"],
        ["呼和浩特市", "包头市", "乌海市", "赤峰市", "通辽市", "呼伦贝尔市", "鄂尔多斯市", "乌兰察布市", "巴彦淖尔市", "兴安盟", "锡林郭勒盟", "阿拉善盟"],
        ["沈阳市", "朝阳市", "阜新市", "铁岭市", "抚顺市", "本溪市", "辽阳市", "鞍山市", "丹东市", "大连市", "营口市", "盘锦市", "锦州市", "葫芦岛市"],
        ["长春市", "白城市", "松原市", "吉林市", "四平市", "辽源市", "通化市", "白山市", "延边州"],
        ["哈尔滨市", "齐齐哈尔市", "七台河市", "黑河市", "大庆市", "鹤岗市", "伊春市", "佳木斯市", "双鸭山市", "鸡西市", "牡丹江市", "绥化市", "大兴安岭地区"],
        ["南京市", "徐州市", "连云港市", "宿迁市", "淮安市", "盐城市", "扬州市", "泰州市", "南通市", "镇江市", "常州市", "无锡市", "苏州市"],
        ["杭州市", "湖州市", "嘉兴市", "舟山市", "宁波市", "绍兴市", "衢州市", "金华市", "台州市", "温州市", "丽水市"],
        ["合肥市", "宿州市", "淮北市", "亳州市", "阜阳市", "蚌埠市", "淮南市", "滁州市", "马鞍山市", "芜湖市", "铜陵市", "安庆市", "黄山市", "六安市", "巢湖市", "池州市", "宣城市"],
        ["福州市", "南平市", "莆田市", "三明市", "泉州市", "厦门市", "漳州市", "龙岩市", "宁德市"],
        ["南昌市", "九江市", "景德镇市", "鹰潭市", "新余市", "萍乡市", "赣州市", "上饶市", "抚州市", "宜春市", "吉安市"],
        ["济南市", "青岛市", "聊城市", "德州市", "东营市", "淄博市", "潍坊市", "烟台市", "威海市", "日照市", "临沂市", "枣庄市", "济宁市", "泰安市", "莱芜市", "滨州市", "菏泽市"],
        ["郑州市", "开封市", "三门峡市", "洛阳市", "焦作市", "新乡市", "鹤壁市", "安阳市", "濮阳市", "商丘市", "许昌市", "漯河市", "平顶山市", "南阳市", "信阳市", "周口市", "驻马店市", "济源市"],
        ["武汉市", "十堰市", "襄樊市", "荆门市", "孝感市", "黄冈市", "鄂州市", "黄石市", "咸宁市", "荆州市", "宜昌市", "随州市", "省直辖县级行政单位", "恩施州"],
        ["长沙市", "张家界市", "常德市", "益阳市", "岳阳市", "株洲市", "湘潭市", "衡阳市", "郴州市", "永州市", "邵阳市", "怀化市", "娄底市", "湘西州"],
        ["广州市", "深圳市", "清远市", "韶关市", "河源市", "梅州市", "潮州市", "汕头市", "揭阳市", "汕尾市", "惠州市", "东莞市", "珠海市", "中山市", "江门市", "佛山市", "肇庆市", "云浮市", "阳江市", "茂名市", "湛江市"],
        ["南宁市", "桂林市", "柳州市", "梧州市", "贵港市", "玉林市", "钦州市", "北海市", "防城港市", "崇左市", "百色市", "河池市", "来宾市", "贺州市"],
        ["海口市", "三亚市", "三沙市", "省直辖县级行政单位"],
        ["成都市", "广元市", "绵阳市", "德阳市", "南充市", "广安市", "遂宁市", "内江市", "乐山市", "自贡市", "泸州市", "宜宾市", "攀枝花市", "巴中市", "达州市", "资阳市", "眉山市", "雅安市", "阿坝州", "甘孜州", "凉山州"],
        ["贵阳市", "六盘水市", "遵义市", "安顺市", "毕节地区", "铜仁地区", "黔东南州", "黔南州", "黔西南州"],
        ["昆明市", "曲靖市", "玉溪市", "保山市", "昭通市", "丽江市", "思茅市", "临沧市", "德宏州", "怒江州", "迪庆州", "大理州", "楚雄州", "红河州", "文山州", "西双版纳州"],
        ["拉萨市", "那曲地区", "昌都地区", "林芝地区", "山南地区", "日喀则地区", "阿里地区"],
        ["西安市", "延安市", "铜川市", "渭南市", "咸阳市", "宝鸡市", "汉中市", "榆林市", "安康市", "商洛市"],
        ["兰州市", "嘉峪关市", "白银市", "天水市", "武威市", "酒泉市", "张掖市", "庆阳市", "平凉市", "定西市", "陇南市", "临夏州", "甘南州"],
        ["西宁市", "海东地区", "海北州", "海南州", "黄南州", "果洛州", "玉树州", "海西州"],
        ["银川市", "石嘴山市", "吴忠市", "固原市", "中卫市"],
        ["乌鲁木齐市", "克拉玛依市", "自治区直辖县级行政单位", "喀什地区", "阿克苏地区", "和田地区", "吐鲁番地区", "哈密地区", "克孜勒苏柯州", "博尔塔拉州", "昌吉州", "巴音郭楞州", "伊犁州", "塔城地区", "阿勒泰地区"],
        ["香港"],
        ["澳门"],
        ["台北市", "高雄市", "台中市", "花莲市", "基隆市", "嘉义市", "金门市", "连江市", "苗栗市", "南投市", "澎湖市", "屏东市", "台东市", "台南市", "桃园市", "新竹市", "宜兰市", "云林市", "彰化市"]
    ]
};

//获得第一级部门
function getProvinces(){
	var workProvince = $("#workAddressProvince");
	workProvince.empty();
	var options = "<option value=''>请选择</option>";
	workProvince.append(options);
	for (var i = 0; i < data.provinces.length; i++) {
    	var province_li =("<option  value= "+i+">" + data.provinces[i] + "</option>");
    	workProvince.append(province_li); 	
    }
}

function getCitys(i){
	var workCity = $("#workAddressCity");
	workCity.empty();	
	for (var j = 0; j< data.citys[i].length; j++) {
	 	var city_li =("<option  value= " +  data.citys[i][j]  + ">" +  data.citys[i][j]  + "</option>");
	 	workCity.append(city_li)
	 }
}