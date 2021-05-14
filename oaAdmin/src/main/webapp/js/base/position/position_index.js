$(function() {
	//初始化部门数据
	var currentCompanyId = $("#currentCompanyId").val();
	initDepartTree(currentCompanyId);
	
	//默认不显示新增和更新职位
	$("#add").click(function(){
		add();
	});
});

var departSetting = {
	open: true,
	data: {
	    key: {
		    title:"部门"
		},
		simpleData: {
			enable: true
		}
	},
	callback: {
		onClick: onClickDepartTree
	}
};

//选择部门节点，加载职位信息
function onClickDepartTree(event, treeId, treeNode){
	var departId = treeNode.id;//部门表主键
	var companyId = treeNode.companyId;//公司表主键
	
	//选择部门,记录选中的部门id
	$("#departId").val(departId);
	$("#clickedCompanyId").val(companyId);
	//选择部门，往新增页面设置隐藏域
	$("#addDepartId").val(departId);
	$("#addCompanyId").val(companyId);
	//选择部门，往编辑页面设置隐藏域
	$("#editDepartId").val(departId);
	$("#eidtCompanyId").val(companyId);
	$("#dataDiv").show();
	gotoPage(0);
}


function gotoPage(page){
	if(!page){
		page = 1;
	}
	$("#pageNo").val(page);
	
	var departId = $("#departId").val();
	var companyId = $("#clickedCompanyId").val();
	
	var url = contextPath + "/position/getPageList.htm";
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data: {page:$("#pageNo").val(),rows:10,departId:departId,companyId:companyId},
		url:  url,
		success:function(response){
			$("#positionList").empty();
			var html = "";
			
            var rows = response.rows;
	        $.each(rows, function (i, item) {  
	        	var position_name = item.positionName;
	        	var level_name = (typeof(item.companyPositionLevel)||typeof(item.companyPositionLevel.name))=="undefined"?"&nbsp;":item.companyPositionLevel.name;
	        	var id = item.id;
	        	html += "<tr><td style='text-align:center;'>"+position_name+"</td>"
	        	     +"<td style='text-align:center;'>"+level_name+"</td><td style='text-align:center;'><a style='color:blue;' id="
	        	     +id+" onclick='del("+id+")' >删除</a>&nbsp;&nbsp;&nbsp;<a style='color:blue;' id="+id+" onclick='update("+id+")' >修改</a></td></tr>"; 
	        });  
			$("#positionList").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			initPage("commonPage",response,page);
		}
	});
}

function power(){
	getPositionTree(1);
}

//获得职位树
function getPositionTree(addOrEdit){
	//初始化职位数据
	$("#addOrEidt").val(addOrEdit);
	window.open(contextPath + "/position/getPositionTree.htm",'选择职位','height=500,width=400,top=300,left=500,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no') 
}

//删除职位
function del(id){
	$("#deleteId").val(id);
	$("#deleteDiv").show();
	//pops.confShow("短信验证码发送提醒","yes",'短信验证码已发送成功，请注意手机360等防毒软件把短信设置为垃圾短信！');
}

//弹出更新职位界面
function update(id){
	//创建新的更新窗口
	var companyId = $("#clickedCompanyId").val();
	$("#addOrEidt").val('2');//修改标识
	$("#id").val(id);
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'id':id},
		url:contextPath + "/position/getByCondition.htm",
		success:function(data){
			if(data != null && data.id != null){
				$("#positionName").val(data.positionName);
				
				//打开更新职位窗口
				initPositionLevel(companyId);
				
				//选中职级和职位序列下拉
				var level_id = (typeof(data.companyPositionLevel)=="undefined"||typeof(data.companyPositionLevel.id)=="undefined")?"":data.companyPositionLevel.id;
				$("#editPositionLevel").val(level_id);
				$("#updateDiv").show();
			}
		}
	});
}

//弹出新增职位界面
function add(){
	var companyId = $("#clickedCompanyId").val();
	$("#addOrEidt").val('1');//新增标识
	initpositionSeq(companyId);
	initPositionLevel(companyId);
	
	$("#addDiv").show();
}

