function update(tar){
	var ipts=$('#updateWin').find('input');
	var companyConfig=$(tar).attr("id");
	companyConfig = JSON.parse(companyConfig);
	$.each(ipts,function(idx,ipt){
		var name=$(ipt).attr('name');
		var value=companyConfig[name];
		$(ipt).val(value);
	});
	JEND.page.dialog.show({ title:'修改配置', id:'updateWin', width:800, height:700 });
}

var loadCompanySelect = function(form){
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		//data:{'id':$(tar).attr('id')},
		data:{"sid":Math.random()},
		url:contextPath + "/company/getListByCondition.htm",
		success:function(data){
			var select = $("#companyId");
			$.each(data, function() {
				select.append("<option value= " + this.id + ">" + this.name + "</option>");
			});
			select.show();
		}
	});
	
}

$(function() {
	
	var datagrid=$('#companyConfigTable').datagrid({
		singleSelect : true,
		idField : 'id',
		url : contextPath + "/companyPositionLevel/pageList.htm",
		method : "post",
		queryParams : $("#queryform").serializeParams(),
		pageSize:10,
		rownumbers:true,
		fit:true,
		pageList:[5,10,20],
		pagination:true,
		columns : [ [ 
		{
			field : 'companyName',
			title : '公司名称',
			width : 180
		},{
			field : 'code',
			title : '配置编码',
			width : 160
		},
		{
			field : 'displayName',
			title : '配置名称',
			width : 180
		},
		{
			field : 'displayCode',
			title : '隐藏码',
			width : 120
		},
		{
			field : 'description',
			title : '描述',
			width : 200
		},
		{
			field : 'userDef1',
			title : '用户自定义1',
			width : 90
		},
		{
			field : 'userDef2',
			title : '用户自定义2',
			width : 90
		},
		{
			field : 'userDef3',
			title : '用户自定义3',
			width : 90
		},
		{
			field : 'userDef4',
			title : '用户自定义4',
			width : 90
		},
		{
			field : 'userDef5',
			title : '用户自定义5',
			width : 90
		},{
			field : 'delFlag',
			title : '有效状态',
			align:'center',
			width : 80,
			formatter:function(value,idx,row){
				return (value=='0'?'有效':'<span style="color:red">无效</span>');
			}
		},{
			field : 'remark',
			title : '备注',
			width : 300
		},{
			field : 'createTime',
			title : '创建时间',
			align:'center',
			width : 160,
			formatter:function(value,idx,row){
				if(value){
					var date=new Date(value);
					value=date.Format("yyyy-MM-dd hh:mm:ss");
				}
				return value;
			}
		},{
			field : 'createUser',
			title : '创建人',
			width : 110
		},{
			field : 'updateTime',
			title : '更新时间',
			align:'center',
			width : 160,
			formatter:function(value,idx,row){
				if(value){
					var date=new Date(value);
					value=date.Format("yyyy-MM-dd hh:mm:ss");
				}
				return value;
			}
		},{
			field : 'updateUser',
			title : '更新人',
			width : 110
		},{
			field : 'opt',
			align : 'center',
			title : '操作',
			width : 200,
			formatter:function(value,row,index){
				var recover='<a class="btn-recover btn-red  btn-common "  id="'+row.id+'" onclick="recover(this)" >恢复</a>';
				var discard='<a class="btn-discard btn-red  btn-common "  id="'+row.id+'" onclick="discard(this)" >作废</a>';
				var deletea='<a class="btn-delete btn-red  btn-common ml5"  id="'+row.id+'" onclick="deletea(this)" >删除</a>';//
				var update='<a class="btn-update btn-red  btn-common ml5"  id=\''+JSON.stringify(row)+'\' onclick="update(this)" >修改</a>';
				var val=(row.status=='1'?discard:recover)+deletea+update;
				return val;
			}
		} ] ],
		onLoadSuccess : function(data){
			$('#companyConfigTable').datagrid('clearChecked');
			$('#companyConfigTable').datagrid('clearSelections');
		},
		onClickRow : function(index, row){
		}
	});
	
	$("#query").click(function(){
		$('#companyConfigTable').datagrid('reload',$("#queryform").serializeParams());
	});
	
	$("#reset").click(function(){
		$("#queryform")[0].reset();
	});
	
	$("#insert").click(function(){
		JEND.page.dialog.show({ title:'新增配置', id:'addWin', width:800, height:700 });
		loadCompanySelect("#addForm");
	});
	
	$("#btn-update").click(function(){
		
		$.ajax({
			async:false,
			type:'post',
			dataType:'json',
			data:$("#updateForm").serializeParams(),
			url:contextPath + "/companyConfig/update.htm",
			success:function(data){
				$.messager.alert('提示信息', data.message, "info", function() {
					if(data.success){
						$('#companyConfigTable').datagrid('reload');
						closeWindown();
					}
				});
			}
		});
	});

	
	$(".btn-cancel").click(function(){
		closeWindown();
	});
	
	$("#btn-delete").click(function(){
		
		$.ajax({
			async:false,
			type:'post',
			dataType:'json',
			data:$("#deleteForm").serializeParams(),
			url:contextPath + "/companyConfig/delete.htm",
			success:function(data){
				if(data.success){
					$('#companyConfigTable').datagrid('reload',$("#queryform").serializeParams());
					closeWindown();
				}else{
					$.messager.alert('提示信息', data.message, "info", function() {});
				}
			}
		});
	});
	
	$("#btn-add").click(function(){
		$.ajax({
			async:false,
			type:'post',
			dataType:'json',
			data:$("#addForm").serializeParams(),
			url:contextPath + "/companyConfig/insert.htm",
			success:function(data){
				if(data.success){
					$.messager.alert('提示信息', data.message, "info", function() {
						$("#addForm")[0].reset();
						$('#companyConfigTable').datagrid('reload');
						closeWindown();
					});
				}else{
					$.messager.alert('提示信息', data.message, "info", function() {});
				}
			}
		});
		
	});
	
	$("#addForm #getValidateAdd").click(function(){
		var phone = $("#addForm #phone").val();
		getValidateCode(phone);
	});
	
	$("#updateForm #getValidateCodeUpd").click(function(){
		var phone = $("#updateForm #phone").val();
		getValidateCode(phone);
	});
	
	$("#deleteForm #getValidateCodeDel").click(function(){
		var phone = $("#deleteForm #phone").val();
		getValidateCode(phone);
	});
	
	var getValidateCode = function(phone){
		$.ajax({
			async:false,
			type:'post',
			dataType:'json',
			data:{phone:phone,sid:Math.random()},
			url:contextPath + "/companyConfig/getValidateCode.htm",
			success:function(data){
				$.messager.alert('提示信息', data.message, "info", function() {});
			}
		});
		
	}

	
});
