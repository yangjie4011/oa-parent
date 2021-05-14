var operationIds = new Array();
$(function () {
	getFirstDepart();
	initAllRoleList();
})

var resourceSetting = {
	open: true,
	check: {
        enable: true,
    },
	data: {
	    key: {
		    title:"name"
		},
		simpleData: {
			enable: true
		}
	},
	view:{
		showLine:true
	},
	callback: {
        onClick: zTreeOnClick,
    },
};
var operationSetting = {
		open: true,
		check: {
	        enable: true,
	    },
		data: {
		    key: {
			    title:"name"
			},
			simpleData: {
				enable: true
			}
		},
		view:{
			showLine:true
		},
		//树复选框选中事件
		callback: {
			onClick: checkedNodes,
			onCheck: zTreeOnCheck
		}
	};

function initAllRoleList(){
	$("#chooseRoleName").val("");
	$("#roleInfoDiv").empty();
	$("#addDepart").val($("#firstDepart").val());
	$("#roleDepartName").val($("#firstDepart option:selected").text());
	var departId = $("#firstDepart").val();
	var html = "";
	html += "<tr>"
	html += "<td style='text-align:center;width:500px;vertical-align:middle;'><div id='allRoleList' style='height:400px;overflow-y:scroll;margin:0 auto;'></div></td>";
	html += "<td style='text-align:center;width:500px;vertical-align:middle;'><div id='roleRemark'></div></td>";
	html += "<td style='width:500px;overflow-y:scroll;'><div style='height:400px;margin:0 auto;'><ul id='roleResourceTree' class='ztree'></ul></div></td>";
	html += "<td style='width:500px;overflow-y:scroll;'><div style='height:400px;margin:0 auto;'><ul id='operationTree' class='ztree'></ul></div></td>";
	html +="</tr>";
	$("#roleInfoDiv").append(html);
	//查询所有角色
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data: {departId:departId},
		url:contextPath + "/uRole/getAllURoleList.htm",
		success:function(data){
			var str = "";
			if(data != null && data != ""){
				for(var i=0; i<data.length; i++){
					var name = data[i].name;
					var id = data[i].id
					str+="<button id='button"+id+"' onclick='getRoleInfo("+id+")' class='blue-sm'  style='float:none;'><span><i class='icon'></i>"+name+"</span></button></br></br>";
				}
			}
			$("#allRoleList").html(str);
			if(data != null && data != ""){
				$("#chooseRoleId").val(data[0].id);
				$("#chooseRoleName").val($("#firstDepart option:selected").text()+"-"+data[0].name);
				var roleId = $("#chooseRoleId").val();
				getRoleInfo(roleId)
			}
		}
	});
}
function getRoleInfo(roleId){
	operationIds = [];
	$("#operationTree").empty();
	pageLoading(true);//开启动画
	//切换按钮颜色
	$("#allRoleList button").removeClass().addClass("blue-sm");
	$("#button"+roleId+"").removeClass().addClass("blue-but");
	$.ajax({
		async:true,
		type:'post',
		data: {roleId:roleId},
		dataType:'json',
		url:contextPath + "/uRole/getRoleInfo.htm",
		success:function(data){
			if(roleId != null && roleId != ""){
				$("#chooseRoleId").val(data.id);
				$("#chooseRoleName").val($("#firstDepart option:selected").text()+"-"+data.name);
				$('#roleRemark').html(data.remark);
			}
		}
	});
	if(roleId != null && roleId != ""){
		initRoleResourceTree(roleId);
		initAllOperationIds(roleId);
	}
	pageLoading(false);//开启动画
}
//生成菜单树
function initRoleResourceTree(roleId){
	$.ajax({
   		async : true,
   		enable:true,
   		type : "post",
 		dataType:"json",
 		data : {'roleId':roleId},
   		url : contextPath + "/uRole/getRoleResourceTree.htm",
   		success : function(data) {
   			var ztreeObj = $.fn.zTree.init($("#roleResourceTree"), resourceSetting, JSON.parse(data.result));
   			ztreeObj.expandAll(true);
   		}
   	});
}

function addRole(){
	if($('#firstDepart').val() != null && $('#firstDepart').val() != "" && $('#firstDepart').val() != undefined){
		$("#addRoleDiv").show();
	}
}
function save(){
	if($('#firstDepart').val() != null && $('#firstDepart').val() != "" && $('#firstDepart').val() != undefined){
		$("#saveResourceDiv").show();
	}
}
function del(){
	if($('#firstDepart').val() != null && $('#firstDepart').val() != "" && $('#firstDepart').val() != undefined){
		$("#delRoleDiv").show();
	}
}

