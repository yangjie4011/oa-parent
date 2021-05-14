$(function(){
	if($("input[type=checkbox][name='type']").val() == 1) {
		$("#leaderDL").css('display','block');
		$("#powerDL").css('display','block');
		$("input[type=checkbox][name='type']").prop('checked', true);
	} else {
		$("#leaderDL").css('display','none');
		$("#powerDL").css('display','none');
		$("input[type=checkbox][name='type']").prop('checked', false);
	}
});
/**
 * 提交修改操作
 */
function updateDepart() {
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:$("#updateForm").serializeParams(),
		url:contextPath + "/depart/update.htm",
		success:function(data){
			if(data.code == '0000'){
				$.messager.alert('提示信息', data.message, "info", function() {
					parent.location.reload();
					parent.closePop();
				});
			}else{
				$.messager.alert('提示信息', data.message, "info", function() {});
			}
		}
	});
}


/**
 * 有独立管理权限的选中事件
 * 如果勾选"有独立管理权限", 则显示部门负责人和行使权力人
 * 勾选后, 部门负责人默认为本部门最高的职位序列，最高职位序列员工空缺或者不唯一，指定行使权利人，否则无需指定
 */
function changeType() {
	if($("input[type='checkbox']").is(':checked') == true) {
		$("#leaderDL").css('display','block');
		$("#type").val(1);
		$.ajax({
			async:false,
			type:'post',
			dataType:'json',
			data:$("#id").val(),
			url:contextPath + "/depart/getMLeaderByDepartId.htm?departId=" + $("#id").val(),
			success:function(data){
				if(data != null && data != "" && data.length == 1) {
					$("#powerDL").css('display','none');
					var positionName = data[0].empPosition.position.positionName;
					var cnName = data[0].cnName;
					var eId = data[0].id;
					if(positionName == null || positionName == undefined || positionName == "undefined") {
						positionName = "";
					}
					if(cnName == null || cnName == undefined || cnName == "undefined") {
						cnName = "";
					}
					$("#leader").text(positionName + " " + cnName);
					$("#leaderId").val(eId);
				} else {
					$("#powerDL").css('display','block');
				}
			},
			error : function (data) {
				$("#powerDL").css('display','block');
			}
		});
	} else {
		$("#leaderDL").css('display','none');
		$("#powerDL").css('display','none');
		$("#type").val(2);
	}
}

/**
 * 选择行使权力人
 */
function choosePower() {
	JEND.load('util.dialog', function() {
	    JEND.util.dialog.pop({
	       title: "选择行使权力人",
	       url: basePath + "/depart/toChooseEmps.htm?departId=" + $("#id").val(),
	       width: 850,
	       height: 500
	    });
	 });
}

//关闭弹框
function closePop(){
	JEND.load('util.dialog', function() {
		JEND.util.dialog.close();
	});
}

/**
 * 取消
 */
function cancel() {
	parent.closePop();
}



//function aa() {
//	$.ajax({
//		async:false,
//		type:'post',
//		dataType:'json',
//		url:basePath + "/depart/treeApp.htm?parentId=",
//		success:function(data){
//			console.log(data);
//		},
//		error : function (data) {
//			$("#powerDL").css('display','block');
//		}
//	});
//}

