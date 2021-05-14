$(function(){
	$(".col-left").height($(window).height()-99);
	//左侧树形部门展示
	var zNodes = zNodesInit();
	var ztreeObj = $.fn.zTree.init($("#tree"), setting, zNodes);
	ztreeObj.expandAll(true);
	//初始化数据
	$("#departId").val(zNodes[0].id);
	
	var datagrid = null; //定义全局变量datagrid
    var editRow = undefined; //定义全局变量：当前编辑的行
    var rowEdit = null;
    var powerDatagrid = null; //定义全局变量powerDatagrid(行使权力人弹框)
	//列表
    datagrid = $('#departTable').datagrid({
		singleSelect: true,
        remoteSort:false,
        selectOnCheck: false,
        checkOnSelect: false,
        method : "post",
        height:$(window).height() - 180,
        pagination:true,
        queryParams : {id:$("#departId").val()},
		url : basePath + "depart/list.htm",
        pagePosition:'bottom',
        pageSize: 100,
        pageList:[20,50,100,200],
        columns :[[
		    {field:'id',width:0},
		    {field:'name',width:200,align:'center',sortable:true,title:"部门名称"},
		    {field:'leaderName',width:120,align:'center',sortable:true,title:"负责人姓名"},
		    {field:'positionName',width:200,align:'center',sortable:true,title:"负责人职位"},
		    {field:'powerName',width:120,align:'center',sortable:true,title:"行使权力人"},
		    {field:'empCount',width:120,align:'center',sortable:true,title:"员工数"},
		    {field:'operation',title:"操作",width:180,align:'center',
		    	formatter: function(value, row, index){
		    		return "<a uid='" + row.id + "' id=" + JSON.stringify(row) + " onclick='update(this)'>修改</a> &nbsp; " +
		    				"<a uid='" + row.id + "' id=" + JSON.stringify(row) + " onclick='del(this)'>删除</a>";
		    	}
		    }
        ]],
        onLoadSuccess:function(response){
        	$(this).datagrid('hideColumn','id');
        },
        onBeforeEdit: function(index, row) {
        	rowEdit=row;
       	},
        toolbar: [{text: '新增', iconCls: 'icon-add', handler: function () {
        	JEND.page.dialog.show({title:'部门新增', id:'addWin', width:600, height:400 });
         }}]
	});
});

/**
 * 提交新增操作
 */
function addDepart() {
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:$("#addForm").serializeParams(),
		url:contextPath + "/depart/save.htm",
		success:function(data){
			if(data.code == '0000'){
				$.messager.alert('提示信息', data.message, "info", function() {
					$('#departTable').datagrid('reload');
					JEND.page.dialog.close();
				});
			}else{
				$.messager.alert('提示信息', data.message, "info", function() {});
			}
		}
	});
}

/**
 * 修改界面
 * @param tar
 */
function update(tar) {
	var depart=$(tar).attr("id");
	depart = JSON.parse(depart);
	JEND.load('util.dialog', function() {
	    JEND.util.dialog.pop({
	       title: "部门修改",
	       url: basePath + "/depart/toEdit.htm?departId=" + depart.id,
	       width: 950,
	       height: 600
	    });
	 });
//	var ipts=$('#updateWin').find('input');
//	var depart=$(tar).attr("id");
//	depart = JSON.parse(depart);
//	$.each(ipts,function(idx,ipt){
//		var name=$(ipt).attr('name');
//		var value=depart[name];
//		$(ipt).val(value);
//	});
//	if($("input[type=checkbox][name='type']").val() == 1) {
//		$("#leaderDL").css('display','block');
//		$("#codeDL").css('display','block');
//		$("input[type=checkbox][name='type']").prop('checked', true);
//	} else {
//		$("#leaderDL").css('display','none');
//		$("#codeDL").css('display','none');
//		$("input[type=checkbox][name='type']").prop('checked', false);
//	}
//	JEND.page.dialog.show({ title:'部门修改', id:'updateWin', width:600, height:400 });
}

/**
 * 删除
 * @param id
 */
function del(tar){
	var depart=$(tar).attr("id");
	depart = JSON.parse(depart);
	JEND.page.confirm('确认删除' + depart.name + '么？', function() {
		$.ajax({
			async:false,
			type:'post',
			dataType:'json',
			data:{'id':depart.id},
			url:contextPath + "/depart/delete.htm",
			success:function(data){
				$.messager.alert('提示信息', data.message, "info", function() {
					if(data.code == '0000'){
						location.reload();
						//$('#departTable').datagrid('reload');
					}
				});
			}
		});
	});
}

//关闭弹框
function closePop(){
	JEND.load('util.dialog', function() {
		JEND.util.dialog.close();
	});
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
	$("#departTableDiv").show();
	$("#departTable").datagrid({queryParams : {id:treeNode.id}});
	
	//给parentId赋值
	$("#parentId").val(treeNode.id);
	$("#parentName").val(treeNode.name);
}
/** ztree设置初始化 end */






