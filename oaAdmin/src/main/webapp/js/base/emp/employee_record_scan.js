//编辑
function toEditHtm(){
	window.location.href = basePath+"employeeRecord/update.htm?type=2&employeeId="+$("#employeeId").val();
}

//返回
function toIndexHtm(){
	window.location.href = basePath+"employeeRecord/index.htm";
}

//下载
function downLoad(){
	window.location.href = basePath+"employeeRecord/downLoadEmployeeRecord.htm?employeeId="+$("#employeeId").val();
}
