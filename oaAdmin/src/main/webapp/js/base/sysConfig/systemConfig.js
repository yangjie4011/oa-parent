$(function(){
	//默认查询所有
	gotoPage(1);
	$("#query").click(function(){
		gotoPage(1);
	});	
	//预加载 添加按钮 curd 弹框 层操作
	$("#add").click(function(){
		add();
	});
});
///////////////////////////////////////////////////curd 弹框 层操作 开始///////////////////////////////////////
//弹出新增职位界面
function add(){
	$("#addDiv").show();
}
//保存职位信息
function savePosition(){
	var code=$("#addForm :text[name='code']").val();
	var rank=$("#addForm :text[name='rank']").val();
	if(code==""){
    	JEND.page.alert({type:"error", message:"系统编码不能为空"});
    }else if(rank==""){
    	JEND.page.alert({type:"error", message:"请添加一个排序数值"});
    }else{
		$.ajax({
			async:false,
			type:'post',
			dataType:'json',
			data: $("#addForm").serialize(),
			url:basePath + "sysConfig/addConfigForm.htm",
			success:function(data){
				closeDiv("addDiv");        	
	            $("#messageBox #messageContent").text(data.result.message);	            	           
	            $("#messageBox").show();    
            	$("#addForm input,select").val(""); 
            	gotoPage(1);//跳转到第几页
			}
		});
    }	
}
//提示框关闭
var closeDiv = function(divName){
	$("#"+divName).hide();
}


//弹出更新职位界面
function update(id){
	//创建新的更新窗口	
	$("#addOrEidt").val('2');//修改标识
	$("#id").val(id);
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'id':id},
		url: basePath + "sysConfig/getConfigbyId.htm",
		success:function(data){
			if(data != null && data.id != null){
				$("#updateForm :text[name='rank']").val(data.rank);
				$("#updateForm :text[name='code']").val(data.code);
				$("#updateForm :text[name='displayCode']").val(data.displayCode);
				$("#updateForm :text[name='description']").val(data.description);
				$("#updateForm :text[name='displayName']").val(data.displayName);
				$("#updateForm :text[name='userDef1']").val(data.userDef1);
				$("#updateForm :text[name='userDef2']").val(data.userDef2);
				$("#updateForm :text[name='userDef3']").val(data.userDef3);
				$("#updateForm :text[name='userDef4']").val(data.userDef4);
				$("#updateForm :text[name='userDef5']").val(data.userDef5);
				$("#updateForm :text[name='delFlag']").val(data.delFlag);
				$("#updateForm :text[name='remark']").val(data.remark);
				$("#id").val(data.id);
				$("#updateDiv").show();
			}
		}
	});
}

//修改公司配置表
function updatePostion(){
	
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data: $("#updateForm").serialize(),
		url:basePath + "sysConfig/updateConfig.htm",
		success:function(data){	
			console.info(data);
			closeDiv("updateDiv");     	
        	$("#messageBox #messageContent").text(data.result.message);            	
            $("#messageBox").show();
        	$('#reportList').children('tr').remove();//删除 dom操作节点
        	gotoPage(1);//跳转到第几页
		}
	});
}

//shanchu
function deletePostion() {
	var id = $("#deleteId").val();
	$.post(basePath + "sysConfig/deleteConfig.htm", {
		id : id
	}, function(data) {
		//1.关闭提示框
    	closeDiv("deleteDiv");
    	$("#messageBox #messageContent").text(data.result.message);
    	$("#messageBox").show();
    	$('#reportList').children('tr').remove();//删除 dom操作节点
    	gotoPage(1);//跳转到第几页
	}, 'json');
}

//删除职位
function del(id){
	$("#deleteId").val(id);
	$("#deleteDiv").show();
}

////////////////////////////////////////end/////////////////////////////////



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
		url:  basePath + "sysConfig/getReportPageList.htm",
		success:function(response){						
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";				
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].description)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].displayName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].displayCode)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].rank)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].userDef1)+"</td>";				
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].userDef2)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].userDef3)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].userDef4)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].userDef5)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].delFlag)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].remark)+"</td>";
				html += "<td style='text-align:center;'><a style='color:blue;' id="
	        	     +response.rows[i].id+" onclick='del(this.id)' >删除</a>&nbsp;&nbsp;&nbsp;<a style='color:blue;' id="+response.rows[i].id+" onclick='update("+response.rows[i].id+")' >修改</a></td></tr>"; 
				html += "</tr>";
			}
			$("#reportList").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}			
			initPage("commonPage",response,page);
		},
		complete:function(XMLHttpRequest,textStatus){  
			pageLoading(false);//关闭动画
        }
	});
}