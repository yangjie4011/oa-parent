var companyArr = [];
var idArr = [];
var typeNameArr = [];
var idArr1 = [];
var workTypeArr = [];
var idArr2 = [];
var whetherSchedulingArr = [];
var idArr3 = [];
$(function(){
	//模拟单选框
    $('body').on('touchstart','.singleSelGroup span',function(){
        $(this).children('em').addClass('current');
        $(this).siblings('span').children('em').removeClass('current');
    });
    //显示或隐藏员工编号输入框
    $("#showCode").click(function(){
    	getCompanyInfo($(".companyName"));
    	$('.pup-bottom').remove();
    	$(".code").show();
    	clearFromInfo();
    });
    $("#hideCode").click(function(){
    	$(".code").hide();
    	$("#code").val("");
    	clearFromInfo();
    });
    
    //根据员工编号带出详细员工信息
    $("#code").mouseout(function(){
    	if($(this).val()!=""){
    		clearFromInfo();
    		getEmployeeByCode($(this).val());
    	}
    });
    //日期初始化
    var calendartime = new lCalendar();
    calendartime.init({
        'trigger': 'birthDate',
        'type': 'date'
    });
    var calendartime = new lCalendar();
    calendartime.init({
        'trigger': 'employmentDate',
        'type': 'date'
    });
	//获取公司
    $.getJSON(contextPath +'/company/getListByCondition.htm',function(data){
        for(var i=0;i<data.length;i++){
        	companyArr.push(data[i].name);
        	idArr.push(data[i].id);
        }
    })
	$('body').on('click','.companyName',function(){
	    var _this = $(this);
	    getCompanyInfo(_this);
	})
	//选择员工类型
    $('body').on('click','.empType',function(){
    	  //获取员工类型
        $.ajaxSettings.async = false;
        $.getJSON(contextPath +'/empType/getListByCondition.htm',function(data){
        	typeNameArr = [];
            idArr1 = [];
            for(var i=0;i<data.length;i++){
            	typeNameArr.push(data[i].typeCName);
            	idArr1.push(data[i].id);
            }
        })
        var _this = $(this);
        bPup(typeNameArr,idArr1,function(data,id){
            _this.children('input').val(data);
            _this.children('input').attr("data-id",id);
        });
    })
    //选择工时种类
    $('body').on('click','.workType',function(){
    	 //获取工时种类'code':'typeOfWork'
        $.ajaxSettings.async = false;
	    $.getJSON(contextPath +'/companyConfig/getListByCondition.htm?code=typeOfWork',function(data){
	    	workTypeArr = [];
	    	idArr2 = [];
	    	for(var i=0;i<data.length;i++){
	        	workTypeArr.push(data[i].displayName);
	        	idArr2.push(data[i].id);
	        }
	    })
        var _this = $(this);
        bPup(workTypeArr,idArr2,function(data,id){
            _this.children('input').val(data);
            _this.children('input').attr("data-id",id);
	        $(".group").children('input').val("");
	        $(".group").children('input').attr("data-id","");
            if(data=="标准工时"&&$(".whetherScheduling").children('input').val()=="是"){
            	$("#group").show();
            	if($(".depart").children('input').attr("data-id").trim()!=""){
            		rebuildGroupSelect();
            	}
            }else{
            	$("#group").hide();
            }
        });
    })
    //选择是否排班
    $('body').on('click','.whetherScheduling',function(){
    	//获取是否排班
	    $.ajaxSettings.async = false;
	    $.getJSON(contextPath +'/sysConfig/getListByCondition.htm?code=whetherScheduling',function(data){
	    	 whetherSchedulingArr = [];
	    	 idArr3 = [];
	    	 for(var i=0;i<data.length;i++){
	        	whetherSchedulingArr.push(data[i].displayName);
	        	idArr3.push(data[i].id);
	        }
	    })
        var _this = $(this);
        bPup(whetherSchedulingArr,idArr3,function(data,id){
            _this.children('input').val(data);
            _this.children('input').attr("data-id",id);
	        $(".group").children('input').val("");
	        $(".group").children('input').attr("data-id","");
            if($(".workType").children('input').val()=="标准工时"&&data=="是"){
            	$("#group").show();
            	 if($(".depart").children('input').attr("data-id").trim()!=""){
                 	rebuildGroupSelect();
             	}
            }else{
            	$("#group").hide();
            }
        });
    })
	//部门职位联动
    $('body').on('click','.depart',function(){
    	 if($(".workType").children('input').val()=="标准工时"&&$(".whetherScheduling").children('input').val()=="是"){
			    $("#group").show();
		 }else{
		        $("#group").hide();
		 }
    	 var _this = $(this);
    	 OA.selPart({
 		    cb:function(data1){//再次查询部门信息
 		    	$.ajax({
 		       		async : false,
 		       		type : "post",
 		     		dataType:"json",
 		     		data : {'departId':data1.partId},
 		       		url : contextPath + "/depart/getInfoById.htm",
 		       		success : function(data) {
 		       			if('' != data){
 		       				if(data.parentName==""){
 		       				    _this.children('input').val(data.name);
 		       				}else{
 		       				    _this.children('input').val(data.parentName+"-"+data.name);
 		       				}
 		       		        _this.children('input').attr("data-id",data1.partId); 
 		       		        $(".position").children('input').val("");
 		       		        $(".positionSeq").children('input').val("");
 		       		        $(".positionLevel").children('input').val("");
		       		        $(".position").children('input').attr("data-id","");
		       		        $(".group").children('input').val("");
		       		        $(".group").children('input').attr("data-id","");
 			       		    $("#departHeader").val(data.leaderName);
 		       			}
 		       		}
 		     });
 		   }
 	    });
     });
    
    //职位
    rebuildPositionSelect();
    //职位序列
    rebuildPositionSeqSelect();
    //职级
    rebuildPositionLevelSelect();
    
    //组别
    rebuildGroupSelect();
    
    //汇报对象下拉
    $('body').on('click','.leader',function(){
    	var _this = $(this);
    	if($(".depart").children('input').attr("data-id")!=""){
    		$.getJSON(contextPath +'/employeeRegister/getReportPerson.htm?departId='+$(".depart").children('input').attr("data-id"),function(data){
    			 var nameArr = [];
		         var idArr = [];
		         for(var i=0;i<data.length;i++){
		        	 nameArr.push(data[i].cnName);
		        	 idArr.push(data[i].id);
		         }
                 bPup(nameArr,idArr,function(data,id){
                     _this.children('input').val(data);
                     _this.children('input').attr("data-id",id);
                 });
    		});
    	}
    });
    
    $('#toPersions').click(function () {
        var $this = $(this);
        OA.selStaff({
            departname: '请选择部门',
            departIdEl: '',
            fuc: function (data) {
                var personIds = [];
                var personVals = [];
                for (var i = 0, ilen = data.length; i < ilen; i++) {
                    personIds.push(data[i].id)
                    personVals.push(data[i].name)
                }

                if (personVals.length) $this.val(personVals[0] + '等' + personVals.length + '个员工').attr('data-id', personIds.join(','));
            },
            condition: {
                jobStatus: 0
            },
            _this: $this
        })
    })
    
    $("#save").click(function(){
		save();
	})
});

