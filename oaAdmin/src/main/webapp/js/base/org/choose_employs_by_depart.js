$(function(){
	$(".col-left").height($(window).height()-99);
	//左侧树形部门展示
	var zNodes = zNodesInit();
	var ztreeObj = $.fn.zTree.init($("#departTree"), setting, zNodes);
	ztreeObj.expandAll(true);
	//初始化数据
	$("#departmentId").val(zNodes[0].id);
	
	var employDatagrid = null; //定义全局变量employDatagrid
	//列表
    employDatagrid = $('#employTable').datagrid({
    	title:"选择人员",
		singleSelect: true,
        remoteSort:false,
        selectOnCheck: false,
        checkOnSelect: false,
        method : "post",
        height:$(window).height() - 80,
        width:561,
        pagination:true,
        queryParams : {departId:$("#departId").val()},
		url : basePath + "depart/powerList.htm",
        pagePosition:'bottom',
        pageSize: 100,
        pageList:[20,50,100,200],
        columns :[[
		    {field:'id',width:0,hidden:true},
		    {field:'cnName',width:110,align:'center',sortable:true,title:"员工姓名"},
		    {field:'departName',width:200,align:'center',sortable:true,title:"所属部门"},
		    {field:'positionName',width:150,align:'center',sortable:true,title:"职位名称"},
		    {field:'operation',title:"操作",width:99,align:'center',
		    	formatter: function(value, row, index){
		    		return "<a uid='" + row.id + "' id=" + JSON.stringify(row) + " onclick='choose(this)'>选择</a>";
		    	}
		    }
        ]],
        onLoadSuccess:function(response){
        }
	});
});

/**
 * 选择人员后,将人员信息带入到修改页面
 * @param val
 */
function choose(tar) {
	var employee = $(tar).attr("id");
	employee = JSON.parse(employee);
	window.parent.document.getElementById("powerName").value = employee.cnName;
	window.parent.document.getElementById("power").value = employee.id;
	parent.closePop();
}

/** ztree设置初始化 begin */
function zNodesInit(){
	var zNodes;
	$.ajax({
   		async : false,
   		type : "get",
   		data : {id:""},
 		dataType:"json",
		url : basePath + "depart/tree.htm",
		success : function(data) {
			zNodes = data;
		}
    });
	return zNodes;
}

var setting = {
	open: true,
	data: {
	    key: {
		    title:"t"
		},
		simpleData: {
			enable: true
		}
	},
	callback: {
		onClick: onClick
	}
};

/**
 * ztree菜单点击事件
 * @param event
 * @param treeId
 * @param treeNode
 * @param clickFlag
 */
function onClick(event, treeId, treeNode, clickFlag) {
	//查询
	$("#employTableDiv").show();
	$("#employTable").datagrid({queryParams : {departId:treeNode.id}});
}
/** ztree设置初始化 end */






