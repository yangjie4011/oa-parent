$(function() {
	//初始化部门数据
	var currentCompanyId = $("#currentCompanyId").val();
	initDepartTree(currentCompanyId);
	
	//默认不显示新增和更新部门
	$("#addWin").window("close");
	$("#updateWin").window("close");
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

//选择部门节点，加载部门信息
function onClickDepartTree(event, treeId, treeNode){
	var departId = treeNode.id;//部门表主键
	var companyId = treeNode.companyId;//公司表主键
	
	//选择部门,记录选中的部门id
	$("#departId").val(departId);
	
	var depart = $('#departTable').datagrid({
        title:"部门管理",
        url: contextPath + "/depart/list.htm",
        queryParams : {id:departId},
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
        //去除点击变蓝效果
        onClickRow: function (rowIndex, rowData) {
      	  $(this).datagrid('unselectRow', rowIndex);
      	  },
        columns :[[
       		    {field:'id',width:0,hidden:true},
       		    {field:'type',width:120,align:'center',sortable:true,title:"部门类型",
       		    	formatter: function(value, row, index){
	   		    		if("0" == value){
	   		    			return "虚拟部门";
	   		    		}else if("1" == value){
	   		    			return "一级部门";
	   		    		}else if("2" == value){
	   		    			return "二级部门";
	   		    		}else{
	   		    			return "未知类型";
	   		    		}
       		    }},
       		    {field:'name',width:200,align:'center',sortable:true,title:"部门名称"},
       		    {field:'leaderName',width:120,align:'center',sortable:true,title:"部门负责人"},
       		    {field:'positionName',width:200,align:'center',sortable:true,title:"负责人职位"},
       		    {field:'empCount',width:120,align:'center',sortable:true,title:"员工数"},
       		    {field:'whetherScheduling',title:"是否排班",width:180,align:'center',
       		    	formatter: function(value, row, index){
       		    		if("1" == value){
	   		    			return '<select id='+row.id+' onchange="update(this)"><option selected="selected" value="1">是</option><option value="0">否</option></select>';
	   		    		}else{
	   		    			return '<select id='+row.id+' onchange="update(this)"><option value="1">是</option><option selected="selected" value="0">否</option></select>';
	   		    		}
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
	
	return depart;
}

//修改是否需要排班属性
function update(obj){
	$.messager.confirm('修改部门','确认要修改部门排班属性？',function(r){
	    if (r){
	    	$.messager.progress({title:"修改部门",msg:"正在修改..."});
	    	
	    	$.post(basePath+"/schedule/updateWhetherSchdule.htm",{id:$(obj).attr("id"),whetherScheduling:$(obj).val()},function(data){
	    		//1.关闭提示框
	    		$.messager.progress("close");
	    		$.messager.alert("修改部门",data.message);
	    		
	    		if("0000" == data.code){
	    			var currentCompanyId = $("#currentCompanyId").val();
	            	initDepartTree(currentCompanyId);
				}
			},'json');
	    }
	});
}
