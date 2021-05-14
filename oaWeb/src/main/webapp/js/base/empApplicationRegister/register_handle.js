$(function(){
	$("#adHandle").click(function(){
		adHandle();
	})
	$("#itHandle").click(function(){
		itHandle();
	})
	$("#confirm").click(function(){
		confirm1();
	})
	$("#cancel").click(function(){
		cancel();
	})
    if($('.oa-new_woker').length){
        //延期入职
        var calendar = new lCalendar();
        calendar.init({
            'trigger': 'delay-work',
            'type': 'date',
            'callback':function(date){
            	delay(date);
            }  
        });
        $('.btn-delay').click(function(){
            $('#delay-work').trigger('click');
        })
    }
	//获取楼层和座位号
    $.getJSON(contextPath +'/companySeat/floors.htm?companyId='+$("#companyId").val(),function(data){
        var floorArr = [];
        var idArr = [];
        for(var i=0;i<data.length;i++){
        	floorArr.push(data[i].name);
        	idArr.push(data[i].id);
        }
        $('body').on('click','.floorName',function(){
            var _this = $(this);
            bPup(floorArr,idArr,function(data,id){
                _this.children('input').val(data);
                _this.children('input').attr("data-id",id);
            });
        })
    })
});

//入职行政处理
function adHandle(){
	if($("#floorId").attr("data-id").trim()==""){
		OA.titlePup('楼层不能为空！','lose');
		return;
	}
	if($("#seatNo").val().trim()==""){
		OA.titlePup('座位号不能为空！','lose');
		return;
	}
	var ret = /[^\u4e00-\u9fa5]$/;
	if(!ret.test($("#seatNo").val().trim())){
		OA.titlePup('座位号格式不正确！','lose');
		return;
	}
	OA.twoSurePop({
		tips:'确认提交吗？',
		sureFn:function(){
			OA.pageLoading(true);
			var param = {
				processInstanceId:$("#id").val(),
				taskId:$("#taskId").val(),
				floorId:$("#floorId").attr("data-id").trim(),
				seatNo:$("#seatNo").val(),
				token:$("#token").val()
			};
			$.ajax({
				async:true,
				type:'post',
				timeout : 5000,
				dataType:'json',
				data:param,
				url:contextPath + "/employeeRegister/adHandle.htm",
				success:function(data){
					if(data.success){
						window.location.href=contextPath + "/ruProcdef/my_examine.htm";
					}else{
						OA.pageLoading(false);
						OA.titlePup(data.message,'lose');
					}
				},
				complete:function(XMLHttpRequest,status){
		             if(status=="timeout"){
		            	 window.location.href=contextPath + "/ruProcdef/my_examine.htm";
		             }
				}
			});
		},
		cancelFn:function(){
			
		}
	})
}

//入职it处理
function itHandle(){
	if($("#email").val().trim()==""){
		OA.titlePup('邮箱不能为空！','lose');
		return;
	}
	var ret = /^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,6}$/;
	if(!ret.test($("#email").val().trim())){
		OA.titlePup('邮箱格式不正确！','lose');
		return;
	}
	var ret1 = /^\d{4}$/;
	if($("#extensionNumber").val().trim()!=""){
		if(!ret1.test($("#extensionNumber").val().trim())){
			OA.titlePup('分机号格式不正确！','lose');
			return;
		}
	}
	OA.twoSurePop({
		tips:'确认提交吗？',
		sureFn:function(){
			OA.pageLoading(true);
			var param = {
				processInstanceId:$("#id").val(),
				email:$("#email").val().trim(),
				taskId:$("#taskId").val(),
				extensionNumber:$("#extensionNumber").val().trim(),
				token:$("#token").val()
			};
			$.ajax({
				async:true,
				type:'post',
				timeout : 5000,
				dataType:'json',
				data:param,
				url:contextPath + "/employeeRegister/itHandle.htm",
				success:function(data){
					if(data.success){
						window.location.href=contextPath + "/ruProcdef/my_examine.htm";
					}else{
						OA.pageLoading(false);
						OA.titlePup(data.message,'lose');
					}
				},
				complete:function(XMLHttpRequest,status){
		             if(status=="timeout"){
		            	 window.location.href=contextPath + "/ruProcdef/my_examine.htm";
		             }
				}
			});
		},
		cancelFn:function(){
			
		}
	})
}

function checkEmail(obj){
	if($(obj).val()!=""){
		var ret = /^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,6}$/;
		if(!ret.test($(obj).val().trim())){
			OA.titlePup('邮箱格式不正确！','lose');
			return;
		}
		$.ajax({
			async:true,
			type:'post',
			timeout : 5000,
			dataType:'json',
			data:{email:$(obj).val()},
			url:contextPath + "/employeeRegister/checkEmail.htm",
			success:function(data){
				if(!data.success){
					$(obj).val();
					OA.titlePup(data.message,'lose');
				}
			}
		});
	}
}