function rebuildPositionSelect(){
	$(".position").click(function() {
	    //添加职位下拉
		var departId = $(".depart").children('input').attr("data-id");
		var position = positionSelect(departId);//职位
		var values = position.values;
		var keys = position.keys;
		var levels = position.levels;
        var m = $(this);
		bPup1(values,keys,levels, function(name,id,level) {
			m.find('input[type="text"]').val(name);
			m.find('input[type="text"]').attr("data-id",id);
			m.find('input[type="text"]').attr("level",level);
			$(".positionSeq").children('input').val("");
		    $(".positionLevel").children('input').val("");
		});
	});
}

function rebuildPositionSeqSelect(){
	$(".positionSeq").click(function() {
	    //添加职位下拉
		var positionType = $(".position").children('input').attr("level");
		var position = positionSeqSelect(positionType);//职位序列
		var values = position.values;
        var m = $(this);
		bPup(values,values, function(name,id) {
			m.find('input[type="text"]').val(name);
		});
	});
}

function rebuildPositionLevelSelect(){
	$(".positionLevel").click(function() {
	    //添加职位下拉
		var positionType = $(".position").children('input').attr("level");
		var position = positionLevelSelect(positionType);//职级
		var values = position.values;
        var m = $(this);
		bPup(values,values, function(name,id) {
			m.find('input[type="text"]').val(name);
		});
	});
}

function rebuildGroupSelect(){
	$(".group").click(function() {
	    //添加职位下拉
		var departId = $(".depart").children('input').attr("data-id");
		var position = groupSelect(departId);//职位
		var values = position.values;
		var keys = position.keys;
        var m = $(this);
		bPup(values,keys, function(name,id) {
			m.find('input[type="text"]').val(name);
			m.find('input[type="text"]').attr("data-id",id);
		});
	});
}

//获取公司相关信息
function getCompanyInfo(obj){
	  var _this = $(obj);
	    bPup(companyArr,idArr,function(data,id){
	    	$(".empType").children('input').val("");
	    	$(".empType").children('input').attr("data-id","");
	        _this.children('input').val(data);
	        _this.children('input').attr("data-id",id);
	    });
}

