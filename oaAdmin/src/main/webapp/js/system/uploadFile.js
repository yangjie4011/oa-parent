function upload(id){
	var uploadFile = $("#uploadFile" + id).val();  
    if(uploadFile == ''){  
        JEND.page.alert({type:"error", message:"请选择excel,再上传"});
    }else if(uploadFile.lastIndexOf(".xls")<0){//可判断以.xls和.xlsx结尾的excel  
        JEND.page.alert({type:"error", message:"只能上传Excel文件"});
    }else{  
       	var url =  basePath + 'sysConfig/uploadExcel.htm';
    	var formId = parseInt(id) - 1;
        var formData = new FormData($('form')[formId]);  
        sendAjaxRequest(url,formData,id);
    }  
}

function sendAjaxRequest(url,formData,id){
	pageLoading(true);//加载动画
	
    $.ajax({  
        url : url,  
        type : 'POST',  
        data : formData,  
        success : function(result) {  
            pageLoading(false);//关闭动画
            $("#uploadFileMsg" + id).html(result);
        },  
        error : function(result) {  
            pageLoading(false);//关闭动画
            $("#uploadFileMsg" + id).html(result.responseText);
        },  
        cache : false,  
        contentType : false,  
        processData : false  
    });  
}