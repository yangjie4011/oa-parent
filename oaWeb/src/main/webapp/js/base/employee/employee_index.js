$(function() {
	var companyId = $("#currentCompanyId").val()
	setTimeout(initEmployeeType(companyId),0);
	
	//选择员工类型加载公司下拉
	$("#empTypeId").change(function(){
		var empTypeId = $("#empTypeId").val();
		getCoopCompanyByEmpType(empTypeId);
	});
	
	//默认加载所有员工信息
	setTimeout(getPage(),0);
	
	//默认加载婚姻状况下拉
	setTimeout(initMaritalStatus(),0);
	
	$("#query").click(function(){
		getPage();
	});
});

function getPage(){
	//查询列表初始化
	$('#employeeTable').datagrid({
        title:"员工信息列表",
        url: contextPath + "/employee/getPageList.htm",
        queryParams : $("#queryForm").serializeParams(),
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
            { field: 'code', title: '工号', width: 70 },
            { field: 'cnName', title: '姓名', width: 70 },
            { field: 'engName', title: '英文名', width: 60},
            { field: 'empType', title: '员工类型', width: 50,
            	formatter:function(value,row,index){
            		return value.typeCName;
            	}
            },
            { field: 'empDepart', title: '部门', width: 130	,
            	formatter:function(value,row,index){
            		if("" != getValByUndefined(value)){
            			return value.depart.name;
            		}
            	}
            },
            { field: 'empPosition', title: '职位', width: 70,
            	formatter:function(value,row,index){
            		if("" != getValByUndefined(value)){
            			return value.position.positionName;
            		}
            	}
            },
            { field: 'mobile', title: '手机', width: 70},
            { field: 'telephone', title: '电话号码', width: 70},
            { field: 'email', title: '邮箱', width: 80},
            { field: 'jobStatus', title: '在职状态', width: 50,
            	formatter:function(value,row,index){
            		if('0'==value){
            			return '在职'
            		}else if('1' == value){
            			return '离职';
            		}
            	}
            },
            { field: 'opt', title: '操作', width: 60,
            	formatter:function(value,row,index){
            		var update='<a id='+row.id+' onclick="update(this.id)" >修改</a>';
            		var check='<a id='+row.id+' onclick="check(this.id)" >查看</a>';
					var deletea='<a id='+row.id+' onclick="del(this.id)" >离职</a>';
					var val=update+"&nbsp;&nbsp;&nbsp;"+check+"&nbsp;&nbsp;&nbsp;"+deletea;
					return val;
            	}
            },
        ]],
        toolbar: [{
        	 text:'<a href="'+contextPath+'/employee/toEmployeeAdd.htm">新增</a>  ',
		}],         
    });
	return true;
}

//获得部门树
function getDepartTree(){
	var currentCompanyId = $("#currentCompanyId").val();
	
	if(currentCompanyId != ''){
		//初始化部门数据
		initDepartTree(currentCompanyId);
		
		JEND.page.dialog.show({ title:'选择部门', id:'showDepartTree', width:800, height:700 });
	}else{
		OA.titlePup('请选择公司','lose');
	}
}

//选择部门树节点事件
function onClickDepartTree(event, treeId, treeNode){
	closeWindown();
	$("#departId").val(treeNode.id);
	$("#departName").val(treeNode.name);
	
	//根据选择部门加载职位下拉
	getPositionByDepart();
}


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

//职位
function getPositionByDepart(){
	var departId = $("#departId").val();
	
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'departId':departId},
		url:contextPath + "/position/getListByCondition.htm",
		success:function(data){
			$("#positionId").empty();
			var options = "<option value=''>请选择</option>";
			
			$("#positionId").append(options);
			$.each(data, function(index) {
				$("#positionId").append("<option value= " + data[index].id + ">" + data[index].positionName + "</option>");
			});
			$("#positionId").show();
		}
	});
}

//修改
function update(id){
	$("#eidtEmployeeId").val(id);
	$("#editForm").submit();
}

//查看
function check(id){
	$("#checkEmployeeId").val(id);
	$("#checkForm").submit();
}

//离职
function del(id){
	JEND.page.confirm('员工离职后不可恢复,该员工确定要离职吗？', function() {
		JEND.page.dialog.show({ title:'员工离职', id:'deleteWin', width:800, height:700 });
		//加载要删除的ID
		$("#deleteID").val(id);
	}, function() {});
}

//清空部门选项
function clearDepart(){
	$("#departId").val('');
	$("#departName").val('');
}