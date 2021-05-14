$(function(){
	var authority = $("#authority").val();
	if(authority == 'hr'){
		$("#showEmpPic").show();
	}
})

//上传图片
function uploadPic(pic){
	if(pic != ""){
		$(pic).unbind();
		$(pic).on('change', function () {
			OA.pageLoading(true);
			$('#empPic').unbind()
			
			//执行上传文件操作的函数  
	        $.ajaxFileUpload({  
		        url:contextPath + "/employeeApp/uploadPic.htm?id="+$("#empId").val(),
		        secureuri:true,                          	 //是否启用安全提交,默认为false   
		        fileElementId:'empPic',               		 //文件选择框的id属性  
		        dataType:'text',                             //服务器返回的格式,选择json或者xml貌似有问题
		        success:function(data){                      //服务器响应成功时的处理函数  
		        	OA.pageLoading(false);
		        	
		        	data = data.replace(/<pre.*?>/g, '');     //ajaxFileUpload会对服务器响应回来的text内容加上<pre style="....">text</pre>前后缀  
		            data = data.replace(/<PRE.*?>/g, '');  
		            data = data.replace("<PRE>", '');  
		            data = data.replace("</PRE>", '');  
		            data = data.replace("<pre>", '');  
		            data = data.replace("</pre>", '');     //本例中设定上传文件完毕后,服务端会返回给前台[0`filepath]  
		           // data = eval('(' + data + ')');
		        	if(data.flag){         //0表示上传成功(后跟上传后的文件路径),1表示失败(后跟失败描述)
			            $("#ePic").css("background:","#fff url(" + data.picUrl + ") no-repeat center;background-size:contain;-webkit-background-size:contain");
			           
			            OA.titlePup(data.msg,'win');
		            }else{  
		            	OA.titlePup(data.msg,'lose');
		            }
		        	
		        	//3秒页面重刷
		        	setTimeout("reload()",2000);
		        },  
		        error:function(data, status, e){ //服务器响应失败时的处理函数  
		        	OA.pageLoading(false);
		            OA.titlePup('员工照片上传失败,请重试','lose');
		        }
	        });  
		});
	}
}

function reload(){
	window.location.reload();
}
