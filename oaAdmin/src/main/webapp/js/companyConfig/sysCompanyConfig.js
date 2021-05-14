$(function(){
	//默认查询所有
	gotoPage(1);
	$("#query").click(function(){
		gotoPage(1);
	});	
	//预加载 公司
	initpositionSeq(null);
	//预加载 添加按钮 curd 弹框 层操作
	$("#add").click(function(){
		add();
	});
});
///////////////////////////////////////////////////curd 弹框 层操作 开始///////////////////////////////////////
//弹出新增职位界面
function add(){
	var companyId = $("#clickedCompanyId").val();
	$("#addOrEidt").val('1');//新增标识
	initpositionSeq(companyId);
	$("#addDiv").show();
}
//保存职位信息
function savePosition(){
	//添加验证	
	var addPositionSeq=$("#addPositionSeq").val();
	var code=$("#addForm :text[name='code']").val();
    if(addPositionSeq==""){
       JEND.page.alert({type:"error", message:"公司选项不能为空"});
    }else if(code==""){
    	JEND.page.alert({type:"error", message:"配置不能为空"});
    }else{
    	$.ajax({
    		async:false,
    		type:'post',
    		dataType:'json',
    		data: $("#addForm").serialize(),
    		url:basePath + "companyConfig/addCompanyForm.htm",
    		success:function(data){   
    			closeDiv("addDiv");            	
                $("#messageBox #messageContent").text("data.result.message");                	
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
//加载公司序列下拉 sel.empty(); 
function initpositionSeq(companyId){
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		url:basePath + "companyConfig/getCompanyinfo.htm",
		success:function(data){
			var addOrEidt = $("#addOrEidt").val();
			var sel= $("#SelectPositionSeq");   //init""第一个给公司下拉			
			if("1" == addOrEidt){	//如果是新增(第一次是空值)
				sel = $("#addPositionSeq");
			}else if("2" == addOrEidt){			
		
				sel = $("#updatePositionSeq");
			}
			sel.empty();
			var options = "<option value=''>请选择</option>";			
			sel.append(options);
			$.each(data, function(index) {
				sel.append("<option value= " + data[index].companyId + ">" + data[index].name + "</option>");
			});	
		}
	});
}




//弹出更新职位界面
function update(id,companyId){
	//创建新的更新窗口
	
	$("#addOrEidt").val('2');//修改标识
	$("#id").val(id);
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'id':id},
		url: basePath + "companyConfig/getConfigbyId.htm",
		success:function(data){
			if(data != null && data.id != null){
				initpositionSeq(companyId);							
				//选中职级和职位序列下拉
				$("#updatePositionSeq").val(getValByUndefined(data.companyId));	
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
		url:basePath + "companyConfig/updateCompanyConfig.htm",
		success:function(data){
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
	$.post(basePath + "companyConfig/deleteCompanyConfig.htm", {
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
		url:  basePath + "companyConfig/getReportPageList.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+response.rows[i].name+"</td>";				
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].displayName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].displayCode)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].rank)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].userDef1)+"</td>";				
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].userDef2)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].userDef3)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].userDef4)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].userDef5)+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].delFlag+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].remark)+"</td>";
				html += "<td style='text-align:center;'><a style='color:blue;' id="
	        	     +response.rows[i].id+" onclick='del(this.id)' >删除</a>&nbsp;&nbsp;&nbsp;<a style='color:blue;' id="+response.rows[i].id+" onclick='update(this.id,"+response.rows[i].companyId+")' >修改</a></td></tr>"; 
				html += "</tr>";
			}
			$("#reportList").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}			
			initPage("commonPage",response,page);
		},
		complete:function(){  
			pageLoading(false);//关闭动画
        }
	});
}