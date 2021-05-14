$(function() {
	$('#dialogParentDiv').show();
	//初始化部门数据
	var currentCompanyId = $("#currentCompanyId").val();
	initScheduleGroupTree(currentCompanyId);
	$("#treeDiv").height($(window).height()-25);
	zTreeOnAsyncSuccess();
	$('#chooseScheduler').click(function(){
		$("input[name='pageStr']").val();
		$("#cancelQuitIds").val();
	    $("#employeeIds").val();
	    PersonSel_luffy.init({
	        conditions:['quitTimeBegin','quitTimeEnd'],
	        cb:function(result){
	            var nameVal = '';
	            var id = '';
	            for(var item in result){
	            	if(result[item].children.length){
	            		var children = result[item].children;
	            		children.forEach(function(worker){
	            			if(!nameVal)nameVal+=children[0].name;
	            			id = worker.id;
	            		})
	            	}
	            }
	            if(result[item].children.length == 1){
	            	$('#chooseScheduler').val(nameVal);
	            	$("#scheduler").val(id);
	            }else{
	            	$.messager.alert("提示","请选择一名员工！","warning");
	            }
	        }
	    })
	})
		$('#chooseAuditor').click(function(){
		$("input[name='pageStr']").val();
		$("#cancelQuitIds").val();
	    $("#employeeIds").val();
	    PersonSel_luffy.init({
	        conditions:['quitTimeBegin','quitTimeEnd'],
	        cb:function(result){
	            var nameVal = '';
	            var id = '';
	            for(var item in result){
	            	if(result[item].children.length){
	            		var children = result[item].children;
	            		children.forEach(function(worker){
	            			if(!nameVal)nameVal+=children[0].name;
	            			id = worker.id;
	            		})
	            	}
	            }
	            if(result[item].children.length == 1){
	            	$('#chooseAuditor').val(nameVal);
	            	$("#auditor").val(id);
	            }else{
	            	$.messager.alert("提示","请选择一名员工！","warning");
	            }
	        }
	    })
	})
	 $('#searchExistEmpList').searchbox({
		   // 在用户按下搜索按钮或回车键的时候调用 searcher 函数
		   searcher : function (value) {
			   var id = $("#scheduleGroupId").val();
			   getAllGroupEmp(id,value);
		   },
		   // 是否禁用搜索框
		   disabled: false
		  });
	
});

