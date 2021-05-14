$(function () {
	initAllRoleList();
	var roleId = $("#chooseRoleId").val();
	getRoleInfo(roleId)
	$("#query").click(function(){
		initAllRoleList();
		var roleId = $("#chooseRoleId").val();
		getRoleInfo(roleId)
	});
	$("#returnbox").click(function(){
		$(".queryFrom input[name='nickname']").val("");
		$(".queryFrom input[name='code']").val("");
		initAllRoleList();
		var roleId = $("#hiddenUid").val();
		getRoleInfo(roleId)
	});
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
	}
	
};

function initAllRoleList(){
	$("#roleInfoDiv").empty();
	var html = "";
	html += "<tr>"
	html += "<td style='text-align:center;width:500px;vertical-align:middle;'><div id='allRoleList' style='height:400px;overflow-y:scroll;margin:0 auto;'></div></td>";
	html += "<td style='text-align:center;width:500px;vertical-align:middle;'><div id='roleRemark'></div></td>";
	html += "<td style='text-align:center;width:100px;vertical-align:middle;'><div id='cnName'></div></td>";
	html += "<td style='text-align:center;width:100px;vertical-align:middle;'><div id='code'></div></td>";
	html += "<td style='width:500px;overflow-y:scroll;'><div style='height:400px;margin:0 auto;'><ul id='roleResourceTree' class='ztree'></ul></div></td>";
	
	html +="</tr>";
	$("#roleInfoDiv").append(html);
	
	var data =$("#queryFrom").serialize();

	//查询所有角色
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:data,
		url:contextPath + "/user/getUserDeptList.htm",
		success:function(data){
			var str = "";
			for(var i=0; i<data.length; i++){
		        var name = data[i].nickname;
		        var id = data[i].id
		        str+="<button id='button"+id+"' onclick='getRoleInfo("+id+")' class='blue-sm'  style='float:none;'><span><i class='icon'></i>"+name+"</span></button></br></br>";
		    }
			if(data.length==0){
				$("#allRoleList").html("");
				$("#chooseRoleId").val(null);
			}else{
				$("#allRoleList").html(str);
				$("#chooseRoleId").val(data[0].id);
			}
			
		}
	});
}
function getRoleInfo(roleId){
	if(roleId=="" || roleId==null){
		return;
	}
	//切换按钮颜色
	$("#allRoleList button").removeClass().addClass("blue-sm");
	$("#button"+roleId+"").removeClass().addClass("blue-but");
	$.ajax({
		async:false,
		type:'post',
		data: {id:roleId},
		dataType:'json',
		url:contextPath + "/user/queryUserById.htm",
		success:function(data){
			if(data.length==0){
				$("#chooseRoleId").val(null);
			}else{
				$("#chooseRoleId").val(data.result.usre.id);
				$('#roleRemark').html(data.result.usre.remark);
				$('#cnName').html(data.result.usre.cnName);
				$('#code').html(data.result.usre.code);
				$("#hiddenUid").val(roleId);
			}
			
		}
	});
	initRoleResourceTree(roleId);
}
//生成菜单树
function initRoleResourceTree(roleId){
	$.ajax({
   		async : false,
   		enable:true,
   		type : "post",
 		dataType:"json",
 		data : {'uid':roleId},
   		url : contextPath + "/user/getDeptTreeByUid.htm",
   		success : function(data) {
   			var ztreeObj = $.fn.zTree.init($("#roleResourceTree"), resourceSetting, JSON.parse(data.result));
   			ztreeObj.expandAll(true);
   		}
   	});
}

function addRole(){
	$("#addRoleDiv").show();
}
function save(){
	$("#saveResourceDiv").show();
}
function closeDiv(){
	//清空缓存
	$("#roleName").val("");
	$("#remark").val("");
	$("#addRoleDiv").hide();
	$("#saveResourceDiv").hide();
	$("#delRoleDiv").hide();
}



function saveResource(){
	//保存修改的角色
	var roleId = $("#chooseRoleId").val();
	//获取选中的菜单
	var treeObj=$.fn.zTree.getZTreeObj("roleResourceTree");
    var nodes =treeObj.getCheckedNodes(true);
    var resourceIds = "";
    for(var i=0;i<nodes.length;i++){
    	resourceIds += nodes[i].id+",";
    	}
    pageLoading(true);//开启动画
    $.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: {uid:roleId,
			deptIds:resourceIds},
		url:  basePath + "user/saveDept.htm",
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