//加载职位序列下拉
function initpositionSeq(companyId){
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		url:contextPath + "/companyPositionSeq/getListByCondition.htm",
		success:function(data){
			var addOrEidt = $("#addOrEidt").val();
			var sel;
			if("" == addOrEidt || "1" == addOrEidt){//如果是新增(第一次是空值)
				sel = $("#addPositionSeq");
				sel.empty();
				var options = "<option value=''>请选择</option>";
				sel.append(options);
			}else if("2" == addOrEidt){//修改
				sel = $("#editPositionSeq");
				sel.empty();
				var options = "<option value=''>请选择</option>";
				sel.append(options);
			}
			$.each(data, function(index) {
				sel.append("<option value= " + data[index].id + ">" + data[index].name + "</option>");
			});
		}
	});
}

//加载职位等级下拉
function initPositionLevel(companyId){
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		url:contextPath + "/companyPositionLevel/getListByCondition.htm",
		success:function(data){
			var addOrEidt = $("#addOrEidt").val();
			var sel;
			if("" == addOrEidt || "1" == addOrEidt){//如果是新增(第一次是空值)
				sel = $("#addPositionLevel");
				sel.empty();
				var options = "<option value=''>请选择</option>";
				sel.append(options);
			}else if("2" == addOrEidt){//修改
				sel = $("#editPositionLevel");
				sel.empty();
				var options = "<option value=''>请选择</option>";
				sel.append(options);
			}
			
			$.each(data, function(index) {
				sel.append("<option value= " + data[index].id + ">" + data[index].name + "</option>");
			});
		}
	});
}

//保存职位信息
function savePosition(){
	
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data: $("#addForm").serialize(),
		url:contextPath + "/position/save.htm",
		success:function(data){
        	
            if(data.result.flag==true){
            	closeDiv("addDiv");
            	JEND.page.alert({type:"success", message:data.result.message});
                //2.刷新树
                //获得树
                var treeObj = $.fn.zTree.getZTreeObj("departTree");
                //给树添加点击事件
                treeObj.setting.callback.onClick(null, treeObj.setting.treeId, treeObj.getNodeByParam("id",$("#addDepartId").val(),null));
                
                //3.清空表格中的内容
                var addCompanyId = $("#addCompanyId").val();
                var addDepartId = $("#addDepartId").val();
                
                //4.隐藏域重新赋值
                $("#addCompanyId").val(addCompanyId);
                $("#addDepartId").val(addDepartId);
            }else{
            	JEND.page.alert({type:"error", message:data.result.message});
            }
		}
	});
}

//修改职位信息
function updatePostion(){
	
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data: $("#updateForm").serialize(),
		url:contextPath + "/position/update.htm",
		success:function(data){
			
            if(data.result.flag==true){
            	closeDiv("updateDiv");
            	JEND.page.alert({type:"success", message:data.result.message});
                //2.刷新树
                //获得树
                var treeObj = $.fn.zTree.getZTreeObj("departTree");
                //给树添加点击事件
                treeObj.setting.callback.onClick(null, treeObj.setting.treeId, treeObj.getNodeByParam("id",$("#editDepartId").val(),null));
            }else{
            	JEND.page.alert({type:"error", message:data.result.message});
            }
		}
	});
}

function deletePostion() {

	var id = $("#deleteId").val();
	$.post(basePath + "/position/delete.htm", {
		id : id
	}, function(data) {
		//1.关闭提示框
    	closeDiv("deleteDiv");
		if (data.result.flag) {
			JEND.page.alert({type:"success", message:data.result.message});
			//2.刷新树
			//获得树
			var treeObj = $.fn.zTree.getZTreeObj("departTree");
			//给树添加点击事件
			treeObj.setting.callback.onClick(null, treeObj.setting.treeId,
					treeObj.getNodeByParam("id", $("#departId").val(), null));
		}else{
			JEND.page.alert({type:"error", message:data.result.message});
		}
	}, 'json');
}

var closeDiv = function(divName){
	$("#"+divName).hide();
}

