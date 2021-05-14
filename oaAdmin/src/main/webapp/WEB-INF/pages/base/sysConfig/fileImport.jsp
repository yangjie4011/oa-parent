<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>文件上传管理</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/system/uploadFile.js?v=20180212006"/></script>
</head>
<body>
	<form id="form1" action="sysConfig/uploadExcel.html" enctype="multipart/form-data">
		<div style="padding-top: 100px;padding-left: 300px;">
			<div>
				<span>
					公司表&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</span>
				<span>
					<input id="uploadFile1" type="file" name="file" placeholder="请选择员工类型表表"/>
				</span>
				<span>
					<input type="button" onclick="upload(1);" value="上传"/>
				</span>
				<span id="uploadFileMsg1"></span>
			</div>
		</div>
	</form>
	
	<form action="sysConfig/uploadExcel.html" method="post" enctype="multipart/form-data">
		<div style="padding-left: 300px;">
			<div>
				<span>
					员工类型表&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</span>
				<span>
					<input id="uploadFile2" type="file" name="file" placeholder="请选择员工类型表表"/>
				</span>
				<span>
					<input type="button" onclick="upload(2);" value="上传"/>
				</span>
				<span id="uploadFileMsg2"></span>
			</div>
		</div>
	</form>
	
	<form action="sysConfig/uploadExcel.html" method="post" enctype="multipart/form-data">
		<div style="padding-left: 300px;">
			<div>
				<span>
					职级表&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</span>
				<span>
					<input id="uploadFile3" type="file" name="file" placeholder="请选择职级表表"/>
				</span>
				<span>
					<input type="button" onclick="upload(3);" value="上传"/>
				</span>
				<span id="uploadFileMsg3"></span>
			</div>
		</div>
	</form>
	
	<form action="sysConfig/uploadExcel.html" method="post" enctype="multipart/form-data">
		<div style="padding-left: 300px;">
			<div>
				<span>
					职位序列表&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</span>
				<span>
					<input id="uploadFile4" type="file" name="file" placeholder="请选择职位序列表表"/>
				</span>
				<span>
					<input type="button" onclick="upload(4);" value="上传"/>
				</span>
				<span id="uploadFileMsg4"></span>
			</div>
		</div>
	</form>
	
	<form action="sysConfig/uploadExcel.html" method="post" enctype="multipart/form-data">
		<div style="padding-left: 300px;">
			<div>
				<span>
					部门表&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</span>
				<span>
					<input id="uploadFile5" type="file" name="file" placeholder="请选择部门表"/>
				</span>
				<span>
					<input type="button" onclick="upload(5);" value="上传"/>
				</span>
				<span id="uploadFileMsg5"></span>
			</div>
		</div>
	</form>
	
	<form action="sysConfig/uploadExcel.html" method="post" enctype="multipart/form-data">
		<div style="padding-left: 300px;">
			<div>
				<span>
					职位表&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</span>
				<span>
					<input id="uploadFile6" type="file" name="file" placeholder="请选择职位表"/>
				</span>
				<span>
					<input type="button" onclick="upload(6);" value="上传"/>
				</span>
				<span id="uploadFileMsg6"></span>
			</div>
		</div>
	</form>
	
	<form action="sysConfig/uploadExcel.html" method="post" enctype="multipart/form-data">
		<div style="padding-left: 300px;">
			<div>
				<span>
					部门和职位关系表
				</span>
				<span>
					<input id="uploadFile7" type="file" name="file" placeholder="请选择部门和职位关系表表"/>
				</span>
				<span>
					<input type="button" onclick="upload(7);" value="上传"/>
				</span>
				<span id="uploadFileMsg7"></span>
			</div>
		</div>
	</form>
	
	<form action="sysConfig/uploadExcel.html" method="post" enctype="multipart/form-data">
		<div style="padding-left: 300px;">
			<div>
				<span>
					员工表&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</span>
				<span>
					<input id="uploadFile8" type="file" name="file" placeholder="请选择员工表表"/>
				</span>
				<span>
					<input type="button" onclick="upload(8);" value="上传"/>
				</span>
				<span id="uploadFileMsg8"></span>
			</div>
		</div>
	</form>
	
	<form action="sysConfig/uploadExcel.html" method="post" enctype="multipart/form-data">
		<div style="padding-left: 300px;">
			<div>
				<span>
					员工和部门关系&nbsp;&nbsp;
				</span>
				<span>
					<input id="uploadFile9" type="file" name="file" placeholder="请选择员工和部门关系表"/>
				</span>
				<span>
					<input type="button" onclick="upload(9);" value="上传"/>
				</span>
				<span id="uploadFileMsg9"></span>
			</div>
		</div>
	</form>
	
	<form action="sysConfig/uploadExcel.html" method="post" enctype="multipart/form-data">
		<div style="padding-left: 300px;">
			<div>
				<span>
					员工和职位关系&nbsp;&nbsp;
				</span>
				<span>
					<input id="uploadFile10" type="file" name="file" placeholder="请选择员工和职位关系表"/>
				</span>
				<span>
					<input type="button" onclick="upload(10);" value="上传"/>
				</span>
				<span id="uploadFileMsg10"></span>
			</div>
		</div>
	</form>
	
	<form action="sysConfig/uploadExcel.html" method="post" enctype="multipart/form-data">
		<div style="padding-left: 300px;">
			<div>
				<span>
					用户表&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</span>
				<span>
					<input id="uploadFile11" type="file" name="file" placeholder="请选择用户表"/>
				</span>
				<span>
					<input type="button" onclick="upload(11);" value="上传"/>
				</span>
				<span id="uploadFileMsg11"></span>
			</div>
		</div>
	</form>
	
	<form action="sysConfig/uploadExcel.html" method="post" enctype="multipart/form-data">
		<div style="padding-left: 300px;">
			<div>
				<span>
					年假表&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</span>
				<span>
					<input id="uploadFile12" type="file" name="file" placeholder="请选择年假表"/>
				</span>
				<span>
					<input type="button" onclick="upload(12);" value="上传"/>
				</span>
				<span id="uploadFileMsg12"></span>
			</div>
		</div>
	</form>
	
	<form action="sysConfig/uploadExcel.html" method="post" enctype="multipart/form-data">
		<div style="padding-left: 300px;">
			<div>
				<span>
					病假表&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</span>
				<span>
					<input id="uploadFile13" type="file" name="file" placeholder="请选择病假表"/>
				</span>
				<span>
					<input type="button" onclick="upload(13);" value="上传"/>
				</span>
				<span id="uploadFileMsg13"></span>
			</div>
		</div>
	</form>
	
	<form action="sysConfig/uploadExcel.html" method="post" enctype="multipart/form-data">
		<div style="padding-left: 300px;">
			<div>
				<span>
					调休表&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</span>
				<span>
					<input id="uploadFile14" type="file" name="file" placeholder="请选择调休表"/>
				</span>
				<span>
					<input type="button" onclick="upload(14);" value="上传"/>
				</span>
				<span id="uploadFileMsg14"></span>
			</div>
		</div>
	</form>
	
	<form action="sysConfig/uploadExcel.html" method="post" enctype="multipart/form-data">
		<div style="padding-left: 300px;">
			<div>
				<span>
					打卡数据表&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</span>
				<span>
					<input id="uploadFile15" type="file" name="file" placeholder="请选择打卡数据表"/>
				</span>
				<span>
					<input type="button" onclick="upload(15);" value="上传"/>
				</span>
				<span id="uploadFileMsg15"></span>
			</div>
		</div>
	</form>
</body>
</html>