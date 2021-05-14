$(function() {
	//初始化权利行使人数据
	getEmpList();
	
	$("#query").click(function(){
		getEmpList();
	});
});

function getEmpList(){
	var emps = $('#empTable').datagrid({
        title:"员工管理",
        url: contextPath + "/employee/getPageList.htm",
        queryParams : $("#queryform").serializeParams(),
        width: 800,
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
        columns :[[
       		    {field:'id',width:0,hidden:true},
       		    {field:'cnName',width:200,align:'center',sortable:true,title:"员工中文名",
       		    	formatter: function(value, row, index){
       		    		var val ='<a id='+row.id+' onclick="setVal(this)" >'+value+'</a>';//
    					return val;
       		    	}
       		    },
       		    {field:'engName',width:120,align:'center',sortable:true,title:"员工英文名"},
       		    {field:'empType.typeCName',width:120,align:'center',sortable:true,title:"员工类型"},
       		    {field:'departName',width:120,align:'center',sortable:true,title:"部门"},
       		    {field:'positionName',width:200,align:'center',sortable:true,title:"职位"}
        ]],
        toolbar: [{
		}],         
    });
	
	return emps;
}

function setVal(row){
	var addOrEdit = window.opener.document.getElementById("addOrEdit").value;
	
	if("1" == addOrEdit){//新增页面
		window.opener.document.getElementById("addPowerId").value = row.id;
		window.opener.document.getElementById("addPowerName").value = row.text;
		window.opener.document.getElementById("addLeaderId").value = "";
		window.opener.document.getElementById("addLeaderName").value = "";
	}else{//编辑页面
		window.opener.document.getElementById("editPowerId").value = row.id;
		window.opener.document.getElementById("editPowerName").value = row.text;
		window.opener.document.getElementById("editLeaderId").value = "";
		window.opener.document.getElementById("editLeaderName").value = "";
	}
	
	window.close();  
}