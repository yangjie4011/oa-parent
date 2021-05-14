$(function() {
	//初始化部门数据
	var currentCompanyId = $("#currentCompanyId").val();
	initDepartTree(currentCompanyId);
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
    check: {    
        enable: true  
    },
	callback: {
		onCheck: onClickDepartTree
	}
};


//选择部门节点，加载人员
function onClickDepartTree(event, treeId, treeNode){
	if(treeNode.checked){
		//选中
		if(treeNode.type=="0"){
			//取出所有的字节点
			var children = treeNode.children;
			$.each(children, function(index) {
			});
		}else{
			
		}
	}else{
		
	}
	
}

//初始化部门树（根据公司id获得部门树）
function initDepartTree(currentCompanyId){
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data : {'companyId':currentCompanyId},
 		url : contextPath + "/depart/getEmployeeTreeList.htm",
 		success : function(data) {
 			var ztreeObj = $.fn.zTree.init($("#departTree"), departSetting, JSON.parse(data.result));
 			ztreeObj.expandAll(false);
 		}
 	});
}