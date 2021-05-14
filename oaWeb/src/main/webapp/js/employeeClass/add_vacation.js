var nameArr = [];
var idArr = [];
$(function(){
	//获取公司
    $.getJSON(contextPath +'/depart/getListByLeaderOrPower.htm',function(data){
        for(var i=0;i<data.length;i++){
        	nameArr.push(data[i].name);
        	idArr.push(data[i].id);
        }
        if(data.length==1){
        	$('#depart').find('input').val(data[0].name);
        	$('#depart').find('input').attr("departId",data[0].id);
        }else{
        	 $('#depart').on('click',function(){
         	    var _this = $(this);
         	    bPup(nameArr,idArr,function(name,id){
         	        _this.find('input').val(name);
         	        _this.find('input').attr("departId",id);
         	    });
         	})
        }
    })
});

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

//新增值班
function addClassDuty(){
	var employeeIds = "";
	$("#selstaff").find("span").each(function(){
		employeeIds += $(this).attr("i-id")+",";
	}) 
	if($('#holiday input').val()==""){
		OA.titlePup('节假日不能为空！','lose');
		return;
	}/*
	if($("#depart").find("input").attr("departId")==""){
		OA.titlePup('部门不能为空！','lose');
		return;
	}*/
	OA.twoSurePop({
		tips:'确认新增吗？',
		sureFn:function(){
		    	OA.pageLoading(true);
				$.ajax({
					async:true,
					type:'post',
					dataType:'json',
					data:{
						departId:$("#depart").find("input").attr("departId"),
						departName:$("#depart").find("input").val(),
						vacationName:$('#holiday input').val(),
						token:$("#token").val()
					},
					url:contextPath + "/employeeClass/addClassDuty.htm",
					success:function(data){
						if(data.success){
							window.location.href=contextPath + "/employeeClass/index.htm";
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