//清除From数据
function clearFromInfo(){
	//公司
	$(".companyName").children('input').attr("data-id","");
	$(".companyName").children('input').val("");
	//员工类型
	$(".empType").children('input').attr("data-id","");
	$(".empType").children('input').val("");
	//工时类型
	$(".workType").children('input').attr("data-id","");
	$(".workType").children('input').val("");
	//是否排班
	$(".whetherScheduling").children('input').attr("data-id","");
	$(".whetherScheduling").children('input').val("");
	//中文名
	$("#cnName").val("");
	//英文名
	$("#engName").val("");
	//性别
	$("#male").removeClass("current");
	$("#female").removeClass("current");
	$("#male").addClass("current");
	//手机号
	$("#mobile").val("");
	//部门
	$(".depart").children('input').attr("data-id","");
	$(".depart").children('input').val("");
	//部门负责人
	$("#departHeader").val("");
	$("#employmentDate").attr("data-lcalendar","2017-01-01,9030-12-12");
	//指纹id
	$("#fingerprintId").val("");
	
}

function bPup(war,war1, call) {
    if($('.pup-bottom').length > 0){return}
    var arr = [];
    arr.push('<div class="pup-bottom">')
    arr.push('<div class="pup-bg"></div>')
    arr.push('<div class="pb-main">')
    arr.push('<ul>')
    arr.push('<div class="t"></div>')
    arr.push('<div class="b"></div>')
    arr.push('<li class="no"></li>')
    for (var a = 0; a < war.length; a++) {
        arr.push('<li id='+war1[a]+'>' + war[a] + '</li>')
    }
    arr.push('<li class="no"></li>')
    arr.push('</ul>')
    arr.push('<div class="cancel">取消</div>')
    arr.push(' </div></div>')
    $('body').append(arr.join(''));
    var pb = $('.pup-bottom');
    $('.pup-bg , .cancel').click(function () { pb.remove() })
    $('.pb-main ul li').click(function () {
        call($(this).html(),$(this).attr('id'));
        pb.remove();
    })
}

function bPup1(war,war1,war2, call) {
    if($('.pup-bottom').length > 0){return}
    var arr = [];
    arr.push('<div class="pup-bottom">')
    arr.push('<div class="pup-bg"></div>')
    arr.push('<div class="pb-main">')
    arr.push('<ul>')
    arr.push('<div class="t"></div>')
    arr.push('<div class="b"></div>')
    arr.push('<li class="no"></li>')
    for (var a = 0; a < war.length; a++) {
        arr.push('<li level='+war2[a]+' id='+war1[a]+'>' + war[a] + '</li>')
    }
    arr.push('<li class="no"></li>')
    arr.push('</ul>')
    arr.push('<div class="cancel">取消</div>')
    arr.push(' </div></div>')
    $('body').append(arr.join(''));
    var pb = $('.pup-bottom');
    $('.pup-bg , .cancel').click(function () { pb.remove() })
    $('.pb-main ul li').click(function () {
        call($(this).html(),$(this).attr('id'),$(this).attr('level'));
        pb.remove();
    })
}

