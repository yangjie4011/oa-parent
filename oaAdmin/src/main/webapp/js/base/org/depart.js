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
       		    {field:'operation',title:"操作",width:180,align:'center',
       		    	formatter: function(value, row, index){
       		    		var deletea='<a id='+row.id+' onclick="del(this.id)" >删除</a>';//
    					var update='<a id='+row.id+' onclick="update(this.id)" >修改</a>';
       		    		
    					var val=deletea+"&nbsp;&nbsp;&nbsp;"+update;
    					return val;
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

//弹出新增部门界面
function add(companyId){
	$("#addOrEdit").val('1');//新增标识
	initDepartType("addDepartType",companyId);
	$("#addParentId").val($("#departId").val());
	
	var saveWin = $("#addWin").window({
		title:"新增部门",
		width:300,
		height:200,
		top: ($(window).height() - 410) * 0.5,
	    left: ($(window).width() - 480) * 0.5,
		shadow: true,
	    modal: true,
	    iconCls: 'icon-add',
	    closed: true,
	});
	
	saveWin.window("open");
}

//弹出更新部门界面
function update(id){
	//创建新的更新窗口
	var updateWin = $("#updateWin").window({
		title:"更新部门",
		width:300,
		height:200,
		top: ($(window).height() - 410) * 0.5,
	    left: ($(window).width() - 480) * 0.5,
		shadow: true,
	    modal: true,
	    iconCls: 'icon-add',
	    closed: true,
	});
	
	$("#addOrEdit").val('2');//修改标识
	$("#id").val(id);
	$("#editParentId").val($("#departId").val());
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'id':id},
		url:basePath + "/depart/getByCondition.htm",
		success:function(data){
			if(data != null && data.id != null){
				//打开更新部门窗口
				updateWin.window('open');
				
				//initDepartType("editDepartType",$("#currentCompanyId").val());
				//选中部门类型下拉
				$("#editDepartType").val(getValByUndefined(data.type));
				$("#editName").val(data.name);
				$("#editLeaderName").val(data.leaderName);
				$("#editLeaderId").val(data.leader);
				$("#editPowerName").val(data.powerName);
				$("#editPowerId").val(data.power);
				
				//保存 之前的部门负责人
				$("#beforeLeaderId").val(data.leader);
			}
		}
	});
}

//获得部门负责人树
function getLeaderTree(departType,addOrEdit){
	$("#addOrEdit").val(addOrEdit);
	if("1" == departType){
		window.open(basePath + "employee/getLeaderIndex.htm",'选择部门负责人','height='+$(window).height()-100+',width='+$(window).width()-50+',top='+$(window).height()-50+',left='+$(window).width()-50+',toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no')
	}else{
		$.messager.alert("提示","部门仅1级部门可以设定部门负责人，其他机构不可增设部门负责人");
	}
}

//保存部门信息
function saveDepart(){
	$.messager.progress({title:"保存部门",msg:"正在保存部门..."});
	
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data: $("#addForm").serialize(),
		url:contextPath + "/depart/save.htm",
		success:function(data){
			$.messager.alert('保存部门', data.message);
			//1.关闭进度条
        	$.messager.progress("close");
        	
            if("0000" == data.code){
            	$("#addWin").window("close");
                
                //2.刷新树
                //获得树
            	var currentCompanyId = $("#currentCompanyId").val();
            	initDepartTree(currentCompanyId);
                
                //3.清空表格中的内容
                $("#addForm").form("clear");
            }
		}
	});
}

//修改部门信息
function updateDepart(){
	$.messager.progress({title:"更新部门",msg:"正在更新部门..."});
	
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data: $("#updateForm").serialize(),
		url:contextPath + "/depart/updateDeptLeaderById.htm",
		success:function(data){
			$.messager.alert('更新部门', data.message);
			$.messager.progress("close");
			
            if("0000" == data.code){
            	$("#updateWin").window("close");
                
                //2.刷新树
                //获得树
            	var currentCompanyId = $("#currentCompanyId").val();
            	initDepartTree(currentCompanyId);
                var treeObj = $.fn.zTree.getZTreeObj("departTree");
                //给树添加点击事件
                treeObj.setting.callback.onClick(null, treeObj.setting.treeId, treeObj.getNodeByParam("id",$("#departId").val(),null));
            }
		}
	});
}

//删除部门
function del(id){
	$.messager.confirm('删除部门','部门删除后不可恢复,确认要删除该部门？',function(r){
	    if (r){
	    	$.messager.progress({title:"删除部门",msg:"正在删除..."});
	    	
	    	$.post(basePath+"/depart/delete.htm",{id:id},function(data){
	    		//1.关闭提示框
	    		$.messager.progress("close");
	    		$.messager.alert("删除部门",data.message);
	    		
	    		if("0000" == data.code){
	    			var currentCompanyId = $("#currentCompanyId").val();
	            	initDepartTree(currentCompanyId);
				}
			},'json');
	    }
	});
}

function setLeaderAndPower(addOrEdit){
	if("1" == addOrEdit){//新增
		var type = $("#addDepartType").val();
		if("1" != type){//不是一级部门，则清空部门负责人和权利行使人
			$("#addLeaderName").val('');
			$("#addLeaderId").val('');
		}
	}else{//编辑
		var type = $("#editDepartType").val();
		if("1" != type){//不是一级部门，则清空部门负责人和权利行使人
			$("#editLeaderName").val('');
			$("#editLeaderId").val('');
		}
	}
}