function closeDiv(){
	//清空缓存
	$("#roleName").val("");
	$("#remark").val("");
	$("#addRoleDiv").hide();
	$("#saveResourceDiv").hide();
	$("#delRoleDiv").hide();
}

//添加角色
function add(){
	var data = $("#addRoleForm").serialize();
	pageLoading(true);//开启动画
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "uRole/addRole.htm",
		success:function(response){
			JEND.load('util.dialog', function() {
				if(response.success){
					JEND.page.alert({type:"success", message:"保存成功！"});
					initAllRoleList();
					getRoleInfo($("#chooseRoleId").val());
				}else{
					JEND.page.showError(response.message);
					initAllRoleList();
					getRoleInfo($("#chooseRoleId").val());
				}
			});
			pageLoading(false);//关闭动画
		}	
	});
	//清除输入框缓存
	$("#roleName").val("");
	$("#remark").val("");
	closeDiv();
}

function saveResource(){
	//保存修改的角色
	var roleId = $("#chooseRoleId").val();
	//获取选中的菜单
	var resourceTreeObj=$.fn.zTree.getZTreeObj("roleResourceTree");
    var resourceNodes =resourceTreeObj.getCheckedNodes(true);
    var resourceIds = "";
    for(var i=0;i<resourceNodes.length;i++){
    	resourceIds += resourceNodes[i].id+",";
    	}
    //获取选中的操作
    for(var i=0;i<operationIds.length;i++){
    	resourceIds += operationIds[i]+",";
    	}
    pageLoading(true);//开启动画
    $.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: {roleId:roleId,
			   resourceIds:resourceIds},
		url:  basePath + "uRole/saveResource.htm",
		success:function(response){
			JEND.load('util.dialog', function() {
				if(response.success){
					JEND.page.alert({type:"success", message:"保存成功！"});
					getRoleInfo(roleId)
				}else{
					JEND.page.showError(response.message);
					getRoleInfo(roleId)
				}
			});
			pageLoading(false);//关闭动画
		}	
	});
	closeDiv();
}
function delRole(){
	var roleId = $("#chooseRoleId").val();
	pageLoading(true);//开启动画
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: {roleId:roleId},
		url:  basePath + "uRole/delRole.htm",
		success:function(response){
			JEND.load('util.dialog', function() {
				if(response.success){
					JEND.page.alert({type:"success", message:"删除成功！"});
					initAllRoleList();
					getRoleInfo($("#chooseRoleId").val());
				}else{
					JEND.page.showError(response.message);
					initAllRoleList();
					getRoleInfo($("#chooseRoleId").val());
				}
			});
			pageLoading(false);//关闭动画
		}	
	});
	closeDiv();
}
function initRoleOperationTree(resourceId,roleId){
	 $.ajax({
			async:true,
			type:'post',
			dataType:'json',
			data: {resourceId:resourceId,roleId:roleId},
			url:  basePath + "uRole/getOperationTree.htm",
			success:function(response){
				var ztreeObj = $.fn.zTree.init($("#operationTree"),operationSetting, JSON.parse(response.result));
	   			ztreeObj.expandAll(true);
	   			checkedNodes();
			}	
		});
}

function zTreeOnClick(event, treeId, treeNode) {
	var roleId = $("#chooseRoleId").val();
    initRoleOperationTree(treeNode.id,roleId);
};

function zTreeOnCheck(event, treeId, treeNode) {
	var isExist = false;
	for(var i=0; i<operationIds.length; i++){
		if(operationIds[i] == treeNode.id){
			isExist = true;
		}
    }
	if(isExist){
		var index = operationIds.indexOf(treeNode.id);
		operationIds.splice(index,1);
	}else{
		operationIds.push(treeNode.id);
	}
};

function initAllOperationIds(roleId){
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: {roleId:roleId},
		url:  basePath + "uRole/initAllOperationIds.htm",
		success:function(data){
			for(var i=0; i<data.length; i++){
				operationIds.push(data[i]);
		    }
		}	
	});
};
function checkedNodes(){
	var zTree = $.fn.zTree.getZTreeObj("operationTree");
	var nodes = zTree.getNodes();
	for(var i=0;i<nodes.length;i++){
		for(var j=0; j<operationIds.length; j++){
			if(nodes[i].id == operationIds[j]){
				zTree.checkNode(nodes[i], true, true);
				zTree.updateNode(nodes[i]);
			}
	    }
	}
	
}