//确定入职
function confirm1(){
	var tips = '<div class="selMatter clearfix">\
        <h4 class="fl">员工编号</h4>\
        <div class="selarea fr">\
            <input value=\''+$("#code").val().trim()+'\' type="text" readonly>\
        </div>\
    </div><div class="selMatter clearfix">\
        <h4 class="fl">指纹ID</h4>\
        <div class="selarea fr">\
            <input value=\''+$(".fingerprintId").text().trim()+'\' id="fingerprintId" type="text" placeholder="指纹ID必填">\
        </div>\
    </div><div class="selMatter clearfix">\
        <h4 class="fl">员工识别号</h4>\
        <div class="selarea fr">\
            <input value=\''+$("#identificationNum").val().trim()+'\' id="identification_num" type="text" placeholder="员工识别号必填">\
        </div>\
     </div>';
	if($("#isRepeat").val()=="true"){
		tips += '<div class="selMatter clearfix">\
	        <h4 class="fl">提示</h4>\
	        <div class="selarea fr" style="height:88px;color:red;">\
			        此员工使用原有员工识别号\
	        </div>\
		     </div>';
	}
	OA.twoSurePop({
        tips:tips,
        sureFn:function(){
        	var fingerprintId = $("#fingerprintId").val().trim();
        	var identificationNum = $("#identification_num").val().trim();
    		OA.twoSurePop({
    			tips:'确认提交吗？',
    			sureFn:function(){
    				OA.pageLoading(true);
    				var param = {
    					processInstanceId:$("#id").val(),
    					taskId:$("#taskId").val(),
    					fingerprintId:fingerprintId,
    					identificationNum:identificationNum,
    					token:$("#token").val()
    				};
    				$.ajax({
    					async:true,
    					type:'post',
    					timeout : 5000,
    					dataType:'json',
    					data:param,
    					url:contextPath + "/employeeRegister/confirm.htm",
    					success:function(data){
    						if(data.success){
    							window.location.href=contextPath + "/ruProcdef/my_examine.htm";
    						}else{
    							$("#token").val(data.token);
    							OA.pageLoading(false);
    							OA.titlePup(data.message,'lose');
    						}
    					},
    					complete:function(XMLHttpRequest,status){
    			             if(status=="timeout"){
    			            	 window.location.href=contextPath + "/ruProcdef/my_examine.htm";
    			             }
    					}
    				});
    			},
    			cancelFn:function(){
    				
    			}
    		})
        },
        cancelFn:function(){
            
        }
    })
	
}

//未入职
function cancel(){
	OA.twoSurePop({
		tips:'确认提交吗？',
		sureFn:function(){
			OA.pageLoading(true);
			var param = {
				processInstanceId:$("#id").val(),
				taskId:$("#taskId").val(),
				ruProcdefId:$("#ruProcdefId").val(),
				token:$("#token").val()
			};
			$.ajax({
				async:true,
				type:'post',
				timeout : 5000,
				dataType:'json',
				data:param,
				url:contextPath + "/employeeRegister/cancel.htm",
				success:function(data){
					if(data.success){
						window.location.href=contextPath + "/ruProcdef/my_examine.htm";
					}else{
						OA.pageLoading(false);
						OA.titlePup(data.message,'lose');
					}
				},
				complete:function(XMLHttpRequest,status){
		             if(status=="timeout"){
		            	 window.location.href=contextPath + "/ruProcdef/my_examine.htm";
		             }
				}
			});
		},
		cancelFn:function(){
			
		}
	})
}

//延期入职
function delay(employmentDate){
	OA.twoSurePop({
		tips:'确认提交吗？',
		sureFn:function(){
			OA.pageLoading(true);
			$.ajax({
				async:true,
				type:'post',
				timeout : 5000,
				dataType:'json',
				data:{id:$("#id1").val(),employmentDate:employmentDate,token:$("#token").val()},
				url:contextPath + "/employeeRegister/delay.htm",
				success:function(data){
					if(data.success){
						window.location.href=contextPath + "/ruProcdef/my_examine.htm";
					}else{
						OA.pageLoading(false);
						OA.titlePup(data.message,'lose');
					}
				},
				complete:function(XMLHttpRequest,status){
		             if(status=="timeout"){
		            	 window.location.href=contextPath + "/ruProcdef/my_examine.htm";
		             }
				}
			});
		},
		cancelFn:function(){
			
		}
	})
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
//撤回操作
function cancelRunBackTask(){
	//TODO:撤回操作
	OA.twoSurePop({
		tips:'确认撤回吗？',
		sureFn:function(){
			OA.pageLoading(true);
			$.ajax({
				async:true,
				type:'post',
				dataType:'json',
				data:{message:$("#approvalReason").val(),processId:$("#id").val(),taskId:$("#taskId").val(),token:$("#token").val()},
				url:contextPath + "/employeeRegister/back.htm",
				success:function(data){
					if(data.success){
						window.location.href=contextPath + "/ruProcdef/my_examine.htm?urlType=6";
					}else{
						OA.pageLoading(false);
						OA.titlePup(data.message,'lose');
					}
				}
			});
		},
		cancelFn:function(){

		}
	})
}
	
