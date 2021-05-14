$(function(){
	//默认查询所有
	gotoPage(1);
	$("#query").click(function(){
		gotoPage($("#pageNo").val());
	});	
	//预加载 添加按钮 curd 弹框 层操作
	$("#add").click(function(){
		add();
	});
	getAdminCompany();	
});
///////////////////////////////////////////////////curd 弹框 层操作 开始///////////////////////////////////////
//弹出新增职位界面
function add(){
	$("#addDiv").show();
}
//保存职位信息
function savePosition(){
	var name=$("#addForm :text[name='name']").val();
	var companyName=$("#companyId").val();
	var code=$("#addForm :text[name='code']").val();
	var rank=$("#addForm :text[name='rank']").val();
	if(code==""){
    	JEND.page.alert({type:"error", message:"职位编码不能为空"});
    	return;
	}
	
	if(rank==""){
    	JEND.page.alert({type:"error", message:"请添加一个排序数值"});
    	return;
	}
	
	if(name==""){
    	JEND.page.alert({type:"error", message:"职位编码不能为空"});
    	return;
	}
	
	if(code==""){
		JEND.page.alert({type:"error", message:"职位名称不能为空"});
    	return;
	}
	
	if(companyName==""){
		JEND.page.alert({type:"error", message:"请选择公司名称"});
    	return;
	}
	
	if(!houver()){
		return;
	}else{
		$.ajax({
			async:false,
			type:'post',
			dataType:'json',
			data: $("#addForm").serialize(),
			url:"add.htm",
			success:function(data){
	            if(data.result.flag==true){
	            	closeDiv("addDiv");
	            	$("#messageBox #messageContent").text("保存成功！");
	            	$("#messageBox").show();    
	            	$("#addForm input,select").val("");            
	            	gotoPage(1);//跳转到第几页
	            }
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
	$("#idupdate").val(id);
	
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'id':id},
		url: "getbyId.htm",
		success:function(data){
			if(data != null && data.id != null){
				getAdminCompany();
				$("#companyIds").val(getValByUndefined(data.companyId));
				//给隐藏 name 和code 传值
				$("#nameupdate").val(data.name);
				$("#codeupdate").val(data.code);
				$("#updateForm :text[name='companyId']").val(getValByUndefined(data.companyId));	
				$("#updateForm :text[name='rank']").val(data.rank);
				$("#updateForm :text[name='code']").val(data.code);
				$("#updateForm :text[name='name']").val(data.name);
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
	if(!houver()){
		return;
	}
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data: $("#updateForm").serialize(),
		url:"update.htm",
		success:function(data){	
            if(data.result.flag==true){
            	closeDiv("updateDiv");
            	$("#messageBox #messageContent").text("修改成功！");
            	$("#messageBox").show();
            	$('#reportList').children('tr').remove();//删除 dom操作节点
            	gotoPage(1);//跳转到第几页
            }
		}
	});
}

//shanchu
function deletePostion() {
	var id = $("#deleteId").val();
	$.post("delete.htm", {
		id : id
	}, function(data) {
		//1.关闭提示框
    	closeDiv("deleteDiv");
    	$("#messageBox #messageContent").text("删除成功！");
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


function houver(){
	var flag = true;
		var name=$("#addForm :text[name='name']").val();
		var nameup=$("#nameup").val();
		var id;
		if(name == ""){
			name=nameup;
			//判断 名字是否跟隐藏name 值相同 如相同则传id
			if(name==$("#nameupdate").val()){			
				id=$("#idupdate").val();
			}			
		}
		if(id == undefined){
			id=0;			
		}
		if(name!=""){
			$.ajax({  
		         type : "post",  
		          url : "queryName.htm",  
		          data : {name : name,id : id},  
		          async : false,  
		          success : function(data){  
					if(data==true){
						flag=false;
						name="";
						nameup="";
						$("#addForm :text[name='name']").val("");	
						$("#nameup").val("");
						id=0;
						JEND.page.alert({type:"error", message:"等级名称不能相同"});
					} 
		          }  
		     }); 
		}	

		if(!flag){//如果第一个验证都不通过，舍弃后面的验证
			return flag;
		}
		
		var code=$("#addForm :text[name='code']").val();
		var codeup=$("#codeup").val();
		var id;
		if(code == ""){
			code=codeup;
			//判断 名字是否跟隐藏code 值相同 如相同则传id
			if(code==$("#codeupdate").val()){	
				id=$("#idupdate").val();
			}			
		}
		if(id == undefined){
			id=0;			
		}
		if(code!=""){
			$.ajax({  
		         type : "post",  
		          url : "queryCode.htm",  
		          data : {code : code,id : id},  
		          async : false,  
		          success : function(data){  
		        	  if(data==true){							
						  code="";
						  codeup="";
						  $("#addForm :text[name='code']").val("");	
					      $("#codeup").val("");
						  id=0;
						  JEND.page.alert({type:"error", message:"等级编码不能相同"});
						  flag=false;							
					  } 
		          }  
		     }); 
		}		
	    return flag;
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
		url:  basePath + "companyPositionLevel/getReportPageList.htm",
		success:function(response){						
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+response.rows[i].companyName+"</td>";				
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].name)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].code)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].rank)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].remark)+"</td>";		
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].delFlag)+"</td>";
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