function save(){
	if($(".companyName").children('input').attr("data-id").trim()==""){
		OA.titlePup('公司名称不能为空！','lose');
		return;
	}
	if($(".empType").children('input').attr("data-id").trim()==""){
		OA.titlePup('员工类型不能为空！','lose');
		return;
	}
	if($(".workType").children('input').attr("data-id").trim()==""){
		OA.titlePup('请选择工时种类！','lose');
		return;
	}
	if($(".whetherScheduling").children('input').attr("data-id").trim()==""){
		OA.titlePup('请选择是否排班！','lose');
		return;
	}
	if($("#cnName").val().trim()==""){
		OA.titlePup('中文名不能为空！','lose');
		return;
	}
	if($("#birthDate").val().trim()==""){
		OA.titlePup('出生日期不能为空！','lose');
		return;
	}
	var ret = /^[\u4e00-\u9fa5]{0,}$/;
	if(!ret.test($("#cnName").val().trim())){
		OA.titlePup('中文名格式不正确！','lose');
		return;
	}
	if($("#mobile").val().trim()==""){
		OA.titlePup('手机号不能为空！','lose');
		return;
	}
	var ret1 = /^1[3456789]\d{9}$/;
	if(!ret1.test($("#mobile").val())){
		OA.titlePup('手机号格式不正确！','lose');
		return;
	}
	if($("#employmentDate").val().trim()==""){
		OA.titlePup('入职日期不能为空！','lose');
		return;
	}
	if($("#travelProvince").val().trim()==""){
		OA.titlePup('工作地点不能为空！','lose');
		return;
	}
	if($("#travelCity").val().trim()==""){
		OA.titlePup('工作地点不能为空！','lose');
		return;
	}
	if($(".depart").children('input').attr("data-id").trim()==""){
		OA.titlePup('部门不能为空！','lose');
		return;
	}
	if($(".position").children('input').attr("data-id").trim()==""){
		OA.titlePup('职位不能为空！','lose');
		return;
	}
	if($(".positionSeq").children('input').val().trim()==""){
		OA.titlePup('职位序列不能为空！','lose');
		return;
	}
	if($(".positionLevel").children('input').val().trim()==""){
		OA.titlePup('职级不能为空！','lose');
		return;
	}
	if($(".leader").children('input').attr("data-id").trim()==""){
		OA.titlePup('汇报对象不能为空！','lose');
		return;
	}
	var sex = "0";
	$("#singleSelGroup").find("em").each(function(){
		if($(this).hasClass("current")){
			sex = $(this).attr("sex");
		}
	})
	var type = "0";
	$("#typeSelGroup").find("em").each(function(){
		if($(this).hasClass("current")){
			type = $(this).attr("type");
		}
	})
	OA.twoSurePop({
		tips:'确认提交吗？',
		sureFn:function(){
			OA.pageLoading(true);
			$.ajax({
				async:true,
				type:'post',
				timeout : 5000,
				dataType:'json',
				data:{
					type:type,
					companyId:$(".companyName").children('input').attr("data-id").trim(),
					code:$("#code").val().trim(),
					employeeTypeId:$(".empType").children('input').attr("data-id").trim(),
					workType:$(".workType").children('input').attr("data-id").trim(),
					whetherScheduling:$(".whetherScheduling").children('input').attr("data-id").trim(),
					cnName:$("#cnName").val().trim(),
					birthDate:$("#birthDate").val().trim(),
					engName:$("#engName").val().trim(),
					mobile:$("#mobile").val().trim(),
					employmentDate:$("#employmentDate").val().trim(),
					departId:$(".depart").children('input').attr("data-id").trim(),
					positionId:$(".position").children('input').attr("data-id"),
					positionSeq:$(".positionSeq").children('input').val().trim(),
					positionLevel:$(".positionLevel").children('input').val().trim(),
					leader:$(".leader").children('input').attr("data-id").trim(),
					sex:sex,
					remarks:$("#remarks").val(),
					toPersions:$("#toPersions").attr("data-id"),
					toEmails:$("#toEmails").val(),
					token:$("#token").val(),
					groupId:$(".group").children('input').attr("data-id").trim(),
					workAddressProvince:$("#travelProvince").val(),
					workAddressCity:$("#travelCity").val()
				},
				url:contextPath + "/employeeRegister/save.htm",
				success:function(data){
					if(data.success){
						window.location.href=contextPath + "/runTask/index.htm";
					}else{
						OA.pageLoading(false);
						OA.titlePup(data.message,'lose');
						$("#token").val(data.token);
					}
				},
				complete:function(XMLHttpRequest,status){
		             if(status=="timeout"){
		            	 window.location.href=contextPath + "/runTask/index.htm";
		             }
				}
			});
		},
		cancelFn:function(){
			
		}
	})
}

function getEmployeeByCode(code){
	$.ajax({
		async:true,
		type:'post',
		timeout : 5000,
		dataType:'json',
		data:{
			code:code
		},
		url:contextPath + "/employee/getInfoByCode.htm",
		success:function(data){
			if(data.success){
				//公司
				$(".companyName").children('input').attr("data-id",data.company.id);
				$(".companyName").children('input').val(data.company.name);
				//工时类型
				$(".workType").children('input').attr("data-id",data.employee.workType);
				$(".workType").children('input').val(data.employee.workTypeName);
				//是否排班
				$(".whetherScheduling").children('input').attr("data-id",data.employee.whetherScheduling);
				$(".whetherScheduling").children('input').val(data.employee.whetherSchedulingName);
				//中文名
				$("#cnName").val(data.employee.cnName);
				//英文名
				$("#engName").val(data.employee.engName);
				//性别
				$("#male").removeClass("current");
				$("#female").removeClass("current");
				if(data.employee.sex==0){
					$("#male").addClass("current");
				}else{
					$("#female").addClass("current");
				}
				//手机号
				$("#mobile").val(data.employee.mobile);
				//部门
				$(".depart").children('input').attr("data-id",data.employee.departId);
				$(".depart").children('input').val(data.employee.departName);
				//部门负责人
				$("#departHeader").val(data.departLeader);
				//限制入职时间
				var quitDate = new Date(Date.parse(data.employee.quitTime.replace(/-/g,  "/"))); 
				quitDate.setDate(quitDate.getDate()+1);
				var startDate = quitDate.format("yyyy/MM/dd").replaceAll("/","-");
				$("#employmentDate").attr("data-lcalendar",startDate+",9030-12-12");
				//指纹id
				$("#fingerprintId").val(data.employee.fingerprintId);
			}else{
				$("#employmentDate").attr("data-lcalendar","2017-01-01,9030-12-12");
				OA.titlePup(data.message,'lose');
			}
		},
	});
}