var groupSetting = {
	open: true,
	data: {
	    key: {
		    title:"name"
		},
		simpleData: {
			enable: true
		}
	},
	callback: {
		onClick: onClickDepartTree,	//点击部门
	},
	view:{
		showLine:false
	}
	
};
//选择部门节点，加载该部门下的排班组别
function onClickDepartTree(event, treeId, treeNode){
	//根节点是部门，只有部门才可以添加组
	if (treeNode.level == 0) {
		var departId = treeNode.id;//部门表主键
		var departName = treeNode.name;
        //选择部门,记录选中的部门id
    	$("#departId").val(departId);
        var scheduleGroup = $('#scheduleGroupTable').datagrid({
            title:departName,
            url: contextPath + "/schedule/getGroupListByDepartId.htm",
            queryParams : {departId:departId},
            width: ($("#mainDiv").width() - $("#mainDiv1").width() - 25),
            method: 'post',
            height:$(window).height()-5,
            fitColumns: true,
            sortName: 'id',
            sortOrder: 'desc',
            idField: 'id',
            pageSize: 10,
            pageList: [5,10,20],
            pagination: true,
            striped: true, //奇偶行是否区分
            singleSelect: true,//单选模式
            cache:false,
            onClickRow: function (rowIndex, rowData) {
            	  $(this).datagrid('unselectRow', rowIndex);
            	  },
            columns :[[
           		    {field:'id',width:0,hidden:true},
           		    {field:'name',width:200,align:'center',sortable:true,title:"分组名称"},
           		    {field:'schedulerName',width:120,align:'center',sortable:true,title:"排班人"},
           		    {field:'auditorName',width:200,align:'center',sortable:true,title:"排班审核人"},
           		    {field:'empCount',width:120,align:'center',sortable:true,title:"分组人数"},
           		    {field:'operation',title:"操作",width:180,align:'center',
           		    	formatter: function(value, row, index){
           		    		var id = row.id;
           		    		var groupName = row.name;
           		    		var update='<a id='+row.id+' onclick="update(this.id)" >编辑</a>';//
        					var groupManagement="<a id='+row.id+' onclick=\"groupManagement('"+id+"','"+groupName+"','"+departId+"')\" >组员管理</a>";
        					var del ='<a id='+row.id+' onclick="del(this.id)" >删除</a>';//
        					var val=update+"&nbsp;&nbsp;&nbsp;"+groupManagement+"&nbsp;&nbsp;&nbsp;"+del;
        					return val;
           		    	}
           		    }
            ]],
            toolbar: [{
    		    text:'添加下级分组',
    			iconCls: 'icon-add',
    			handler: function(){
    				add(departId);
    			}
    		}],         
        });
    	
    	return scheduleGroup;
    }else{
    	var departId = treeNode.pId;
    	$("#departId").val(departId);
    	var groupId = treeNode.id;
    	var groupName = treeNode.name;
    	var scheduleGroup = $('#scheduleGroupTable').datagrid({
    		  title:groupName,
              toolbar:false,
              url: contextPath + "/schedule/getGroupInfoByGroupId.htm",
              queryParams : {groupId:groupId},
              width: ($("#mainDiv").width() - $("#mainDiv1").width() - 25),
              height:$(window).height()-5,
              fitColumns: true,
              sortName: 'id',
              cache:false,
              sortOrder: 'desc',
              idField: 'id',
              pageSize: 10,
              pageList: [5,10,20],
              pagination: true,
              striped: true, //奇偶行是否区分
              singleSelect: true,//单选模式
              onClickRow: function (rowIndex, rowData) {
            	  $(this).datagrid('unselectRow', rowIndex);
              },
              columns :[[
            	  	{field:'id',width:0,hidden:true},
         		    {field:'name',width:150,align:'center',sortable:true,title:"分组名称"},
         		    {field:'schedulerName',width:150,align:'center',sortable:true,title:"排班人"},
         		    {field:'auditorName',width:150,align:'center',sortable:true,title:"排班审核人"},
         		    {field:'empCount',width:120,align:'center',sortable:true,title:"分组人数"},
         		    {field:'operation',title:"操作",width:180,align:'center',
         		    	formatter: function(value, row, index){
         		    		var id = row.id;
         		    		var groupName = row.name;
         		    		var update='<a id='+row.id+' onclick="update(this.id)" >编辑</a>';//
         		    		var groupManagement="<a id='+row.id+' onclick=\"groupManagement('"+id+"','"+groupName+"','"+departId+"')\" >组员管理</a>";
         		    		var del ='<a id='+row.id+' onclick="del(this.id)" >删除</a>';//
      					var val=update+"&nbsp;&nbsp;&nbsp;"+groupManagement+"&nbsp;&nbsp;&nbsp;"+del;
      					return val;
         		    	}
         		    }
          ]],
    	});
    	return scheduleGroup;
    }
}


//用于捕获异步加载正常结束的事件回调函数
function zTreeOnAsyncSuccess(){
	var treeId ="scheduleGroupTree";
    var treeObj = $.fn.zTree.getZTreeObj(treeId);
    var nodes = treeObj.getNodes();
    if (nodes.length>0) {
		//调用点击事件查询第一个部门班组信息
		onClickDepartTree(event,treeId,nodes[0]);
      }
}

//弹出新增分组界面
function add(departId){
	//先清空内容
	$("#saveForm").form('clear');
	$("#addOrEdit").val('1');//新增标识
	$("#addParentId").val($("#departId").val());
	var saveWin = $("#saveWin").window({
		title:"新增",
		width:300,
		height:200,
		collapsible:false,
		top: ($(window).height() - 410) * 0.5,
	    left: ($(window).width() - 480) * 0.5,
		shadow: true,
	    modal: true,
	    iconCls: 'icon-add',
	    closed: true,
	});
	$("#saveSchduleGroup").click(function(){
		addScheduleGroup();
	})
	saveWin.window("open");
}

//弹出更新班组界面
function update(id){
	//清空表单
	$("#saveForm").form('clear');
	$("#addOrEdit").val('2');//修改标识
	$("#addParentId").val($("#departId").val());
	$("#groupId").val(id);
	//回填班组信息
	getGroupById(id)
	//创建新的更新窗口
	var saveWin = $("#saveWin").window({
		title:"编辑分组",
		width:300,
		height:200,
		collapsible:false,
		top: ($(window).height() - 410) * 0.5,
	    left: ($(window).width() - 480) * 0.5,
		shadow: true,
	    modal: true,
	    iconCls: 'icon-add',
	    closed: true,
	});
	$("#saveSchduleGroup").click(function(){
		updateScheduleGroup();
	})
	saveWin.window('open');
}

