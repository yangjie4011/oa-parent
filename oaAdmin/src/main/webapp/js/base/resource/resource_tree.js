$(function(){
	//初始化资源树
	var currentCompanyId = $("#currentCompanyId").val();
	var roleId = $("#roleId").val();
	initResourceTree(currentCompanyId,roleId);
});

var resourceSetting = {
		open: true,
		data: {
		    key: {
			    title:"资源"
			},
			simpleData: {
				enable: true
			}
		},
		check: {    
	        enable: true  
	    }
};

//选择部门节点，加载职位信息
function onClickResourceTree(event, treeId, treeNode){
	var departId = treeNode.id;//部门表主键
	var companyId = treeNode.companyId;//公司表主键
	
	//选择部门,记录选中的部门id
	$("#departId").val(departId);
	//选择部门，往新增页面设置隐藏域
	$("#addDepartId").val(departId);
	$("#addCompanyId").val(companyId);
	//选择部门，往编辑页面设置隐藏域
	$("#editDepartId").val(departId);
	$("#eidtCompanyId").val(companyId);
	
	var position = $('#positionTable').datagrid({
        title:"部门职位管理",
        url: contextPath + "/position/getPageList.htm",
        queryParams : {'departId':departId,'companyId':companyId},
        width: ($("#mainDiv").width() - $("#mainDiv1").width() - 25),
        methord: 'post',
        height:500,
        fitColumns: true,
        sortName: 'id',
        sortOrder: 'desc',
        idField: 'id',
        pageSize: 10,
        pageList: [5,10,20],
        pagination: true,
        striped: true, //奇偶行是否区分
        singleSelect: true,//单选模式
        rownumbers: true,//行号
        columns: [[
            { field: 'positionName', title: '职位名称', width: 30 },
            { field: 'companyPositionSeq', title: '职位序列', width: 30,
            	formatter:function(value,row,index){
            		if("" != getValByUndefined(value)){
            			return value.name;
            		}
            		
            		return "";
            	}
            },
            { field: 'companyPositionLevel', title: '职位等级', width: 30,
            	formatter:function(value,row,index){
	        		if("" != getValByUndefined(value)){
	        			return value.name;
	        		}
	        		
	        		return "";
            	}
        	}
        ]],
        toolbar: [{
		    text:'新增',
			iconCls: 'icon-add',
			handler: function(){
				add(companyId);
			}
		}],         
    });
	
	return position;
}

function saveSet(){
	var treeObj = $.fn.zTree.getZTreeObj("resourceTree");
	var nodes = treeObj.getCheckedNodes(true);
	var list = new Array();
	$.each(nodes, function(index) {
		list.push(nodes[index].id);
	});
	JEND.load('util.dialog', function() {
		 JEND.util.dialog.showLoading({loadingText:'正在保存设置...'});
	});
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{roleId:$("#roleId").val(),sourceIds:JSON.stringify(list)},
		url:contextPath + "/resource/saveSet.htm",
		success:function(data){
			JEND.load('util.dialog', function() {
				JEND.page.alert({type:"success", message:data.result.message});
			});
		}
	});
}

//全选
function CheckAllNodes() {
    var treeObj = $.fn.zTree.getZTreeObj("resourceTree");
    treeObj.checkAllNodes(true);
}

//全取消
function CancelAllNodes() {
    var treeObj = $.fn.zTree.getZTreeObj("resourceTree");
    treeObj.checkAllNodes(false);
}