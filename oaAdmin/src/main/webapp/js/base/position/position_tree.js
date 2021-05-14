$(function() {
	//初始化职位数据
	var departId = $("#departId").val();
	initPositionTree(departId);
});

//选择职位树节点事件
function onClickPositionTree(event, treeId, treeNode){
	var addOrEidt = window.opener.document.getElementById("addOrEidt").value;
	
	if('1' == addOrEidt){
		window.opener.document.getElementById("addParentName").value = treeNode.name;
		window.opener.document.getElementById("addParrentId").value = treeNode.id;
	}else if('2' == addOrEidt){
		window.opener.document.getElementById("editParentName").value = treeNode.name;
		window.opener.document.getElementById("editParentId").value = treeNode.id;
	}
	
	//关闭子窗口
    window.close();
}

var positionSetting = {
	open: true,
	data: {
	    key: {
		    title:"职位"
		},
		simpleData: {
			enable: true
		}
	},
	callback: {
		onClick: onClickPositionTree
	}
};