function getGroupById(id){
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data: {groupId:id},
		url:contextPath + "/schedule/getGroupInfoByGroupId.htm",
		success:function(data){
			$("#name").val(data[0].name);
			$("#scheduler").val(data[0].scheduler);
			$("#auditor").val(data[0].auditor);
			$("#chooseScheduler").val(data[0].schedulerName);
			$("#chooseAuditor").val(data[0].auditorName);
		}
	});
}
//保存分组信息
function addScheduleGroup(){
	$.messager.progress({title:"保存排班组别",msg:"正在保存排班组别..."});
	var departId =$("#departId").val();
	var groupId = $("#scheduleGroupId").val();
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data: $("#saveForm").serialize(),
		url:contextPath + "/schedule/addScheduleGroup.htm",
		success:function(data){
			$.messager.alert('保存排班组别', data.message);
			//1.关闭进度条
        	$.messager.progress("close");
        	
            if("0000" == data.code){
            	$("#saveWin").window("close");
                //2.刷新树
            	window.location.href=basePath+"schedule/scheduleGroupSeting.htm";
            }
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				$.messager.progress("close");
				$("#saveWin").window("close");
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
        }
	});
}
//编辑分组信息
function updateScheduleGroup(){
	$.messager.progress({title:"保存排班组别",msg:"正在保存排班组别..."});
	var departId =$("#departId").val();
	var groupId = $("#scheduleGroupId").val();
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data: $("#saveForm").serialize(),
		url:contextPath + "/schedule/updateScheduleGroup.htm",
		success:function(data){
			$.messager.alert('保存排班组别', data.message);
			//1.关闭进度条
        	$.messager.progress("close");
        	
            if("0000" == data.code){
            	$("#saveWin").window("close");
                //2.刷新树
            	window.location.href=basePath+"schedule/scheduleGroupSeting.htm";
            }
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				$.messager.progress("close");
				$("#saveWin").window("close");
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
        }
	});
}

//删除分组
function del(id){
	$.messager.confirm('删除分组','是否确认删除排班分组？',function(r){
	    if (r){
	    	$.messager.progress({title:"删除分组",msg:"正在删除..."});
	    	
	    	$.post(basePath+"/schedule/deleteScheduleGroup.htm",{groupId:id},function(data){
	    		//1.关闭提示框
	    		$.messager.progress("close");
	    		$.messager.alert("删除分组",data.message);
	    		
	    		if("0000" == data.code){
	    			window.location.href=basePath+"schedule/scheduleGroupSeting.htm";
				}
			},'json');
	    }
	});
}

//管理组成员
function groupManagement(id,groupName,departId) {
	$("#scheduleGroupId").val("");
	$("#scheduleGroupId").val(id);
	//设置弹框标题
	$('#groupEmp').layout('panel', 'center').panel({ title: groupName });
	var groupManagementWin = $("#groupManagementWin").window({
		title:"组员管理",
		collapsible:false,
		top: ($(window).height() - 400) * 0.5,
	    left: ($(window).width() - 880) * 0.5,
		shadow: true,
	    modal: true,
	    iconCls: 'icon-man',
	    closed: true,
	});
	getUngroupedEmp();
	getAllGroupEmp(id);
	groupManagementWin.window("open");
}
//查询部门下未分组的员工
function getUngroupedEmp(){
	//清空缓存
	cleanCheck();
	var departId =$("#departId").val();
	var firstEntryTime = $('#firstEntryTime').datebox('getValue');
	$('#unGroupEmp').datagrid({
	    url: contextPath + "/schedule/getUngroupedEmp.htm",
	    queryParams :  {
	    	departId:departId,
	    	code:$('#code').val(),
	    	cnName:$('#cnName').val(),
	    	firstEntryTime:firstEntryTime,
	    },
	    method: 'post',
	    fitColumns: true,
	    sortName: 'id',
	    sortOrder: 'desc',
	    striped: true, //奇偶行是否区分
	    rownumbers: false,//行号
	    cache:false,
	    columns :[[
	    		{field:'ck',width:5,checkbox:true},
	    		{field:'id',width:5,hidden:true},
	   		    {field:'cnName',width:150,align:'center',sortable:true,title:"姓名"},
	   		    ]]
		});
}

//查询分组下所有员工
function getAllGroupEmp(id,value){
	var groupId = id;
	$('#existEmpList').datagrid({
    url: contextPath + "/schedule/getAllGroupEmp.htm",
    queryParams : {
    	id:groupId,
    	condition:value
    	},
    method: 'post',
    height:250,
    width:450,
    fitColumns: true,
    sortName: 'id',
    sortOrder: 'desc',
    striped: true, //奇偶行是否区分
    cache:false,
    columns :[[
   		    {field:'id',width:0,hidden:true},
   		    {field:'code',width:150,align:'center',sortable:true,title:"员工编号"},
   		    {field:'cnName',width:150,align:'center',sortable:true,title:"姓名"},
   		    {field:'departName',width:150,align:'center',sortable:true,title:"部门"},
   		    {field:'operation',title:"操作",width:180,align:'center',
 		    	formatter: function(value, row, index){
 		    		var empId = row.id;
 		    		var code = row.code;
 		    		var cnName = row.cnName;
 		    		var del="<a id='+row.id+' onclick=\"delMember('"+empId+"','"+groupId+"','"+code+"','"+cnName+"')\" >删除</a>";
					return del;
 		    	}
 		     }
   		    ]]
	});
}

//添加组员
function addMember(){
	var groupId = $("#scheduleGroupId").val();
	var departId =$("#departId").val();
	//获取所有选中的员工
	var empList = "";
	var rows = $('#unGroupEmp').datagrid('getSelections');
	if(rows.length < 1){
		$.messager.alert("提示","请选择员工！","warning")
	}else{
		for(var i=0; i<rows.length; i++){
			empList += rows[i].id+",";
		}
		$.messager.progress({title:"保存组员",msg:"正在保存组员..."});
		$.ajax({
			async:true,
			type:'post',
			dataType:'json',
			data: {
				empList:empList,
				groupId:groupId
			},
			url:contextPath + "/schedule/addMember.htm",
			success:function(data){
				$.messager.alert('保存组员', data.message);
				//1.关闭进度条
	        	$.messager.progress("close");
	            if("0000" == data.code){
	                //2.刷新表格中的内容
	                $('#unGroupEmp').datagrid('reload',{departId:departId});
	                $('#existEmpList').datagrid('reload',{id:groupId});
	                $('#scheduleGroupTable').datagrid('reload',{departId:departId});
	                $('#scheduleGroupTable').datagrid('reload',{groupId:groupId});
	            }
			},
			complete:function(XMLHttpRequest,textStatus){  
				if(XMLHttpRequest.status=="403"){
					$.messager.progress("close");
					$("#groupManagementWin").window("close");
					JEND.page.alert({type:"error", message:"您没有该操作权限！"});
				}
	        }
		});
		//清空缓存
		cleanCheck();
	}
	
}
//删除组员
function delMember(empId,groupId,code,cnName){
	var departId =$("#departId").val();
	$.messager.confirm('提示', "确认将"+code+"--"+cnName+"从组中移除吗？", function(b){
	      if (b){
	    	  $.messager.progress({title:"删除组员",msg:"正在删除组员..."});
	    	  $.ajax({
	    			async:true,
	    			type:'post',
	    			dataType:'json',
	    			data: {
	    				empId:empId,
	    				groupId:groupId
	    			},
	    			url:contextPath + "/schedule/delMember.htm",
	    			success:function(data){
	    				$.messager.alert('删除组员', data.message);
	    				//1.关闭进度条
	    	        	$.messager.progress("close");
	    	            if("0000" == data.code){
	    	                //2.刷新表格中的内容
	    	                $('#unGroupEmp').datagrid('reload',{departId:departId});
	    	                $('#existEmpList').datagrid('reload',{id:groupId});
	    	                $('#scheduleGroupTable').datagrid('reload',{departId:departId});
	    	                $('#scheduleGroupTable').datagrid('reload',{groupId:groupId});
	    	            }
	    			},
	    			complete:function(XMLHttpRequest,textStatus){  
	    				if(XMLHttpRequest.status=="403"){
	    					$.messager.progress("close");
	    					$("#groupManagementWin").window("close");
	    					JEND.page.alert({type:"error", message:"您没有该操作权限！"});
	    				}
	    	        }
	    		});
	      }
	});
}
function cleanCheck(){
	$('#unGroupEmp').datagrid('unselectAll');
}
function closeWindow(){
	//先清空内容
	$("#saveForm").form('clear');
	$("#saveWin").window("close");
}
